package com.onlinechessgame.app.chess.ui.leaderboard

import com.onlinechessgame.app.R
import com.onlinechessgame.app.chess.model.AvatarCategory
import com.onlinechessgame.app.chess.model.AvatarItem
import com.onlinechessgame.app.chess.model.Country

object LeaderboardData {
    val topPlayers: List<LeaderboardPlayer> = listOf(
        LeaderboardPlayer(
            rank = 1,
            username = "Arav_Sharma",
            country = Country("IN", "India", "🇮🇳"),
            ratingPoints = 2882,
            wins = 412,
            titleBadge = "Grandmaster",
            avatar = AvatarItem(
                id = "gm_1",
                title = "Arav",
                category = AvatarCategory.REALISTIC_MAN,
                resId = R.drawable.img_avatar_man_1
            )
        ),
        LeaderboardPlayer(
            rank = 2,
            username = "Vikram_Singh",
            country = Country("IN", "India", "🇮🇳"),
            ratingPoints = 2815,
            wins = 385,
            titleBadge = "Grandmaster",
            avatar = AvatarItem(
                id = "gm_2",
                title = "Vikram",
                category = AvatarCategory.REALISTIC_MAN,
                resId = R.drawable.img_avatar_man_2
            )
        ),
        LeaderboardPlayer(
            rank = 3,
            username = "Priya_Patel",
            country = Country("IN", "India", "🇮🇳"),
            ratingPoints = 2795,
            wins = 360,
            titleBadge = "Grandmaster",
            avatar = AvatarItem(
                id = "gm_3",
                title = "Priya",
                category = AvatarCategory.REALISTIC_WOMAN,
                resId = R.drawable.img_avatar_woman_1
            )
        ),
        LeaderboardPlayer(
            rank = 4,
            username = "Michael_Brown",
            country = Country("US", "United States", "🇺🇸"),
            ratingPoints = 2780,
            wins = 345,
            titleBadge = "Grandmaster",
            avatar = AvatarItem(
                id = "gm_4",
                title = "Michael",
                category = AvatarCategory.CUSTOM_AVATAR,
                iconEmoji = "👑"
            )
        ),
        LeaderboardPlayer(
            rank = 5,
            username = "Sarah_Williams",
            country = Country("UK", "United Kingdom", "🇬🇧"),
            ratingPoints = 2740,
            wins = 320,
            titleBadge = "International Master",
            avatar = AvatarItem(
                id = "gm_5",
                title = "Sarah",
                category = AvatarCategory.REALISTIC_WOMAN,
                resId = R.drawable.img_avatar_woman_2
            )
        ),
        LeaderboardPlayer(
            rank = 6,
            username = "Aryan_Mehta",
            country = Country("IN", "India", "🇮🇳"),
            ratingPoints = 2730,
            wins = 310,
            titleBadge = "Grandmaster",
            avatar = AvatarItem(
                id = "gm_6",
                title = "Aryan",
                category = AvatarCategory.CUSTOM_AVATAR,
                iconEmoji = "⚡"
            )
        ),
        LeaderboardPlayer(
            rank = 7,
            username = "Robert_Johnson",
            country = Country("FR", "France", "🇫🇷"),
            ratingPoints = 2690,
            wins = 295,
            titleBadge = "Master",
            avatar = AvatarItem(
                id = "gm_7",
                title = "Robert",
                category = AvatarCategory.REALISTIC_MAN,
                resId = R.drawable.img_avatar_man_1
            )
        ),
        LeaderboardPlayer(
            rank = 8,
            username = "Tanaka_Hiro",
            country = Country("JP", "Japan", "🇯🇵"),
            ratingPoints = 2650,
            wins = 280,
            titleBadge = "Master",
            avatar = AvatarItem(
                id = "gm_8",
                title = "Tanaka",
                category = AvatarCategory.CUSTOM_AVATAR,
                iconEmoji = "🏯"
            )
        ),
        LeaderboardPlayer(
            rank = 9,
            username = "Diego_Rodriguez",
            country = Country("BR", "Brazil", "🇧🇷"),
            ratingPoints = 2610,
            wins = 265,
            titleBadge = "Diamond Master",
            avatar = AvatarItem(
                id = "gm_9",
                title = "Diego",
                category = AvatarCategory.CUSTOM_AVATAR,
                iconEmoji = "♞"
            )
        ),
        LeaderboardPlayer(
            rank = 10,
            username = "Anna_Ivanova",
            country = Country("PL", "Poland", "🇵🇱"),
            ratingPoints = 2580,
            wins = 240,
            titleBadge = "Diamond Master",
            avatar = AvatarItem(
                id = "gm_10",
                title = "Anna",
                category = AvatarCategory.REALISTIC_WOMAN,
                resId = R.drawable.img_avatar_woman_1
            )
        )
    )
}
