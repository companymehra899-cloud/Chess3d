package com.onlinechessgame.app.chess.matchmaking

import com.onlinechessgame.app.R
import com.onlinechessgame.app.chess.model.AvatarCategory
import com.onlinechessgame.app.chess.model.AvatarItem
import com.onlinechessgame.app.chess.model.Country
import com.onlinechessgame.app.chess.model.OnlinePlayer
import kotlinx.coroutines.delay
import kotlin.random.Random

object MatchmakingEngine {

    val globalOpponents = listOf(
        OnlinePlayer(
            id = "bot_1",
            username = "Viktor_Strategist",
            country = Country("NO", "Norway", "🇳🇴"),
            rating = 1210,
            avatar = AvatarItem("gm_1", "Viktor", AvatarCategory.REALISTIC_MAN, resId = R.drawable.img_avatar_man_1),
            titleBadge = "Silver Tactician"
        ),
        OnlinePlayer(
            id = "bot_2",
            username = "Sofia_Gambit",
            country = Country("UA", "Ukraine", "🇺🇦"),
            rating = 1190,
            avatar = AvatarItem("gm_3", "Sofia", AvatarCategory.REALISTIC_WOMAN, resId = R.drawable.img_avatar_woman_1),
            titleBadge = "Gold Knight"
        ),
        OnlinePlayer(
            id = "bot_3",
            username = "Arjun_Tactics",
            country = Country("IN", "India", "🇮🇳"),
            rating = 1225,
            avatar = AvatarItem("gm_6", "Arjun", AvatarCategory.CUSTOM_AVATAR, iconEmoji = "⚡"),
            titleBadge = "Blitz Wizard"
        ),
        OnlinePlayer(
            id = "bot_4",
            username = "Marcus_Apex",
            country = Country("US", "United States", "🇺🇸"),
            rating = 1180,
            avatar = AvatarItem("gm_2", "Marcus", AvatarCategory.REALISTIC_MAN, resId = R.drawable.img_avatar_man_2),
            titleBadge = "Silver Strategist"
        ),
        OnlinePlayer(
            id = "bot_5",
            username = "Elena_Defense",
            country = Country("DE", "Germany", "🇩🇪"),
            rating = 1240,
            avatar = AvatarItem("gm_5", "Elena", AvatarCategory.REALISTIC_WOMAN, resId = R.drawable.img_avatar_woman_2),
            titleBadge = "Diamond Defender"
        ),
        OnlinePlayer(
            id = "bot_6",
            username = "Ren_Senshi",
            country = Country("JP", "Japan", "🇯🇵"),
            rating = 1205,
            avatar = AvatarItem("custom_knight", "Ren", AvatarCategory.CUSTOM_AVATAR, iconEmoji = "♞"),
            titleBadge = "Grand Strategist"
        ),
        OnlinePlayer(
            id = "bot_7",
            username = "Lucas_Rook",
            country = Country("BR", "Brazil", "🇧🇷"),
            rating = 1195,
            avatar = AvatarItem("custom_castle", "Lucas", AvatarCategory.CUSTOM_AVATAR, iconEmoji = "🏰"),
            titleBadge = "Fortress Master"
        )
    )

    fun findRandomOpponent(playerRating: Int, realUsers: List<OnlinePlayer> = emptyList()): OnlinePlayer {
        if (realUsers.isNotEmpty()) {
            return realUsers.random()
        }
        val opponent = globalOpponents.random()
        // Adjust opponent rating slightly close to player's rating for fair competitive matchmaking
        val adjustedRating = (playerRating + Random.nextInt(-45, 46)).coerceAtLeast(600)
        return opponent.copy(rating = adjustedRating)
    }

    fun getOpponentChatReply(playerMsg: String, opponentName: String): String {
        val lower = playerMsg.lowercase()
        return when {
            lower.contains("good luck") || lower.contains("luck") -> "You too! May the best player win ♟️"
            lower.contains("nice move") || lower.contains("good move") -> "Thank you! You're playing very solid 👏"
            lower.contains("checkmate") -> "Not so fast, I still have tricks up my sleeve! ⚔️"
            lower.contains("well played") || lower.contains("gg") -> "Great game! Truly enjoyed playing with you 🤝"
            lower.contains("oops") || lower.contains("blunder") -> "Chess happens! Keep fighting ♟️"
            lower.contains("rematch") -> "I'd love a rematch right after this! 🔥"
            lower.contains("thanks") || lower.contains("thank") -> "Anytime! Focus on the board now 😄"
            lower.contains("interesting") || lower.contains("tactic") -> "Calculated this variation deep! 🤔"
            else -> "Playing with focus! Let's see how this develops 👑"
        }
    }
}
