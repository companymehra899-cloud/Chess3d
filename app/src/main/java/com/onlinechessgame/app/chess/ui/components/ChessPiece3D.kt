package com.onlinechessgame.app.chess.ui.components

import android.graphics.BlurMaskFilter
import android.graphics.Paint
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.onlinechessgame.app.chess.model.ChessPieceColorTheme
import com.onlinechessgame.app.chess.model.ChessPieceStyle
import com.onlinechessgame.app.chess.model.Piece
import com.onlinechessgame.app.chess.model.PieceColor
import com.onlinechessgame.app.chess.model.PieceType

@Composable
fun ChessPiece3D(
    piece: Piece,
    modifier: Modifier = Modifier,
    pieceStyle: String = "TOURNAMENT_PLASTIC",
    pieceColorTheme: String = "CLASSIC",
    isElevated: Boolean = false,
    boardRotationX: Float = 0f,
    boardRotationZ: Float = 0f
) {
    ChessPiece3D(
        type = piece.type,
        color = piece.color,
        modifier = modifier,
        pieceStyle = pieceStyle,
        pieceColorTheme = pieceColorTheme,
        isElevated = isElevated,
        boardRotationX = boardRotationX,
        boardRotationZ = boardRotationZ
    )
}

@Composable
fun ChessPiece3D(
    type: PieceType,
    color: PieceColor,
    modifier: Modifier = Modifier,
    pieceStyle: String = "TOURNAMENT_PLASTIC",
    pieceColorTheme: String = "CLASSIC",
    isElevated: Boolean = false,
    boardRotationX: Float = 0f,
    boardRotationZ: Float = 0f
) {
    val isWhite = color == PieceColor.WHITE
    val activeStyle = ChessPieceStyle.fromKey(pieceStyle)
    val activeColorTheme = ChessPieceColorTheme.fromKey(pieceColorTheme)
    val materials = rememberPieceMaterials(isWhite, activeStyle, activeColorTheme)

    val animatedScale by animateFloatAsState(
        targetValue = if (isElevated) 1.12f else 1f,
        animationSpec = tween(180, easing = FastOutSlowInEasing),
        label = "piece_scale"
    )
    val animatedLift by animateFloatAsState(
        targetValue = if (isElevated) -10f else 0f,
        animationSpec = tween(180, easing = FastOutSlowInEasing),
        label = "piece_lift"
    )
    val shadowBoost by animateFloatAsState(
        targetValue = if (isElevated) 1.35f else 1f,
        animationSpec = tween(180, easing = FastOutSlowInEasing),
        label = "piece_shadow"
    )

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .offset(y = animatedLift.dp)
            .scale(animatedScale)
            .graphicsLayer {
                rotationX = -boardRotationX
                rotationZ = -boardRotationZ
                cameraDistance = 18f * density
                transformOrigin = TransformOrigin(0.5f, 1f)
                clip = false
            }
    ) {
        when (type) {
            PieceType.PAWN -> drawPawn(materials, shadowBoost)
            PieceType.ROOK -> drawRook(materials, shadowBoost)
            PieceType.KNIGHT -> drawKnight(materials, shadowBoost)
            PieceType.BISHOP -> drawBishop(materials, shadowBoost)
            PieceType.QUEEN -> drawQueen(materials, shadowBoost)
            PieceType.KING -> drawKing(materials, shadowBoost)
        }
    }
}

private data class PieceMaterials(
    val cylinder: List<Color>,
    val radialCenter: Color,
    val radialEdge: Color,
    val groove: Color,
    val specular: Color,
    val rim: Color,
    val felt: Color,
    val outline: Color,
    val grain: Color,
    val accent: Color
)

