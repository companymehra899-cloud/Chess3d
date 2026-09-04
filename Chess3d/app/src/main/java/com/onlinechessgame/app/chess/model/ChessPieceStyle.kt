package com.onlinechessgame.app.chess.model

enum class ChessPieceStyle(
    val id: String,
    val title: String,
    val subtitle: String,
    val badge: String,
    val description: String
) {
    TOURNAMENT_PLASTIC(
        id = "TOURNAMENT_PLASTIC",
        title = "Weighted Tournament Plastic",
        subtitle = "Standard Club Staunton",
        badge = "PLASTIC",
        description = "Standard heavy weighted club Staunton pieces."
    ),
    WOODEN_MARSHALL(
        id = "WOODEN_MARSHALL",
        title = "Hand-Carved Wooden Marshall",
        subtitle = "Boxwood & Sheesham Wood",
        badge = "WOODEN",
        description = "Classic hand-carved wooden Staunton pieces with green felt base."
    ),
    ROYAL_3D(
        id = "ROYAL_3D",
        title = "Royal Luxury Wood 3D",
        subtitle = "Rosewood & Boxwood",
        badge = "WOOD 3D",
        description = "Handcrafted artisan carved wooden pieces."
    ),
    NEON_CYBER(
        id = "NEON_CYBER",
        title = "Cyber Neon Hologram",
        subtitle = "Tron Cyan & Crimson",
        badge = "CYBER",
        description = "Futuristic glowing laser and neon design."
    ),
    CLASSIC_CLEAN(
        id = "CLASSIC_CLEAN",
        title = "Championship Minimalist",
        subtitle = "FIDE Tournament",
        badge = "CLEAN",
        description = "Razor-sharp tournament silhouettes."
    );

    companion object {
        fun fromKey(key: String?): ChessPieceStyle {
            return entries.find { it.id == key } ?: TOURNAMENT_PLASTIC
        }
    }
}
