package com.onlinechessgame.app.chess.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.window.Dialog
import com.onlinechessgame.app.chess.model.GameStatus
import com.onlinechessgame.app.chess.model.PieceColor
import com.onlinechessgame.app.chess.model.PieceType

@Composable
fun PawnPromotionDialog(
    color: PieceColor,
    onSelect: (PieceType) -> Unit
) {
    Dialog(onDismissRequest = { /* Must select */ }) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF38BDF8)),
            modifier = Modifier.testTag("pawn_promotion_dialog")
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Promote Your Pawn",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Choose your desired piece:",
                    color = Color(0xFF94A3B8),
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    val pieces = listOf(
                        PieceType.QUEEN to "Queen",
                        PieceType.ROOK to "Rook",
                        PieceType.BISHOP to "Bishop",
                        PieceType.KNIGHT to "Knight"
                    )

                    for ((pType, name) in pieces) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF0F172A),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF475569)),
                            modifier = Modifier
                                .testTag("promo_$name")
                                .clickable { onSelect(pType) }
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                ChessPiece3D(
                                    type = pType,
                                    color = color,
                                    modifier = Modifier.size(44.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = name,
                                    color = Color(0xFFE2E8F0),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GameEndResultDialog(
    status: GameStatus,
    playerColor: PieceColor,
    opponentName: String,
    onRematch: () -> Unit,
    onMenu: () -> Unit,
    onAddFriend: (() -> Unit)? = null,
    isFriendRequestSent: Boolean = false
) {
    val isPlayerWinner = (status == GameStatus.WHITE_WON && playerColor == PieceColor.WHITE) ||
            (status == GameStatus.BLACK_WON && playerColor == PieceColor.BLACK)
    val isPlayerLoser = (status == GameStatus.WHITE_WON && playerColor == PieceColor.BLACK) ||
            (status == GameStatus.BLACK_WON && playerColor == PieceColor.WHITE)
    val isDraw = !isPlayerWinner && !isPlayerLoser

    val title = when {
        isPlayerWinner -> "VICTORY! 🏆"
        isPlayerLoser -> "DEFEAT ♟️"
        else -> "DRAW 🤝"
    }

    val subtitle = when (status) {
        GameStatus.WHITE_WON -> "Checkmate - White wins"
        GameStatus.BLACK_WON -> "Checkmate - Black wins"
        GameStatus.DRAW_STALEMATE -> "Draw by Stalemate"
        GameStatus.DRAW_INSUFFICIENT_MATERIAL -> "Draw by Insufficient Material"
        GameStatus.DRAW_50_MOVES -> "Draw by 50-Move Rule"
        GameStatus.DRAW_AGREED -> "Draw by Mutual Agreement"
        else -> "Game Concluded"
    }

    val pointsText = when {
        isPlayerWinner -> "+10 Points"
        isPlayerLoser -> "-10 Points"
        else -> "0 Points"
    }

    val tokensText = when {
        isPlayerWinner -> "+50 Tokens Bonus"
        isPlayerLoser -> "-25 Tokens Loss"
        else -> "0 Tokens"
    }

    Dialog(onDismissRequest = onMenu) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            border = androidx.compose.foundation.BorderStroke(
                2.dp,
                if (isPlayerWinner) Color(0xFF10B981) else if (isPlayerLoser) Color(0xFFEF4444) else Color(0xFF38BDF8)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("game_end_dialog")
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    color = if (isPlayerWinner) Color(0xFF34D399) else if (isPlayerLoser) Color(0xFFF87171) else Color(0xFF67E8F9),
                    fontWeight = FontWeight.Black,
                    fontSize = 26.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    color = Color(0xFF94A3B8),
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Rewards Card
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF1E293B),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Rating Change:", color = Color(0xFFCBD5E1), fontSize = 13.sp)
                            Text(
                                text = pointsText,
                                color = if (isPlayerWinner) Color(0xFF34D399) else if (isPlayerLoser) Color(0xFFF87171) else Color(0xFFCBD5E1),
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Token Change:", color = Color(0xFFCBD5E1), fontSize = 13.sp)
                            Text(
                                text = tokensText,
                                color = if (isPlayerWinner) Color(0xFF34D399) else if (isPlayerLoser) Color(0xFFF87171) else Color(0xFFFBBF24),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Add Opponent as Friend Button
                if (onAddFriend != null) {
                    Button(
                        onClick = onAddFriend,
                        enabled = !isFriendRequestSent,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("add_opponent_friend_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isFriendRequestSent) Color(0xFF065F46) else Color(0xFF1E293B),
                            contentColor = if (isFriendRequestSent) Color(0xFF6EE7B7) else Color(0xFF38BDF8)
                        )
                    ) {
                        Text(
                            text = if (isFriendRequestSent) "✓ Friend Request Sent" else "👥 Add $opponentName as Friend",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Actions
                Button(
                    onClick = onRematch,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("rematch_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isPlayerWinner) Color(0xFF10B981) else Color(0xFF2563EB)
                    )
                ) {
                    Text("Play Next Match", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = onMenu,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("back_to_menu_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF94A3B8))
                ) {
                    Text("Return to Menu", fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
fun MatchDetailsDialog(
    opponentName: String,
    opponentFlag: String,
    opponentRating: Int,
    result: String,
    pointsDelta: Int,
    tokensEarned: Int,
    totalMoves: Int,
    gameMode: String,
    movesSummary: String = "",
    onDismiss: () -> Unit,
    onChallengeRematch: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF334155)),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("match_details_dialog")
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Match Details",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    val badgeColor = when (result) {
                        "WIN" -> Color(0xFF10B981)
                        "LOSS" -> Color(0xFFEF4444)
                        else -> Color(0xFF64748B)
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = badgeColor.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, badgeColor)
                    ) {
                        Text(
                            text = result,
                            color = badgeColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Opponent Card
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF1E293B),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = opponentFlag, fontSize = 28.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = opponentName,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "Rating: $opponentRating",
                                color = Color(0xFF94A3B8),
                                fontSize = 12.sp
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = if (pointsDelta > 0) "+$pointsDelta pts" else "$pointsDelta pts",
                                color = if (pointsDelta > 0) Color(0xFF34D399) else if (pointsDelta < 0) Color(0xFFF87171) else Color(0xFF94A3B8),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = if (tokensEarned > 0) "+$tokensEarned 🪙" else if (tokensEarned < 0) "$tokensEarned 🪙" else "0 🪙",
                                color = if (tokensEarned > 0) Color(0xFF34D399) else if (tokensEarned < 0) Color(0xFFF87171) else Color(0xFFFBBF24),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Match Information Card (No raw game logic or algebraic notations)
                val badgeColor = when (result) {
                    "WIN" -> Color(0xFF10B981)
                    "LOSS" -> Color(0xFFEF4444)
                    else -> Color(0xFF64748B)
                }
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF1E293B).copy(alpha = 0.6f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Game Mode", color = Color(0xFF94A3B8), fontSize = 13.sp)
                            Text(
                                text = gameMode,
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Total Moves Played", color = Color(0xFF94A3B8), fontSize = 13.sp)
                            Text(
                                text = "$totalMoves Moves",
                                color = Color(0xFF38BDF8),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Match Outcome", color = Color(0xFF94A3B8), fontSize = 13.sp)
                            val outcomeText = when (result) {
                                "WIN" -> "Victory"
                                "LOSS" -> "Defeat"
                                else -> "Draw"
                            }
                            Text(
                                text = outcomeText,
                                color = badgeColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Actions
                Button(
                    onClick = onChallengeRematch,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("challenge_rematch_history_button")
                ) {
                    Text("Challenge to Match ⚔️", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF94A3B8)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                ) {
                    Text("Close", fontSize = 13.sp)
                }
            }
        }
    }
}