@Composable
private fun rememberPieceMaterials(
    isWhite: Boolean,
    style: ChessPieceStyle,
    theme: ChessPieceColorTheme
): PieceMaterials = remember(isWhite, style, theme) {
    if (style == ChessPieceStyle.NEON_CYBER) {
        return@remember if (isWhite) {
            PieceMaterials(
                cylinder = listOf(Color(0xFF082F49), Color(0xFF38BDF8), Color(0xFFE0F2FE), Color(0xFF0284C7), Color(0xFF0C4A6E)),
                radialCenter = Color(0xFFE0F2FE),
                radialEdge = Color(0xFF0369A1),
                groove = Color(0xFF155E75),
                specular = Color(0xCCFFFFFF),
                rim = Color(0xFF7DD3FC),
                felt = Color(0xFF082F49),
                outline = Color(0xFF38BDF8),
                grain = Color(0x6638BDF8),
                accent = Color(0xFF22D3EE)
            )
        } else {
            PieceMaterials(
                cylinder = listOf(Color(0xFF4C0519), Color(0xFFF43F5E), Color(0xFFFFE4E6), Color(0xFFBE123C), Color(0xFF881337)),
                radialCenter = Color(0xFFFECDD3),
                radialEdge = Color(0xFF9F1239),
                groove = Color(0xFF881337),
                specular = Color(0xAAFFFFFF),
                rim = Color(0xFFFB7185),
                felt = Color(0xFF4C0519),
                outline = Color(0xFFF43F5E),
                grain = Color(0x66FB7185),
                accent = Color(0xFFFB7185)
            )
        }
    }

    val ivory = PieceMaterials(
        cylinder = listOf(
            Color(0xFFC9A36A),
            Color(0xFFF3E2C0),
            Color(0xFFFFF8EC),
            Color(0xFFE7D0A2),
            Color(0xFFB8894A)
        ),
        radialCenter = Color(0xFFFFFBF3),
        radialEdge = Color(0xFFC4A06A),
        groove = Color(0xFFA67C42),
        specular = Color(0xE6FFFFFF),
        rim = Color(0x66FFFFFF),
        felt = Color(0xFF2F6B46),
        outline = Color(0xFF8A6A3A),
        grain = Color(0x33A67C42),
        accent = Color(0xFFF8E7C4)
    )
    val walnut = PieceMaterials(
        cylinder = listOf(
            Color(0xFF1A0E08),
            Color(0xFF5A3318),
            Color(0xFF8A562C),
            Color(0xFF3E2210),
            Color(0xFF120A06)
        ),
        radialCenter = Color(0xFFA06A38),
        radialEdge = Color(0xFF24140C),
        groove = Color(0xFF1A0C08),
        specular = Color(0x99F6E2C4),
        rim = Color(0x66C4A06A),
        felt = Color(0xFF1F3D2C),
        outline = Color(0xFF120804),
        grain = Color(0x44C4A074),
        accent = Color(0xFF6B4224)
    )

    val base = if (isWhite) ivory else walnut
    return@remember when (theme) {
        ChessPieceColorTheme.CLASSIC, ChessPieceColorTheme.BROWN -> base
        ChessPieceColorTheme.RED -> base.copy(
            cylinder = if (isWhite) {
                listOf(Color(0xFFB91C1C), Color(0xFFFECACA), Color(0xFFFFF1F2), Color(0xFFF87171), Color(0xFF7F1D1D))
            } else {
                listOf(Color(0xFF3F0A0A), Color(0xFF9F1239), Color(0xFFBE123C), Color(0xFF4C0519), Color(0xFF1C0508))
            }
        )
        ChessPieceColorTheme.YELLOW -> base.copy(
            cylinder = if (isWhite) {
                listOf(Color(0xFFA16207), Color(0xFFFEF08A), Color(0xFFFFFBEB), Color(0xFFFACC15), Color(0xFF854D0E))
            } else {
                listOf(Color(0xFF422006), Color(0xFFA16207), Color(0xFFCA8A04), Color(0xFF713F12), Color(0xFF1C1004))
            }
        )
        ChessPieceColorTheme.BLACK -> if (isWhite) {
            base.copy(
                cylinder = listOf(Color(0xFF64748B), Color(0xFFE2E8F0), Color(0xFFF8FAFC), Color(0xFF94A3B8), Color(0xFF475569))
            )
        } else {
            base.copy(
                cylinder = listOf(Color(0xFF020617), Color(0xFF334155), Color(0xFF64748B), Color(0xFF1E293B), Color(0xFF020617))
            )
        }
        ChessPieceColorTheme.WHITE -> if (isWhite) {
            base.copy(
                cylinder = listOf(Color(0xFFD6D3D1), Color(0xFFF5F5F4), Color(0xFFFFFFFF), Color(0xFFE7E5E4), Color(0xFFA8A29E))
            )
        } else {
            base.copy(
                cylinder = listOf(Color(0xFF44403C), Color(0xFFA8A29E), Color(0xFFD6D3D1), Color(0xFF57534E), Color(0xFF1C1917))
            )
        }
    }
}

