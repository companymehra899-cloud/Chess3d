package com.onlinechessgame.app.chess.model

import androidx.annotation.DrawableRes

enum class PieceColor {
    WHITE, BLACK;

    fun opposite(): PieceColor = if (this == WHITE) BLACK else WHITE
}

enum class PieceType(val notation: String, val value: Int) {
    PAWN("P", 1),
    KNIGHT("N", 3),
    BISHOP("B", 3),
    ROOK("R", 5),
    QUEEN("Q", 9),
    KING("K", 100)
}

data class Piece(
    val type: PieceType,
    val color: PieceColor,
    val hasMoved: Boolean = false
)

data class Position(val row: Int, val col: Int) {
    init {
        require(row in 0..7 && col in 0..7) { "Row and col must be in 0..7: ($row, $col)" }
    }

    val file: Char get() = ('a'.code + col).toChar()
    val rank: Int get() = 8 - row

    fun toAlgebraic(): String = "$file$rank"

    companion object {
        fun fromAlgebraic(alg: String): Position {
            val col = alg[0] - 'a'
            val row = 8 - alg[1].digitToInt()
            return Position(row, col)
        }
    }
}

data class Move(
    val from: Position,
    val to: Position,
    val piece: Piece,
    val capturedPiece: Piece? = null,
    val promotion: PieceType? = null,
    val isCastling: Boolean = false,
    val isEnPassant: Boolean = false
) {
    fun toNotation(): String {
        if (isCastling) {
            return if (to.col > from.col) "O-O" else "O-O-O"
        }
        val pStr = if (piece.type == PieceType.PAWN) {
            if (capturedPiece != null) "${from.file}x" else ""
        } else {
            piece.type.notation + (if (capturedPiece != null) "x" else "")
        }
        val promoStr = if (promotion != null) "=${promotion.notation}" else ""
        return "$pStr${to.toAlgebraic()}$promoStr"
    }
}

enum class GameStatus {
    IN_PROGRESS,
    WHITE_WON,
    BLACK_WON,
    DRAW_STALEMATE,
    DRAW_INSUFFICIENT_MATERIAL,
    DRAW_50_MOVES,
    DRAW_AGREED
}

data class Country(
    val code: String,
    val name: String,
    val flag: String
)

