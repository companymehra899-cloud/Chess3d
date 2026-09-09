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
import androidx.compose.foundation.layout.widthIn
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
import com.onlinechessgame.app.ui.theme.AppSpace

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
        lightSquare = Color(0xFFE5C995),
        darkSquare = Color(0xFF70472A),
        frameStart = Color(0xFF6E3F20),
        frameEnd = Color(0xFF2A160D),
        labelColor = Color(0xFFE4BB6C),
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
        lightSquare = Color(0xFFE5C995),
        darkSquare = Color(0xFF70472A),
        frameStart = Color(0xFF6E3F20),
        frameEnd = Color(0xFF2A160D),
        labelColor = Color(0xFFE4BB6C)
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
        title = "Studio Wood 3D",
        subtitle = "Mahogany table, cream & dark wood pieces",
        lightSquare = Color(0xFFE5C995),
        darkSquare = Color(0xFF70472A),
        frameStart = Color(0xFF6E3F20),
        frameEnd = Color(0xFF2A160D),
        labelColor = Color(0xFFE4BB6C)
    );

    companion object {
        const val ISOMETRIC_ROTATION_X = 56f
        const val ISOMETRIC_ROTATION_Z = 45f

        fun fromKey(key: String?): BoardThemeStyle {
            return entries.find { it.id == key || it.name == key } ?: ISOMETRIC_TRUE_3D
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
    boardTheme: BoardThemeStyle = BoardThemeStyle.ISOMETRIC_TRUE_3D,
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
            .widthIn(max = AppSpace.boardMax)
            .aspectRatio(1f)
            .shadow(22.dp, RoundedCornerShape(10.dp))
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
                    Brush.linearGradient(
                        colors = listOf(
                            boardTheme.frameStart,
                            Color(0xFF8B5428),
                            boardTheme.frameEnd
                        )
                    )
                )
                .padding(if (isometric) 14.dp else 6.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val cornerRadius = androidx.compose.ui.geometry.CornerRadius(8.dp.toPx())
                val rim = 10.dp.toPx()

                drawRoundRect(
                    color = Color(0xFF2A160D),
                    size = Size(w, h),
                    cornerRadius = cornerRadius
                )
                drawRoundRect(
                    color = Color(0xFF8B5428),
                    topLeft = Offset(rim * 0.18f, rim * 0.18f),
                    size = Size(w - rim * 0.36f, h - rim * 0.36f),
                    cornerRadius = cornerRadius,
                    style = Stroke(width = rim)
                )
                drawRoundRect(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0x66E4BB6C), Color(0x22000000)),
                        start = Offset(0f, 0f),
                        end = Offset(w * 0.35f, h * 0.35f)
                    ),
                    size = Size(w, h),
                    cornerRadius = cornerRadius,
                    style = Stroke(width = 5.dp.toPx())
                )
                drawRoundRect(
                    color = Color(0x66120804),
                    topLeft = Offset(rim, rim),
                    size = Size(w - rim * 2f, h - rim * 2f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx()),
                    style = Stroke(width = 3.dp.toPx())
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(if (isometric) 8.dp else 3.5.dp)
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
                                val isWoodTheme = !boardTheme.id.contains("CYBER") &&
                                    !boardTheme.id.contains("BLUE") &&
                                    !boardTheme.id.contains("MIDNIGHT")
                                
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

            if (isLastMoveSquare) {
                drawRect(color = Color(0x66B5854C))
            }

            if (isSelected) {
                drawRect(color = Color(0xAAE1B866))
                drawRect(
                    color = Color(0xFFE4BB6C),
                    style = Stroke(width = 3.2f)
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
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFFE4BB6C), Color(0xFFBD7D2E)),
                        center = Offset(w / 2f, h / 2f),
                        radius = w * 0.16f
                    ),
                    radius = w * 0.14f,
                    center = Offset(w / 2f, h / 2f)
                )
                drawCircle(
                    color = Color(0x66E1B866),
                    radius = w * 0.20f,
                    center = Offset(w / 2f, h / 2f),
                    style = Stroke(width = 2.2f)
                )
            } else if (isCaptureTarget) {
                val ringRadius = w * 0.38f
                drawCircle(
                    color = Color(0xFFE4BB6C),
                    radius = ringRadius,
                    center = Offset(w / 2f, h / 2f),
                    style = Stroke(width = 3.2f, cap = StrokeCap.Round)
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
                fontSize = 14.sp,
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
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 2.dp, bottom = 0.5.dp)
            )
        }
    }
}