private fun DrawScope.pieceRect(): Rect {
    val w = size.width
    val h = size.height
    val pieceW = w * 0.82f
    val pieceH = h * 0.94f
    val left = (w - pieceW) / 2f
    val top = h - pieceH
    return Rect(left, top, left + pieceW, top + pieceH)
}

private fun DrawScope.drawSoftDropShadow(
    cx: Float,
    cy: Float,
    radiusX: Float,
    radiusY: Float,
    boost: Float
) {
    val blurRadius = (radiusX * 0.42f * boost).coerceIn(6f, 28f)
    val offsetX = radiusX * 0.18f
    val offsetY = radiusY * 0.55f
    drawIntoCanvas { canvas ->
        val paint = Paint().apply {
            isAntiAlias = true
            color = Color.Black.copy(alpha = 0.4f).toArgb()
            maskFilter = BlurMaskFilter(blurRadius, BlurMaskFilter.Blur.NORMAL)
        }
        canvas.nativeCanvas.drawOval(
            cx - radiusX + offsetX,
            cy - radiusY + offsetY,
            cx + radiusX + offsetX,
            cy + radiusY + offsetY,
            paint
        )
    }
    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.Black.copy(alpha = 0.4f * boost.coerceAtMost(1.2f)),
                Color.Black.copy(alpha = 0.16f),
                Color.Transparent
            ),
            center = Offset(cx + offsetX, cy + offsetY),
            radius = radiusX * 1.25f
        ),
        topLeft = Offset(cx - radiusX + offsetX, cy - radiusY + offsetY),
        size = Size(radiusX * 2f, radiusY * 2f)
    )
}

private fun DrawScope.cylinderBrush(left: Float, right: Float, m: PieceMaterials): Brush {
    return Brush.horizontalGradient(
        colorStops = arrayOf(
            0f to m.cylinder[0],
            0.22f to m.cylinder[1],
            0.42f to m.cylinder[2],
            0.68f to m.cylinder[3],
            1f to m.cylinder[4]
        ),
        startX = left,
        endX = right
    )
}

private fun DrawScope.drawStem(
    cx: Float,
    top: Float,
    bottom: Float,
    topHalf: Float,
    bottomHalf: Float,
    m: PieceMaterials
) {
    val path = Path().apply {
        moveTo(cx - bottomHalf, bottom)
        quadraticTo(cx - bottomHalf * 1.04f, (top + bottom) * 0.5f, cx - topHalf, top)
        lineTo(cx + topHalf, top)
        quadraticTo(cx + bottomHalf * 1.04f, (top + bottom) * 0.5f, cx + bottomHalf, bottom)
        close()
    }
    val maxHalf = maxOf(topHalf, bottomHalf)
    drawPath(path, brush = cylinderBrush(cx - maxHalf, cx + maxHalf, m))
    drawPath(
        path,
        color = m.outline.copy(alpha = 0.55f),
        style = Stroke(width = 1.1f, join = StrokeJoin.Round)
    )
    drawLine(
        color = m.specular,
        start = Offset(cx - topHalf * 0.52f, top + 3f),
        end = Offset(cx - bottomHalf * 0.48f, bottom - 3f),
        strokeWidth = (topHalf * 0.18f).coerceIn(1.6f, 3.4f),
        cap = StrokeCap.Round
    )
    drawLine(
        color = m.rim.copy(alpha = 0.35f),
        start = Offset(cx + topHalf * 0.58f, top + 4f),
        end = Offset(cx + bottomHalf * 0.55f, bottom - 4f),
        strokeWidth = 1.4f,
        cap = StrokeCap.Round
    )
    drawWoodGrain(cx, top, bottom, maxHalf, m)
}

private fun DrawScope.drawWoodGrain(cx: Float, top: Float, bottom: Float, half: Float, m: PieceMaterials) {
    val h = bottom - top
    if (h <= 8f) return
    for (i in 0..3) {
        val x = cx - half * 0.28f + i * half * 0.16f
        drawLine(
            color = m.grain,
            start = Offset(x, top + h * 0.08f),
            end = Offset(x + half * 0.04f, bottom - h * 0.08f),
            strokeWidth = 1f,
            cap = StrokeCap.Round
        )
    }
}

