package com.onlinechessgame.app.chess.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: String = "guest_user_1",
    val username: String = "PlayerOne",
    val isGuest: Boolean = true,
    val level: Int = 15,
    val xp: Int = 1200,
    val maxXp: Int = 2000,
    val gems: Int = 650,
    val ratingPoints: Int = 1200,
    val tokens: Int = 1000, // Free bonus 1000 tokens for every user!
    val wins: Int = 0,
    val losses: Int = 0,
    val draws: Int = 0,
    val puzzlesSolved: Int = 0,
    val countryCode: String = "US",
    val countryName: String = "United States",
    val countryFlag: String = "🇺🇸",
    val selectedAvatarId: String = "champion_boy",
    val unlockedAvatarIds: String = "champion_boy,champion_girl,cool_guy,explorer_boy,scholar_girl,master_aarav,master_lukas,master_chloe,master_ethan,imperial_empress,cyber_valkyrie,celestial_queen,stylish_man_free,modern_woman_free,british_woman_free,simple_man_free,champion_man_free,fox_tactician,white_tiger,free_panda,free_golden_prince,free_royal_princess,free_brave_pawn,free_wise_owl,man_portrait_1,man_portrait_2,woman_portrait_1,woman_portrait_2,oil_india_boy,oil_africa_boy,oil_german_boy,oil_american_boy,oil_american_girl,oil_england_boy,oil_england_girl,oil_australia_boy,oil_australia_girl,oil_europe_boy,oil_europe_girl,custom_golden_king,custom_cyber_rook,custom_shadow_knight,custom_mystic_bishop,custom_cosmic_queen,custom_valiant_pawn",
    val customAvatarTitle: String = "The Strategist",
    val boardTheme: String = "SIMPLE_GREEN_BUFF",
    val pieceStyle: String = "TOURNAMENT_PLASTIC",
    val pieceColor: String = "CLASSIC",
    val soundEffects: Boolean = true,
    val hapticFeedback: Boolean = true,
    val autoQueenPromotion: Boolean = true
)

@Entity(tableName = "match_history")
data class MatchHistoryEntity(
    @PrimaryKey(autoGenerate = true) val matchId: Long = 0,
    val opponentName: String,
    val opponentCountryFlag: String,
    val opponentRating: Int,
    val result: String, // "WIN", "LOSS", "DRAW"
    val pointsDelta: Int, // +10 or -10 or 0
    val tokensEarned: Int,
    val totalMoves: Int,
    val gameMode: String = "Rapid (3 min)",
    val movesSummary: String = "1. e4 e5 2. Nf3 Nc6 3. Bc4 Bc5",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_accounts")
data class UserAccountEntity(
    @PrimaryKey val username: String, // Stored in lower case for unique lookup
    val displayName: String,
    val passwordHash: String,
    val countryCode: String = "US",
    val countryName: String = "United States",
    val countryFlag: String = "🇺🇸",
    val avatarId: String = "man_portrait_1",
    val ratingPoints: Int = 1200,
    val tokens: Int = 1000,
    val gems: Int = 650,
    val level: Int = 15,
    val xp: Int = 1200,
    val wins: Int = 0,
    val losses: Int = 0,
    val draws: Int = 0,
    val puzzlesSolved: Int = 0,
    val unlockedAvatarIds: String = "champion_boy,champion_girl,cool_guy,explorer_boy,scholar_girl,master_aarav,master_lukas,master_chloe,master_ethan,imperial_empress,cyber_valkyrie,celestial_queen,stylish_man_free,modern_woman_free,british_woman_free,simple_man_free,champion_man_free,fox_tactician,white_tiger,free_panda,free_golden_prince,free_royal_princess,free_brave_pawn,free_wise_owl,man_portrait_1,man_portrait_2,woman_portrait_1,woman_portrait_2,oil_india_boy,oil_africa_boy,oil_german_boy,oil_american_boy,oil_american_girl,oil_england_boy,oil_england_girl,oil_australia_boy,oil_australia_girl,oil_europe_boy,oil_europe_girl,custom_golden_king,custom_cyber_rook,custom_shadow_knight,custom_mystic_bishop,custom_cosmic_queen,custom_valiant_pawn",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "friends")
data class FriendEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val username: String,
    val countryFlag: String = "🌐",
    val countryName: String = "International",
    val rating: Int = 1200,
    val avatarId: String = "man_portrait_1",
    val status: String = "ONLINE", // "ONLINE", "IN_GAME", "OFFLINE"
    val requestStatus: String = "ACCEPTED", // "ACCEPTED", "PENDING_INCOMING", "PENDING_OUTGOING"
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "matchmaking_queue")
data class MatchmakingQueueEntity(
    @PrimaryKey val userId: String,
    val username: String,
    val rating: Int,
    val countryCode: String,
    val countryName: String,
    val countryFlag: String,
    val avatarId: String,
    val timestamp: Long = System.currentTimeMillis()
)

