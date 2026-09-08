package com.onlinechessgame.app.chess.data.repository

import com.onlinechessgame.app.R
import com.onlinechessgame.app.chess.data.local.ChessDao
import com.onlinechessgame.app.chess.data.local.FriendEntity
import com.onlinechessgame.app.chess.data.local.MatchHistoryEntity
import com.onlinechessgame.app.chess.data.local.UserAccountEntity
import com.onlinechessgame.app.chess.data.local.UserProfileEntity
import com.onlinechessgame.app.chess.model.AvatarCategory
import com.onlinechessgame.app.chess.model.AvatarItem
import com.onlinechessgame.app.chess.model.AvatarRarity
import kotlinx.coroutines.flow.Flow

class ChessRepository(private val dao: ChessDao) {

    val userProfileFlow: Flow<UserProfileEntity?> = dao.getUserProfileFlow()
    val matchHistoryFlow: Flow<List<MatchHistoryEntity>> = dao.getMatchHistoryFlow()

    // Friends Flows
    val allFriendsFlow: Flow<List<FriendEntity>> = dao.getAllFriendsFlow()
    val acceptedFriendsFlow: Flow<List<FriendEntity>> = dao.getAcceptedFriendsFlow()
    val incomingRequestsFlow: Flow<List<FriendEntity>> = dao.getIncomingRequestsFlow()
    val outgoingRequestsFlow: Flow<List<FriendEntity>> = dao.getOutgoingRequestsFlow()
    val pendingRequestsCountFlow: Flow<Int> = dao.getPendingRequestsCountFlow()

    suspend fun ensureProfileExists(): UserProfileEntity {
        var profile = dao.getUserProfile()
        if (profile == null) {
            // New user registration / Guest login with 1000 Free Bonus Tokens!
            profile = UserProfileEntity(
                id = "guest_user_1",
                username = "Guest_${(1000..9999).random()}",
                isGuest = true,
                ratingPoints = 1200,
                tokens = 1000,
                wins = 0,
                losses = 0,
                draws = 0,
                puzzlesSolved = 0,
                countryCode = "US",
                countryName = "United States",
                countryFlag = "🇺🇸",
                selectedAvatarId = "man_portrait_1"
            )
            dao.insertOrUpdateProfile(profile)
        }
        seedDefaultFriendsAndHistoryIfEmpty()
        return profile
    }

    suspend fun registerAccount(
        username: String,
        password: String,
        countryCode: String = "US",
        countryName: String = "United States",
        countryFlag: String = "🇺🇸",
        avatarId: String = "man_portrait_1"
    ): Result<UserProfileEntity> {
        val cleanName = username.trim()
        val key = cleanName.lowercase()
        if (cleanName.length < 3) {
            return Result.failure(Exception("Username must be at least 3 characters."))
        }
        if (password.length < 4) {
            return Result.failure(Exception("Password must be at least 4 characters."))
        }
        val existing = dao.getAccountByUsername(key)
        if (existing != null) {
            return Result.failure(Exception("Username '$cleanName' is already registered. Please login."))
        }

        val newAccount = UserAccountEntity(
            username = key,
            displayName = cleanName,
            passwordHash = password,
            countryCode = countryCode,
            countryName = countryName,
            countryFlag = countryFlag,
            avatarId = avatarId,
            ratingPoints = 1200,
            tokens = 1000,
            gems = 650,
            level = 15,
            xp = 1200,
            wins = 0,
            losses = 0,
            draws = 0,
            puzzlesSolved = 0
        )
        dao.insertAccount(newAccount)

        val profile = UserProfileEntity(
            id = "guest_user_1",
            username = cleanName,
            isGuest = false,
            ratingPoints = newAccount.ratingPoints,
            tokens = newAccount.tokens,
            gems = newAccount.gems,
            level = newAccount.level,
            xp = newAccount.xp,
            wins = newAccount.wins,
            losses = newAccount.losses,
            draws = newAccount.draws,
            puzzlesSolved = newAccount.puzzlesSolved,
            countryCode = newAccount.countryCode,
            countryName = newAccount.countryName,
            countryFlag = newAccount.countryFlag,
            selectedAvatarId = newAccount.avatarId,
            unlockedAvatarIds = newAccount.unlockedAvatarIds
        )
        dao.insertOrUpdateProfile(profile)
        seedDefaultFriendsAndHistoryIfEmpty()
        return Result.success(profile)
    }

    suspend fun loginAccount(username: String, password: String): Result<UserProfileEntity> {
        val cleanName = username.trim()
        val key = cleanName.lowercase()
        if (cleanName.isEmpty() || password.isEmpty()) {
            return Result.failure(Exception("Please enter both username and password."))
        }
        val account = dao.getAccountByUsername(key)
            ?: return Result.failure(Exception("Account '$cleanName' not found. Please Sign Up."))
        if (account.passwordHash != password) {
            return Result.failure(Exception("Incorrect password. Please try again."))
        }

        val profile = UserProfileEntity(
            id = "guest_user_1",
            username = account.displayName,
            isGuest = false,
            ratingPoints = account.ratingPoints,
            tokens = account.tokens,
            gems = account.gems,
            level = account.level,
            xp = account.xp,
            wins = account.wins,
            losses = account.losses,
            draws = account.draws,
            puzzlesSolved = account.puzzlesSolved,
            countryCode = account.countryCode,
            countryName = account.countryName,
            countryFlag = account.countryFlag,
            selectedAvatarId = account.avatarId,
            unlockedAvatarIds = account.unlockedAvatarIds
        )
        dao.insertOrUpdateProfile(profile)
        seedDefaultFriendsAndHistoryIfEmpty()
        return Result.success(profile)
    }

    suspend fun playAsGuest(): UserProfileEntity {
        val guestProfile = UserProfileEntity(
            id = "guest_user_1",
            username = "Guest_${(1000..9999).random()}",
            isGuest = true,
            ratingPoints = 1200,
            tokens = 1000,
            gems = 650,
            level = 15,
            xp = 1200,
            wins = 0,
            losses = 0,
            draws = 0,
            puzzlesSolved = 0,
            countryCode = "US",
            countryName = "United States",
            countryFlag = "🇺🇸",
            selectedAvatarId = "man_portrait_1"
        )
        dao.insertOrUpdateProfile(guestProfile)
        seedDefaultFriendsAndHistoryIfEmpty()
        return guestProfile
    }

    suspend fun syncCurrentProfileToAccountIfRegistered() {
        val profile = dao.getUserProfile() ?: return
        if (!profile.isGuest) {
            val key = profile.username.trim().lowercase()
            dao.syncAccountData(
                username = key,
                rating = profile.ratingPoints,
                tokens = profile.tokens,
                gems = profile.gems,
                level = profile.level,
                xp = profile.xp,
                wins = profile.wins,
                losses = profile.losses,
                draws = profile.draws,
                puzzles = profile.puzzlesSolved,
                avatarId = profile.selectedAvatarId,
                unlockedAvatars = profile.unlockedAvatarIds
            )
        }
    }

