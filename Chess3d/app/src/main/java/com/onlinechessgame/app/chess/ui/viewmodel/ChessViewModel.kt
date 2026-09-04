package com.onlinechessgame.app.chess.ui.viewmodel

import android.app.Application
import android.content.Context
import android.media.AudioManager
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.onlinechessgame.app.chess.audio.SoundManager
import com.onlinechessgame.app.chess.data.local.ChessDatabase
import com.onlinechessgame.app.chess.data.local.FriendEntity
import com.onlinechessgame.app.chess.data.local.MatchHistoryEntity
import com.onlinechessgame.app.chess.data.local.MatchmakingQueueEntity
import com.onlinechessgame.app.chess.data.local.UserProfileEntity
import com.onlinechessgame.app.chess.data.repository.ChessRepository
import com.onlinechessgame.app.chess.engine.ChessEngine
import com.onlinechessgame.app.chess.matchmaking.MatchmakingEngine
import com.onlinechessgame.app.chess.model.AvatarItem
import com.onlinechessgame.app.chess.model.ChatMessage
import com.onlinechessgame.app.chess.model.Country
import com.onlinechessgame.app.chess.model.DEFAULT_COUNTRIES
import com.onlinechessgame.app.chess.model.GameStatus
import com.onlinechessgame.app.chess.model.Move
import com.onlinechessgame.app.chess.model.OnlinePlayer
import com.onlinechessgame.app.chess.model.Piece
import com.onlinechessgame.app.chess.model.PieceColor
import com.onlinechessgame.app.chess.model.PieceType
import com.onlinechessgame.app.chess.model.Position
import com.onlinechessgame.app.chess.puzzles.ChessPuzzle
import com.onlinechessgame.app.chess.puzzles.ChessPuzzlesRepository
import com.onlinechessgame.app.chess.ui.components.BoardThemeStyle
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class AppTab {
    PLAY,
    FRIENDS,
    HISTORY,
    PUZZLES,
    LEADERBOARD,
    PROFILE,
    SETTINGS
}

enum class HistoryFilter {
    ALL,
    WINS,
    LOSSES,
    DRAWS
}

enum class MatchmakingState {
    IDLE,
    SEARCHING,
    FOUND,
    IN_GAME
}

data class ChessGameUiState(
    val board: Array<Array<Piece?>> = Array(8) { Array(8) { null } },
    val selectedPosition: Position? = null,
    val legalMoves: List<Move> = emptyList(),
    val lastMove: Move? = null,
    val currentTurn: PieceColor = PieceColor.WHITE,
    val playerColor: PieceColor = PieceColor.WHITE,
    val isCheck: Boolean = false,
    val kingInCheckPos: Position? = null,
    val capturedByWhite: List<Piece> = emptyList(),
    val capturedByBlack: List<Piece> = emptyList(),
    val gameStatus: GameStatus = GameStatus.IN_PROGRESS,
    val playerTimeSeconds: Int = 180,
    val opponentTimeSeconds: Int = 180,
    val opponent: OnlinePlayer? = null,
    val chatMessages: List<ChatMessage> = emptyList(),
    val unreadChatCount: Int = 0,
    val isChatOpen: Boolean = false,
    val pendingPromotionMove: Move? = null,
    val showGameEndDialog: Boolean = false
)

class ChessViewModel(application: Application) : AndroidViewModel(application) {

    private val db = ChessDatabase.getDatabase(application)
    private val repository = ChessRepository(db.chessDao())
    val soundManager = SoundManager.getInstance(application)

    private val authPrefs = application.getSharedPreferences("chess_auth_prefs", Context.MODE_PRIVATE)
    // Default to true so first time launch automatically enters as guest directly to homepage
    private val _isAuthCompleted = MutableStateFlow(authPrefs.getBoolean("is_auth_completed", true))
    val isAuthCompleted: StateFlow<Boolean> = _isAuthCompleted.asStateFlow()

    private val _authErrorMessage = MutableStateFlow<String?>(null)
    val authErrorMessage: StateFlow<String?> = _authErrorMessage.asStateFlow()

    private val _authSuccessMessage = MutableStateFlow<String?>(null)
    val authSuccessMessage: StateFlow<String?> = _authSuccessMessage.asStateFlow()

    private val _isAuthLoading = MutableStateFlow(false)
    val isAuthLoading: StateFlow<Boolean> = _isAuthLoading.asStateFlow()

    private val currentMatchmakingUserId: String = java.util.UUID.randomUUID().toString()

    private val _currentTab = MutableStateFlow(AppTab.PLAY)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    private val _userProfile = MutableStateFlow<UserProfileEntity?>(null)
    val userProfile: StateFlow<UserProfileEntity?> = _userProfile.asStateFlow()

    private val _matchmakingState = MutableStateFlow(MatchmakingState.IDLE)
    val matchmakingState: StateFlow<MatchmakingState> = _matchmakingState.asStateFlow()

    private val _selectedTimeControlMinutes = MutableStateFlow(5)
    val selectedTimeControlMinutes: StateFlow<Int> = _selectedTimeControlMinutes.asStateFlow()

