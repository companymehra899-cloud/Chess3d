package com.onlinechessgame.app.chess.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.onlinechessgame.app.chess.model.Move
import com.onlinechessgame.app.chess.model.Piece
import com.onlinechessgame.app.chess.model.PieceColor
import com.onlinechessgame.app.chess.model.PieceType
import com.onlinechessgame.app.chess.model.Position

enum class BoardThemeStyle(
    val id: String,
    val title: String,
    val subtitle: String,
    val lightSquare: Color,
    val darkSquare: Color,
    val frameStart: Color,
    val frameEnd: Color,
    val labelColor: Color,
    val isSimple: Boolean = false
) {
    SIMPLE_GREEN_BUFF(
        id = "SIMPLE_GREEN_BUFF",
        title = "Tournament Green & Buff",
        subtitle = "Standard Club Vinyl (Simple)",
        lightSquare = Color(0xFFEEEED2),
        darkSquare = Color(0xFF769656),
        frameStart = Color(0xFF2C3E25),
        frameEnd = Color(0xFF1E2B1A),
        labelColor = Color(0xFFF1F5F9),
        isSimple = true
    ),
    SIMPLE_WOOD_MAPLE(
        id = "SIMPLE_WOOD_MAPLE",
        title = "Classic Maple & Walnut",
        subtitle = "Natural Tournament Wood (Simple)",
        lightSquare = Color(0xFFF0D9B5),
        darkSquare = Color(0xFFB58863),
        frameStart = Color(0xFF4A3728),
        frameEnd = Color(0xFF2D1F16),
        labelColor = Color(0xFFFDE68A),
        isSimple = true
    ),
    SIMPLE_BLUE_ICE(
        id = "SIMPLE_BLUE_ICE",
        title = "Pure White Board",
        subtitle = "Clean Minimalist All-White Plastic (Simple)",
        lightSquare = Color(0xFFF8FAFC),
        darkSquare = Color(0xFFCAD5E2),
        frameStart = Color(0xFFE2E8F0),
        frameEnd = Color(0xFFCBD5E1),
        labelColor = Color(0xFF334155),
        isSimple = true
    ),
    MARSHALL_CLASSIC_3D(
        id = "MARSHALL_CLASSIC_3D",
        title = "Marshall Luxury 3D",
        subtitle = "Rich Mahogany & Boxwood",
        lightSquare = Color(0xFFF6EADB),
        darkSquare = Color(0xFFA06C42),
        frameStart = Color(0xFF45220C),
        frameEnd = Color(0xFF241005),
        labelColor = Color(0xFFE2B777)
    ),
    WALNUT_3D(
        id = "WALNUT_3D",
        title = "Royal Walnut 3D",
        subtitle = "Deep Walnut & Amber",
        lightSquare = Color(0xFFE8D4B4),
        darkSquare = Color(0xFF8F5D38),
        frameStart = Color(0xFF382314),
        frameEnd = Color(0xFF1E120A),
        labelColor = Color(0xFFD4AF37)
    ),
    CYBER_3D(
        id = "CYBER_3D",
        title = "Cyber Neon 3D",
        subtitle = "Futuristic Slate & Cyan",
        lightSquare = Color(0xFF1E293B),
        darkSquare = Color(0xFF0F172A),
        frameStart = Color(0xFF090D16),
        frameEnd = Color(0xFF020617),
        labelColor = Color(0xFF38BDF8)
    ),
    MARBLE_3D(
        id = "MARBLE_3D",
        title = "Emerald Marble 3D",
        subtitle = "Polished Jade & Slate",
        lightSquare = Color(0xFFF1F5F9),
        darkSquare = Color(0xFF2D6A4F),
        frameStart = Color(0xFF1B4332),
        frameEnd = Color(0xFF081C15),
        labelColor = Color(0xFF52B788)
    ),
    MIDNIGHT_3D(
        id = "MIDNIGHT_3D",
        title = "Midnight Slate 3D",
        subtitle = "Stealth Graphite & Charcoal",
        lightSquare = Color(0xFF475569),
        darkSquare = Color(0xFF1E293B),
        frameStart = Color(0xFF181E29),
        frameEnd = Color(0xFF0F131A),
        labelColor = Color(0xFFCBD5E1)
    ),
    PREMIUM_3D(
        id = "PREMIUM_3D",
        title = "Premium 3D Board",
        subtitle = "Luxury Walnut & Maple",
        lightSquare = Color(0xFFF6EADB),
        darkSquare = Color(0xFFA06C42),
        frameStart = Color(0xFF45220C),
        frameEnd = Color(0xFF241005),
        labelColor = Color(0xFFD4AF37)
    ),
    ISOMETRIC_TRUE_3D(
        id = "ISOMETRIC_TRUE_3D",
        title = "True 3D Isometric",
        subtitle = "Walnut table, ivory & ebony pieces",
        lightSquare = Color(0xFFD7B48A),
        darkSquare = Color(0xFF6D4428),
        frameStart = Color(0xFF5A3820),
        frameEnd = Color(0xFF3B2414),
        labelColor = Color(0xFFE2B777)
    );

    companion object {
        const val ISOMETRIC_ROTATION_X = 56f
        const val ISOMETRIC_ROTATION_Z = 45f

        fun fromKey(key: String?): BoardThemeStyle {
            return entries.find { it.id == key || it.name == key } ?: SIMPLE_GREEN_BUFF
        }
    }
}

