package com.onlinechessgame.app.chess.engine

import com.onlinechessgame.app.chess.model.GameStatus
import com.onlinechessgame.app.chess.model.Move
import com.onlinechessgame.app.chess.model.Piece
import com.onlinechessgame.app.chess.model.PieceColor
import com.onlinechessgame.app.chess.model.PieceType
import com.onlinechessgame.app.chess.model.Position
import kotlin.math.abs

class ChessEngine {

    val board: Array<Array<Piece?>> = Array(8) { Array(8) { null } }
    var currentTurn: PieceColor = PieceColor.WHITE
    var enPassantTarget: Position? = null
    var halfMoveClock: Int = 0
    var fullMoveNumber: Int = 1

    val moveHistory = mutableListOf<Move>()
    val capturedByWhite = mutableListOf<Piece>()
    val capturedByBlack = mutableListOf<Piece>()

    var gameStatus: GameStatus = GameStatus.IN_PROGRESS

    init {
        setupStandardBoard()
    }

    fun setupStandardBoard() {
        for (r in 0..7) {
            for (c in 0..7) {
                board[r][c] = null
            }
        }
        moveHistory.clear()
        capturedByWhite.clear()
        capturedByBlack.clear()
        currentTurn = PieceColor.WHITE
        enPassantTarget = null
        halfMoveClock = 0
        fullMoveNumber = 1
        gameStatus = GameStatus.IN_PROGRESS

        // Setup Black pieces (row 0 & 1)
        board[0][0] = Piece(PieceType.ROOK, PieceColor.BLACK)
        board[0][1] = Piece(PieceType.KNIGHT, PieceColor.BLACK)
        board[0][2] = Piece(PieceType.BISHOP, PieceColor.BLACK)
        board[0][3] = Piece(PieceType.QUEEN, PieceColor.BLACK)
        board[0][4] = Piece(PieceType.KING, PieceColor.BLACK)
        board[0][5] = Piece(PieceType.BISHOP, PieceColor.BLACK)
        board[0][6] = Piece(PieceType.KNIGHT, PieceColor.BLACK)
        board[0][7] = Piece(PieceType.ROOK, PieceColor.BLACK)
        for (c in 0..7) {
            board[1][c] = Piece(PieceType.PAWN, PieceColor.BLACK)
        }

        // Setup White pieces (row 6 & 7)
        for (c in 0..7) {
            board[6][c] = Piece(PieceType.PAWN, PieceColor.WHITE)
        }
        board[7][0] = Piece(PieceType.ROOK, PieceColor.WHITE)
        board[7][1] = Piece(PieceType.KNIGHT, PieceColor.WHITE)
        board[7][2] = Piece(PieceType.BISHOP, PieceColor.WHITE)
        board[7][3] = Piece(PieceType.QUEEN, PieceColor.WHITE)
        board[7][4] = Piece(PieceType.KING, PieceColor.WHITE)
        board[7][5] = Piece(PieceType.BISHOP, PieceColor.WHITE)
        board[7][6] = Piece(PieceType.KNIGHT, PieceColor.WHITE)
        board[7][7] = Piece(PieceType.ROOK, PieceColor.WHITE)
    }

    fun getPiece(pos: Position): Piece? = board[pos.row][pos.col]

    fun findKing(color: PieceColor): Position? {
        for (r in 0..7) {
            for (c in 0..7) {
                val p = board[r][c]
                if (p != null && p.type == PieceType.KING && p.color == color) {
                    return Position(r, c)
                }
            }
        }
        return null
    }

    fun isSquareAttacked(pos: Position, byColor: PieceColor): Boolean {
        for (r in 0..7) {
            for (c in 0..7) {
                val p = board[r][c]
                if (p != null && p.color == byColor) {
                    val candidateMoves = getPseudoLegalMoves(Position(r, c), considerCastling = false)
                    if (candidateMoves.any { it.to == pos }) {
                        return true
                    }
                }
            }
        }
        return false
    }

    fun isInCheck(color: PieceColor): Boolean {
        val kingPos = findKing(color) ?: return false
        return isSquareAttacked(kingPos, color.opposite())
    }