    private suspend fun seedDefaultFriendsAndHistoryIfEmpty() {
        if (dao.getFriendsCount() == 0) {
            // Seed sample active friends & incoming/outgoing requests
            val defaultFriends = listOf(
                FriendEntity(
                    username = "Magnus_Viking",
                    countryFlag = "🇳🇴",
                    countryName = "Norway",
                    rating = 1845,
                    avatarId = "man_portrait_1",
                    status = "ONLINE",
                    requestStatus = "ACCEPTED"
                ),
                FriendEntity(
                    username = "Hikaru_Blitz",
                    countryFlag = "🇺🇸",
                    countryName = "United States",
                    rating = 1790,
                    avatarId = "man_portrait_2",
                    status = "IN_GAME",
                    requestStatus = "ACCEPTED"
                ),
                FriendEntity(
                    username = "Elena_Queen",
                    countryFlag = "🇺🇦",
                    countryName = "Ukraine",
                    rating = 1520,
                    avatarId = "woman_portrait_2",
                    status = "OFFLINE",
                    requestStatus = "ACCEPTED"
                ),
                FriendEntity(
                    username = "Viswanathan_GM",
                    countryFlag = "🇮🇳",
                    countryName = "India",
                    rating = 1680,
                    avatarId = "custom_golden_king",
                    status = "ONLINE",
                    requestStatus = "ACCEPTED"
                ),
                // Incoming Requests awaiting player acceptance/decline
                FriendEntity(
                    username = "Sofia_Master",
                    countryFlag = "🇪🇸",
                    countryName = "Spain",
                    rating = 1480,
                    avatarId = "woman_portrait_1",
                    status = "ONLINE",
                    requestStatus = "PENDING_INCOMING"
                ),
                FriendEntity(
                    username = "Lucas_Rio",
                    countryFlag = "🇧🇷",
                    countryName = "Brazil",
                    rating = 1340,
                    avatarId = "custom_cyber_rook",
                    status = "ONLINE",
                    requestStatus = "PENDING_INCOMING"
                ),
                // Outgoing Request sent by player
                FriendEntity(
                    username = "Kenji_Samurai",
                    countryFlag = "🇯🇵",
                    countryName = "Japan",
                    rating = 1410,
                    avatarId = "custom_shadow_knight",
                    status = "ONLINE",
                    requestStatus = "PENDING_OUTGOING"
                )
            )
            for (f in defaultFriends) {
                dao.insertFriend(f)
            }
        }

        if (dao.getMatchHistoryCount() == 0) {
            val now = System.currentTimeMillis()
            val defaultHistory = listOf(
                MatchHistoryEntity(
                    opponentName = "Tanaka_Hiro",
                    opponentCountryFlag = "🇯🇵",
                    opponentRating = 1410,
                    result = "WIN",
                    pointsDelta = 10,
                    tokensEarned = 50,
                    totalMoves = 34,
                    gameMode = "Rapid (3 min)",
                    movesSummary = "1. e4 e5 2. Nf3 Nc6 3. Bc4 Nf6 4. d3 Bc5 5. c3 d6 6. O-O O-O",
                    timestamp = now - (15 * 60 * 1000)
                ),
                MatchHistoryEntity(
                    opponentName = "Sarah_Williams",
                    opponentCountryFlag = "🇬🇧",
                    opponentRating = 1520,
                    result = "LOSS",
                    pointsDelta = -10,
                    tokensEarned = -25,
                    totalMoves = 42,
                    gameMode = "Rapid (3 min)",
                    movesSummary = "1. d4 d5 2. c4 c6 3. Nf3 Nf6 4. Nc3 e6 5. Bg5 Nbd7 6. e3",
                    timestamp = now - (2 * 3600 * 1000)
                ),
                MatchHistoryEntity(
                    opponentName = "Diego_Rodriguez",
                    opponentCountryFlag = "🇧🇷",
                    opponentRating = 1340,
                    result = "WIN",
                    pointsDelta = 10,
                    tokensEarned = 50,
                    totalMoves = 28,
                    gameMode = "Rapid (3 min)",
                    movesSummary = "1. e4 c5 2. Nf3 d6 3. d4 cxd4 4. Nxd4 Nf6 5. Nc3 a6 6. Be3",
                    timestamp = now - (6 * 3600 * 1000)
                ),
                MatchHistoryEntity(
                    opponentName = "Hans_Schmidt",
                    opponentCountryFlag = "🇩🇪",
                    opponentRating = 1390,
                    result = "DRAW",
                    pointsDelta = 0,
                    tokensEarned = 0,
                    totalMoves = 52,
                    gameMode = "Rapid (3 min)",
                    movesSummary = "1. e4 e6 2. d4 d5 3. Nc3 Bb4 4. e5 c5 5. a3 Bxc3+ 6. bxc3",
                    timestamp = now - (24 * 3600 * 1000)
                )
            )
            for (m in defaultHistory) {
                dao.insertMatchHistory(m)
            }
        }
    }

    suspend fun recordWin(
        opponentName: String,
        opponentFlag: String,
        opponentRating: Int,
        moves: Int,
        movesSummary: String = "1. e4 e5 2. Nf3 Nc6 3. Bc4 Bc5",
        gameMode: String = "Rapid (3 min)"
    ) {
        // Win match: +10 points!
        dao.recordMatchResult(delta = 10, winIncr = 1, lossIncr = 0, drawIncr = 0, tokensIncr = 50)
        dao.insertMatchHistory(
            MatchHistoryEntity(
                opponentName = opponentName,
                opponentCountryFlag = opponentFlag,
                opponentRating = opponentRating,
                result = "WIN",
                pointsDelta = 10,
                tokensEarned = 50,
                totalMoves = moves,
                gameMode = gameMode,
                movesSummary = movesSummary
            )
        )
        syncCurrentProfileToAccountIfRegistered()
    }

    suspend fun recordLoss(
        opponentName: String,
        opponentFlag: String,
        opponentRating: Int,
        moves: Int,
        movesSummary: String = "1. e4 e5 2. Nf3 Nc6 3. Bc4 Bc5",
        gameMode: String = "Rapid (3 min)"
    ) {
        // Lose match / Resign: -10 points, -25 tokens!
        dao.recordMatchResult(delta = -10, winIncr = 0, lossIncr = 1, drawIncr = 0, tokensIncr = -25)
        dao.insertMatchHistory(
            MatchHistoryEntity(
                opponentName = opponentName,
                opponentCountryFlag = opponentFlag,
                opponentRating = opponentRating,
                result = "LOSS",
                pointsDelta = -10,
                tokensEarned = -25,
                totalMoves = moves,
                gameMode = gameMode,
                movesSummary = movesSummary
            )
        )
        syncCurrentProfileToAccountIfRegistered()
    }

    suspend fun recordDraw(
        opponentName: String,
        opponentFlag: String,
        opponentRating: Int,
        moves: Int,
        movesSummary: String = "1. e4 e5 2. Nf3 Nc6 3. Bc4 Bc5",
        gameMode: String = "Rapid (3 min)"
    ) {
        dao.recordMatchResult(delta = 0, winIncr = 0, lossIncr = 0, drawIncr = 1, tokensIncr = 0)
        dao.insertMatchHistory(
            MatchHistoryEntity(
                opponentName = opponentName,
                opponentCountryFlag = opponentFlag,
                opponentRating = opponentRating,
                result = "DRAW",
                pointsDelta = 0,
                tokensEarned = 0,
                totalMoves = moves,
                gameMode = gameMode,
                movesSummary = movesSummary
            )
        )
        syncCurrentProfileToAccountIfRegistered()
    }

    // FRIEND ACTIONS
    suspend fun acceptFriendRequest(friendId: Long) {
        dao.acceptFriendRequest(friendId)
    }

    suspend fun declineFriendRequest(friendId: Long) {
        dao.deleteFriend(friendId)
    }

    suspend fun cancelOutgoingRequest(friendId: Long) {
        dao.deleteFriend(friendId)
    }

    suspend fun removeFriend(friendId: Long) {
        dao.deleteFriend(friendId)
    }

    suspend fun sendFriendRequest(
        username: String,
        countryFlag: String = "🌐",
        countryName: String = "Global",
        rating: Int = 1300,
        avatarId: String = "custom_golden_king"
    ): Boolean {
        val cleanName = username.trim()
        if (cleanName.isBlank()) return false
        val existing = dao.findFriendByUsername(cleanName)
        if (existing != null) {
            return false // already requested or friend
        }
        dao.insertFriend(
            FriendEntity(
                username = cleanName,
                countryFlag = countryFlag,
                countryName = countryName,
                rating = rating,
                avatarId = avatarId,
                status = "ONLINE",
                requestStatus = "PENDING_OUTGOING"
            )
        )
        return true
    }

    suspend fun sendFriendRequestToOpponent(
        opponentName: String,
        flag: String = "🌐",
        rating: Int = 1300
    ): Boolean {
        return sendFriendRequest(
            username = opponentName,
            countryFlag = flag,
            countryName = "Opponent",
            rating = rating,
            avatarId = "custom_cyber_rook"
        )
    }

    suspend fun updateAvatar(avatarId: String) {
        dao.updateAvatar(avatarId)
        syncCurrentProfileToAccountIfRegistered()
    }

    suspend fun updateCountry(code: String, name: String, flag: String) {
        dao.updateCountry(code, name, flag)
        syncCurrentProfileToAccountIfRegistered()
    }

    suspend fun updateUsername(username: String) {
        dao.updateUsername(username)
        syncCurrentProfileToAccountIfRegistered()
    }

    suspend fun updateSettings(theme: String, piece: String, color: String = "CLASSIC", sound: Boolean, haptic: Boolean, autoQueen: Boolean) {
        dao.updateSettings(theme, piece, color, sound, haptic, autoQueen)
    }

    suspend fun recordPuzzleSolved(tokens: Int = 20, points: Int = 5) {
        dao.recordPuzzleSolved(tokens, points)
        syncCurrentProfileToAccountIfRegistered()
    }

    suspend fun claimReferralReward(tokens: Int, points: Int) {
        dao.addReferralBonus(tokens, points)
        syncCurrentProfileToAccountIfRegistered()
    }

