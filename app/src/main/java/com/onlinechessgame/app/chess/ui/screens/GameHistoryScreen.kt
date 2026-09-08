package com.onlinechessgame.app.chess.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.onlinechessgame.app.chess.data.local.MatchHistoryEntity
import com.onlinechessgame.app.chess.model.Country
import com.onlinechessgame.app.chess.model.OnlinePlayer
import com.onlinechessgame.app.chess.ui.components.MatchDetailsDialog
import com.onlinechessgame.app.chess.ui.viewmodel.AppTab
import com.onlinechessgame.app.chess.ui.viewmodel.ChessViewModel
import com.onlinechessgame.app.chess.ui.viewmodel.HistoryFilter

@Composable
fun GameHistoryScreen(
    viewModel: ChessViewModel,
    modifier: Modifier = Modifier
) {
    val matches by viewModel.matchHistoryList.collectAsState()
    val filter by viewModel.historyFilter.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    var selectedMatchForDetails by remember { mutableStateOf<MatchHistoryEntity?>(null) }

    val filteredMatches = remember(matches, filter) {
        when (filter) {
            HistoryFilter.ALL -> matches
            HistoryFilter.WINS -> matches.filter { it.result == "WIN" }
            HistoryFilter.LOSSES -> matches.filter { it.result == "LOSS" }
            HistoryFilter.DRAWS -> matches.filter { it.result == "DRAW" }
        }
    }

    val totalWins = matches.count { it.result == "WIN" }
    val totalLosses = matches.count { it.result == "LOSS" }
    val totalDraws = matches.count { it.result == "DRAW" }
    val winRate = if (matches.isNotEmpty()) {
        ((totalWins.toFloat() / matches.size) * 100).toInt()
    } else 0

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F172A),
                        Color(0xFF0B0F19),
                        Color(0xFF05070D)
                    )
                )
            )
            .testTag("game_history_screen")
    ) {
        // Header
        Surface(
            color = Color(0xFF1E293B).copy(alpha = 0.7f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Game History & Overview",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Review completed matches and career stats",
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Career Summary Card
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StatColumn(label = "Matches", value = "${matches.size}", color = Color.White)
                        StatColumn(label = "Wins", value = "$totalWins", color = Color(0xFF34D399))
                        StatColumn(label = "Losses", value = "$totalLosses", color = Color(0xFFF87171))
                        StatColumn(label = "Draws", value = "$totalDraws", color = Color(0xFF94A3B8))
                        StatColumn(label = "Win Rate", value = "$winRate%", color = Color(0xFF38BDF8))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Filter Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = filter == HistoryFilter.ALL,
                        onClick = { viewModel.setHistoryFilter(HistoryFilter.ALL) },
                        label = { Text("All (${matches.size})", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF2563EB),
                            selectedLabelColor = Color.White,
                            containerColor = Color(0xFF0F172A),
                            labelColor = Color(0xFF94A3B8)
                        ),
                        modifier = Modifier.testTag("filter_all")
                    )
                    FilterChip(
                        selected = filter == HistoryFilter.WINS,
                        onClick = { viewModel.setHistoryFilter(HistoryFilter.WINS) },
                        label = { Text("Wins ($totalWins)", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF059669),
                            selectedLabelColor = Color.White,
                            containerColor = Color(0xFF0F172A),
                            labelColor = Color(0xFF94A3B8)
                        ),
                        modifier = Modifier.testTag("filter_wins")
                    )
                    FilterChip(
                        selected = filter == HistoryFilter.LOSSES,
                        onClick = { viewModel.setHistoryFilter(HistoryFilter.LOSSES) },
                        label = { Text("Losses ($totalLosses)", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFDC2626),
                            selectedLabelColor = Color.White,
                            containerColor = Color(0xFF0F172A),
                            labelColor = Color(0xFF94A3B8)
                        ),
                        modifier = Modifier.testTag("filter_losses")
                    )
                    FilterChip(
                        selected = filter == HistoryFilter.DRAWS,
                        onClick = { viewModel.setHistoryFilter(HistoryFilter.DRAWS) },
                        label = { Text("Draws ($totalDraws)", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF475569),
                            selectedLabelColor = Color.White,
                            containerColor = Color(0xFF0F172A),
                            labelColor = Color(0xFF94A3B8)
                        ),
                        modifier = Modifier.testTag("filter_draws")
                    )
                }
            }
        }

        // Match List or Empty State
        if (filteredMatches.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "📜", fontSize = 48.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "No Matches Found",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Play competitive online 3D chess matches to build your match history and track your progress!",
                    color = Color(0xFF94A3B8),
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = { viewModel.setTab(AppTab.PLAY) },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                    modifier = Modifier.testTag("play_match_from_history_button")
                ) {
                    Text("Play a Match Now ⚔️", fontWeight = FontWeight.Bold)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredMatches, key = { it.matchId }) { match ->
                    MatchCard(
                        match = match,
                        onClick = { selectedMatchForDetails = match }
                    )
                }
            }
        }
    }

    // Details / Move Review Dialog
    selectedMatchForDetails?.let { match ->
        MatchDetailsDialog(
            opponentName = match.opponentName,
            opponentFlag = match.opponentCountryFlag,
            opponentRating = match.opponentRating,
            result = match.result,
            pointsDelta = match.pointsDelta,
            tokensEarned = match.tokensEarned,
            totalMoves = match.totalMoves,
            gameMode = match.gameMode,
            movesSummary = match.movesSummary,
            onDismiss = { selectedMatchForDetails = null },
            onChallengeRematch = {
                selectedMatchForDetails = null
                val avatar = com.onlinechessgame.app.chess.data.repository.ChessRepository.getAvailableAvatars().first()
                val opponent = OnlinePlayer(
                    id = "match_${match.matchId}",
                    username = match.opponentName,
                    country = Country("XX", "Opponent", match.opponentCountryFlag),
                    rating = match.opponentRating,
                    avatar = avatar,
                    titleBadge = "Rematch Challenger"
                )
                viewModel.setTab(AppTab.PLAY)
                viewModel.startDirectGameWithOpponent(opponent)
            }
        )
    }
}