val DEFAULT_COUNTRIES = listOf(
    Country("IN", "India", "🇮🇳"),
    Country("US", "United States", "🇺🇸"),
    Country("GB", "United Kingdom", "🇬🇧"),
    Country("CA", "Canada", "🇨🇦"),
    Country("AU", "Australia", "🇦🇺"),
    Country("DE", "Germany", "🇩🇪"),
    Country("FR", "France", "🇫🇷"),
    Country("ES", "Spain", "🇪🇸"),
    Country("IT", "Italy", "🇮🇹"),
    Country("RU", "Russia", "🇷🇺"),
    Country("CN", "China", "🇨🇳"),
    Country("JP", "Japan", "🇯🇵"),
    Country("KR", "South Korea", "🇰🇷"),
    Country("BR", "Brazil", "🇧🇷"),
    Country("AR", "Argentina", "🇦🇷"),
    Country("MX", "Mexico", "🇲🇽"),
    Country("NO", "Norway", "🇳🇴"),
    Country("UZ", "Uzbekistan", "🇺🇿"),
    Country("NL", "Netherlands", "🇳🇱"),
    Country("PL", "Poland", "🇵🇱"),
    Country("UA", "Ukraine", "🇺🇦"),
    Country("TR", "Turkey", "🇹🇷"),
    Country("SE", "Sweden", "🇸🇪"),
    Country("CH", "Switzerland", "🇨🇭"),
    Country("AM", "Armenia", "🇦🇲"),
    Country("AZ", "Azerbaijan", "🇦🇿"),
    Country("GE", "Georgia", "🇬🇪"),
    Country("KZ", "Kazakhstan", "🇰🇿"),
    Country("IR", "Iran", "🇮🇷"),
    Country("AE", "United Arab Emirates", "🇦🇪"),
    Country("SA", "Saudi Arabia", "🇸🇦"),
    Country("QA", "Qatar", "🇶🇦"),
    Country("EG", "Egypt", "🇪🇬"),
    Country("ZA", "South Africa", "🇿🇦"),
    Country("NG", "Nigeria", "🇳🇬"),
    Country("KE", "Kenya", "🇰🇪"),
    Country("MA", "Morocco", "🇲🇦"),
    Country("GH", "Ghana", "🇬🇭"),
    Country("ID", "Indonesia", "🇮🇩"),
    Country("PH", "Philippines", "🇵🇭"),
    Country("VN", "Vietnam", "🇻🇳"),
    Country("TH", "Thailand", "🇹🇭"),
    Country("MY", "Malaysia", "🇲🇾"),
    Country("SG", "Singapore", "🇸🇬"),
    Country("PK", "Pakistan", "🇵🇰"),
    Country("BD", "Bangladesh", "🇧🇩"),
    Country("LK", "Sri Lanka", "🇱🇰"),
    Country("NP", "Nepal", "🇳🇵"),
    Country("NZ", "New Zealand", "🇳🇿"),
    Country("PT", "Portugal", "🇵🇹"),
    Country("GR", "Greece", "🇬🇷"),
    Country("BE", "Belgium", "🇧🇪"),
    Country("AT", "Austria", "🇦🇹"),
    Country("DK", "Denmark", "🇩🇰"),
    Country("FI", "Finland", "🇫🇮"),
    Country("IE", "Ireland", "🇮🇪"),
    Country("CZ", "Czech Republic", "🇨🇿"),
    Country("HU", "Hungary", "🇭🇺"),
    Country("RO", "Romania", "🇷🇴"),
    Country("CO", "Colombia", "🇨🇴"),
    Country("CL", "Chile", "🇨🇱"),
    Country("PE", "Peru", "🇵🇪"),
    Country("CU", "Cuba", "🇨🇺"),
    Country("RS", "Serbia", "🇷🇸"),
    Country("HR", "Croatia", "🇭🇷"),
    Country("BG", "Bulgaria", "🇧🇬"),
    Country("IL", "Israel", "🇮🇱"),
    Country("IS", "Iceland", "🇮🇸"),
    Country("JM", "Jamaica", "🇯🇲"),
    Country("INT", "International", "🌐")
)

enum class AvatarCategory {
    ALL,
    FREE,
    PREMIUM,
    SPECIAL,
    BOYS,
    GIRLS,
    ANIMALS,
    KINGS,
    REALISTIC_MAN,
    REALISTIC_WOMAN,
    CUSTOM_AVATAR
}

enum class AvatarRarity(val label: String, val colorHex: Long) {
    FREE("FREE", 0xFF10B981),
    COMMON("COMMON", 0xFF38BDF8),
    RARE("RARE", 0xFF6366F1),
    EPIC("EPIC", 0xFFA855F7),
    LEGENDARY("LEGENDARY", 0xFFF59E0B),
    SPECIAL("SPECIAL", 0xFFEC4899)
}

data class AvatarItem(
    val id: String,
    val title: String,
    val category: AvatarCategory,
    val rarity: AvatarRarity = AvatarRarity.FREE,
    @DrawableRes val resId: Int? = null,
    val iconEmoji: String = "",
    val unlocked: Boolean = true,
    val costGems: Int = 0,
    val costTokens: Int = 0,
    val requiredLevel: Int = 1,
    val description: String = "A grandmaster champion ready to conquer the 64 squares.",
    val nationality: String = "International",
    val countryFlag: String = "🌐",
    val gender: String = "Boy",
    val artStyle: String = "3D Game Portrait",
    val playStyle: String = "Tactical Prodigy",
    val biography: String = "Master of classical strategy with deep oil-on-canvas patience.",
    val quote: String = "Every move is a brushstroke of victory."
)

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val senderName: String,
    val message: String,
    val isFromPlayer: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

data class OnlinePlayer(
    val id: String,
    val username: String,
    val country: Country,
    val rating: Int,
    val avatar: AvatarItem,
    val titleBadge: String = "Grandmaster"
)