    suspend fun unlockAndEquipAvatar(gemsCost: Int, newUnlockedIds: String, selectedId: String) {
        dao.unlockAndEquipAvatar(gemsCost, newUnlockedIds, selectedId)
        syncCurrentProfileToAccountIfRegistered()
    }

    suspend fun addGems(gemsDelta: Int) {
        dao.addGems(gemsDelta)
        syncCurrentProfileToAccountIfRegistered()
    }

    companion object {
        fun getAvailableAvatars(): List<AvatarItem> {
            return listOf(
                // =========================================================
                // 20 OFFICIAL REFERENCE SHEET AVATARS (100% FREE & UNLOCKED)
                // Style: Realistic 3D Character / Fur / Cinematic Lighting
                // =========================================================

                // 1. ROYAL KING
                AvatarItem(
                    id = "royal_king",
                    title = "Royal King",
                    category = AvatarCategory.FREE,
                    rarity = AvatarRarity.FREE,
                    resId = R.drawable.img_avatar_golden_king,
                    unlocked = true,
                    costGems = 0,
                    requiredLevel = 1,
                    description = "Regal sovereign with ornate golden chess crown, ermine robe and commanding majesty.",
                    nationality = "High Monarchy",
                    countryFlag = "👑",
                    gender = "Boy",
                    artStyle = "Realistic 3D Character",
                    playStyle = "Sovereign Command",
                    biography = "Supreme ruler of the sixty-four squares, leading noble armies with commanding tactical wisdom and kingly poise.",
                    quote = "Rule every square with honor and victory."
                ),

                // 2. MODERN CHAMPION
                AvatarItem(
                    id = "modern_champion",
                    title = "Modern Champion",
                    category = AvatarCategory.FREE,
                    rarity = AvatarRarity.FREE,
                    resId = R.drawable.img_avatar_stylish_man,
                    unlocked = true,
                    costGems = 0,
                    requiredLevel = 1,
                    description = "Charismatic modern grandmaster with sharp athletic hoodie and fierce championship focus.",
                    nationality = "United States",
                    countryFlag = "🇺🇸",
                    gender = "Boy",
                    artStyle = "Realistic 3D Character",
                    playStyle = "Dynamic Attacker",
                    biography = "Elite modern competitor dominating classical and speed chess arenas with unyielding tactical confidence.",
                    quote = "Every move is an aggressive step toward the trophy."
                ),

                // 3. KNIGHT
                AvatarItem(
                    id = "knight_armored",
                    title = "Knight",
                    category = AvatarCategory.FREE,
                    rarity = AvatarRarity.FREE,
                    resId = R.drawable.img_avatar_knight_armored,
                    unlocked = true,
                    costGems = 0,
                    requiredLevel = 1,
                    description = "Valiant medieval knight in polished steel helmet, visor slit and crimson cloak.",
                    nationality = "Iron Vanguard",
                    countryFlag = "⚔️",
                    gender = "Boy",
                    artStyle = "Realistic 3D Character",
                    playStyle = "Knight Fork Vanguard",
                    biography = "Armored defender of the realm renowned for lethal L-shaped knight forks and unbreakable defensive formations.",
                    quote = "Honor in the duel, steel in every maneuver."
                ),

                // 4. GENTLEMAN
                AvatarItem(
                    id = "gentleman_master",
                    title = "Gentleman",
                    category = AvatarCategory.FREE,
                    rarity = AvatarRarity.FREE,
                    resId = R.drawable.img_avatar_man_1,
                    unlocked = true,
                    costGems = 0,
                    requiredLevel = 1,
                    description = "Sophisticated grandmaster in tailored charcoal suit touching his beard with deep calculation.",
                    nationality = "United Kingdom",
                    countryFlag = "🇬🇧",
                    gender = "Boy",
                    artStyle = "Realistic 3D Character",
                    playStyle = "Positional Maestro",
                    biography = "Distinguished grandmaster whose calm demeanor and deep classical positional plays dismantle opponents effortlessly.",
                    quote = "Strategy is best served with patience and composure."
                ),

                // 5. SHADOW PLAYER
                AvatarItem(
                    id = "shadow_player",
                    title = "Shadow Player",
                    category = AvatarCategory.FREE,
                    rarity = AvatarRarity.FREE,
                    resId = R.drawable.img_avatar_cyber_ninja,
                    unlocked = true,
                    costGems = 0,
                    requiredLevel = 1,
                    description = "Mysterious hooded tactician lurking in dark shadows with intense piercing eyes.",
                    nationality = "Shadow Syndicate",
                    countryFlag = "🌑",
                    gender = "Boy",
                    artStyle = "Realistic 3D Character",
                    playStyle = "Stealth Ambush",
                    biography = "A master of unexpected gambits and silent queen sacrifices that materialize out of nowhere to strike checkmate.",
                    quote = "You never see the winning move until it strikes."
                ),

                // 6. INTELLECT
                AvatarItem(
                    id = "intellect_genius",
                    title = "Intellect",
                    category = AvatarCategory.FREE,
                    rarity = AvatarRarity.FREE,
                    resId = R.drawable.img_avatar_german_boy,
                    unlocked = true,
                    costGems = 0,
                    requiredLevel = 1,
                    description = "Brilliant youthful genius with glasses and dark hoodie calculating deep variations.",
                    nationality = "Germany",
                    countryFlag = "🇩🇪",
                    gender = "Boy",
                    artStyle = "Realistic 3D Character",
                    playStyle = "Algorithmic Precision",
                    biography = "Young calculation prodigy who evaluates 25 moves deep in milliseconds, leaving zero chance for counterplay.",
                    quote = "Chess is pure mathematics and beautiful logic."
                ),

                // 7. MODERN WOMAN
                AvatarItem(
                    id = "modern_woman_new",
                    title = "Modern Stylist",
                    category = AvatarCategory.FREE,
                    rarity = AvatarRarity.FREE,
                    resId = R.drawable.modern_woman_3d_1788461136287,
                    unlocked = true,
                    costGems = 0,
                    requiredLevel = 1,
                    description = "Stylish modern woman with trendy look.",
                    nationality = "United States",
                    countryFlag = "🇺🇸",
                    gender = "Girl",
                    artStyle = "3D Game Portrait",
                    playStyle = "Modern Strategist",
                    biography = "A modern trendsetter on the chessboard.",
                    quote = "Stay stylish, play smart."
                ),

                // 8. ELEGANT LADY
                AvatarItem(
                    id = "elegant_lady",
                    title = "Elegant Lady",
                    category = AvatarCategory.FREE,
                    rarity = AvatarRarity.FREE,
                    resId = R.drawable.img_avatar_modern_woman,
                    unlocked = true,
                    costGems = 0,
                    requiredLevel = 1,
                    description = "Graceful brunette grandmaster in pristine high-collar lace dress with poised elegance.",
                    nationality = "France",
                    countryFlag = "🇫🇷",
                    gender = "Girl",
                    artStyle = "Realistic 3D Character",
                    playStyle = "Harmonic Harmony",
                    biography = "Parisian chess virtuoso whose graceful piece coordination and quiet pressure win without a single mistake.",
                    quote = "True elegance is effortless checkmate."
                ),

                // 9. PUNJABI TRADITIONAL WOMAN
                AvatarItem(
                    id = "punjabi_traditional",
                    title = "Punjabi Traditional",
                    category = AvatarCategory.FREE,
                    rarity = AvatarRarity.FREE,
                    resId = R.drawable.punjabi_trad_woman_3d_1788461174702,
                    unlocked = true,
                    costGems = 0,
                    requiredLevel = 1,
                    description = "Traditional Punjabi woman in vibrant dress.",
                    nationality = "India",
                    countryFlag = "🇮🇳",
                    gender = "Girl",
                    artStyle = "3D Game Portrait",
                    playStyle = "Cultural Strategist",
                    biography = "A graceful representation of Punjabi tradition on the chess board.",
                    quote = "Tradition meets strategy."
                ),

                // 9. WARRIOR QUEEN
                AvatarItem(
                    id = "warrior_queen",
                    title = "Warrior Queen",
                    category = AvatarCategory.FREE,
                    rarity = AvatarRarity.FREE,
                    resId = R.drawable.img_avatar_champion_girl,
                    unlocked = true,
                    costGems = 0,
                    requiredLevel = 1,
                    description = "Fierce Valkyrie queen with warrior braids, red war paint and armored shoulder mantle.",
                    nationality = "Nordic Empire",
                    countryFlag = "🛡️",
                    gender = "Girl",
                    artStyle = "Realistic 3D Character",
                    playStyle = "Furious Onslaught",
                    biography = "Battle-hardened shieldmaiden who launches ferocious pawn storms and destroys opposing kings with fearless zeal.",
                    quote = "Attack without fear, conquer without mercy."
                ),

                // 10. MODERN PLAYER
                AvatarItem(
                    id = "modern_player",
                    title = "Modern Player",
                    category = AvatarCategory.FREE,
                    rarity = AvatarRarity.FREE,
                    resId = R.drawable.img_avatar_gamer_girl,
                    unlocked = true,
                    costGems = 0,
                    requiredLevel = 1,
                    description = "Trendy speed-chess streamer wearing black cap with white knight emblem and headphones.",
                    nationality = "South Korea",
                    countryFlag = "🎧",
                    gender = "Girl",
                    artStyle = "Realistic 3D Character",
                    playStyle = "Blitz Speed Demon",
                    biography = "High-APM internet sensation streaming bullet chess victories with style, energy and rapid tactical combos.",
                    quote = "Checkmate in 3, don't forget to like and subscribe!"
                ),

                // 11. MYSTIC QUEEN
                AvatarItem(
                    id = "mystic_queen",
                    title = "Mystic Queen",
                    category = AvatarCategory.FREE,
                    rarity = AvatarRarity.FREE,
                    resId = R.drawable.img_avatar_cyber_queen,
                    unlocked = true,
                    costGems = 0,
                    requiredLevel = 1,
                    description = "Silver-haired mystical grandmaster in high-collar dark tactical robes with hypnotic presence.",
                    nationality = "Arcane Citadel",
                    countryFlag = "🔮",
                    gender = "Girl",
                    artStyle = "Realistic 3D Character",
                    playStyle = "Mystic Entanglement",
                    biography = "Sorceress of the chessboard whose hypnotic piece traps bewilder opponents and twist games into inevitable defeat.",
                    quote = "The future of the board is already written."
                ),

                // 12. SMART PLAYER
                AvatarItem(
                    id = "smart_player",
                    title = "Smart Player",
                    category = AvatarCategory.FREE,
                    rarity = AvatarRarity.FREE,
                    resId = R.drawable.img_avatar_woman_1,
                    unlocked = true,
                    costGems = 0,
                    requiredLevel = 1,
                    description = "Studious girl with messy bun and black-rimmed glasses in comfy grey hoodie.",
                    nationality = "Canada",
                    countryFlag = "🇨🇦",
                    gender = "Girl",
                    artStyle = "Realistic 3D Character",
                    playStyle = "Endgame Theoretical Virtuoso",
                    biography = "Dedicated chess theorist who has memorized every classic endgame tablebase to convert micro-advantages cleanly.",
                    quote = "Never underestimate the power of deep study."
                ),

                // 13. IMPERIAL EMPRESS (REMOVED)
                // Replaced by American Traditional Woman
                AvatarItem(
                    id = "american_traditional",
                    title = "American Traditional",
                    category = AvatarCategory.FREE,
                    rarity = AvatarRarity.FREE,
                    resId = R.drawable.american_trad_woman_3d_1788461156492,
                    unlocked = true,
                    costGems = 0,
                    requiredLevel = 1,
                    description = "American traditional woman in elegant 1950s style.",
                    nationality = "United States",
                    countryFlag = "🇺🇸",
                    gender = "Girl",
                    artStyle = "3D Game Portrait",
                    playStyle = "Vintage Strategist",
                    biography = "A classic and elegant representation on the board.",
                    quote = "Classic style, modern strategy."
                ),

                // 14. CYBER VALKYRIE
                AvatarItem(
                    id = "cyber_valkyrie",
                    title = "Cyber Valkyrie",
                    category = AvatarCategory.FREE,
                    rarity = AvatarRarity.FREE,
                    resId = R.drawable.img_avatar_cyber_valkyrie,
                    unlocked = true,
                    costGems = 0,
                    requiredLevel = 1,
                    description = "Futuristic Valkyrie warrior queen with neon cyan lighting and white braided hair.",
                    nationality = "Neo Valkyrie",
                    countryFlag = "⚡",
                    gender = "Girl",
                    artStyle = "Realistic 3D Character",
                    playStyle = "Cybernetic Attack Vector",
                    biography = "High-tech warrior whose laser-focused knight outposts strike fear into every opposing defense.",
                    quote = "Lightning strikes the weakest square."
                ),

                // 15. CELESTIAL QUEEN
                AvatarItem(
                    id = "celestial_queen",
                    title = "Celestial Queen",
                    category = AvatarCategory.FREE,
                    rarity = AvatarRarity.FREE,
                    resId = R.drawable.img_avatar_celestial_queen,
                    unlocked = true,
                    costGems = 0,
                    requiredLevel = 1,
                    description = "Ethereal starlight goddess queen with glowing golden tiara and serene expression.",
                    nationality = "Starlight Realm",
                    countryFlag = "✨",
                    gender = "Girl",
                    artStyle = "Realistic 3D Character",
                    playStyle = "Starlight Harmony",
                    biography = "Ethereal grandmaster weaving constellation-like pawn structures and serene endgame wins.",
                    quote = "The stars guide every winning move."
                ),
                // ADDITIONAL TRADITIONAL INDIAN WOMEN AVATARS
                AvatarItem(
                    id = "indian_traditional_1",
                    title = "Traditional Indian Woman",
                    category = AvatarCategory.FREE,
                    rarity = AvatarRarity.FREE,
                    resId = R.drawable.indian_traditional_woman_1_1788459996731,
                    unlocked = true,
                    costGems = 0,
                    requiredLevel = 1,
                    description = "Traditional Indian woman in cultural dress, vibrant colors.",
                    nationality = "India",
                    countryFlag = "🇮🇳",
                    gender = "Girl",
                    artStyle = "3D Game Portrait",
                    playStyle = "Cultural Strategist",
                    biography = "A graceful representation of Indian tradition on the chess board.",
                    quote = "Tradition meets strategy."
                ),
                AvatarItem(
                    id = "indian_traditional_2",
                    title = "Saree Elegant Woman",
                    category = AvatarCategory.FREE,
                    rarity = AvatarRarity.FREE,
                    resId = R.drawable.indian_traditional_woman_2_1788460011249,
                    unlocked = true,
                    costGems = 0,
                    requiredLevel = 1,
                    description = "Traditional Indian woman in elegant saree.",
                    nationality = "India",
                    countryFlag = "🇮🇳",
                    gender = "Girl",
                    artStyle = "3D Game Portrait",
                    playStyle = "Graceful Strategist",
                    biography = "Elegance and tradition combined on the chess board.",
                    quote = "Grace in every move."
                ),



                // 13. WOLF
                AvatarItem(
                    id = "wolf_alpha",
                    title = "Wolf",
                    category = AvatarCategory.FREE,
                    rarity = AvatarRarity.FREE,
                    resId = R.drawable.img_avatar_cyber_wolf,
                    unlocked = true,
                    costGems = 0,
                    requiredLevel = 1,
                    description = "Majestic arctic white wolf with thick textured fur and intense piercing cyan-blue eyes.",
                    nationality = "Frost Tundra",
                    countryFlag = "🐺",
                    gender = "Animal",
                    artStyle = "Realistic 3D Fur & Character",
                    playStyle = "Pack Hunter Predator",
                    biography = "Alpha predator of the snowfields who coordinates rooks and bishops like a synchronized wolf pack hunting the enemy king.",
                    quote = "The lone wolf rules the open files."
                ),

                // 14. LION
                AvatarItem(
                    id = "lion_sovereign",
                    title = "Lion",
                    category = AvatarCategory.FREE,
                    rarity = AvatarRarity.FREE,
                    resId = R.drawable.img_avatar_king_lion_3d,
                    unlocked = true,
                    costGems = 0,
                    requiredLevel = 1,
                    description = "Magnificent golden lion with heavy regal mane and fierce dignified amber gaze.",
                    nationality = "Golden Savanna",
                    countryFlag = "🦁",
                    gender = "Animal",
                    artStyle = "Realistic 3D Fur & Character",
                    playStyle = "Apex Roar Dominance",
                    biography = "Sovereign king of beasts claiming the e4/d4 center squares with an earth-shaking roar and ferocious power.",
                    quote = "A lion does not negotiate for center squares."
                ),

                // 15. TIGER
                AvatarItem(
                    id = "tiger_predator",
                    title = "Tiger",
                    category = AvatarCategory.FREE,
                    rarity = AvatarRarity.FREE,
                    resId = R.drawable.img_avatar_white_tiger,
                    unlocked = true,
                    costGems = 0,
                    requiredLevel = 1,
                    description = "Formidable Bengal tiger with amber stripes, textured fur and lethal stalking gaze.",
                    nationality = "Bengal Rainforest",
                    countryFlag = "🐯",
                    gender = "Animal",
                    artStyle = "Realistic 3D Fur & Character",
                    playStyle = "Leaping Ambush",
                    biography = "Stealthy predator who stalks opponent weaknesses from afar before leaping across diagonals for a decisive kill.",
                    quote = "Silence in the hunt, lightning in the strike."
                ),

                // 16. EAGLE
                AvatarItem(
                    id = "eagle_soar",
                    title = "Eagle",
                    category = AvatarCategory.FREE,
                    rarity = AvatarRarity.FREE,
                    resId = R.drawable.img_avatar_eagle_king,
                    unlocked = true,
                    costGems = 0,
                    requiredLevel = 1,
                    description = "Magnificent bald eagle with snow-white feathered head, golden beak and piercing raptor eyes.",
                    nationality = "Sky Peaks",
                    countryFlag = "🦅",
                    gender = "Animal",
                    artStyle = "Realistic 3D Feather & Character",
                    playStyle = "Panoramic Aerial Vision",
                    biography = "High-flying sovereign possessing aerial vision that spots opponent tactical oversights across the entire board.",
                    quote = "Soar above the board, strike from the sky."
                ),

                // 17. OWL
                AvatarItem(
                    id = "owl_sage",
                    title = "Owl",
                    category = AvatarCategory.FREE,
                    rarity = AvatarRarity.FREE,
                    resId = R.drawable.img_avatar_owl_tactician,
                    unlocked = true,
                    costGems = 0,
                    requiredLevel = 1,
                    description = "Wise horned owl with intricate patterned feathers and huge luminous glowing orange-amber eyes.",
                    nationality = "Ancient Woods",
                    countryFlag = "🦉",
                    gender = "Animal",
                    artStyle = "Realistic 3D Feather & Character",
                    playStyle = "Nocturnal Foresight",
                    biography = "Ancient sage of the woodland who sees through every bluff, trap and decoy move with calm nocturnal intelligence.",
                    quote = "Patience sees what haste overlooks."
                ),

                // 18. FOX
                AvatarItem(
                    id = "fox_cunning",
                    title = "Fox",
                    category = AvatarCategory.FREE,
                    rarity = AvatarRarity.FREE,
                    resId = R.drawable.img_avatar_fox_tactician,
                    unlocked = true,
                    costGems = 0,
                    requiredLevel = 1,
                    description = "Clever red fox with vibrant amber fur, pointed alert ears and cunning tactical grin.",
                    nationality = "Whispering Forest",
                    countryFlag = "🦊",
                    gender = "Animal",
                    artStyle = "Realistic 3D Fur & Character",
                    playStyle = "Cunning Gambiteer",
                    biography = "Playful yet deadly tactician specializing in treacherous pawn gambits and outsmarting higher-rated opponents.",
                    quote = "Outsmart the tiger with fox cunning."
                ),

                // 19. BEAR
                AvatarItem(
                    id = "bear_crusher",
                    title = "Bear",
                    category = AvatarCategory.FREE,
                    rarity = AvatarRarity.FREE,
                    resId = R.drawable.img_avatar_bear_roaring,
                    unlocked = true,
                    costGems = 0,
                    requiredLevel = 1,
                    description = "Massive roaring grizzly bear with bared fangs, thick bristled fur and unstoppable raw power.",
                    nationality = "Taiga Mountains",
                    countryFlag = "🐻",
                    gender = "Animal",
                    artStyle = "Realistic 3D Fur & Character",
                    playStyle = "Heavyweight Pawn Steamroller",
                    biography = "A colossal force that steamrolls through opposing fortresses with massive doubled rooks and unstoppable pawn storms.",
                    quote = "Crush the defense with unstoppable might."
                ),

                // 20. PANTHER
                AvatarItem(
                    id = "panther_shadow",
                    title = "Panther",
                    category = AvatarCategory.FREE,
                    rarity = AvatarRarity.FREE,
                    resId = R.drawable.img_avatar_panther_stealth,
                    unlocked = true,
                    costGems = 0,
                    requiredLevel = 1,
                    description = "Sleek black panther with glossy midnight coat, glowing yellow-green eyes and stealthy poise.",
                    nationality = "Midnight Canopy",
                    countryFlag = "🐆",
                    gender = "Animal",
                    artStyle = "Realistic 3D Fur & Character",
                    playStyle = "Dark Square Phantom",
                    biography = "Silent shadow stalking the dark squares, executing deadly knight outposts and delivering checkmate unnoticed.",
                    quote = "Strike when the shadow is deepest."
                ),

                // ==========================================
                // ADDITIONAL FREE COMMUNITY HEROES
                // ==========================================

                AvatarItem(
                    id = "champion_boy",
                    title = "Champion Boy",
                    category = AvatarCategory.FREE,
                    rarity = AvatarRarity.FREE,
                    resId = R.drawable.img_avatar_champion_boy,
                    unlocked = true,
                    costGems = 0,
                    requiredLevel = 1,
                    description = "A confident champion always ready to win!",
                    nationality = "United States",
                    countryFlag = "🇺🇸",
                    gender = "Boy",
                    artStyle = "3D Game Portrait",
                    playStyle = "Tactical Prodigy",
                    biography = "A fearless chess prodigy whose intuition and smiling confidence break down the toughest defenses.",
                    quote = "Play every move with courage and a smile!"
                ),
                AvatarItem(
                    id = "master_aarav",
                    title = "Master Aarav",
                    category = AvatarCategory.FREE,
                    rarity = AvatarRarity.FREE,
                    resId = R.drawable.img_avatar_indian_boy,
                    unlocked = true,
                    costGems = 0,
                    requiredLevel = 1,
                    description = "Rapid calculation prodigy with fearless king attacks.",
                    nationality = "India",
                    countryFlag = "🇮🇳",
                    gender = "Boy",
                    artStyle = "3D Game Portrait",
                    playStyle = "Rapid Tactician",
                    biography = "Young genius from Chennai who solves 20-move deep puzzle tactics in seconds.",
                    quote = "Focus, calculate, and strike!"
                ),
                AvatarItem(
                    id = "master_chloe",
                    title = "Master Chloe",
                    category = AvatarCategory.FREE,
                    rarity = AvatarRarity.FREE,
                    resId = R.drawable.img_avatar_woman_2,
                    unlocked = true,
                    costGems = 0,
                    requiredLevel = 1,
                    description = "Elegant French defense master with razor-sharp counters.",
                    nationality = "France",
                    countryFlag = "🇫🇷",
                    gender = "Girl",
                    artStyle = "3D Game Portrait",
                    playStyle = "Counterpunch Virtuoso",
                    biography = "Parisian tournament regular who turns opponent mistakes into immediate checkmates.",
                    quote = "Every move should be a work of art."
                ),
                AvatarItem(
                    id = "explorer_boy",
                    title = "Explorer Boy",
                    category = AvatarCategory.FREE,
                    rarity = AvatarRarity.FREE,
                    resId = R.drawable.img_avatar_african_boy,
                    unlocked = true,
                    costGems = 0,
                    requiredLevel = 1,
                    description = "Eager adventurer exploring deep opening variations.",
                    nationality = "Kenya / Africa",
                    countryFlag = "🌍",
                    gender = "Boy",
                    artStyle = "3D Game Portrait",
                    playStyle = "Opening Explorer",
                    biography = "Curious youth venturing into uncharted theoretical variations with adventurous spirit.",
                    quote = "Every new square holds a great adventure!"
                ),
                AvatarItem(
                    id = "simple_man_free",
                    title = "Simple Tactician",
                    category = AvatarCategory.FREE,
                    rarity = AvatarRarity.FREE,
                    resId = R.drawable.img_avatar_simple_man,
                    unlocked = true,
                    costGems = 0,
                    requiredLevel = 1,
                    description = "Friendly everyday 3D chess enthusiast with warm smile and sharp intuition.",
                    nationality = "International",
                    countryFlag = "🌐",
                    gender = "Boy",
                    artStyle = "3D Stylized Model",
                    playStyle = "Intuitive Guerrilla",
                    biography = "Relaxed, relatable player who turns casual friendly games into brilliant knight forks and surprise checkmates.",
                    quote = "Keep it simple, play your best move!"
                ),
                AvatarItem(
                    id = "champion_man_free",
                    title = "World Champion",
                    category = AvatarCategory.FREE,
                    rarity = AvatarRarity.FREE,
                    resId = R.drawable.img_avatar_champion_man,
                    unlocked = true,
                    costGems = 0,
                    requiredLevel = 1,
                    description = "Victorious 3D champion in athletic gold championship jacket.",
                    nationality = "Norway",
                    countryFlag = "🇳🇴",
                    gender = "Boy",
                    artStyle = "3D Stylized Model",
                    playStyle = "Unstoppable Champion",
                    biography = "Undisputed arena heavyweight who lifts championship trophies with unwavering fighting spirit.",
                    quote = "Victory belongs to those who never surrender."
                ),

                // ==========================================
                // STYLISH BUY / PREMIUM SHOP AVATARS (GEMS)
                // ==========================================

                // 19. 3D ROYAL KING LION (PREMIUM / LEGENDARY)
                AvatarItem(
                    id = "king_lion_3d",
                    title = "Royal Lion King",
                    category = AvatarCategory.PREMIUM,
                    rarity = AvatarRarity.LEGENDARY,
                    resId = R.drawable.img_avatar_king_lion_3d,
                    unlocked = false,
                    costGems = 650,
                    requiredLevel = 20,
                    description = "Majestic 3D King Lion wearing a grand glittering golden crown and ruby mantle!",
                    nationality = "Savannah Realm",
                    countryFlag = "🦁",
                    gender = "Animal",
                    artStyle = "3D Stylized Model",
                    playStyle = "Sovereign Apex Dominance",
                    biography = "The supreme monarch of the 64 squares. Roars with ferocious tactical power and unstoppable kingside onslaughts.",
                    quote = "The chessboard bows to the Lion King."
                ),

                // 20. 3D CYBER WOLF ALPHA (PREMIUM / EPIC)
                AvatarItem(
                    id = "cyber_wolf",
                    title = "Cyber Wolf Alpha",
                    category = AvatarCategory.PREMIUM,
                    rarity = AvatarRarity.EPIC,
                    resId = R.drawable.img_avatar_cyber_wolf,
                    unlocked = false,
                    costGems = 550,
                    requiredLevel = 18,
                    description = "Futuristic 3D Cyber Wolf with glowing neon cyan eyes and titanium armor!",
                    nationality = "Neo Arctic",
                    countryFlag = "🐺",
                    gender = "Animal",
                    artStyle = "3D Stylized Model",
                    playStyle = "Cybernetic Predator",
                    biography = "High-tech alpha predator tracking opposing kings with quantum precision and razor-sharp diagonal cuts.",
                    quote = "System locked. The pack hunts in silence."
                ),

                // 21. GOLDEN KING (PREMIUM / LEGENDARY)
                AvatarItem(
                    id = "golden_king",
                    title = "Golden King",
                    category = AvatarCategory.PREMIUM,
                    rarity = AvatarRarity.LEGENDARY,
                    resId = R.drawable.img_avatar_golden_king,
                    unlocked = false,
                    costGems = 600,
                    requiredLevel = 20,
                    description = "Sovereign 3D Monarch in crimson robes and pure golden crown!",
                    nationality = "High Sovereign",
                    countryFlag = "👑",
                    gender = "Boy",
                    artStyle = "3D Game Portrait",
                    playStyle = "Sovereign Command",
                    biography = "The ultimate royal authority on sixty-four squares. Inspires fearless piece sacrifices.",
                    quote = "Bow before the supremacy of pure strategy."
                ),

                // 20. LION KING (PREMIUM / EPIC)
                AvatarItem(
                    id = "lion_king",
                    title = "Lion King",
                    category = AvatarCategory.PREMIUM,
                    rarity = AvatarRarity.EPIC,
                    resId = R.drawable.img_avatar_lion_king,
                    unlocked = false,
                    costGems = 500,
                    requiredLevel = 15,
                    description = "Show your strength with the royal apex predator avatar!",
                    nationality = "Savannah Realm",
                    countryFlag = "🦁",
                    gender = "Animal",
                    artStyle = "3D Game Portrait",
                    playStyle = "Apex Dominance",
                    biography = "The sovereign predator of sixty-four squares. Roars with aggressive center sacrifices and crushing attacks.",
                    quote = "The chessboard is my kingdom."
                ),

                // 21. CYBER QUEEN (PREMIUM / EPIC)
                AvatarItem(
                    id = "cyber_queen",
                    title = "Cyber Queen",
                    category = AvatarCategory.PREMIUM,
                    rarity = AvatarRarity.EPIC,
                    resId = R.drawable.img_avatar_cyber_queen,
                    unlocked = false,
                    costGems = 550,
                    requiredLevel = 18,
                    description = "Futuristic neon cyberpunk empress with glowing visor and crown!",
                    nationality = "Neo Tokyo",
                    countryFlag = "⚡",
                    gender = "Girl",
                    artStyle = "3D Game Portrait",
                    playStyle = "Hyperion Calculation",
                    biography = "High-tech synth queen who executes checkmates with laser speed and neon flair.",
                    quote = "System online. Target king acquired."
                ),

                // 22. NINJA ASSASSIN (PREMIUM / RARE)
                AvatarItem(
                    id = "ninja",
                    title = "Ninja Assassin",
                    category = AvatarCategory.PREMIUM,
                    rarity = AvatarRarity.RARE,
                    resId = R.drawable.img_avatar_cyber_ninja,
                    unlocked = false,
                    costGems = 250,
                    requiredLevel = 10,
                    description = "Silent strikes from unseen diagonals on the board.",
                    nationality = "Japan",
                    countryFlag = "🇯🇵",
                    gender = "Boy",
                    artStyle = "3D Game Portrait",
                    playStyle = "Stealth Infiltration",
                    biography = "Shadow warrior striking from hidden angles. Masters bishop forks and surprise knight maneuvers.",
                    quote = "In silence lies the deadliest strike."
                ),

                // 23. GAMER GIRL PRO (PREMIUM / RARE)
                AvatarItem(
                    id = "gamer_girl",
                    title = "Gamer Girl Pro",
                    category = AvatarCategory.PREMIUM,
                    rarity = AvatarRarity.RARE,
                    resId = R.drawable.img_avatar_gamer_girl,
                    unlocked = false,
                    costGems = 300,
                    requiredLevel = 12,
                    description = "Lightning-fast blitz queen streaming every tactic with neon RGB style!",
                    nationality = "South Korea",
                    countryFlag = "🇰🇷",
                    gender = "Girl",
                    artStyle = "3D Game Portrait",
                    playStyle = "Hyper Blitz Attack",
                    biography = "High-APM tournament streamer known for speed chess reflexes and relentless tactical combos.",
                    quote = "GG is only one move away!"
                ),

                // 24. CYBER MECHA AI (PREMIUM / EPIC)
                AvatarItem(
                    id = "cyber_mecha",
                    title = "Cyber Mecha AI",
                    category = AvatarCategory.PREMIUM,
                    rarity = AvatarRarity.EPIC,
                    iconEmoji = "🤖",
                    unlocked = false,
                    costGems = 400,
                    requiredLevel = 15,
                    description = "Futuristic AI engine calculating 20 moves deep.",
                    nationality = "Cyber Core",
                    countryFlag = "⚡",
                    gender = "Boy",
                    artStyle = "3D Game Portrait",
                    playStyle = "Quantum Calculation",
                    biography = "Advanced cybernetic computing unit evaluating millions of positions per second with zero error.",
                    quote = "Human error detected. Executing checkmate."
                ),

                // 25. SAMURAI MASTER (PREMIUM / EPIC)
                AvatarItem(
                    id = "samurai",
                    title = "Samurai Master",
                    category = AvatarCategory.PREMIUM,
                    rarity = AvatarRarity.EPIC,
                    resId = R.drawable.img_avatar_man_2,
                    iconEmoji = "⚔️",
                    unlocked = false,
                    costGems = 450,
                    requiredLevel = 18,
                    description = "Honorable blade warrior with unyielding discipline.",
                    nationality = "Japan",
                    countryFlag = "🇯🇵",
                    gender = "Boy",
                    artStyle = "3D Game Portrait",
                    playStyle = "Bushido Precision",
                    biography = "Devoted to the code of honor and absolute tactical focus. Never surrenders a square without a fight.",
                    quote = "My blade is my bishop, my honor is victory."
                ),

                // 26. DRAGON KNIGHT (PREMIUM / LEGENDARY)
                AvatarItem(
                    id = "dragon_knight",
                    title = "Dragon Knight",
                    category = AvatarCategory.PREMIUM,
                    rarity = AvatarRarity.LEGENDARY,
                    iconEmoji = "🐉",
                    unlocked = false,
                    costGems = 600,
                    requiredLevel = 20,
                    description = "Fire-breathing draconic rider raining flame across open files!",
                    nationality = "Dragon Peak",
                    countryFlag = "🔥",
                    gender = "Boy",
                    artStyle = "3D Game Portrait",
                    playStyle = "Draconic Assault",
                    biography = "Fearless dragon tamer whose attacks scorch enemy pawn chains into cinders.",
                    quote = "Unleash the dragon's wrath!"
                ),

                // 27. VALKYRIE QUEEN (PREMIUM / EPIC)
                AvatarItem(
                    id = "valkyrie_warrior",
                    title = "Valkyrie Queen",
                    category = AvatarCategory.PREMIUM,
                    rarity = AvatarRarity.EPIC,
                    iconEmoji = "🛡️",
                    unlocked = false,
                    costGems = 480,
                    requiredLevel = 16,
                    description = "Golden winged shield maiden escorting pieces into Valhalla!",
                    nationality = "Valhalla",
                    countryFlag = "🪽",
                    gender = "Girl",
                    artStyle = "3D Game Portrait",
                    playStyle = "Shield Maiden Assault",
                    biography = "Mythic Norse warrior leading pawns fearlessly through the storm of battle.",
                    quote = "To victory and eternal glory!"
                ),

                // 28. PHARAOH MONARCH (PREMIUM / EPIC)
                AvatarItem(
                    id = "pharaoh_monarch",
                    title = "Pharaoh Monarch",
                    category = AvatarCategory.PREMIUM,
                    rarity = AvatarRarity.EPIC,
                    iconEmoji = "🏺",
                    unlocked = false,
                    costGems = 520,
                    requiredLevel = 18,
                    description = "Golden ruler of the ancient pyramids wielding timeless strategy.",
                    nationality = "Ancient Nile",
                    countryFlag = "🏛️",
                    gender = "Boy",
                    artStyle = "3D Game Portrait",
                    playStyle = "Pyramid Fortress",
                    biography = "Ancient pharaoh commanding an eternal legion of stone and golden pieces.",
                    quote = "Strategy stands eternal as the pyramids."
                ),

                // 29. COSMIC SOVEREIGN (PREMIUM / LEGENDARY)
                AvatarItem(
                    id = "cosmic_sovereign",
                    title = "Cosmic Sovereign",
                    category = AvatarCategory.PREMIUM,
                    rarity = AvatarRarity.LEGENDARY,
                    iconEmoji = "🌌",
                    unlocked = false,
                    costGems = 700,
                    requiredLevel = 25,
                    description = "Interstellar emperor holding galaxies in the palm of his hand.",
                    nationality = "Cosmos Realm",
                    countryFlag = "🌌",
                    gender = "Boy",
                    artStyle = "3D Game Portrait",
                    playStyle = "Cosmic Singularity",
                    biography = "Transcendent grandmaster who bends the laws of spatial geometry across 64 squares.",
                    quote = "The stars align for this checkmate."
                ),

                // 30. COMBAT ROBOT TITAN (PREMIUM / EPIC)
                AvatarItem(
                    id = "combat_robot",
                    title = "Combat Robot Titan",
                    category = AvatarCategory.PREMIUM,
                    rarity = AvatarRarity.EPIC,
                    iconEmoji = "🦾",
                    unlocked = false,
                    costGems = 450,
                    requiredLevel = 14,
                    description = "Heavy titanium armor turning pawns into impenetrable fortresses.",
                    nationality = "Cyber Core",
                    countryFlag = "🛡️",
                    gender = "Boy",
                    artStyle = "3D Game Portrait",
                    playStyle = "Iron Bastion",
                    biography = "Forged in titanium, built to absorb enemy assaults and counter with devastating power.",
                    quote = "Shields at maximum. Advance."
                ),

                // 31. COWBOY BANDIT (SPECIAL / RARE)
                AvatarItem(
                    id = "cowboy_bandit",
                    title = "Cowboy Bandit",
                    category = AvatarCategory.SPECIAL,
                    rarity = AvatarRarity.RARE,
                    iconEmoji = "🤠",
                    unlocked = false,
                    costGems = 200,
                    requiredLevel = 8,
                    description = "Wild west quick-draw master of speed chess shootouts.",
                    nationality = "Wild West",
                    countryFlag = "🌵",
                    gender = "Boy",
                    artStyle = "3D Game Portrait",
                    playStyle = "Quickdraw Gambiteer",
                    biography = "Fastest gun in the chess frontier. Thrives in chaotic shootouts and wild queen sacrifices.",
                    quote = "Draw your pawns, partner!"
                ),

                // 32. PIRATE CAPTAIN (SPECIAL / RARE)
                AvatarItem(
                    id = "pirate_skeleton",
                    title = "Pirate Captain",
                    category = AvatarCategory.SPECIAL,
                    rarity = AvatarRarity.RARE,
                    iconEmoji = "🏴‍☠️",
                    unlocked = false,
                    costGems = 350,
                    requiredLevel = 14,
                    description = "Plundering the enemy back rank with ruthless forks.",
                    nationality = "High Seas",
                    countryFlag = "🏴‍☠️",
                    gender = "Boy",
                    artStyle = "3D Game Portrait",
                    playStyle = "Back Rank Buccaneer",
                    biography = "Undead captain haunting open files and pillaging unprotected rooks across the seven seas.",
                    quote = "Dead men tell no checkmates!"
                ),

                // 33. PUMPKIN SORCERER (SPECIAL)
                AvatarItem(
                    id = "pumpkin_head",
                    title = "Pumpkin Sorcerer",
                    category = AvatarCategory.SPECIAL,
                    rarity = AvatarRarity.SPECIAL,
                    iconEmoji = "🎃",
                    unlocked = false,
                    costGems = 300,
                    requiredLevel = 12,
                    description = "Spooky harvest trickster conjuring eerie endgame gambits.",
                    nationality = "Halloween Valley",
                    countryFlag = "🎃",
                    gender = "Boy",
                    artStyle = "3D Game Portrait",
                    playStyle = "Trickster Gambit",
                    biography = "Eerie pumpkin lord weaving sinister tactical illusions and spooky endgame traps.",
                    quote = "Trick or checkmate!"
                ),

                // 34. PANDA MASTER ZEN (PREMIUM / EPIC)
                AvatarItem(
                    id = "panda_master",
                    title = "Panda Master Zen",
                    category = AvatarCategory.PREMIUM,
                    rarity = AvatarRarity.EPIC,
                    iconEmoji = "🐼",
                    unlocked = false,
                    costGems = 350,
                    requiredLevel = 15,
                    description = "Peaceful martial grandmaster with gentle lethal counters.",
                    nationality = "Bamboo Mountains",
                    countryFlag = "🎋",
                    gender = "Animal",
                    artStyle = "3D Game Portrait",
                    playStyle = "Zen Counter",
                    biography = "Gentle demeanor masking unstoppable inner power. Redirects opponent momentum effortlessly.",
                    quote = "Flow like water, strike like bamboo."
                ),

                // 35. GOLDEN PRINCESS (PREMIUM / LEGENDARY)
                AvatarItem(
                    id = "golden_princess",
                    title = "Golden Princess",
                    category = AvatarCategory.PREMIUM,
                    rarity = AvatarRarity.LEGENDARY,
                    resId = R.drawable.img_avatar_imperial_empress,
                    iconEmoji = "👸",
                    unlocked = false,
                    costGems = 500,
                    requiredLevel = 20,
                    description = "Elegance and supreme tactical majesty united.",
                    nationality = "Royal Dynasty",
                    countryFlag = "✨",
                    gender = "Girl",
                    artStyle = "3D Game Portrait",
                    playStyle = "Queen's Dominion",
                    biography = "Graceful sovereign commanding the board with refined poise, decisive pins, and royal triumph.",
                    quote = "True victory is crowned with grace."
                ),

                // --- REALISTIC OIL ART: ENGLAND BOYS & GIRLS ---
                AvatarItem(
                    id = "oil_england_boy",
                    title = "Oliver Wright",
                    category = AvatarCategory.REALISTIC_MAN,
                    resId = R.drawable.img_avatar_man_1,
                    unlocked = true,
                    costTokens = 0,
                    nationality = "England / UK",
                    countryFlag = "🇬🇧",
                    gender = "Boy",
                    artStyle = "Realistic Oil Canvas",
                    playStyle = "English Opening Maestro",
                    biography = "Oxford chess scholar combining classical Victorian opening theory with modern computing depth.",
                    quote = "Pawns are indeed the soul of chess."
                ),
                AvatarItem(
                    id = "oil_england_girl",
                    title = "Charlotte Evans",
                    category = AvatarCategory.REALISTIC_WOMAN,
                    resId = R.drawable.img_avatar_woman_1,
                    unlocked = true,
                    costTokens = 0,
                    nationality = "England / UK",
                    countryFlag = "🇬🇧",
                    gender = "Girl",
                    artStyle = "Realistic Oil Canvas",
                    playStyle = "Classical Endgame Virtuoso",
                    biography = "London grandmaster tournament veteran with peerless rook and pawn endgame technique.",
                    quote = "Simplicity is the ultimate sophistication."
                ),

                // --- REALISTIC OIL ART: AUSTRALIAN BOYS & GIRLS ---
                AvatarItem(
                    id = "oil_australia_boy",
                    title = "Liam Campbell",
                    category = AvatarCategory.REALISTIC_MAN,
                    resId = R.drawable.img_avatar_man_2,
                    unlocked = true,
                    costTokens = 0,
                    nationality = "Australia",
                    countryFlag = "🇦🇺",
                    gender = "Boy",
                    artStyle = "Realistic Oil Canvas",
                    playStyle = "King's Gambit Romantic",
                    biography = "Melbourne tactical daredevil who welcomes romantic gambits, piece sacrifices, and open board skirmishes.",
                    quote = "Fortune favors the audacious knight."
                ),
                AvatarItem(
                    id = "oil_australia_girl",
                    title = "Mia Harrison",
                    category = AvatarCategory.REALISTIC_WOMAN,
                    resId = R.drawable.img_avatar_woman_2,
                    unlocked = true,
                    costTokens = 0,
                    nationality = "Australia",
                    countryFlag = "🇦🇺",
                    gender = "Girl",
                    artStyle = "Realistic Oil Canvas",
                    playStyle = "Caro-Kann Fortress",
                    biography = "Sydney master renowned for impenetrable pawn fortresses, patient strangulation, and deadly knight outposts.",
                    quote = "An unbreakable shield yields the swiftest spear."
                ),

                // --- REALISTIC OIL ART: EUROPEAN BOYS & GIRLS ---
                AvatarItem(
                    id = "oil_europe_boy",
                    title = "Mateo Rossi",
                    category = AvatarCategory.REALISTIC_MAN,
                    resId = R.drawable.img_avatar_german_boy,
                    unlocked = true,
                    costTokens = 0,
                    nationality = "Europe / Italy",
                    countryFlag = "🇪🇺",
                    gender = "Boy",
                    artStyle = "Realistic Oil Canvas",
                    playStyle = "Italian Game Connoisseur",
                    biography = "Rome open champion blending artistic flair with surgical piece coordination and kingside assaults.",
                    quote = "Logic and passion dance on sixty-four squares."
                ),
                AvatarItem(
                    id = "oil_europe_girl",
                    title = "Elena Rostova",
                    category = AvatarCategory.REALISTIC_WOMAN,
                    resId = R.drawable.img_avatar_woman_2,
                    unlocked = true,
                    costTokens = 0,
                    nationality = "Europe / Spain",
                    countryFlag = "🇪🇺",
                    gender = "Girl",
                    artStyle = "Realistic Oil Canvas",
                    playStyle = "Catalan Strategist",
                    biography = "Barcelona grandmaster combining harmonic light-square bishop dominance with subtle tactical traps.",
                    quote = "Quiet moves echo the loudest on the board."
                ),

                // Legacy Realistic Avatars
                AvatarItem(
                    id = "man_portrait_1",
                    title = "Grandmaster Alex",
                    category = AvatarCategory.REALISTIC_MAN,
                    resId = R.drawable.img_avatar_man_1,
                    unlocked = true,
                    costTokens = 0,
                    nationality = "United Kingdom",
                    countryFlag = "🇬🇧",
                    gender = "Boy",
                    artStyle = "Realistic Oil Canvas",
                    playStyle = "Classical Master",
                    biography = "Veteran grandmaster with decades of international championship experience.",
                    quote = "Checkmate is just the beginning."
                ),
                AvatarItem(
                    id = "man_portrait_2",
                    title = "Strategist Ethan",
                    category = AvatarCategory.REALISTIC_MAN,
                    resId = R.drawable.img_avatar_man_2,
                    unlocked = true,
                    costTokens = 0,
                    nationality = "United States",
                    countryFlag = "🇺🇸",
                    gender = "Boy",
                    artStyle = "Realistic Oil Canvas",
                    playStyle = "Blitz Dominator",
                    biography = "Fast tactical calculator with aggressive opening repertoires.",
                    quote = "Time waits for no king."
                ),
                AvatarItem(
                    id = "woman_portrait_1",
                    title = "Master Sofia",
                    category = AvatarCategory.REALISTIC_WOMAN,
                    resId = R.drawable.img_avatar_woman_1,
                    unlocked = true,
                    costTokens = 0,
                    nationality = "Spain",
                    countryFlag = "🇪🇸",
                    gender = "Girl",
                    artStyle = "Realistic Oil Canvas",
                    playStyle = "Tactical Virtuoso",
                    biography = "Renowned international master celebrated for dynamic pawn play.",
                    quote = "Every piece has a heart."
                ),
                AvatarItem(
                    id = "woman_portrait_2",
                    title = "Champion Elena",
                    category = AvatarCategory.REALISTIC_WOMAN,
                    resId = R.drawable.img_avatar_woman_2,
                    unlocked = true,
                    costTokens = 0,
                    nationality = "Ukraine",
                    countryFlag = "🇺🇦",
                    gender = "Girl",
                    artStyle = "Realistic Oil Canvas",
                    playStyle = "Endgame Sorceress",
                    biography = "World rapid silver medalist with flawless rook endgames.",
                    quote = "Precision over speed, always."
                ),

                // Custom Emoji Avatars
                AvatarItem(
                    id = "custom_golden_king",
                    title = "Golden Sovereign",
                    category = AvatarCategory.CUSTOM_AVATAR,
                    iconEmoji = "👑",
                    unlocked = true,
                    costTokens = 0,
                    nationality = "Royal",
                    countryFlag = "👑",
                    artStyle = "3D Custom Badge",
                    playStyle = "Royal Ascendance",
                    biography = "Mythic sovereign emblem for players who rule the chess realm.",
                    quote = "Long live the king."
                ),
                AvatarItem(
                    id = "custom_cyber_rook",
                    title = "Cyber Bastion",
                    category = AvatarCategory.CUSTOM_AVATAR,
                    iconEmoji = "🏰",
                    unlocked = true,
                    costTokens = 0,
                    nationality = "Cyber",
                    countryFlag = "⚡",
                    artStyle = "3D Custom Badge",
                    playStyle = "Impenetrable Fortress",
                    biography = "High-tech defense crest representing unstoppable rook corridors.",
                    quote = "Castling into eternity."
                ),
                AvatarItem(
                    id = "custom_shadow_knight",
                    title = "Shadow Stallion",
                    category = AvatarCategory.CUSTOM_AVATAR,
                    iconEmoji = "♞",
                    unlocked = true,
                    costTokens = 0,
                    nationality = "Stealth",
                    countryFlag = "🌑",
                    artStyle = "3D Custom Badge",
                    playStyle = "Fork Specialist",
                    biography = "Unpredictable knight maneuver emblem striking out of the shadows.",
                    quote = "Jump the ranks, seize the queen."
                ),
                AvatarItem(
                    id = "custom_mystic_bishop",
                    title = "Mystic Oracle",
                    category = AvatarCategory.CUSTOM_AVATAR,
                    iconEmoji = "🔮",
                    unlocked = true,
                    costTokens = 0,
                    nationality = "Arcane",
                    countryFlag = "✨",
                    artStyle = "3D Custom Badge",
                    playStyle = "Long Diagonal Sniper",
                    biography = "Arcane bishop targeting opposing kings across infinite diagonals.",
                    quote = "Vision beyond sight."
                ),
                AvatarItem(
                    id = "custom_cosmic_queen",
                    title = "Cosmic Empress",
                    category = AvatarCategory.CUSTOM_AVATAR,
                    iconEmoji = "⚡",
                    unlocked = true,
                    costTokens = 0,
                    nationality = "Cosmic",
                    countryFlag = "🌌",
                    artStyle = "3D Custom Badge",
                    playStyle = "Omnipresent Ruler",
                    biography = "The ultimate power across ranks, files, and diagonals.",
                    quote = "The board answers to me."
                ),
                AvatarItem(
                    id = "custom_valiant_pawn",
                    title = "Vanguard Hero",
                    category = AvatarCategory.CUSTOM_AVATAR,
                    iconEmoji = "🛡️",
                    unlocked = true,
                    costTokens = 0,
                    nationality = "Vanguard",
                    countryFlag = "⚔️",
                    artStyle = "3D Custom Badge",
                    playStyle = "Promotion Seeker",
                    biography = "The humble pawn destined to transform into a queen.",
                    quote = "One step forward at a time."
                )
            )
        }
    }
}
