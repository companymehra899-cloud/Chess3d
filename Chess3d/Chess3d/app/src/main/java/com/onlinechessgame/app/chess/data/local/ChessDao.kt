package com.onlinechessgame.app.chess.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ChessDao {

    @Query("SELECT * FROM user_profile WHERE id = 'guest_user_1' LIMIT 1")
    fun getUserProfileFlow(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE id = 'guest_user_1' LIMIT 1")
    suspend fun getUserProfile(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfileEntity)

    @Update
    suspend fun updateProfile(profile: UserProfileEntity)

    @Query("UPDATE user_profile SET ratingPoints = MAX(0, ratingPoints + :delta), wins = wins + :winIncr, losses = losses + :lossIncr, draws = draws + :drawIncr, tokens = MAX(0, tokens + :tokensIncr) WHERE id = 'guest_user_1'")
    suspend fun recordMatchResult(delta: Int, winIncr: Int, lossIncr: Int, drawIncr: Int, tokensIncr: Int)

    @Query("UPDATE user_profile SET selectedAvatarId = :avatarId WHERE id = 'guest_user_1'")
    suspend fun updateAvatar(avatarId: String)

    @Query("UPDATE user_profile SET gems = MAX(0, gems - :gemsCost), unlockedAvatarIds = :newUnlockedIds, selectedAvatarId = :selectedId WHERE id = 'guest_user_1'")
    suspend fun unlockAndEquipAvatar(gemsCost: Int, newUnlockedIds: String, selectedId: String)

    @Query("UPDATE user_profile SET gems = gems + :gemsDelta WHERE id = 'guest_user_1'")
    suspend fun addGems(gemsDelta: Int)

    @Query("UPDATE user_profile SET countryCode = :code, countryName = :name, countryFlag = :flag WHERE id = 'guest_user_1'")
    suspend fun updateCountry(code: String, name: String, flag: String)

    @Query("UPDATE user_profile SET username = :newUsername WHERE id = 'guest_user_1'")
    suspend fun updateUsername(newUsername: String)

    @Query("UPDATE user_profile SET boardTheme = :theme, pieceStyle = :piece, pieceColor = :color, soundEffects = :sound, hapticFeedback = :haptic, autoQueenPromotion = :autoQueen WHERE id = 'guest_user_1'")
    suspend fun updateSettings(theme: String, piece: String, color: String = "CLASSIC", sound: Boolean, haptic: Boolean, autoQueen: Boolean)

    @Query("UPDATE user_profile SET puzzlesSolved = puzzlesSolved + 1, tokens = tokens + :rewardTokens, ratingPoints = ratingPoints + :rewardPoints WHERE id = 'guest_user_1'")
    suspend fun recordPuzzleSolved(rewardTokens: Int, rewardPoints: Int)

    @Query("UPDATE user_profile SET tokens = tokens + :tokensReward, ratingPoints = ratingPoints + :ratingReward WHERE id = 'guest_user_1'")
    suspend fun addReferralBonus(tokensReward: Int, ratingReward: Int)

    @Query("SELECT * FROM match_history ORDER BY timestamp DESC LIMIT 50")
    fun getMatchHistoryFlow(): Flow<List<MatchHistoryEntity>>

    @Insert
    suspend fun insertMatchHistory(match: MatchHistoryEntity)

    @Query("SELECT COUNT(*) FROM match_history")
    suspend fun getMatchHistoryCount(): Int

    // FRIENDS & FRIEND REQUESTS QUERIES
    @Query("SELECT * FROM friends ORDER BY timestamp DESC")
    fun getAllFriendsFlow(): Flow<List<FriendEntity>>

    @Query("SELECT * FROM friends WHERE requestStatus = 'ACCEPTED' ORDER BY CASE WHEN status = 'ONLINE' THEN 1 WHEN status = 'IN_GAME' THEN 2 ELSE 3 END, rating DESC")
    fun getAcceptedFriendsFlow(): Flow<List<FriendEntity>>

    @Query("SELECT * FROM friends WHERE requestStatus = 'PENDING_INCOMING' ORDER BY timestamp DESC")
    fun getIncomingRequestsFlow(): Flow<List<FriendEntity>>

    @Query("SELECT * FROM friends WHERE requestStatus = 'PENDING_OUTGOING' ORDER BY timestamp DESC")
    fun getOutgoingRequestsFlow(): Flow<List<FriendEntity>>

    @Query("SELECT COUNT(*) FROM friends WHERE requestStatus = 'PENDING_INCOMING'")
    fun getPendingRequestsCountFlow(): Flow<Int>

    @Query("SELECT COUNT(*) FROM friends")
    suspend fun getFriendsCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFriend(friend: FriendEntity): Long

    @Query("UPDATE friends SET requestStatus = 'ACCEPTED' WHERE id = :friendId")
    suspend fun acceptFriendRequest(friendId: Long)

    @Query("DELETE FROM friends WHERE id = :friendId")
    suspend fun deleteFriend(friendId: Long)

    @Query("SELECT * FROM friends WHERE username = :username LIMIT 1")
    suspend fun findFriendByUsername(username: String): FriendEntity?

    // USER ACCOUNTS (LOCAL OFFLINE AUTH)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: UserAccountEntity)

    @Query("SELECT * FROM user_accounts WHERE username = :username LIMIT 1")
    suspend fun getAccountByUsername(username: String): UserAccountEntity?

    @Query("SELECT * FROM user_accounts ORDER BY createdAt DESC")
    suspend fun getAllAccounts(): List<UserAccountEntity>

    @Query("UPDATE user_accounts SET ratingPoints = :rating, tokens = :tokens, gems = :gems, level = :level, xp = :xp, wins = :wins, losses = :losses, draws = :draws, puzzlesSolved = :puzzles, avatarId = :avatarId, unlockedAvatarIds = :unlockedAvatars WHERE username = :username")
    suspend fun syncAccountData(
        username: String,
        rating: Int,
        tokens: Int,
        gems: Int,
        level: Int,
        xp: Int,
        wins: Int,
        losses: Int,
        draws: Int,
        puzzles: Int,
        avatarId: String,
        unlockedAvatars: String
    )

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun joinMatchmakingQueue(entry: MatchmakingQueueEntity)

    @Query("DELETE FROM matchmaking_queue WHERE userId = :userId")
    suspend fun leaveMatchmakingQueue(userId: String)

    @Query("SELECT * FROM matchmaking_queue WHERE userId != :currentUserId AND timestamp > :minTimestamp ORDER BY timestamp ASC LIMIT 1")
    suspend fun findWaitingRealPlayer(currentUserId: String, minTimestamp: Long): MatchmakingQueueEntity?
}