private fun DrawScope.drawCollar(cx: Float, cy: Float, rx: Float, ry: Float, m: PieceMaterials) {
    drawOval(
        color = m.groove,
        topLeft = Offset(cx - rx, cy - ry * 0.35f),
        size = Size(rx * 2f, ry * 1.55f)
    )
    drawOval(
        brush = cylinderBrush(cx - rx, cx + rx, m),
        topLeft = Offset(cx - rx, cy - ry),
        size = Size(rx * 2f, ry * 2f)
    )
    drawOval(
        color = m.specular.copy(alpha = 0.55f),
        topLeft = Offset(cx - rx * 0.55f, cy - ry * 0.72f),
        size = Size(rx * 0.55f, ry * 0.7f)
    )
    drawOval(
        color = m.outline.copy(alpha = 0.4f),
        topLeft = Offset(cx - rx, cy - ry),
        size = Size(rx * 2f, ry * 2f),
        style = Stroke(width = 1f)
    )
}

private fun DrawScope.drawBase(cx: Float, bottom: Float, rx: Float, m: PieceMaterials) {
    val ry = rx * 0.28f
    drawOval(
        color = m.felt,
        topLeft = Offset(cx - rx * 0.82f, bottom - ry * 0.55f),
        size = Size(rx * 1.64f, ry * 1.05f)
    )
    drawOval(
        brush = cylinderBrush(cx - rx, cx + rx, m),
        topLeft = Offset(cx - rx, bottom - ry * 2.4f),
        size = Size(rx * 2f, ry * 2.15f)
    )
    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(m.radialCenter.copy(alpha = 0.55f), Color.Transparent),
            center = Offset(cx - rx * 0.28f, bottom - ry * 1.7f),
            radius = rx * 0.7f
        ),
        topLeft = Offset(cx - rx, bottom - ry * 2.4f),
        size = Size(rx * 2f, ry * 2.15f)
    )
    drawOval(
        color = m.outline.copy(alpha = 0.5f),
        topLeft = Offset(cx - rx, bottom - ry * 2.4f),
        size = Size(rx * 2f, ry * 2.15f),
        style = Stroke(width = 1.15f)
    )
}

private fun DrawScope.drawSphere(cx: Float, cy: Float, r: Float, m: PieceMaterials) {
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(m.radialCenter, m.cylinder[1], m.radialEdge),
            center = Offset(cx - r * 0.28f, cy - r * 0.34f),
            radius = r * 1.25f
        ),
        radius = r,
        center = Offset(cx, cy)
    )
    drawCircle(
        color = m.specular,
        radius = r * 0.22f,
        center = Offset(cx - r * 0.32f, cy - r * 0.38f)
    )
    drawArc(
        color = m.specular.copy(alpha = 0.55f),
        startAngle = 200f,
        sweepAngle = 70f,
        useCenter = false,
        topLeft = Offset(cx - r, cy - r),
        size = Size(r * 2f, r * 2f),
        style = Stroke(width = r * 0.12f, cap = StrokeCap.Round)
    )
    drawCircle(
        color = m.outline.copy(alpha = 0.4f),
        radius = r,
        center = Offset(cx, cy),
        style = Stroke(width = 1.05f)
    )
}

private fun DrawScope.drawPawn(m: PieceMaterials, shadowBoost: Float) {
    val b = pieceRect()
    val cx = b.center.x
    val w = b.width
    val h = b.height
    drawSoftDropShadow(cx, b.bottom - h * 0.04f, w * 0.34f, h * 0.07f, shadowBoost)
    drawBase(cx, b.bottom - h * 0.02f, w * 0.36f, m)
    drawStem(cx, b.top + h * 0.46f, b.bottom - h * 0.16f, w * 0.13f, w * 0.22f, m)
    drawCollar(cx, b.top + h * 0.44f, w * 0.20f, h * 0.045f, m)
    drawSphere(cx, b.top + h * 0.28f, w * 0.175f, m)
}

