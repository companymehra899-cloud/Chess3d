package com.onlinechessgame.app.chess.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.onlinechessgame.app.chess.model.AvatarItem
import com.onlinechessgame.app.chess.model.Piece
import com.onlinechessgame.app.chess.model.PieceColor

@Composable
fun PlayerCard3D(
    username: String,
    countryFlag: String,
    rating: Int,
    avatar: AvatarItem,
    isCurrentTurn: Boolean,
    timeRemainingSeconds: Int,
    capturedPieces: List<Piece>,
    playerColor: PieceColor,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "turn_pulse")
    val borderAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    val activeBorderBrush = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFFFBBF24).copy(alpha = borderAlpha),
            Color(0xFF10B981).copy(alpha = borderAlpha),
            Color(0xFFFBBF24).copy(alpha = borderAlpha)
        )
    )

    val containerColor = if (isCurrentTurn) Color(0xFF1E293B) else Color(0xFF0F172A)
    
    val borderModifier = if (isCurrentTurn) {
        Modifier.border(2.dp, activeBorderBrush, RoundedCornerShape(14.dp))
    } else {
        Modifier.border(1.dp, Color(0xFF334155), RoundedCornerShape(14.dp))
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(borderModifier)
            .testTag("player_card_$username"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isCurrentTurn) 6.dp else 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Avatar Circular Portrait
            AvatarCircularPortrait(
                avatar = avatar,
                isEquipped = true,
                isLocked = false,
                hasGlowSunburst = isCurrentTurn,
                hasGreenSparkle = isCurrentTurn,
                sizeDp = 50,
                modifier = Modifier.size(50.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // 2. Info details
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = username,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    
                    Spacer(modifier = Modifier.width(6.dp))
                    
                    // Country Flag Badge
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = countryFlag,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Rating badge
                    Surface(
                        color = Color(0xFF334155),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = rating.toString(),
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Captured pieces or status text
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    if (capturedPieces.isEmpty()) {
                        Text(
                            text = if (playerColor == PieceColor.WHITE) "White (Playing)" else "Black (Playing)",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )
                    } else {
                        val displayList = capturedPieces.take(7)
                        for (cPiece in displayList) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .padding(horizontal = 0.5.dp)
                            ) {
                                ChessPiece3D(
                                    type = cPiece.type,
                                    color = cPiece.color,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                        if (capturedPieces.size > 7) {
                            Text(
                                text = "+${capturedPieces.size - 7}",
                                color = Color(0xFFFBBF24),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(start = 2.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // 3. Timer Component
            val minutes = timeRemainingSeconds / 60
            val seconds = timeRemainingSeconds % 60
            val timeFormatted = String.format("%02d:%02d", minutes, seconds)
            val isLowTime = timeRemainingSeconds <= 30

            val timerBgColor = when {
                isLowTime && isCurrentTurn -> Color(0xFFEF4444).copy(alpha = 0.3f)
                isCurrentTurn -> Color(0xFF3B82F6).copy(alpha = 0.25f)
                else -> Color(0xFF1E293B)
            }

            val timerBorderColor = when {
                isLowTime && isCurrentTurn -> Color(0xFFEF4444)
                isCurrentTurn -> Color(0xFF3B82F6)
                else -> Color(0xFF475569)
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = timerBgColor,
                border = BorderStroke(1.dp, timerBorderColor),
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = "Time",
                        tint = if (isLowTime && isCurrentTurn) Color(0xFFEF4444) else Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = timeFormatted,
                        color = if (isLowTime && isCurrentTurn) Color(0xFFEF4444) else Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
