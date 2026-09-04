package com.onlinechessgame.app.chess.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter

enum class ChessPieceColorTheme(
    val id: String,
    val title: String,
    val subtitle: String,
    val swatchColor: Color,
    val whiteTint: ColorFilter?,
    val blackTint: ColorFilter?
) {
    CLASSIC(
        id = "CLASSIC",
        title = "Classic Natural",
        subtitle = "Standard White & Dark Pieces",
        swatchColor = Color(0xFF38BDF8),
        whiteTint = null,
        blackTint = null
    ),
    RED(
        id = "RED",
        title = "Ruby Red",
        subtitle = "Crimson Red & Ruby Pieces",
        swatchColor = Color(0xFFEF4444),
        whiteTint = ColorFilter.lighting(Color(0xFFFECDD3), Color(0xFF991B1B).copy(alpha = 0.25f)),
        blackTint = ColorFilter.lighting(Color(0xFFFCA5A5), Color(0xFF7F1D1D))
    ),
    YELLOW(
        id = "YELLOW",
        title = "Golden Yellow",
        subtitle = "Amber & Gold Tinted Pieces",
        swatchColor = Color(0xFFEAB308),
        whiteTint = ColorFilter.lighting(Color(0xFFFEF08A), Color(0xFF854D0E).copy(alpha = 0.2f)),
        blackTint = ColorFilter.lighting(Color(0xFFFDE047), Color(0xFF713F12))
    ),
    BROWN(
        id = "BROWN",
        title = "Mahogany Brown",
        subtitle = "Warm Wooden Walnut & Birch",
        swatchColor = Color(0xFFB45309),
        whiteTint = ColorFilter.lighting(Color(0xFFFFF7ED), Color(0xFF92400E).copy(alpha = 0.2f)),
        blackTint = ColorFilter.lighting(Color(0xFFFED7AA), Color(0xFF451A03))
    ),
    BLACK(
        id = "BLACK",
        title = "Midnight Black",
        subtitle = "Jet Black & Charcoal Silver",
        swatchColor = Color(0xFF334155),
        whiteTint = ColorFilter.lighting(Color(0xFFE2E8F0), Color(0xFF1E293B).copy(alpha = 0.2f)),
        blackTint = ColorFilter.lighting(Color(0xFF94A3B8), Color(0xFF020617))
    ),
    WHITE(
        id = "WHITE",
        title = "Pure Ivory White",
        subtitle = "Clean Ivory & Snow White",
        swatchColor = Color(0xFFF8FAFC),
        whiteTint = ColorFilter.lighting(Color(0xFFFFFFFF), Color(0xFFE2E8F0).copy(alpha = 0.3f)),
        blackTint = ColorFilter.lighting(Color(0xFFE2E8F0), Color(0xFF334155))
    );

    companion object {
        fun fromKey(key: String?): ChessPieceColorTheme {
            return entries.find { it.id == key } ?: CLASSIC
        }
    }
}
