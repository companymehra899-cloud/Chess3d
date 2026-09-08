package com.onlinechessgame.app.chess.online

import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.onlinechessgame.app.chess.model.ChatMessage
import com.onlinechessgame.app.chess.model.Country
import com.onlinechessgame.app.chess.model.GameStatus
import com.onlinechessgame.app.chess.model.Move
import com.onlinechessgame.app.chess.model.OnlinePlayer
import com.onlinechessgame.app.chess.model.PieceColor
import com.onlinechessgame.app.chess.model.PieceType
import com.onlinechessgame.app.chess.model.Position
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

data class RemotePlayerInfo(
    val uid: String = "",
    val username: String = "Player",
    val rating: Int = 1200,
    val countryCode: String = "US",
    val countryName: String = "United States",
    val countryFlag: String = "US",
    val avatarId: String = "man_portrait_1"
)

data class RemoteMove(
    val fromRow: Int = 0,
    val fromCol: Int = 0,
    val toRow: Int = 0,
    val toCol: Int = 0,
    val promotion: String? = null,
    val seq: Int = 0,
    val byUid: String = ""
) {
    fun toPositions(): Pair<Position, Position> = Position(fromRow, fromCol) to Position(toRow, toCol)

    fun promotionType(): PieceType? = when (promotion) {
        "Q" -> PieceType.QUEEN
        "R" -> PieceType.ROOK
        "B" -> PieceType.BISHOP
        "N" -> PieceType.KNIGHT
        else -> null
    }
}

data class RemoteGameState(
    val gameId: String,
    val white: RemotePlayerInfo,
    val black: RemotePlayerInfo,
    val status: String,
    val currentTurn: String,
    val lastMove: RemoteMove?,
    val moveCount: Int,
    val whiteTime: Int,
    val blackTime: Int,
    val gameStatus: String,
    val drawOfferFrom: String?,
    val resignFrom: String?
)

sealed class MatchResult {
    data class Matched(val gameId: String, val playerColor: PieceColor, val opponent: RemotePlayerInfo) : MatchResult()
    data object TimedOut : MatchResult()
    data object Cancelled : MatchResult()
    data class Failed(val message: String) : MatchResult()
}

class FirebaseOnlineService {

    private val auth: FirebaseAuth? by lazy {
        runCatching { FirebaseAuth.getInstance() }.getOrNull()
    }

    private val db: FirebaseDatabase? by lazy {
        runCatching { FirebaseDatabase.getInstance() }.getOrNull()
    }

    val uid: String?
        get() = auth?.currentUser?.uid

    fun isAvailable(): Boolean {
        return try {
            FirebaseApp.getInstance()
            auth != null && db != null
        } catch (_: Exception) {
            false
        }
    }

    suspend fun ensureSignedIn(): String {
        val existing = auth?.currentUser?.uid
        if (existing != null) return existing
        val result = auth?.signInAnonymously()?.await()
            ?: throw IllegalStateException("Firebase Auth is not configured")
        return result.user?.uid ?: throw IllegalStateException("Anonymous sign-in failed")
    }