    fun getLegalMoves(pos: Position): List<Move> {
        val piece = getPiece(pos) ?: return emptyList()
        if (piece.color != currentTurn) return emptyList()

        val pseudo = getPseudoLegalMoves(pos, considerCastling = true)
        val validMoves = mutableListOf<Move>()

        for (move in pseudo) {
            // Simulate move
            val originalFromPiece = board[move.from.row][move.from.col]
            val originalToPiece = board[move.to.row][move.to.col]
            val originalEp = enPassantTarget

            // Apply temporarily
            board[move.to.row][move.to.col] = move.promotion?.let { Piece(it, piece.color, true) }
                ?: originalFromPiece?.copy(hasMoved = true)
            board[move.from.row][move.from.col] = null

            var epCapturedP: Piece? = null
            var epPos: Position? = null
            if (move.isEnPassant) {
                val capRow = if (piece.color == PieceColor.WHITE) move.to.row + 1 else move.to.row - 1
                epPos = Position(capRow, move.to.col)
                epCapturedP = board[capRow][move.to.col]
                board[capRow][move.to.col] = null
            }

            val kingSafe = !isInCheck(piece.color)

            // Undo
            board[move.from.row][move.from.col] = originalFromPiece
            board[move.to.row][move.to.col] = originalToPiece
            if (move.isEnPassant && epPos != null) {
                board[epPos.row][epPos.col] = epCapturedP
            }
            enPassantTarget = originalEp

            if (kingSafe) {
                validMoves.add(move)
            }
        }

        return validMoves
    }

    fun getAllLegalMoves(color: PieceColor): List<Move> {
        val all = mutableListOf<Move>()
        for (r in 0..7) {
            for (c in 0..7) {
                val p = board[r][c]
                if (p != null && p.color == color) {
                    all.addAll(getLegalMoves(Position(r, c)))
                }
            }
        }
        return all
    }

    private fun getPseudoLegalMoves(pos: Position, considerCastling: Boolean): List<Move> {
        val piece = board[pos.row][pos.col] ?: return emptyList()
        val moves = mutableListOf<Move>()

        when (piece.type) {
            PieceType.PAWN -> {
                val forward = if (piece.color == PieceColor.WHITE) -1 else 1
                val startRow = if (piece.color == PieceColor.WHITE) 6 else 1
                val promoRow = if (piece.color == PieceColor.WHITE) 0 else 7

                // 1 step forward
                val nextR = pos.row + forward
                if (nextR in 0..7 && board[nextR][pos.col] == null) {
                    if (nextR == promoRow) {
                        listOf(PieceType.QUEEN, PieceType.ROOK, PieceType.BISHOP, PieceType.KNIGHT).forEach { promo ->
                            moves.add(Move(pos, Position(nextR, pos.col), piece, promotion = promo))
                        }
                    } else {
                        moves.add(Move(pos, Position(nextR, pos.col), piece))
                    }

                    // 2 steps forward
                    val doubleR = pos.row + 2 * forward
                    if (pos.row == startRow && board[doubleR][pos.col] == null) {
                        moves.add(Move(pos, Position(doubleR, pos.col), piece))
                    }
                }

                // Captures
                for (dc in listOf(-1, 1)) {
                    val capC = pos.col + dc
                    if (capC in 0..7 && nextR in 0..7) {
                        val target = board[nextR][capC]
                        if (target != null && target.color != piece.color) {
                            if (nextR == promoRow) {
                                listOf(PieceType.QUEEN, PieceType.ROOK, PieceType.BISHOP, PieceType.KNIGHT).forEach { promo ->
                                    moves.add(Move(pos, Position(nextR, capC), piece, capturedPiece = target, promotion = promo))
                                }
                            } else {
                                moves.add(Move(pos, Position(nextR, capC), piece, capturedPiece = target))
                            }
                        } else if (enPassantTarget == Position(nextR, capC)) {
                            // En passant
                            val capPiece = board[pos.row][capC]
                            moves.add(Move(pos, Position(nextR, capC), piece, capturedPiece = capPiece, isEnPassant = true))
                        }
                    }
                }
            }

            PieceType.KNIGHT -> {
                val offsets = listOf(
                    -2 to -1, -2 to 1, -1 to -2, -1 to 2,
                    1 to -2, 1 to 2, 2 to -1, 2 to 1
                )
                for ((dr, dc) in offsets) {
                    val nr = pos.row + dr
                    val nc = pos.col + dc
                    if (nr in 0..7 && nc in 0..7) {
                        val target = board[nr][nc]
                        if (target == null || target.color != piece.color) {
                            moves.add(Move(pos, Position(nr, nc), piece, capturedPiece = target))
                        }
                    }
                }
            }

            PieceType.BISHOP -> {
                addRayMoves(pos, piece, listOf(-1 to -1, -1 to 1, 1 to -1, 1 to 1), moves)
            }

            PieceType.ROOK -> {
                addRayMoves(pos, piece, listOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1), moves)
            }

            PieceType.QUEEN -> {
                addRayMoves(pos, piece, listOf(
                    -1 to -1, -1 to 1, 1 to -1, 1 to 1,
                    -1 to 0, 1 to 0, 0 to -1, 0 to 1
                ), moves)
            }

            PieceType.KING -> {
                for (dr in -1..1) {
                    for (dc in -1..1) {
                        if (dr == 0 && dc == 0) continue
                        val nr = pos.row + dr
                        val nc = pos.col + dc
                        if (nr in 0..7 && nc in 0..7) {
                            val target = board[nr][nc]
                            if (target == null || target.color != piece.color) {
                                moves.add(Move(pos, Position(nr, nc), piece, capturedPiece = target))
                            }
                        }
                    }
                }

                // Castling
                if (considerCastling && !piece.hasMoved && !isInCheck(piece.color)) {
                    val r = pos.row
                    // Kingside
                    val kRook = board[r][7]
                    if (kRook != null && kRook.type == PieceType.ROOK && !kRook.hasMoved && kRook.color == piece.color) {
                        if (board[r][5] == null && board[r][6] == null) {
                            if (!isSquareAttacked(Position(r, 5), piece.color.opposite()) &&
                                !isSquareAttacked(Position(r, 6), piece.color.opposite())
                            ) {
                                moves.add(Move(pos, Position(r, 6), piece, isCastling = true))
                            }
                        }
                    }
                    // Queenside
                    val qRook = board[r][0]
                    if (qRook != null && qRook.type == PieceType.ROOK && !qRook.hasMoved && qRook.color == piece.color) {
                        if (board[r][1] == null && board[r][2] == null && board[r][3] == null) {
                            if (!isSquareAttacked(Position(r, 2), piece.color.opposite()) &&
                                !isSquareAttacked(Position(r, 3), piece.color.opposite())
                            ) {
                                moves.add(Move(pos, Position(r, 2), piece, isCastling = true))
                            }
                        }
                    }
                }
            }
        }

