package com.onlinechessgame.app.chess.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.onlinechessgame.app.chess.puzzles.ChessPuzzlesRepository
import com.onlinechessgame.app.chess.ui.components.BoardThemeStyle
import com.onlinechessgame.app.chess.ui.components.ChessBoard3D
import com.onlinechessgame.app.chess.ui.viewmodel.ChessViewModel

@Composable
fun PuzzlesScreen(
    viewModel: ChessViewModel,
    modifier: Modifier = Modifier
) {
    val puzzleIdx by viewModel.currentPuzzleIndex.collectAsState()
    val feedback by viewModel.puzzleFeedback.collectAsState()
    val isSolved by viewModel.puzzleSolved.collectAsState()
    val engine by viewModel.puzzleBoard.collectAsState()
    val selectedPos by viewModel.puzzleSelectedPos.collectAsState()
    val legalMoves by viewModel.puzzleLegalMoves.collectAsState()
    val profile by viewModel.userProfile.collectAsState()

    val currentPuzzle = ChessPuzzlesRepository.puzzles[puzzleIdx]

    val selectedTheme = remember(profile?.boardTheme) {
        BoardThemeStyle.fromKey(profile?.boardTheme)
    }

    val pieceStyle = remember(profile?.pieceStyle) {
        profile?.pieceStyle ?: "TOURNAMENT_PLASTIC"
    }

    val pieceColorTheme = remember(profile?.pieceColor) {
        profile?.pieceColor ?: "CLASSIC"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0B0F17))
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .testTag("puzzles_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header info
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Puzzle #${puzzleIdx + 1}: ${currentPuzzle.title}",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF3B82F6).copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3B82F6))
                    ) {
                        Text(
                            text = currentPuzzle.tacticTheme,
                            color = Color(0xFF93C5FD),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = currentPuzzle.description,
                    color = Color(0xFF94A3B8),
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFF59E0B).copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "+${currentPuzzle.rewardTokens} Tokens",
                            color = Color(0xFFFBBF24),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF10B981).copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "+${currentPuzzle.rewardPoints} Rating Pts",
                            color = Color(0xFF34D399),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Feedback Banner
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = when {
                isSolved -> Color(0xFF10B981).copy(alpha = 0.25f)
                feedback.contains("Incorrect") -> Color(0xFFEF4444).copy(alpha = 0.25f)
                else -> Color(0xFF1E293B)
            },
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                when {
                    isSolved -> Color(0xFF10B981)
                    feedback.contains("Incorrect") -> Color(0xFFEF4444)
                    else -> Color(0xFF475569)
                }
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = feedback,
                color = when {
                    isSolved -> Color(0xFF6EE7B7)
                    feedback.contains("Incorrect") -> Color(0xFFFCA5A5)
                    else -> Color(0xFFE2E8F0)
                },
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 10.dp, horizontal = 14.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 3D Vector Chess Board for Puzzles
        ChessBoard3D(
            board = engine.board,
            selectedPosition = selectedPos,
            legalMoves = legalMoves,
            lastMove = engine.moveHistory.lastOrNull(),
            isCheck = engine.isInCheck(engine.currentTurn),
            kingInCheckPos = if (engine.isInCheck(engine.currentTurn)) engine.findKing(engine.currentTurn) else null,
            boardTheme = selectedTheme,
            pieceStyle = pieceStyle,
            pieceColorTheme = pieceColorTheme,
            flipped = false,
            onSquareClick = { pos -> viewModel.onPuzzleSquareClicked(pos) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = { viewModel.showPuzzleHint() },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFBBF24)),
                modifier = Modifier
                    .weight(1f)
                    .height(46.dp)
                    .testTag("puzzle_hint_button")
            ) {
                Icon(Icons.Default.Lightbulb, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Hint", fontSize = 13.sp)
            }

            OutlinedButton(
                onClick = { viewModel.loadPuzzle(puzzleIdx) },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF94A3B8)),
                modifier = Modifier
                    .weight(1f)
                    .height(46.dp)
                    .testTag("puzzle_reset_button")
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Reset", fontSize = 13.sp)
            }

            Button(
                onClick = { viewModel.nextPuzzle() },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSolved) Color(0xFF10B981) else Color(0xFF2563EB)
                ),
                modifier = Modifier
                    .weight(1.3f)
                    .height(46.dp)
                    .testTag("puzzle_next_button")
            ) {
                Text(if (isSolved) "Next Puzzle" else "Skip", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
            }
        }
    }
}