private fun DrawScope.drawRook(m: PieceMaterials, shadowBoost: Float) {
    val b = pieceRect()
    val cx = b.center.x
    val w = b.width
    val h = b.height
    drawSoftDropShadow(cx, b.bottom - h * 0.04f, w * 0.36f, h * 0.07f, shadowBoost)
    drawBase(cx, b.bottom - h * 0.02f, w * 0.38f, m)
    drawStem(cx, b.top + h * 0.38f, b.bottom - h * 0.16f, w * 0.18f, w * 0.24f, m)
    drawCollar(cx, b.top + h * 0.38f, w * 0.24f, h * 0.04f, m)
    val bodyTop = b.top + h * 0.18f
    val bodyBottom = b.top + h * 0.36f
    val half = w * 0.22f
    val body = Path().apply {
        addRoundRect(
            RoundRect(
                left = cx - half,
                top = bodyTop,
                right = cx + half,
                bottom = bodyBottom,
                cornerRadius = CornerRadius(w * 0.04f, w * 0.04f)
            )
        )
    }
    drawPath(body, brush = cylinderBrush(cx - half, cx + half, m))
    drawPath(body, color = m.outline.copy(alpha = 0.45f), style = Stroke(1.1f))
    val merlonW = w * 0.095f
    val merlonH = h * 0.09f
    val merlonTop = b.top + h * 0.08f
    val gaps = listOf(-1.5f, -0.5f, 0.5f, 1.5f)
    gaps.forEach { slot ->
        val mx = cx + slot * merlonW * 1.35f
        drawRoundRect(
            brush = cylinderBrush(mx - merlonW / 2f, mx + merlonW / 2f, m),
            topLeft = Offset(mx - merlonW / 2f, merlonTop),
            size = Size(merlonW, merlonH + 4f),
            cornerRadius = CornerRadius(2f, 2f)
        )
    }
    drawLine(
        color = m.specular,
        start = Offset(cx - half * 0.55f, bodyTop + 4f),
        end = Offset(cx - half * 0.55f, bodyBottom - 4f),
        strokeWidth = 2.4f,
        cap = StrokeCap.Round
    )
}

private fun DrawScope.drawBishop(m: PieceMaterials, shadowBoost: Float) {
    val b = pieceRect()
    val cx = b.center.x
    val w = b.width
    val h = b.height
    drawSoftDropShadow(cx, b.bottom - h * 0.04f, w * 0.34f, h * 0.07f, shadowBoost)
    drawBase(cx, b.bottom - h * 0.02f, w * 0.36f, m)
    drawStem(cx, b.top + h * 0.50f, b.bottom - h * 0.16f, w * 0.12f, w * 0.21f, m)
    drawCollar(cx, b.top + h * 0.48f, w * 0.19f, h * 0.04f, m)
    val mitre = Path().apply {
        moveTo(cx, b.top + h * 0.08f)
        cubicTo(cx + w * 0.18f, b.top + h * 0.18f, cx + w * 0.20f, b.top + h * 0.36f, cx + w * 0.14f, b.top + h * 0.46f)
        quadraticTo(cx, b.top + h * 0.50f, cx - w * 0.14f, b.top + h * 0.46f)
        cubicTo(cx - w * 0.20f, b.top + h * 0.36f, cx - w * 0.18f, b.top + h * 0.18f, cx, b.top + h * 0.08f)
        close()
    }
    drawPath(mitre, brush = cylinderBrush(cx - w * 0.20f, cx + w * 0.20f, m))
    drawPath(mitre, color = m.outline.copy(alpha = 0.45f), style = Stroke(1.1f))
    val slit = Path().apply {
        moveTo(cx + w * 0.02f, b.top + h * 0.16f)
        lineTo(cx + w * 0.07f, b.top + h * 0.30f)
        lineTo(cx + w * 0.03f, b.top + h * 0.32f)
        lineTo(cx - w * 0.02f, b.top + h * 0.18f)
        close()
    }
    drawPath(slit, color = m.groove.copy(alpha = 0.75f))
    drawLine(
        color = m.specular,
        start = Offset(cx - w * 0.08f, b.top + h * 0.18f),
        end = Offset(cx - w * 0.06f, b.top + h * 0.42f),
        strokeWidth = 2.2f,
        cap = StrokeCap.Round
    )
    drawSphere(cx, b.top + h * 0.075f, w * 0.055f, m)
}

