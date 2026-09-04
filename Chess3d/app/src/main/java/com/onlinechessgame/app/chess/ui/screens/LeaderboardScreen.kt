package com.onlinechessgame.app.chess.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.onlinechessgame.app.chess.data.repository.ChessRepository
import com.onlinechessgame.app.chess.ui.leaderboard.LeaderboardData
import com.onlinechessgame.app.chess.ui.leaderboard.LeaderboardPlayer
import com.onlinechessgame.app.chess.ui.viewmodel.ChessViewModel

@Composable
fun LeaderboardScreen(
    viewModel: ChessViewModel,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.userProfile.collectAsState()

    val userAvatar = remember(profile?.selectedAvatarId) {
        ChessRepository.getAvailableAvatars().find { it.id == profile?.selectedAvatarId }
            ?: ChessRepository.getAvailableAvatars().first()
    }

    // Stable, non-fluctuating container with static background
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0B0F17))
            .testTag("leaderboard_screen")
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Pinned Top Section - Header & User Standing stay completely fixed
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 12.dp, bottom = 6.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Header Title
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = Color(0xFFFBBF24),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Global Leadership",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "Worldwide Ranked Grandmasters",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp
                        )
                    }
                }

                // Current User Standing Highlight Card (Pinned so it never shakes or scrolls out)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("user_leaderboard_standing_card"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF111A2E)),
                    border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFF1E3A8A).copy(alpha = 0.8f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            // Prominent Rank Box: "RANK" on top, number underneath
                            Column(
                                modifier = Modifier
                                    .width(62.dp)
                                    .height(44.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF0C1A30))
                                    .border(1.2.dp, Color(0xFF38BDF8).copy(alpha = 0.7f), RoundedCornerShape(8.dp)),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "RANK",
                                    color = Color(0xFF38BDF8),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.8.sp
                                )
                                Text(
                                    text = "#1,420",
                                    color = Color.White,
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF1E293B))
                                    .border(1.2.dp, Color(0xFF38BDF8), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                if (userAvatar.resId != null) {
                                    Image(
                                        painter = painterResource(id = userAvatar.resId),
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Text(userAvatar.iconEmoji.ifEmpty { "♟️" }, fontSize = 18.sp)
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = profile?.username ?: "Guest Player",
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.5.sp
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(profile?.countryFlag ?: "🇺🇸", fontSize = 12.5.sp)
                            }
                        }

                        // Top Right Rating Badge
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF0C1322),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.45f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Color(0xFFFBBF24),
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${profile?.ratingPoints ?: 1200}",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.5.sp
                                )
                            }
                        }
                    }
                }

                // Section Title
                Text(
                    text = "Top Grandmasters",
                    color = Color(0xFFE2E8F0),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            }

            // Scrollable Leaderboard List - Smooth, isolated scrolling without moving background or header
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 2.dp,
                    bottom = 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(9.dp)
            ) {
                items(LeaderboardData.topPlayers, key = { it.rank }) { player ->
                    LeaderboardItemRow(player = player)
                }
            }
        }
    }
}

@Composable
private fun LeaderboardItemRow(player: LeaderboardPlayer) {
    val rankGradient = when (player.rank) {
        1 -> Brush.verticalGradient(listOf(Color(0xFFD97706), Color(0xFF78350F)))
        2 -> Brush.verticalGradient(listOf(Color(0xFF64748B), Color(0xFF334155)))
        3 -> Brush.verticalGradient(listOf(Color(0xFFB45309), Color(0xFF451A03)))
        else -> Brush.verticalGradient(listOf(Color(0xFF1E293B), Color(0xFF0F172A)))
    }
    val rankBorderColor = when (player.rank) {
        1 -> Color(0xFFFBBF24)
        2 -> Color(0xFFCBD5E1)
        3 -> Color(0xFFF59E0B)
        else -> Color(0xFF334155)
    }
    val rankHeaderColor = when (player.rank) {
        1 -> Color(0xFFFEF08A)
        2 -> Color(0xFFF1F5F9)
        3 -> Color(0xFFFDE68A)
        else -> Color(0xFF38BDF8)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("leaderboard_row_${player.rank}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF111A2E)),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (player.rank <= 3) rankBorderColor.copy(alpha = 0.55f) else Color(0xFF1E293B)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Rank + Avatar + Name
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Prominent Rank or Trophy
                if (player.rank <= 3) {
                    Box(
                        modifier = Modifier
                            .width(48.dp)
                            .height(44.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "Trophy",
                            tint = when (player.rank) {
                                1 -> Color(0xFFFFD700) // Gold
                                2 -> Color(0xFFC0C0C0) // Silver
                                else -> Color(0xFFCD7F32) // Bronze
                            },
                            modifier = Modifier.size(36.dp)
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .width(48.dp)
                            .height(44.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(rankGradient)
                            .border(
                                1.2.dp,
                                rankBorderColor.copy(alpha = if (player.rank <= 3) 0.95f else 0.45f),
                                RoundedCornerShape(8.dp)
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "RANK",
                            color = rankHeaderColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.8.sp
                        )
                        Text(
                            text = "${player.rank}",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(9.dp))

                // Avatar
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1E293B))
                        .border(1.2.dp, rankBorderColor.copy(alpha = 0.8f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (player.avatar.resId != null) {
                        Image(
                            painter = painterResource(id = player.avatar.resId),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text(player.avatar.iconEmoji.ifEmpty { "♟️" }, fontSize = 18.sp)
                    }
                }

                Spacer(modifier = Modifier.width(9.dp))

                // Player Name & Title & Country
                Column(verticalArrangement = Arrangement.Center) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = player.username,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.5.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(player.country.flag, fontSize = 12.sp)
                    }
                    Text(
                        text = player.titleBadge,
                        color = if (player.rank <= 3) Color(0xFFFBBF24) else Color(0xFF94A3B8),
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Rating - Clean number with star
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFF0C1322),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFBBF24),
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "${player.ratingPoints}",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