@Composable
fun ChessBoard3D(
    board: Array<Array<Piece?>>,
    selectedPosition: Position?,
    legalMoves: List<Move>,
    lastMove: Move?,
    isCheck: Boolean,
    kingInCheckPos: Position?,
    boardTheme: BoardThemeStyle = BoardThemeStyle.SIMPLE_GREEN_BUFF,
    pieceStyle: String = "TOURNAMENT_PLASTIC",
    pieceColorTheme: String = "CLASSIC",
    showCoordinates: Boolean = false,
    flipped: Boolean = false,
    onSquareClick: (Position) -> Unit,
    modifier: Modifier = Modifier
) {
    val isometric = !boardTheme.isSimple
    val boardRotationX = if (isometric) BoardThemeStyle.ISOMETRIC_ROTATION_X else 0f
    val boardRotationZ = if (isometric) BoardThemeStyle.ISOMETRIC_ROTATION_Z else 0f

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(if (isometric) 1.12f else 1f)
            .shadow(16.dp, RoundedCornerShape(12.dp))
            .graphicsLayer {
                if (isometric) {
                    rotationX = boardRotationX
                    rotationZ = boardRotationZ
                    cameraDistance = 18f * density
                    transformOrigin = TransformOrigin.Center
                    clip = false
                }
            }
            .testTag("chess_board_card")
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(boardTheme.frameStart, boardTheme.frameEnd)
                    )
                )
                .padding(4.dp)
        ) {
            val boardSize = maxWidth
            val squareSize = (boardSize - 8.dp) / 8f

            // Outer molded plastic tournament bevel frame canvas
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val cornerRadius = androidx.compose.ui.geometry.CornerRadius(12.dp.toPx())
                
                // Outer highlight
                drawRoundRect(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0x80FFFFFF), Color(0x20FFFFFF)),
                        start = Offset(0f, 0f),
                        end = Offset(w/10, h/10)
                    ),
                    size = Size(w, h),
                    cornerRadius = cornerRadius,
                    style = Stroke(width = 6.dp.toPx())
                )
                // Inner shadow
                drawRoundRect(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0x10000000), Color(0x50000000)),
                        start = Offset(w - w/10, h - h/10),
                        end = Offset(w, h)
                    ),
                    size = Size(w, h),
                    cornerRadius = cornerRadius,
                    style = Stroke(width = 6.dp.toPx())
                )
            }

            // Central Board Grid with coordinates
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(3.5.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { clip = false }
                ) {
                    for (rowIdx in 0..7) {
                        val actualRow = if (flipped) 7 - rowIdx else rowIdx
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .graphicsLayer { clip = false }
                        ) {
                            for (colIdx in 0..7) {
                                val actualCol = if (flipped) 7 - colIdx else colIdx
                                val pos = Position(actualRow, actualCol)
                                val isLight = (actualRow + actualCol) % 2 == 0
                                val piece = board[actualRow][actualCol]
                                
                                val baseSquareColor = if (isLight) boardTheme.lightSquare else boardTheme.darkSquare
                                val isWoodTheme = boardTheme.id.contains("WOOD") || boardTheme.id.contains("MARSHALL") || boardTheme.id.contains("WALNUT")
                                
                                ChessSquareView(
                                    position = pos,
                                    piece = piece,
                                    baseColor = baseSquareColor,
                                    isWoodTheme = isWoodTheme,
                                    isSelected = selectedPosition == pos,
                                    isMoveTarget = legalMoves.any { it.to == pos },
                                    isCaptureTarget = legalMoves.any { it.to == pos && (board[actualRow][actualCol] != null || legalMoves.any { it.to == pos && it.isEnPassant }) },
                                    isLastMoveSquare = lastMove != null && (lastMove.from == pos || lastMove.to == pos),
                                    isCheckSquare = isCheck && kingInCheckPos == pos,
                                    showFileLabel = showCoordinates && rowIdx == 7,
                                    showRankLabel = showCoordinates && colIdx == 0,
                                    labelColor = if (boardTheme == BoardThemeStyle.SIMPLE_BLUE_ICE) {
                                        Color(0xFF475569)
                                    } else if (isLight) {
                                        boardTheme.darkSquare
                                    } else {
                                        boardTheme.lightSquare
                                    },
                                    pieceStyle = pieceStyle,
                                    pieceColorTheme = pieceColorTheme,
                                    boardRotationX = boardRotationX,
                                    boardRotationZ = boardRotationZ,
                                    onClick = { onSquareClick(pos) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxSize()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawWoodGrain(w: Float, h: Float, baseColor: Color) {
    val grainColor = baseColor.copy(alpha = 0.25f)
    for (i in 0 until 8) {
        val y = h * (i + 1) / 9f + (Math.random() * 6 - 3).toFloat()
        drawLine(
            color = grainColor,
            start = Offset(0f, y),
            end = Offset(w, y),
            strokeWidth = (Math.random() * 3 + 1).toFloat()
        )
    }
}

@Composable
private fun ChessSquareView(
    position: Position,
    piece: Piece?,
    baseColor: Color,
    isWoodTheme: Boolean,
    isSelected: Boolean,
    isMoveTarget: Boolean,
    isCaptureTarget: Boolean,
    isLastMoveSquare: Boolean,
    isCheckSquare: Boolean,
    showFileLabel: Boolean,
    showRankLabel: Boolean,
    labelColor: Color,
    pieceStyle: String,
    pieceColorTheme: String = "CLASSIC",
    boardRotationX: Float = 0f,
    boardRotationZ: Float = 0f,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .testTag("square_${position.toAlgebraic()}")
            .graphicsLayer { clip = false }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        // Square Background and 3D Plastic Finish
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // 1. Base tile color
            drawRect(color = baseColor)
            
            // 1.5 Procedural Wood Grain Texture (for wood themes)
            if (isWoodTheme) {
                drawWoodGrain(w, h, baseColor)
            }

            // 2. Realistic 3D satin plastic sheen gradient from top-left
            drawRect(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0x20FFFFFF),
                        Color(0x06FFFFFF),
                        Color(0x00000000),
                        Color(0x18000000)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(w, h)
                )
            )

            // 3. 3D Molded plastic tile bevel highlight (top/left) and seam shadow (bottom/right)
            // Enhanced 3D bevel effect
            drawRect(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0x60FFFFFF), Color(0x20FFFFFF)),
                    start = Offset(0f, 0f),
                    end = Offset(w/4, h/4)
                ),
                size = Size(w, h),
                style = Stroke(width = 2.5f)
            )
            drawRect(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0x20000000), Color(0x60000000)),
                    start = Offset(w - w/4, h - h/4),
                    end = Offset(w, h)
                ),
                size = Size(w, h),
                style = Stroke(width = 2.5f)
            )

            // Last move highlight (soft emerald tint)
            if (isLastMoveSquare) {
                drawRect(color = Color(0x4410B981))
            }

            // Selected piece highlight (golden amber aura)
            if (isSelected) {
                drawRect(color = Color(0x66F59E0B))
                drawRect(
                    color = Color(0xFFF59E0B),
                    style = Stroke(width = 3f)
                )
            }

            // King in check indicator (crimson alert radial pulse)
            if (isCheckSquare) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xCCEF4444), Color(0x66DC2626), Color(0x00000000)),
                        center = Offset(w / 2f, h / 2f),
                        radius = w * 0.6f
                    )
                )
            }

            // Move Target Indicators
            if (isMoveTarget && !isCaptureTarget) {
                // Circular 3D glowing dot for empty legal moves
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF38BDF8), Color(0x880284C7)),
                        center = Offset(w / 2f, h / 2f),
                        radius = w * 0.18f
                    ),
                    radius = w * 0.16f,
                    center = Offset(w / 2f, h / 2f)
                )
                drawCircle(
                    color = Color(0xFFFFFFFF),
                    radius = w * 0.06f,
                    center = Offset(w / 2f, h / 2f)
                )
            } else if (isCaptureTarget) {
                // 3D Corner Bracket Rings for Captures
                val ringRadius = w * 0.40f
                drawCircle(
                    color = Color(0xFFEF4444),
                    radius = ringRadius,
                    center = Offset(w / 2f, h / 2f),
                    style = Stroke(width = 3.5f, cap = StrokeCap.Round)
                )
            }
        }

        if (piece != null) {
            val isometricPiece = boardRotationX != 0f || boardRotationZ != 0f
            ChessPiece3D(
                type = piece.type,
                color = piece.color,
                pieceStyle = pieceStyle,
                pieceColorTheme = pieceColorTheme,
                isElevated = isSelected,
                boardRotationX = boardRotationX,
                boardRotationZ = boardRotationZ,
                modifier = if (isometricPiece) {
                    Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .wrapContentHeight(align = Alignment.Bottom, unbounded = true)
                        .aspectRatio(0.58f)
                        .offset(y = (-10).dp)
                } else {
                    Modifier.fillMaxSize()
                }
            )
        }

        // Coordinate Labels on edge squares (subtle small corner indicators)
        if (showRankLabel) {
            Text(
                text = "${position.rank}",
                color = labelColor.copy(alpha = 0.65f),
                fontSize = 8.5.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 2.dp, top = 0.5.dp)
            )
        }
        if (showFileLabel) {
            Text(
                text = "${position.file}",
                color = labelColor.copy(alpha = 0.65f),
                fontSize = 8.5.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 2.dp, bottom = 0.5.dp)
            )
        }
    }
}