private fun DrawScope.drawKnight(m: PieceMaterials, shadowBoost: Float) {
    val b = pieceRect()
    val cx = b.center.x
    val w = b.width
    val h = b.height
    drawSoftDropShadow(cx, b.bottom - h * 0.04f, w * 0.36f, h * 0.07f, shadowBoost)
    drawBase(cx, b.bottom - h * 0.02f, w * 0.37f, m)
    drawStem(cx, b.top + h * 0.62f, b.bottom - h * 0.16f, w * 0.20f, w * 0.24f, m)
    drawCollar(cx, b.top + h * 0.62f, w * 0.23f, h * 0.035f, m)

    val horse = Path().apply {
        fillType = PathFillType.EvenOdd
        moveTo(cx - w * 0.16f, b.top + h * 0.60f)
        cubicTo(cx - w * 0.22f, b.top + h * 0.46f, cx - w * 0.10f, b.top + h * 0.34f, cx - w * 0.04f, b.top + h * 0.26f)
        cubicTo(cx - w * 0.08f, b.top + h * 0.16f, cx - w * 0.02f, b.top + h * 0.10f, cx + w * 0.06f, b.top + h * 0.12f)
        cubicTo(cx + w * 0.10f, b.top + h * 0.06f, cx + w * 0.16f, b.top + h * 0.07f, cx + w * 0.18f, b.top + h * 0.13f)
        cubicTo(cx + w * 0.28f, b.top + h * 0.18f, cx + w * 0.34f, b.top + h * 0.28f, cx + w * 0.32f, b.top + h * 0.36f)
        cubicTo(cx + w * 0.30f, b.top + h * 0.42f, cx + w * 0.20f, b.top + h * 0.40f, cx + w * 0.16f, b.top + h * 0.36f)
        cubicTo(cx + w * 0.18f, b.top + h * 0.44f, cx + w * 0.08f, b.top + h * 0.50f, cx + w * 0.10f, b.top + h * 0.60f)
        close()
    }
    drawPath(horse, brush = cylinderBrush(cx - w * 0.22f, cx + w * 0.34f, m))
    drawPath(horse, color = m.outline.copy(alpha = 0.5f), style = Stroke(width = 1.2f, join = StrokeJoin.Round))
    drawLine(
        color = m.groove.copy(alpha = 0.7f),
        start = Offset(cx - w * 0.02f, b.top + h * 0.22f),
        end = Offset(cx + w * 0.10f, b.top + h * 0.34f),
        strokeWidth = 1.6f,
        cap = StrokeCap.Round
    )
    drawLine(
        color = m.groove.copy(alpha = 0.55f),
        start = Offset(cx + w * 0.02f, b.top + h * 0.28f),
        end = Offset(cx + w * 0.14f, b.top + h * 0.38f),
        strokeWidth = 1.3f,
        cap = StrokeCap.Round
    )
    drawCircle(
        color = m.groove,
        radius = w * 0.022f,
        center = Offset(cx + w * 0.14f, b.top + h * 0.22f)
    )
    drawCircle(
        color = m.specular,
        radius = w * 0.01f,
        center = Offset(cx + w * 0.132f, b.top + h * 0.214f)
    )
    drawArc(
        color = m.specular,
        startAngle = 210f,
        sweepAngle = 50f,
        useCenter = false,
        topLeft = Offset(cx - w * 0.08f, b.top + h * 0.14f),
        size = Size(w * 0.22f, h * 0.22f),
        style = Stroke(width = 2.3f, cap = StrokeCap.Round)
    )
    drawLine(
        color = m.specular.copy(alpha = 0.7f),
        start = Offset(cx + w * 0.20f, b.top + h * 0.20f),
        end = Offset(cx + w * 0.28f, b.top + h * 0.30f),
        strokeWidth = 2f,
        cap = StrokeCap.Round
    )
}