    private val _searchingCyclingPlayer = MutableStateFlow<OnlinePlayer?>(null)
    val searchingCyclingPlayer: StateFlow<OnlinePlayer?> = _searchingCyclingPlayer.asStateFlow()

    private val _gameUiState = MutableStateFlow(ChessGameUiState())
    val gameUiState: StateFlow<ChessGameUiState> = _gameUiState.asStateFlow()

    // Friends & Requests State
    private val _acceptedFriends = MutableStateFlow<List<FriendEntity>>(emptyList())
    val acceptedFriends: StateFlow<List<FriendEntity>> = _acceptedFriends.asStateFlow()

    private val _incomingFriendRequests = MutableStateFlow<List<FriendEntity>>(emptyList())
    val incomingFriendRequests: StateFlow<List<FriendEntity>> = _incomingFriendRequests.asStateFlow()

    private val _outgoingFriendRequests = MutableStateFlow<List<FriendEntity>>(emptyList())
    val outgoingFriendRequests: StateFlow<List<FriendEntity>> = _outgoingFriendRequests.asStateFlow()

    private val _pendingRequestsCount = MutableStateFlow(0)
    val pendingRequestsCount: StateFlow<Int> = _pendingRequestsCount.asStateFlow()

    private val _friendActionMessage = MutableStateFlow<String?>(null)
    val friendActionMessage: StateFlow<String?> = _friendActionMessage.asStateFlow()

    private val _friendRequestSentToOpponent = MutableStateFlow(false)
    val friendRequestSentToOpponent: StateFlow<Boolean> = _friendRequestSentToOpponent.asStateFlow()

    // Match History State
    private val _matchHistoryList = MutableStateFlow<List<MatchHistoryEntity>>(emptyList())
    val matchHistoryList: StateFlow<List<MatchHistoryEntity>> = _matchHistoryList.asStateFlow()

    private val _historyFilter = MutableStateFlow(HistoryFilter.ALL)
    val historyFilter: StateFlow<HistoryFilter> = _historyFilter.asStateFlow()

    // Share & Refer State
    private val referralPrefs = application.getSharedPreferences("chess_referral_prefs", Context.MODE_PRIVATE)
    private val _userReferralCode = MutableStateFlow(
        referralPrefs.getString("user_referral_code", null) ?: run {
            val code = "CHESS-" + (1000..9999).random() + "-" + ('A'..'Z').shuffled().take(3).joinToString("")
            referralPrefs.edit().putString("user_referral_code", code).apply()
            code
        }
    )
    val userReferralCode: StateFlow<String> = _userReferralCode.asStateFlow()

    private val _referralCount = MutableStateFlow(referralPrefs.getInt("referral_count", 0))
    val referralCount: StateFlow<Int> = _referralCount.asStateFlow()

    private val _isReferralRedeemed = MutableStateFlow(referralPrefs.getBoolean("is_referral_redeemed", false))
    val isReferralRedeemed: StateFlow<Boolean> = _isReferralRedeemed.asStateFlow()

    private val _referralStatusMessage = MutableStateFlow<Pair<String, Boolean>?>(null)
    val referralStatusMessage: StateFlow<Pair<String, Boolean>?> = _referralStatusMessage.asStateFlow()

    // Puzzles state
    private val _currentPuzzleIndex = MutableStateFlow(0)
    val currentPuzzleIndex: StateFlow<Int> = _currentPuzzleIndex.asStateFlow()

    private val _puzzleFeedback = MutableStateFlow("Find the best tactical move!")
    val puzzleFeedback: StateFlow<String> = _puzzleFeedback.asStateFlow()

    private val _puzzleSolved = MutableStateFlow(false)
    val puzzleSolved: StateFlow<Boolean> = _puzzleSolved.asStateFlow()

    private val _puzzleBoard = MutableStateFlow(ChessEngine())
    val puzzleBoard: StateFlow<ChessEngine> = _puzzleBoard.asStateFlow()

    private val _puzzleSelectedPos = MutableStateFlow<Position?>(null)
    val puzzleSelectedPos: StateFlow<Position?> = _puzzleSelectedPos.asStateFlow()

    private val _puzzleLegalMoves = MutableStateFlow<List<Move>>(emptyList())
    val puzzleLegalMoves: StateFlow<List<Move>> = _puzzleLegalMoves.asStateFlow()

    private var engine = ChessEngine()
    private var timerJob: Job? = null
    private var matchmakingJob: Job? = null
    private var opponentAiJob: Job? = null

    init {
        viewModelScope.launch {
            repository.ensureProfileExists()
            launch {
                repository.userProfileFlow.collect { profile ->
                    _userProfile.value = profile
                    soundManager.setSoundEnabled(profile?.soundEffects ?: true)
                }
            }
            launch {
                repository.acceptedFriendsFlow.collect { friends ->
                    _acceptedFriends.value = friends
                }
            }
            launch {
                repository.incomingRequestsFlow.collect { requests ->
                    _incomingFriendRequests.value = requests
                }
            }
            launch {
                repository.outgoingRequestsFlow.collect { requests ->
                    _outgoingFriendRequests.value = requests
                }
            }
            launch {
                repository.pendingRequestsCountFlow.collect { count ->
                    _pendingRequestsCount.value = count
                }
            }
            launch {
                repository.matchHistoryFlow.collect { history ->
                    _matchHistoryList.value = history
                }
            }
        }
        loadPuzzle(0)
    }