        return moves
    }

    private fun addRayMoves(
        pos: Position,
        piece: Piece,
        directions: List<Pair<Int, Int>>,
        outMoves: MutableList<Move>
    ) {
        for ((dr, dc) in directions) {
            var r = pos.row + dr
            var c = pos.col + dc
            while (r in 0..7 && c in 0..7) {
                val target = board[r][c]
                if (target == null) {
                    outMoves.add(Move(pos, Position(r, c), piece))
                } else {
                    if (target.color != piece.color) {
                        outMoves.add(Move(pos, Position(r, c), piece, capturedPiece = target))
                    }
                    break
                }
                r += dr
                c += dc
            }
        }
    }

    fun makeMove(move: Move): Boolean {
        val legalMoves = getLegalMoves(move.from)
        val validMove = legalMoves.find {
            it.from == move.from && it.to == move.to && (it.promotion == move.promotion || move.promotion == null)
        } ?: return false

        val actualMove = if (move.promotion == null && validMove.promotion != null) {
            // Default to queen if promotion requested without explicit piece
            validMove.copy(promotion = PieceType.QUEEN)
        } else {
            validMove
        }

        // Handle piece capture tracking
        val captured = actualMove.capturedPiece
        if (captured != null) {
            if (currentTurn == PieceColor.WHITE) {
                capturedByWhite.add(captured)
            } else {
                capturedByBlack.add(captured)
            }
        }

        // Apply piece move
        val movedPiece = actualMove.piece.copy(hasMoved = true)
        board[actualMove.from.row][actualMove.from.col] = null

        if (actualMove.promotion != null) {
            board[actualMove.to.row][actualMove.to.col] = Piece(actualMove.promotion, movedPiece.color, true)
        } else {
            board[actualMove.to.row][actualMove.to.col] = movedPiece
        }

        // Handle Castling Rook move
        if (actualMove.isCastling) {
            val r = actualMove.from.row
            if (actualMove.to.col == 6) {
                // Kingside
                val rook = board[r][7]?.copy(hasMoved = true)
                board[r][7] = null
                board[r][5] = rook
            } else if (actualMove.to.col == 2) {
                // Queenside
                val rook = board[r][0]?.copy(hasMoved = true)
                board[r][0] = null
                board[r][3] = rook
            }
        }

        // Handle En Passant remove
        if (actualMove.isEnPassant) {
            val capRow = if (actualMove.piece.color == PieceColor.WHITE) actualMove.to.row + 1 else actualMove.to.row - 1
            board[capRow][actualMove.to.col] = null
        }

        // Update En Passant target
        if (actualMove.piece.type == PieceType.PAWN && abs(actualMove.to.row - actualMove.from.row) == 2) {
            val midRow = (actualMove.from.row + actualMove.to.row) / 2
            enPassantTarget = Position(midRow, actualMove.from.col)
        } else {
            enPassantTarget = null
        }

        // Half move clock
        if (actualMove.piece.type == PieceType.PAWN || actualMove.capturedPiece != null) {
            halfMoveClock = 0
        } else {
            halfMoveClock++
        }

        if (currentTurn == PieceColor.BLACK) {
            fullMoveNumber++
        }

        moveHistory.add(actualMove)

        // Switch turn
        currentTurn = currentTurn.opposite()

        // Check game status
        val nextLegalMoves = getAllLegalMoves(currentTurn)
        if (nextLegalMoves.isEmpty()) {
            if (isInCheck(currentTurn)) {
                gameStatus = if (currentTurn == PieceColor.WHITE) GameStatus.BLACK_WON else GameStatus.WHITE_WON
            } else {
                gameStatus = GameStatus.DRAW_STALEMATE
            }
        } else if (halfMoveClock >= 100) {
            gameStatus = GameStatus.DRAW_50_MOVES
        } else if (isInsufficientMaterial()) {
            gameStatus = GameStatus.DRAW_INSUFFICIENT_MATERIAL
        }

        return true
    }

    private fun isInsufficientMaterial(): Boolean {
        val pieces = mutableListOf<Piece>()
        for (r in 0..7) {
            for (c in 0..7) {
                board[r][c]?.let { pieces.add(it) }
            }
        }
        if (pieces.size <= 2) return true // K vs K
        if (pieces.size == 3 && pieces.any { it.type == PieceType.BISHOP || it.type == PieceType.KNIGHT }) return true // KB vs K or KN vs K
        return false
    }

    // AI evaluation for online random opponent simulation
    fun computeBestMove(color: PieceColor, depth: Int = 2): Move? {
        val legalMoves = getAllLegalMoves(color)
        if (legalMoves.isEmpty()) return null

        var bestMove: Move? = null
        var bestScore = if (color == PieceColor.WHITE) -100000 else 100000

        // Shuffle slightly for natural human play variety
        val shuffledMoves = legalMoves.shuffled()

        for (move in shuffledMoves) {
            // Immediate checkmate priority
            val captured = move.capturedPiece
            var score = 0

            if (captured != null) {
                score += captured.type.value * 10
            }
            if (move.promotion == PieceType.QUEEN) {
                score += 90
            }

            // Center control
            if (move.to.row in 3..4 && move.to.col in 3..4) {
                score += 3
            }

            if (color == PieceColor.WHITE) {
                if (score > bestScore) {
                    bestScore = score
                    bestMove = move
                }
            } else {
                val blackScore = -score
                if (blackScore < bestScore) {
                    bestScore = blackScore
                    bestMove = move
                }
            }
        }

        return bestMove ?: shuffledMoves.firstOrNull()
    }

    // Setup custom position for puzzles
    fun setupCustomPosition(
        piecePlacements: List<Triple<Position, PieceType, PieceColor>>,
        turn: PieceColor = PieceColor.WHITE
    ) {
        for (r in 0..7) {
            for (c in 0..7) {
                board[r][c] = null
            }
        }
        moveHistory.clear()
        capturedByWhite.clear()
        capturedByBlack.clear()
        currentTurn = turn
        enPassantTarget = null
        halfMoveClock = 0
        gameStatus = GameStatus.IN_PROGRESS

        for ((pos, type, color) in piecePlacements) {
            board[pos.row][pos.col] = Piece(type, color)
        }
    }
}