    suspend fun findOrCreateMatch(
        player: RemotePlayerInfo,
        timeControlMinutes: Int,
        onQueueJoined: () -> Unit,
        shouldCancel: () -> Boolean
    ): MatchResult {
        val database = db ?: return MatchResult.Failed("Firebase Database is not configured")
        val myUid = try {
            ensureSignedIn()
        } catch (e: Exception) {
            return MatchResult.Failed(e.message ?: "Sign-in failed")
        }

        val queueRef = database.getReference("matchmaking/queue")
        val gamesRef = database.getReference("games")
        val myQueueRef = queueRef.child(myUid)

        val me = player.copy(uid = myUid)
        val now = System.currentTimeMillis()
        val waitingCutoff = now - 25_000

        val waiting = runCatching {
            queueRef.get().await().children.mapNotNull { snap ->
                val info = snap.toPlayer() ?: return@mapNotNull null
                val ts = snap.child("timestamp").getValue(Long::class.java) ?: 0L
                val matched = snap.child("matchedGameId").getValue(String::class.java)
                val timeControl = snap.child("timeControlMinutes").getValue(Int::class.java) ?: 5
                Triple(info, ts, matched to timeControl)
            }.firstOrNull { (info, ts, extra) ->
                info.uid != myUid && extra.first.isNullOrBlank() && extra.second == timeControlMinutes && ts >= waitingCutoff
            }?.first
        }.getOrNull()

        if (waiting != null) {
            val gameId = gamesRef.push().key ?: "${myUid}_${waiting.uid}"
            val iAmWhite = myUid < waiting.uid
            val white = if (iAmWhite) me else waiting
            val black = if (iAmWhite) waiting else me
            val gamePayload = mapOf(
                "whiteUid" to white.uid,
                "blackUid" to black.uid,
                "white" to white.toMap(),
                "black" to black.toMap(),
                "status" to "playing",
                "currentTurn" to "WHITE",
                "moveCount" to 0,
                "whiteTime" to timeControlMinutes * 60,
                "blackTime" to timeControlMinutes * 60,
                "gameStatus" to GameStatus.IN_PROGRESS.name,
                "createdAt" to ServerValue.TIMESTAMP,
                "timeControlMinutes" to timeControlMinutes
            )
            gamesRef.child(gameId).updateChildren(gamePayload).await()
            queueRef.child(waiting.uid).child("matchedGameId").setValue(gameId).await()
            myQueueRef.removeValue().await()
            val color = if (iAmWhite) PieceColor.WHITE else PieceColor.BLACK
            val opponent = if (iAmWhite) black else white
            return MatchResult.Matched(gameId, color, opponent)
        }

        val queuePayload = me.toMap() + mapOf(
            "timestamp" to ServerValue.TIMESTAMP,
            "timeControlMinutes" to timeControlMinutes,
            "matchedGameId" to ""
        )
        myQueueRef.setValue(queuePayload).await()
        onQueueJoined()

        return try {
            val startTime = System.currentTimeMillis()
            while (System.currentTimeMillis() - startTime < 22_000) {
                if (shouldCancel()) {
                    myQueueRef.removeValue().await()
                    return MatchResult.Cancelled
                }
                delay(400)

                val mySnap = myQueueRef.get().await()
                val assignedGameId = mySnap.child("matchedGameId").getValue(String::class.java)
                if (!assignedGameId.isNullOrBlank()) {
                    myQueueRef.removeValue().await()
                    return loadMatchedGame(gamesRef, assignedGameId, myUid)
                }

                val other = findWaitingPlayer(queueRef, myUid, timeControlMinutes, System.currentTimeMillis() - 25_000)
                if (other != null && myUid > other.uid) {
                    val created = createGame(gamesRef, queueRef, myQueueRef, me, other, timeControlMinutes)
                    if (created != null) return created
                }
            }
            myQueueRef.removeValue().await()
            MatchResult.TimedOut
        } catch (e: CancellationException) {
            runCatching { myQueueRef.removeValue().await() }
            throw e
        } catch (e: Exception) {
            runCatching { myQueueRef.removeValue().await() }
            MatchResult.Failed(e.message ?: "Matchmaking failed")
        }
    }

    private suspend fun findWaitingPlayer(
        queueRef: com.google.firebase.database.DatabaseReference,
        myUid: String,
        timeControlMinutes: Int,
        waitingCutoff: Long
    ): RemotePlayerInfo? {
        return runCatching {
            queueRef.get().await().children.mapNotNull { snap ->
                val info = snap.toPlayer() ?: return@mapNotNull null
                val ts = snap.child("timestamp").getValue(Long::class.java) ?: 0L
                val matched = snap.child("matchedGameId").getValue(String::class.java)
                val timeControl = snap.child("timeControlMinutes").getValue(Int::class.java) ?: 5
                Triple(info, ts, matched to timeControl)
            }.firstOrNull { (info, ts, extra) ->
                info.uid != myUid && extra.first.isNullOrBlank() && extra.second == timeControlMinutes && ts >= waitingCutoff
            }?.first
        }.getOrNull()
    }