    fun playClickSound() {
        soundManager.playClickSound()
    }

    fun playMoveSound() {
        soundManager.playMoveSound()
    }

    fun playCaptureSound() {
        soundManager.playCaptureSound()
    }

    fun playCheckmateSound() {
        soundManager.playCheckmateSound()
    }

    fun setTab(tab: AppTab) {
        soundManager.playClickSound()
        _currentTab.value = tab
    }

    // MATCHMAKING & GAME LIFECYCLE
    fun startQuickMatch() {
        val profile = _userProfile.value
        val currentRating = profile?.ratingPoints ?: 1200
        val username = profile?.username ?: "PlayerOne"
        val countryCode = profile?.countryCode ?: "US"
        val countryName = profile?.countryName ?: "United States"
        val countryFlag = profile?.countryFlag ?: "🇺🇸"
        val avatarId = profile?.selectedAvatarId ?: "champion_boy"

        _matchmakingState.value = MatchmakingState.SEARCHING

        matchmakingJob?.cancel()
        matchmakingJob = viewModelScope.launch {
            val queueEntry = MatchmakingQueueEntity(
                userId = currentMatchmakingUserId,
                username = username,
                rating = currentRating,
                countryCode = countryCode,
                countryName = countryName,
                countryFlag = countryFlag,
                avatarId = avatarId,
                timestamp = System.currentTimeMillis()
            )
            try {
                db.chessDao().joinMatchmakingQueue(queueEntry)
            } catch (e: Exception) {}

            val pool = MatchmakingEngine.globalOpponents
            var matchedOpponent: OnlinePlayer? = null
            val startTime = System.currentTimeMillis()

            while (System.currentTimeMillis() - startTime < 3000) {
                val cycleIdx = ((System.currentTimeMillis() - startTime) / 450).toInt()
                _searchingCyclingPlayer.value = pool[cycleIdx % pool.size]

                // Check for real player waiting in queue (active within last 10 seconds)
                val minTimestamp = System.currentTimeMillis() - 10000
                val realMatch = db.chessDao().findWaitingRealPlayer(currentMatchmakingUserId, minTimestamp)
                if (realMatch != null) {
                    val matchedAvatar = ChessRepository.getAvailableAvatars().find { it.id == realMatch.avatarId } ?: pool[0].avatar
                    matchedOpponent = OnlinePlayer(
                        id = realMatch.userId,
                        username = realMatch.username,
                        country = Country(realMatch.countryCode, realMatch.countryName, realMatch.countryFlag),
                        rating = realMatch.rating,
                        avatar = matchedAvatar,
                        titleBadge = "Grandmaster Challenger"
                    )
                    // Remove both from queue
                    db.chessDao().leaveMatchmakingQueue(realMatch.userId)
                    db.chessDao().leaveMatchmakingQueue(currentMatchmakingUserId)
                    break
                }
                delay(300)
            }

            if (matchedOpponent == null) {
                db.chessDao().leaveMatchmakingQueue(currentMatchmakingUserId)
                matchedOpponent = MatchmakingEngine.findRandomOpponent(currentRating)
            }

            _searchingCyclingPlayer.value = matchedOpponent
            _matchmakingState.value = MatchmakingState.FOUND
            triggerHaptic()
            delay(1200)

            startOnlineGame(matchedOpponent)
        }
    }

    fun setTimeControlMinutes(minutes: Int) {
        _selectedTimeControlMinutes.value = minutes.coerceIn(1, 60)
    }

    fun cancelMatchmaking() {
        matchmakingJob?.cancel()
        viewModelScope.launch {
            try {
                db.chessDao().leaveMatchmakingQueue(currentMatchmakingUserId)
            } catch (e: Exception) {}
        }
        _matchmakingState.value = MatchmakingState.IDLE
    }

    private fun startOnlineGame(opponent: OnlinePlayer) {
        engine = ChessEngine()
        _matchmakingState.value = MatchmakingState.IN_GAME
        val timeSecs = _selectedTimeControlMinutes.value * 60

        _gameUiState.value = ChessGameUiState(
            board = copyBoard(engine.board),
            selectedPosition = null,
            legalMoves = emptyList(),
            lastMove = null,
            currentTurn = PieceColor.WHITE,
            playerColor = PieceColor.WHITE,
            isCheck = false,
            kingInCheckPos = null,
            capturedByWhite = emptyList(),
            capturedByBlack = emptyList(),
            gameStatus = GameStatus.IN_PROGRESS,
            playerTimeSeconds = timeSecs,
            opponentTimeSeconds = timeSecs,
            opponent = opponent,
            chatMessages = listOf(
                ChatMessage(
                    senderName = opponent.username,
                    message = "Hello from ${opponent.country.name} ${opponent.country.flag}! Good luck!",
                    isFromPlayer = false
                )
            ),
            unreadChatCount = 1,
            isChatOpen = false,
            showGameEndDialog = false
        )

        startClockTimer()
    }

    private fun copyBoard(src: Array<Array<Piece?>>): Array<Array<Piece?>> {
        return Array(8) { r ->
            Array(8) { c ->
                src[r][c]?.copy()
            }
        }
    }

