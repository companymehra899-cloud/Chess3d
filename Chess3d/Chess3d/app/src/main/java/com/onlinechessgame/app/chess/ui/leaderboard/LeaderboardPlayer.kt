package com.onlinechessgame.app.chess.ui.leaderboard

import com.onlinechessgame.app.chess.model.AvatarItem
import com.onlinechessgame.app.chess.model.Country

data class LeaderboardPlayer(
    val rank: Int,
    val username: String,
    val country: Country,
    val ratingPoints: Int,
    val wins: Int,
    val titleBadge: String,
    val avatar: AvatarItem
)