    private suspend fun createGame(
        gamesRef: com.google.firebase.database.DatabaseReference,
        queueRef: com.google.firebase.database.DatabaseReference,
        myQueueRef: com.google.firebase.database.DatabaseReference,
        me: RemotePlayerInfo,
        waiting: RemotePlayerInfo,
        timeControlMinutes: Int
    ): MatchResult.Matched? {
        val myUid = me.uid
        val gameId = gamesRef.push().key ?: "${myUid}_${waiting.uid}"
        val iAmWhite = myUid < waiting.uid
        val white = if (iAmWhite) me else waiting
        val black = if (iAmWhite) waiting else me
        val gamePayload = mapOf(
            "whiteUid" to white.uid,
            "blackUid" to black.uid,
            "white" to white.toMap(),
            "black" to black.toMap(),
            "status" to "playing",
            "currentTurn" to "WHITE",
            "moveCount" to 0,
            "whiteTime" to timeControlMinutes * 60,
            "blackTime" to timeControlMinutes * 60,
            "gameStatus" to GameStatus.IN_PROGRESS.name,
            "createdAt" to ServerValue.TIMESTAMP,
            "timeControlMinutes" to timeControlMinutes
        )
        gamesRef.child(gameId).updateChildren(gamePayload).await()
        queueRef.child(waiting.uid).child("matchedGameId").setValue(gameId).await()
        myQueueRef.removeValue().await()
        val color = if (iAmWhite) PieceColor.WHITE else PieceColor.BLACK
        val opponent = if (iAmWhite) black else white
        return MatchResult.Matched(gameId, color, opponent)
    }

    private suspend fun loadMatchedGame(
        gamesRef: com.google.firebase.database.DatabaseReference,
        gameId: String,
        myUid: String
    ): MatchResult {
        val gameSnap = gamesRef.child(gameId).get().await()
        val white = gameSnap.child("white").toPlayer()
        val black = gameSnap.child("black").toPlayer()
        val whiteUid = gameSnap.child("whiteUid").getValue(String::class.java)
        val color = if (whiteUid == myUid) PieceColor.WHITE else PieceColor.BLACK
        val opponent = if (color == PieceColor.WHITE) black else white
        return if (opponent != null) {
            MatchResult.Matched(gameId, color, opponent)
        } else {
            MatchResult.Failed("Matched game is missing player data")
        }
    }

    fun leaveQueue() {
        val myUid = uid ?: return
        db?.getReference("matchmaking/queue")?.child(myUid)?.removeValue()
    }