private fun DrawScope.drawQueen(m: PieceMaterials, shadowBoost: Float) {
    val b = pieceRect()
    val cx = b.center.x
    val w = b.width
    val h = b.height
    drawSoftDropShadow(cx, b.bottom - h * 0.04f, w * 0.38f, h * 0.07f, shadowBoost)
    drawBase(cx, b.bottom - h * 0.02f, w * 0.40f, m)
    drawStem(cx, b.top + h * 0.46f, b.bottom - h * 0.16f, w * 0.13f, w * 0.23f, m)
    drawCollar(cx, b.top + h * 0.45f, w * 0.22f, h * 0.04f, m)
    val cup = Path().apply {
        moveTo(cx - w * 0.22f, b.top + h * 0.28f)
        quadraticTo(cx - w * 0.24f, b.top + h * 0.38f, cx - w * 0.16f, b.top + h * 0.44f)
        lineTo(cx + w * 0.16f, b.top + h * 0.44f)
        quadraticTo(cx + w * 0.24f, b.top + h * 0.38f, cx + w * 0.22f, b.top + h * 0.28f)
        close()
    }
    drawPath(cup, brush = cylinderBrush(cx - w * 0.24f, cx + w * 0.24f, m))
    drawPath(cup, color = m.outline.copy(alpha = 0.4f), style = Stroke(1.1f))
    val coronetY = b.top + h * 0.18f
    val spikes = listOf(-2, -1, 0, 1, 2)
    spikes.forEach { i ->
        val sx = cx + i * w * 0.09f
        val tipY = if (i == 0) b.top + h * 0.05f else b.top + h * 0.10f
        val spike = Path().apply {
            moveTo(sx - w * 0.035f, b.top + h * 0.28f)
            lineTo(sx, tipY)
            lineTo(sx + w * 0.035f, b.top + h * 0.28f)
            close()
        }
        drawPath(spike, brush = cylinderBrush(sx - w * 0.04f, sx + w * 0.04f, m))
        drawSphere(sx, tipY, if (i == 0) w * 0.038f else w * 0.028f, m)
    }
    drawLine(
        color = m.specular,
        start = Offset(cx - w * 0.12f, coronetY + h * 0.08f),
        end = Offset(cx - w * 0.08f, b.top + h * 0.42f),
        strokeWidth = 2.3f,
        cap = StrokeCap.Round
    )
    drawSphere(cx, b.top + h * 0.035f, w * 0.042f, m)
}

private fun DrawScope.drawKing(m: PieceMaterials, shadowBoost: Float) {
    val b = pieceRect()
    val cx = b.center.x
    val w = b.width
    val h = b.height
    drawSoftDropShadow(cx, b.bottom - h * 0.04f, w * 0.40f, h * 0.07f, shadowBoost)
    drawBase(cx, b.bottom - h * 0.02f, w * 0.42f, m)
    drawStem(cx, b.top + h * 0.48f, b.bottom - h * 0.16f, w * 0.14f, w * 0.24f, m)
    drawCollar(cx, b.top + h * 0.47f, w * 0.23f, h * 0.042f, m)
    val cup = Path().apply {
        moveTo(cx - w * 0.23f, b.top + h * 0.26f)
        quadraticTo(cx - w * 0.25f, b.top + h * 0.40f, cx - w * 0.17f, b.top + h * 0.46f)
        lineTo(cx + w * 0.17f, b.top + h * 0.46f)
        quadraticTo(cx + w * 0.25f, b.top + h * 0.40f, cx + w * 0.23f, b.top + h * 0.26f)
        close()
    }
    drawPath(cup, brush = cylinderBrush(cx - w * 0.25f, cx + w * 0.25f, m))
    drawPath(cup, color = m.outline.copy(alpha = 0.4f), style = Stroke(1.1f))
    drawCollar(cx, b.top + h * 0.24f, w * 0.17f, h * 0.03f, m)
    val crossCx = cx
    val crossCy = b.top + h * 0.12f
    val arm = w * 0.045f
    drawRoundRect(
        brush = cylinderBrush(crossCx - arm, crossCx + arm, m),
        topLeft = Offset(crossCx - arm, b.top + h * 0.02f),
        size = Size(arm * 2f, h * 0.20f),
        cornerRadius = CornerRadius(arm * 0.4f, arm * 0.4f)
    )
    drawRoundRect(
        brush = cylinderBrush(crossCx - w * 0.11f, crossCx + w * 0.11f, m),
        topLeft = Offset(crossCx - w * 0.11f, crossCy - arm),
        size = Size(w * 0.22f, arm * 2f),
        cornerRadius = CornerRadius(arm * 0.4f, arm * 0.4f)
    )
    drawLine(
        color = m.specular,
        start = Offset(cx - w * 0.12f, b.top + h * 0.30f),
        end = Offset(cx - w * 0.08f, b.top + h * 0.44f),
        strokeWidth = 2.4f,
        cap = StrokeCap.Round
    )
    drawLine(
        color = m.specular,
        start = Offset(crossCx - arm * 0.3f, b.top + h * 0.04f),
        end = Offset(crossCx - arm * 0.3f, b.top + h * 0.20f),
        strokeWidth = 1.8f,
        cap = StrokeCap.Round
    )
}