@Composable
private fun StatColumn(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            color = color,
            fontWeight = FontWeight.Black,
            fontSize = 17.sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = Color(0xFF94A3B8),
            fontSize = 11.sp
        )
    }
}

@Composable
private fun MatchCard(
    match: MatchHistoryEntity,
    onClick: () -> Unit
) {
    val isWin = match.result == "WIN"
    val isLoss = match.result == "LOSS"

    val resultBadgeColor = when {
        isWin -> Color(0xFF10B981)
        isLoss -> Color(0xFFEF4444)
        else -> Color(0xFF64748B)
    }

    val resultText = when {
        isWin -> "VICTORY"
        isLoss -> "DEFEAT"
        else -> "DRAW"
    }

    val pointsText = when {
        match.pointsDelta > 0 -> "+${match.pointsDelta} pts"
        match.pointsDelta < 0 -> "${match.pointsDelta} pts"
        else -> "0 pts"
    }

    val formattedTime = remember(match.timestamp) {
        val diffMs = System.currentTimeMillis() - match.timestamp
        val minutes = diffMs / (60 * 1000)
        val hours = diffMs / (3600 * 1000)
        val days = diffMs / (24 * 3600 * 1000)

        when {
            minutes < 1 -> "Just now"
            minutes < 60 -> "${minutes}m ago"
            hours < 24 -> "${hours}h ago"
            days == 1L -> "Yesterday"
            else -> "${days}d ago"
        }
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("match_item_${match.matchId}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Result Icon/Badge
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = resultBadgeColor.copy(alpha = 0.15f),
                border = androidx.compose.foundation.BorderStroke(1.dp, resultBadgeColor.copy(alpha = 0.5f)),
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = when {
                            isWin -> "🏆"
                            isLoss -> "♟️"
                            else -> "🤝"
                        },
                        fontSize = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Opponent & Match Details
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = resultText,
                        color = resultBadgeColor,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 12.sp,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• $pointsText",
                        color = if (isWin) Color(0xFF34D399) else if (isLoss) Color(0xFFF87171) else Color(0xFF94A3B8),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    val tokensText = when {
                        match.tokensEarned > 0 -> "+${match.tokensEarned} 🪙"
                        match.tokensEarned < 0 -> "${match.tokensEarned} 🪙"
                        else -> "0 🪙"
                    }
                    val tokensColor = when {
                        match.tokensEarned > 0 -> Color(0xFF34D399)
                        match.tokensEarned < 0 -> Color(0xFFF87171)
                        else -> Color(0xFF94A3B8)
                    }
                    Text(
                        text = tokensText,
                        color = tokensColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = match.opponentCountryFlag, fontSize = 15.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = match.opponentName,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "(${match.opponentRating})",
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = "${match.gameMode} • ${match.totalMoves} moves • $formattedTime",
                    color = Color(0xFF64748B),
                    fontSize = 11.sp
                )
            }

            // Arrow to review
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Review moves",
                tint = Color(0xFF64748B),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