    fun observeGame(gameId: String): Flow<RemoteGameState> = callbackFlow {
        val ref = db?.getReference("games")?.child(gameId)
        if (ref == null) {
            close()
            return@callbackFlow
        }
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val white = snapshot.child("white").toPlayer() ?: return
                val black = snapshot.child("black").toPlayer() ?: return
                val lastMoveSnap = snapshot.child("lastMove")
                val lastMove = if (lastMoveSnap.exists()) {
                    RemoteMove(
                        fromRow = lastMoveSnap.child("fromRow").getValue(Int::class.java) ?: 0,
                        fromCol = lastMoveSnap.child("fromCol").getValue(Int::class.java) ?: 0,
                        toRow = lastMoveSnap.child("toRow").getValue(Int::class.java) ?: 0,
                        toCol = lastMoveSnap.child("toCol").getValue(Int::class.java) ?: 0,
                        promotion = lastMoveSnap.child("promotion").getValue(String::class.java),
                        seq = lastMoveSnap.child("seq").getValue(Int::class.java) ?: 0,
                        byUid = lastMoveSnap.child("byUid").getValue(String::class.java).orEmpty()
                    )
                } else null
                trySend(
                    RemoteGameState(
                        gameId = gameId,
                        white = white,
                        black = black,
                        status = snapshot.child("status").getValue(String::class.java) ?: "playing",
                        currentTurn = snapshot.child("currentTurn").getValue(String::class.java) ?: "WHITE",
                        lastMove = lastMove,
                        moveCount = snapshot.child("moveCount").getValue(Int::class.java) ?: 0,
                        whiteTime = snapshot.child("whiteTime").getValue(Int::class.java) ?: 180,
                        blackTime = snapshot.child("blackTime").getValue(Int::class.java) ?: 180,
                        gameStatus = snapshot.child("gameStatus").getValue(String::class.java)
                            ?: GameStatus.IN_PROGRESS.name,
                        drawOfferFrom = snapshot.child("drawOfferFrom").getValue(String::class.java),
                        resignFrom = snapshot.child("resignFrom").getValue(String::class.java)
                    )
                )
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    fun observeChat(gameId: String): Flow<ChatMessage> = callbackFlow {
        val ref = db?.getReference("games")?.child(gameId)?.child("chat")
        if (ref == null) {
            close()
            return@callbackFlow
        }
        val myUid = uid
        val listener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val senderUid = snapshot.child("senderUid").getValue(String::class.java).orEmpty()
                val senderName = snapshot.child("senderName").getValue(String::class.java) ?: "Player"
                val message = snapshot.child("message").getValue(String::class.java) ?: return
                val timestamp = snapshot.child("timestamp").getValue(Long::class.java) ?: System.currentTimeMillis()
                trySend(
                    ChatMessage(
                        id = snapshot.key ?: java.util.UUID.randomUUID().toString(),
                        senderName = senderName,
                        message = message,
                        isFromPlayer = senderUid == myUid,
                        timestamp = timestamp
                    )
                )
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        ref.addChildEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    suspend fun publishMove(gameId: String, move: Move, seq: Int, nextTurn: PieceColor, gameStatus: GameStatus) {
        val myUid = uid ?: return
        val payload = mapOf(
            "fromRow" to move.from.row,
            "fromCol" to move.from.col,
            "toRow" to move.to.row,
            "toCol" to move.to.col,
            "promotion" to move.promotion?.notation,
            "seq" to seq,
            "byUid" to myUid
        )
        db?.getReference("games")?.child(gameId)?.updateChildren(
            mapOf(
                "lastMove" to payload,
                "moveCount" to seq,
                "currentTurn" to nextTurn.name,
                "gameStatus" to gameStatus.name,
                "status" to if (gameStatus == GameStatus.IN_PROGRESS) "playing" else "finished",
                "drawOfferFrom" to null
            )
        )?.await()
    }

    suspend fun sendChat(gameId: String, senderName: String, message: String) {
        val myUid = uid ?: return
        val payload = mapOf(
            "senderUid" to myUid,
            "senderName" to senderName,
            "message" to message,
            "timestamp" to ServerValue.TIMESTAMP
        )
        db?.getReference("games")?.child(gameId)?.child("chat")?.push()?.setValue(payload)?.await()
    }

    suspend fun resign(gameId: String, winnerStatus: GameStatus) {
        val myUid = uid ?: return
        db?.getReference("games")?.child(gameId)?.updateChildren(
            mapOf(
                "resignFrom" to myUid,
                "gameStatus" to winnerStatus.name,
                "status" to "finished"
            )
        )?.await()
    }

    suspend fun offerDraw(gameId: String) {
        val myUid = uid ?: return
        db?.getReference("games")?.child(gameId)?.child("drawOfferFrom")?.setValue(myUid)?.await()
    }

    suspend fun acceptDraw(gameId: String) {
        db?.getReference("games")?.child(gameId)?.updateChildren(
            mapOf(
                "gameStatus" to GameStatus.DRAW_AGREED.name,
                "status" to "finished",
                "drawOfferFrom" to null
            )
        )?.await()
    }

    suspend fun declineDraw(gameId: String) {
        db?.getReference("games")?.child(gameId)?.child("drawOfferFrom")?.setValue(null)?.await()
    }

    private fun RemotePlayerInfo.toMap(): Map<String, Any> = mapOf(
        "uid" to uid,
        "username" to username,
        "rating" to rating,
        "countryCode" to countryCode,
        "countryName" to countryName,
        "countryFlag" to countryFlag,
        "avatarId" to avatarId
    )

    private fun DataSnapshot.toPlayer(): RemotePlayerInfo? {
        val playerUid = child("uid").getValue(String::class.java) ?: key ?: return null
        return RemotePlayerInfo(
            uid = playerUid,
            username = child("username").getValue(String::class.java) ?: "Player",
            rating = child("rating").getValue(Int::class.java) ?: 1200,
            countryCode = child("countryCode").getValue(String::class.java) ?: "US",
            countryName = child("countryName").getValue(String::class.java) ?: "United States",
            countryFlag = child("countryFlag").getValue(String::class.java) ?: "US",
            avatarId = child("avatarId").getValue(String::class.java) ?: "man_portrait_1"
        )
    }
}

fun RemotePlayerInfo.toOnlinePlayer(avatarResolver: (String) -> com.onlinechessgame.app.chess.model.AvatarItem): OnlinePlayer {
    return OnlinePlayer(
        id = uid,
        username = username,
        country = Country(countryCode, countryName, countryFlag),
        rating = rating,
        avatar = avatarResolver(avatarId),
        titleBadge = "Live Opponent"
    )
}
