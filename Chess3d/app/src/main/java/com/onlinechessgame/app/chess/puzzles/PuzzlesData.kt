package com.onlinechessgame.app.chess.puzzles

import com.onlinechessgame.app.chess.model.PieceColor
import com.onlinechessgame.app.chess.model.PieceType
import com.onlinechessgame.app.chess.model.Position

data class PuzzleStep(
    val playerMoveFrom: Position,
    val playerMoveTo: Position,
    val opponentReplyFrom: Position? = null,
    val opponentReplyTo: Position? = null
)

data class ChessPuzzle(
    val id: String,
    val title: String,
    val tacticTheme: String,
    val difficulty: String,
    val rewardTokens: Int = 20,
    val rewardPoints: Int = 5,
    val description: String,
    val initialTurn: PieceColor = PieceColor.WHITE,
    val pieces: List<Triple<Position, PieceType, PieceColor>>,
    val solutionSteps: List<PuzzleStep>
)

object ChessPuzzlesRepository {

    val puzzles = listOf(
        // Puzzle 1: Queen & Bishop battery mate on h7
        ChessPuzzle(
            id = "puzzle_1",
            title = "Morning Dawn Mate",
            tacticTheme = "Battery Attack",
            difficulty = "Easy",
            description = "White has a lethal Queen and Bishop battery pointing at h7. Deliver checkmate in 1 move!",
            initialTurn = PieceColor.WHITE,
            pieces = listOf(
                // Black King at g8, pawns at f7, g7, h7, Rook at f8
                Triple(Position(0, 6), PieceType.KING, PieceColor.BLACK), // g8
                Triple(Position(0, 5), PieceType.ROOK, PieceColor.BLACK), // f8
                Triple(Position(1, 5), PieceType.PAWN, PieceColor.BLACK), // f7
                Triple(Position(1, 6), PieceType.PAWN, PieceColor.BLACK), // g7
                Triple(Position(1, 7), PieceType.PAWN, PieceColor.BLACK), // h7
                // White Queen at c2, Bishop at d3, King at g1
                Triple(Position(6, 2), PieceType.QUEEN, PieceColor.WHITE), // c2
                Triple(Position(5, 3), PieceType.BISHOP, PieceColor.WHITE), // d3
                Triple(Position(7, 6), PieceType.KING, PieceColor.WHITE), // g1
                Triple(Position(6, 6), PieceType.PAWN, PieceColor.WHITE), // g2
                Triple(Position(6, 7), PieceType.PAWN, PieceColor.WHITE)  // h2
            ),
            solutionSteps = listOf(
                PuzzleStep(
                    playerMoveFrom = Position(6, 2), // Qc2
                    playerMoveTo = Position(1, 7)    // Qxh7#
                )
            )
        ),
        // Puzzle 2: Classic Back-Rank Checkmate
        ChessPuzzle(
            id = "puzzle_2",
            title = "The Corridors of Gold",
            tacticTheme = "Back Rank Mate",
            difficulty = "Easy",
            description = "Black's King is trapped behind its own pawns on the 8th rank. Infiltrate with your Rook!",
            initialTurn = PieceColor.WHITE,
            pieces = listOf(
                Triple(Position(0, 6), PieceType.KING, PieceColor.BLACK), // g8
                Triple(Position(1, 5), PieceType.PAWN, PieceColor.BLACK), // f7
                Triple(Position(1, 6), PieceType.PAWN, PieceColor.BLACK), // g7
                Triple(Position(1, 7), PieceType.PAWN, PieceColor.BLACK), // h7
                Triple(Position(7, 3), PieceType.ROOK, PieceColor.WHITE), // d1
                Triple(Position(7, 6), PieceType.KING, PieceColor.WHITE), // g1
                Triple(Position(6, 5), PieceType.PAWN, PieceColor.WHITE), // f2
                Triple(Position(6, 6), PieceType.PAWN, PieceColor.WHITE), // g2
                Triple(Position(6, 7), PieceType.PAWN, PieceColor.WHITE)  // h2
            ),
            solutionSteps = listOf(
                PuzzleStep(
                    playerMoveFrom = Position(7, 3), // Rd1
                    playerMoveTo = Position(0, 3)    // Rd8#
                )
            )
        ),
        // Puzzle 3: Royal Knight Fork
        ChessPuzzle(
            id = "puzzle_3",
            title = "The Sovereign Fork",
            tacticTheme = "Knight Fork",
            difficulty = "Medium",
            description = "Black's King and Queen are vulnerable. Jump your Knight into c7 to deliver a devastating royal fork!",
            initialTurn = PieceColor.WHITE,
            pieces = listOf(
                Triple(Position(0, 4), PieceType.KING, PieceColor.BLACK), // e8
                Triple(Position(1, 3), PieceType.QUEEN, PieceColor.BLACK), // d7
                Triple(Position(0, 0), PieceType.ROOK, PieceColor.BLACK), // a8
                Triple(Position(4, 4), PieceType.KNIGHT, PieceColor.WHITE), // e4
                Triple(Position(7, 4), PieceType.KING, PieceColor.WHITE), // e1
                Triple(Position(6, 4), PieceType.PAWN, PieceColor.WHITE)  // e2
            ),
            solutionSteps = listOf(
                PuzzleStep(
                    playerMoveFrom = Position(4, 4), // Ne4
                    playerMoveTo = Position(2, 2),   // Nc6 or c7 check
                    opponentReplyFrom = Position(0, 4),
                    opponentReplyTo = Position(0, 5)
                ),
                PuzzleStep(
                    playerMoveFrom = Position(2, 2),
                    playerMoveTo = Position(1, 3)    // NxQd7 win queen!
                )
            )
        ),
        // Puzzle 4: Smothered Checkmate
        ChessPuzzle(
            id = "puzzle_4",
            title = "Smothered Kingdom",
            tacticTheme = "Smothered Mate",
            difficulty = "Hard",
            description = "The Black King is suffocated by friendly pieces in the corner. Strike with your Knight!",
            initialTurn = PieceColor.WHITE,
            pieces = listOf(
                Triple(Position(0, 7), PieceType.KING, PieceColor.BLACK), // h8
                Triple(Position(0, 6), PieceType.ROOK, PieceColor.BLACK), // g8
                Triple(Position(1, 6), PieceType.PAWN, PieceColor.BLACK), // g7
                Triple(Position(1, 7), PieceType.PAWN, PieceColor.BLACK), // h7
                Triple(Position(2, 4), PieceType.KNIGHT, PieceColor.WHITE), // e6
                Triple(Position(7, 6), PieceType.KING, PieceColor.WHITE)
            ),
            solutionSteps = listOf(
                PuzzleStep(
                    playerMoveFrom = Position(2, 4), // Ne6
                    playerMoveTo = Position(1, 5)    // Nf7#
                )
            )
        )
    )
}