    private fun startClockTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_gameUiState.value.gameStatus == GameStatus.IN_PROGRESS) {
                delay(1000)
                val state = _gameUiState.value
                if (state.currentTurn == state.playerColor) {
                    val newTime = (state.playerTimeSeconds - 1).coerceAtLeast(0)
                    _gameUiState.value = state.copy(playerTimeSeconds = newTime)
                    if (newTime == 0) {
                        endGameWithTimeout(isPlayerTimeout = true)
                        break
                    }
                } else {
                    val newTime = (state.opponentTimeSeconds - 1).coerceAtLeast(0)
                    _gameUiState.value = state.copy(opponentTimeSeconds = newTime)
                    if (newTime == 0) {
                        endGameWithTimeout(isPlayerTimeout = false)
                        break
                    }
                }
            }
        }
    }

    fun onSquareClicked(pos: Position) {
        val state = _gameUiState.value
        if (state.gameStatus != GameStatus.IN_PROGRESS) return
        if (state.currentTurn != state.playerColor) return // Wait for opponent

        val clickedPiece = engine.getPiece(pos)

        // If clicking on existing selected piece, deselect
        if (state.selectedPosition == pos) {
            _gameUiState.value = state.copy(selectedPosition = null, legalMoves = emptyList())
            return
        }

        // Check if clicking on a legal destination move
        val targetMove = state.legalMoves.find { it.to == pos }
        if (targetMove != null) {
            // Check for pawn promotion
            if (targetMove.piece.type == PieceType.PAWN && (pos.row == 0 || pos.row == 7)) {
                val profile = _userProfile.value
                if (profile?.autoQueenPromotion == true) {
                    executePlayerMove(targetMove.copy(promotion = PieceType.QUEEN))
                } else {
                    _gameUiState.value = state.copy(pendingPromotionMove = targetMove)
                }
            } else {
                executePlayerMove(targetMove)
            }
            return
        }

        // Otherwise select piece if it's the player's piece
        if (clickedPiece != null && clickedPiece.color == state.playerColor) {
            val legal = engine.getLegalMoves(pos)
            _gameUiState.value = state.copy(selectedPosition = pos, legalMoves = legal)
        } else {
            _gameUiState.value = state.copy(selectedPosition = null, legalMoves = emptyList())
        }
    }

    fun onPromotionSelected(pieceType: PieceType) {
        val pending = _gameUiState.value.pendingPromotionMove ?: return
        _gameUiState.value = _gameUiState.value.copy(pendingPromotionMove = null)
        executePlayerMove(pending.copy(promotion = pieceType))
    }

    private fun executePlayerMove(move: Move) {
        val destinationPiece = engine.getPiece(move.to)
        val isCapture = move.capturedPiece != null || destinationPiece != null || (move.piece.type == PieceType.PAWN && move.from.col != move.to.col)

        val success = engine.makeMove(move)
        if (!success) return

        triggerHaptic()
        updateGameUiFromEngine(lastMove = move)

        // Play appropriate sound effect
        if (engine.gameStatus == GameStatus.WHITE_WON || engine.gameStatus == GameStatus.BLACK_WON) {
            soundManager.playCheckmateSound()
        } else if (engine.isInCheck(engine.currentTurn)) {
            soundManager.playCheckSound()
        } else if (isCapture) {
            soundManager.playCaptureSound()
        } else {
            soundManager.playMoveSound()
        }

        if (engine.gameStatus != GameStatus.IN_PROGRESS) {
            handleGameEnd(engine.gameStatus)
            return
        }

        // Trigger simulated online opponent move with realistic human thinking delay
        triggerOpponentMove()
    }

    private fun triggerOpponentMove() {
        opponentAiJob?.cancel()
        opponentAiJob = viewModelScope.launch {
            // Opponent thinking delay: 1.5 - 3.2 seconds
            delay(Random.nextLong(1500, 3200))

            if (_gameUiState.value.gameStatus != GameStatus.IN_PROGRESS) return@launch

            val opponentMove = engine.computeBestMove(PieceColor.BLACK)
            if (opponentMove != null) {
                val destinationPiece = engine.getPiece(opponentMove.to)
                val isCapture = opponentMove.capturedPiece != null || destinationPiece != null || (opponentMove.piece.type == PieceType.PAWN && opponentMove.from.col != opponentMove.to.col)

                engine.makeMove(opponentMove)
                triggerHaptic()
                updateGameUiFromEngine(lastMove = opponentMove)

                if (engine.gameStatus == GameStatus.WHITE_WON || engine.gameStatus == GameStatus.BLACK_WON) {
                    soundManager.playCheckmateSound()
                } else if (engine.isInCheck(engine.currentTurn)) {
                    soundManager.playCheckSound()
                } else if (isCapture) {
                    soundManager.playCaptureSound()
                } else {
                    soundManager.playMoveSound()
                }

                if (engine.gameStatus != GameStatus.IN_PROGRESS) {
                    handleGameEnd(engine.gameStatus)
                }
            }
        }
    }

    private fun updateGameUiFromEngine(lastMove: Move?) {
        val isCheck = engine.isInCheck(engine.currentTurn)
        val kingPos = if (isCheck) engine.findKing(engine.currentTurn) else null

        _gameUiState.value = _gameUiState.value.copy(
            board = copyBoard(engine.board),
            selectedPosition = null,
            legalMoves = emptyList(),
            lastMove = lastMove,
            currentTurn = engine.currentTurn,
            isCheck = isCheck,
            kingInCheckPos = kingPos,
            capturedByWhite = engine.capturedByWhite.toList(),
            capturedByBlack = engine.capturedByBlack.toList(),
            gameStatus = engine.gameStatus
        )
    }

    private fun handleGameEnd(status: GameStatus) {
        timerJob?.cancel()
        val state = _gameUiState.value
        val opponent = state.opponent ?: return
        val totalMoves = engine.moveHistory.size

        val movesFormatted = if (engine.moveHistory.isNotEmpty()) {
            val sb = StringBuilder()
            for (i in engine.moveHistory.indices) {
                if (i % 2 == 0) sb.append("${(i / 2) + 1}. ")
                sb.append(engine.moveHistory[i].toNotation()).append(" ")
            }
            sb.toString().trim()
        } else {
            "1. e4 e5 2. Nf3 Nc6 3. Bc4 Bc5"
        }

        viewModelScope.launch {
            when (status) {
                GameStatus.WHITE_WON -> {
                    // Win: +10 rating points, +50 tokens bonus!
                    repository.recordWin(opponent.username, opponent.country.flag, opponent.rating, totalMoves, movesSummary = movesFormatted)
                }
                GameStatus.BLACK_WON -> {
                    // Loss: -10 rating points!
                    repository.recordLoss(opponent.username, opponent.country.flag, opponent.rating, totalMoves, movesSummary = movesFormatted)
                }
                else -> {
                    // Draw
                    repository.recordDraw(opponent.username, opponent.country.flag, opponent.rating, totalMoves, movesSummary = movesFormatted)
                }
            }
        }

        _gameUiState.value = _gameUiState.value.copy(
            gameStatus = status,
            showGameEndDialog = true
        )
    }

    fun dismissGameEndDialog() {
        _gameUiState.value = _gameUiState.value.copy(showGameEndDialog = false)
        _matchmakingState.value = MatchmakingState.IDLE
        _friendRequestSentToOpponent.value = false
    }

    // FRIENDS & REQUESTS METHODS
    fun acceptFriendRequest(friend: FriendEntity) {
        viewModelScope.launch {
            repository.acceptFriendRequest(friend.id)
            _friendActionMessage.value = "Accepted friend request from ${friend.username}! 🎉"
        }
    }

    fun declineFriendRequest(friend: FriendEntity) {
        viewModelScope.launch {
            repository.declineFriendRequest(friend.id)
            _friendActionMessage.value = "Declined friend request from ${friend.username}."
        }
    }

    fun cancelOutgoingRequest(friend: FriendEntity) {
        viewModelScope.launch {
            repository.cancelOutgoingRequest(friend.id)
            _friendActionMessage.value = "Cancelled friend request to ${friend.username}."
        }
    }

    fun removeFriend(friend: FriendEntity) {
        viewModelScope.launch {
            repository.removeFriend(friend.id)
            _friendActionMessage.value = "Removed ${friend.username} from friends."
        }
    }

    fun sendFriendRequest(
        username: String,
        countryFlag: String = "🌐",
        countryName: String = "Global",
        rating: Int = 1350,
        avatarId: String = "custom_golden_king"
    ) {
        val clean = username.trim()
        if (clean.isBlank()) return
        viewModelScope.launch {
            val success = repository.sendFriendRequest(clean, countryFlag, countryName, rating, avatarId)
            if (success) {
                _friendActionMessage.value = "Friend request sent to $clean! ✉️"
            } else {
                _friendActionMessage.value = "$clean is already in your friends or requested."
            }
        }
    }

    fun sendFriendRequestToCurrentOpponent() {
        val opponent = _gameUiState.value.opponent ?: return
        viewModelScope.launch {
            val success = repository.sendFriendRequestToOpponent(opponent.username, opponent.country.flag, opponent.rating)
            _friendRequestSentToOpponent.value = true
            if (success) {
                _friendActionMessage.value = "Friend request sent to ${opponent.username}! 🤝"
            } else {
                _friendActionMessage.value = "${opponent.username} is already in your friends."
            }
        }
    }

    fun challengeFriend(friend: FriendEntity) {
        val avatar = ChessRepository.getAvailableAvatars().find { it.id == friend.avatarId }
            ?: ChessRepository.getAvailableAvatars().first()
        val onlinePlayer = OnlinePlayer(
            id = "friend_${friend.id}",
            username = friend.username,
            country = Country("XX", friend.countryName, friend.countryFlag),
            rating = friend.rating,
            avatar = avatar,
            titleBadge = "Friend"
        )
        _currentTab.value = AppTab.PLAY
        startDirectGameWithOpponent(onlinePlayer)
    }

    fun startDirectGameWithOpponent(opponent: OnlinePlayer) {
        matchmakingJob?.cancel()
        _searchingCyclingPlayer.value = opponent
        _matchmakingState.value = MatchmakingState.FOUND
        triggerHaptic()
        viewModelScope.launch {
            delay(1000)
            startOnlineGame(opponent)
        }
    }

    fun clearFriendActionMessage() {
        _friendActionMessage.value = null
    }

    fun setHistoryFilter(filter: HistoryFilter) {
        _historyFilter.value = filter
    }

    private fun endGameWithTimeout(isPlayerTimeout: Boolean) {
        val status = if (isPlayerTimeout) GameStatus.BLACK_WON else GameStatus.WHITE_WON
        handleGameEnd(status)
    }

    fun resignMatch() {
        if (_gameUiState.value.gameStatus == GameStatus.IN_PROGRESS) {
            handleGameEnd(GameStatus.BLACK_WON)
        }
    }

    fun offerDraw() {
        if (_gameUiState.value.gameStatus == GameStatus.IN_PROGRESS) {
            // Opponent accepts or counters
            val acceptDraw = Random.nextBoolean()
            if (acceptDraw) {
                handleGameEnd(GameStatus.DRAW_AGREED)
            } else {
                sendOpponentChatMessage("Draw declined! Let's play it out ♟️")
            }
        }
    }

    // CHAT SYSTEM
    fun toggleChatSheet(open: Boolean) {
        _gameUiState.value = _gameUiState.value.copy(
            isChatOpen = open,
            unreadChatCount = if (open) 0 else _gameUiState.value.unreadChatCount
        )
    }

    fun sendPlayerChatMessage(text: String) {
        val profile = _userProfile.value
        val sender = profile?.username ?: "You"
        val newMsg = ChatMessage(senderName = sender, message = text, isFromPlayer = true)
        val updated = _gameUiState.value.chatMessages + newMsg
        _gameUiState.value = _gameUiState.value.copy(chatMessages = updated)

        // Opponent contextual response after 1 second
        val opponent = _gameUiState.value.opponent
        if (opponent != null) {
            viewModelScope.launch {
                delay(1200)
                val reply = MatchmakingEngine.getOpponentChatReply(text, opponent.username)
                sendOpponentChatMessage(reply)
            }
        }
    }

    private fun sendOpponentChatMessage(text: String) {
        val opponent = _gameUiState.value.opponent ?: return
        val oppMsg = ChatMessage(senderName = opponent.username, message = text, isFromPlayer = false)
        val currentList = _gameUiState.value.chatMessages
        val isChatOpen = _gameUiState.value.isChatOpen
        _gameUiState.value = _gameUiState.value.copy(
            chatMessages = currentList + oppMsg,
            unreadChatCount = if (!isChatOpen) _gameUiState.value.unreadChatCount + 1 else 0
        )
    }

    // PUZZLES LOGIC
    fun loadPuzzle(index: Int) {
        val puzzleList = ChessPuzzlesRepository.puzzles
        if (index !in puzzleList.indices) return

        _currentPuzzleIndex.value = index
        _puzzleSolved.value = false
        _puzzleFeedback.value = "Find the best tactical move!"
        _puzzleSelectedPos.value = null
        _puzzleLegalMoves.value = emptyList()

        val p = puzzleList[index]
        val pEngine = ChessEngine()
        pEngine.setupCustomPosition(p.pieces, p.initialTurn)
        _puzzleBoard.value = pEngine
    }

    fun nextPuzzle() {
        val nextIdx = (_currentPuzzleIndex.value + 1) % ChessPuzzlesRepository.puzzles.size
        loadPuzzle(nextIdx)
    }

    fun onPuzzleSquareClicked(pos: Position) {
        if (_puzzleSolved.value) return
        val pEngine = _puzzleBoard.value
        val clickedPiece = pEngine.getPiece(pos)
        val activePuzzle = ChessPuzzlesRepository.puzzles[_currentPuzzleIndex.value]
        val step = activePuzzle.solutionSteps.firstOrNull() ?: return

        // Destination tap
        val selected = _puzzleSelectedPos.value
        if (selected != null) {
            if (selected == step.playerMoveFrom && pos == step.playerMoveTo) {
                // Correct Move!
                val destinationPiece = pEngine.getPiece(pos)
                val isCapture = destinationPiece != null
                val move = Move(selected, pos, pEngine.getPiece(selected)!!)
                pEngine.makeMove(move)
                triggerHaptic()

                if (isCapture) {
                    soundManager.playCaptureSound()
                } else {
                    soundManager.playMoveSound()
                }

                // Check if opponent has response in puzzle
                if (step.opponentReplyFrom != null && step.opponentReplyTo != null) {
                    viewModelScope.launch {
                        delay(600)
                        val replyPiece = pEngine.getPiece(step.opponentReplyFrom)
                        if (replyPiece != null) {
                            val replyCapture = pEngine.getPiece(step.opponentReplyTo) != null
                            pEngine.makeMove(Move(step.opponentReplyFrom, step.opponentReplyTo, replyPiece))
                            if (replyCapture) soundManager.playCaptureSound() else soundManager.playMoveSound()
                        }
                    }
                }

                _puzzleSolved.value = true
                soundManager.playCheckmateSound()
                _puzzleFeedback.value = "Brilliant move! +${activePuzzle.rewardTokens} Tokens & +${activePuzzle.rewardPoints} Points!"
                _puzzleSelectedPos.value = null
                _puzzleLegalMoves.value = emptyList()

                // Reward user
                viewModelScope.launch {
                    repository.recordPuzzleSolved(activePuzzle.rewardTokens, activePuzzle.rewardPoints)
                }
                return
            } else {
                // Wrong move
                _puzzleFeedback.value = "Incorrect move! Try again."
                _puzzleSelectedPos.value = null
                _puzzleLegalMoves.value = emptyList()
                return
            }
        }

        if (clickedPiece != null && clickedPiece.color == pEngine.currentTurn) {
            _puzzleSelectedPos.value = pos
            _puzzleLegalMoves.value = pEngine.getLegalMoves(pos)
        } else {
            _puzzleSelectedPos.value = null
            _puzzleLegalMoves.value = emptyList()
        }
    }

    fun showPuzzleHint() {
        val puzzle = ChessPuzzlesRepository.puzzles[_currentPuzzleIndex.value]
        val step = puzzle.solutionSteps.firstOrNull() ?: return
        _puzzleFeedback.value = "Hint: Look at square ${step.playerMoveFrom.toAlgebraic()}!"
    }

    // PROFILE & SETTINGS CUSTOMIZATION
    fun selectAvatar(avatarId: String) {
        viewModelScope.launch {
            repository.updateAvatar(avatarId)
        }
    }

    fun isAvatarUnlocked(avatarId: String): Boolean {
        val profile = _userProfile.value ?: return false
        val unlockedList = profile.unlockedAvatarIds.split(",").map { it.trim() }
        return unlockedList.contains(avatarId)
    }

    fun unlockAndEquipAvatar(avatar: AvatarItem) {
        val profile = _userProfile.value ?: return
        val currentUnlocked = profile.unlockedAvatarIds.split(",").map { it.trim() }.toMutableSet()
        currentUnlocked.add(avatar.id)
        val newUnlockedString = currentUnlocked.joinToString(",")
        viewModelScope.launch {
            repository.unlockAndEquipAvatar(
                gemsCost = 0,
                newUnlockedIds = newUnlockedString,
                selectedId = avatar.id
            )
        }
    }

    fun buyAvatarWithGems(avatar: AvatarItem): Boolean {
        val profile = _userProfile.value ?: return false
        if (profile.gems < avatar.costGems) return false
        val currentUnlocked = profile.unlockedAvatarIds.split(",").map { it.trim() }.toMutableSet()
        currentUnlocked.add(avatar.id)
        val newUnlockedString = currentUnlocked.joinToString(",")
        viewModelScope.launch {
            repository.unlockAndEquipAvatar(
                gemsCost = avatar.costGems,
                newUnlockedIds = newUnlockedString,
                selectedId = avatar.id
            )
        }
        return true
    }

    fun addGems(gems: Int) {
        viewModelScope.launch {
            repository.addGems(gems)
        }
    }

    fun selectCountry(country: Country) {
        viewModelScope.launch {
            repository.updateCountry(country.code, country.name, country.flag)
        }
    }

    fun updateUsername(name: String) {
        if (name.isNotBlank()) {
            viewModelScope.launch {
                repository.updateUsername(name.trim())
            }
        }
    }

    fun updateBoardTheme(themeName: String) {
        val current = _userProfile.value ?: return
        viewModelScope.launch {
            repository.updateSettings(
                theme = themeName,
                piece = current.pieceStyle,
                color = current.pieceColor,
                sound = current.soundEffects,
                haptic = current.hapticFeedback,
                autoQueen = current.autoQueenPromotion
            )
        }
    }

    fun updatePieceStyle(pieceStyleName: String) {
        val current = _userProfile.value ?: return
        viewModelScope.launch {
            repository.updateSettings(
                theme = current.boardTheme,
                piece = pieceStyleName,
                color = current.pieceColor,
                sound = current.soundEffects,
                haptic = current.hapticFeedback,
                autoQueen = current.autoQueenPromotion
            )
        }
    }

    fun updatePieceColor(pieceColorName: String) {
        val current = _userProfile.value ?: return
        viewModelScope.launch {
            repository.updateSettings(
                theme = current.boardTheme,
                piece = current.pieceStyle,
                color = pieceColorName,
                sound = current.soundEffects,
                haptic = current.hapticFeedback,
                autoQueen = current.autoQueenPromotion
            )
        }
    }

    fun toggleAutoQueen(enabled: Boolean) {
        val current = _userProfile.value ?: return
        viewModelScope.launch {
            repository.updateSettings(
                theme = current.boardTheme,
                piece = current.pieceStyle,
                color = current.pieceColor,
                sound = current.soundEffects,
                haptic = current.hapticFeedback,
                autoQueen = enabled
            )
        }
    }

    fun toggleSound(enabled: Boolean) {
        soundManager.setSoundEnabled(enabled)
        if (enabled) {
            soundManager.playClickSound()
        }
        val current = _userProfile.value ?: return
        viewModelScope.launch {
            repository.updateSettings(
                theme = current.boardTheme,
                piece = current.pieceStyle,
                color = current.pieceColor,
                sound = enabled,
                haptic = current.hapticFeedback,
                autoQueen = current.autoQueenPromotion
            )
        }
    }

    fun toggleHaptic(enabled: Boolean) {
        val current = _userProfile.value ?: return
        viewModelScope.launch {
            repository.updateSettings(
                theme = current.boardTheme,
                piece = current.pieceStyle,
                color = current.pieceColor,
                sound = current.soundEffects,
                haptic = enabled,
                autoQueen = current.autoQueenPromotion
            )
        }
    }

    private fun triggerHaptic() {
        if (_userProfile.value?.hapticFeedback == false) return
        try {
            val context = getApplication<Application>()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(
                    VibrationEffect.createOneShot(35, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                vibrator?.vibrate(35)
            }
        } catch (_: Exception) {
        }
    }

    // SHARE & REFER METHODS
    fun recordShareApp() {
        val newCount = _referralCount.value + 1
        _referralCount.value = newCount
        referralPrefs.edit().putInt("referral_count", newCount).apply()
        viewModelScope.launch {
            // Reward 50 bonus tokens and 10 rating points for sharing
            repository.claimReferralReward(tokens = 50, points = 10)
            soundManager.playCheckmateSound()
            _referralStatusMessage.value = Pair("🎉 App shared! You earned +50 Bonus Tokens & +10 Rating Points!", true)
        }
    }

    fun redeemReferralCode(enteredCode: String) {
        val code = enteredCode.trim().uppercase()
        if (code.isBlank()) {
            _referralStatusMessage.value = Pair("Please enter a referral code.", false)
            return
        }
        if (_isReferralRedeemed.value) {
            _referralStatusMessage.value = Pair("You have already claimed a referral bonus code.", false)
            return
        }
        if (code == _userReferralCode.value.uppercase()) {
            _referralStatusMessage.value = Pair("You cannot redeem your own referral code!", false)
            return
        }
        if (code.length < 5) {
            _referralStatusMessage.value = Pair("Invalid referral code format. Example: CHESS-1234-ABC", false)
            return
        }

        viewModelScope.launch {
            referralPrefs.edit()
                .putBoolean("is_referral_redeemed", true)
                .putString("redeemed_code", code)
                .apply()
            _isReferralRedeemed.value = true

            // Reward 250 Tokens & 25 Rating Points
            repository.claimReferralReward(tokens = 250, points = 25)
            soundManager.playCheckmateSound()
            _referralStatusMessage.value = Pair("🎁 Referral Code Redeemed! +250 Free Tokens & +25 Rating Points Added!", true)
        }
    }

    fun clearReferralStatusMessage() {
        _referralStatusMessage.value = null
    }

    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _authErrorMessage.value = "Please enter both username and password."
            return
        }
        viewModelScope.launch {
            _isAuthLoading.value = true
            _authErrorMessage.value = null
            delay(300)
            val res = repository.loginAccount(username, password)
            _isAuthLoading.value = false
            if (res.isSuccess) {
                authPrefs.edit().putBoolean("is_auth_completed", true).putString("logged_user", username.trim()).apply()
                _authSuccessMessage.value = "Welcome back, ${res.getOrNull()?.username}!"
                _isAuthCompleted.value = true
            } else {
                _authErrorMessage.value = res.exceptionOrNull()?.message ?: "Login failed."
            }
        }
    }

    fun signUp(
        username: String,
        password: String,
        countryCode: String,
        countryName: String,
        countryFlag: String,
        avatarId: String
    ) {
        if (username.isBlank() || password.isBlank()) {
            _authErrorMessage.value = "Please enter username and password."
            return
        }
        viewModelScope.launch {
            _isAuthLoading.value = true
            _authErrorMessage.value = null
            delay(300)
            val res = repository.registerAccount(
                username = username,
                password = password,
                countryCode = countryCode,
                countryName = countryName,
                countryFlag = countryFlag,
                avatarId = avatarId
            )
            _isAuthLoading.value = false
            if (res.isSuccess) {
                authPrefs.edit().putBoolean("is_auth_completed", true).putString("logged_user", username.trim()).apply()
                _authSuccessMessage.value = "Account created successfully! Welcome, ${res.getOrNull()?.username}!"
                _isAuthCompleted.value = true
            } else {
                _authErrorMessage.value = res.exceptionOrNull()?.message ?: "Sign up failed."
            }
        }
    }

    fun playAsGuest() {
        viewModelScope.launch {
            _isAuthLoading.value = true
            _authErrorMessage.value = null
            delay(200)
            val guest = repository.playAsGuest()
            authPrefs.edit().putBoolean("is_auth_completed", true).putString("logged_user", guest.username).apply()
            _isAuthLoading.value = false
            _isAuthCompleted.value = true
        }
    }

    fun logout() {
        viewModelScope.launch {
            authPrefs.edit().putBoolean("is_auth_completed", false).remove("logged_user").apply()
            _isAuthCompleted.value = false
            _authErrorMessage.value = null
            _authSuccessMessage.value = null
        }
    }

    fun clearAuthMessages() {
        _authErrorMessage.value = null
        _authSuccessMessage.value = null
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        matchmakingJob?.cancel()
        opponentAiJob?.cancel()
    }
}
