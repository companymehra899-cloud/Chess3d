package com.onlinechessgame.app.chess.engine

import com.onlinechessgame.app.chess.model.Move
import com.onlinechessgame.app.chess.model.PieceColor
import com.onlinechessgame.app.chess.model.PieceType
import com.onlinechessgame.app.chess.model.Position
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ChessEngineTest {

    @Test
    fun pawnAttacksForwardDiagonalsOnly() {
        val engine = ChessEngine()
        engine.setupCustomPosition(
            listOf(
                Triple(Position(4, 4), PieceType.PAWN, PieceColor.WHITE),
                Triple(Position(7, 4), PieceType.KING, PieceColor.WHITE),
                Triple(Position(0, 4), PieceType.KING, PieceColor.BLACK)
            ),
            turn = PieceColor.WHITE
        )
        assertTrue(engine.isSquareAttacked(Position(3, 3), PieceColor.WHITE))
        assertTrue(engine.isSquareAttacked(Position(3, 5), PieceColor.WHITE))
        assertFalse(engine.isSquareAttacked(Position(5, 3), PieceColor.WHITE))
        assertFalse(engine.isSquareAttacked(Position(5, 5), PieceColor.WHITE))
    }

    @Test
    fun getAllLegalMovesWorksForOppositeColor() {
        val engine = ChessEngine()
        val blackMoves = engine.getAllLegalMoves(PieceColor.BLACK)
        assertTrue(blackMoves.isNotEmpty())
        assertEquals(PieceColor.WHITE, engine.currentTurn)
        assertTrue(blackMoves.all { it.piece.color == PieceColor.BLACK })
    }

    @Test
    fun scholarsMateIsCheckmate() {
        val engine = ChessEngine()
        fun play(from: String, to: String) {
            val fromPos = Position.fromAlgebraic(from)
            val toPos = Position.fromAlgebraic(to)
            val piece = engine.getPiece(fromPos)!!
            assertTrue(engine.makeMove(Move(fromPos, toPos, piece)))
        }
        play("e2", "e4")
        play("e7", "e5")
        play("d1", "h5")
        play("b8", "c6")
        play("f1", "c4")
        play("g8", "f6")
        play("h5", "f7")
        assertEquals(com.onlinechessgame.app.chess.model.GameStatus.WHITE_WON, engine.gameStatus)
    }

    @Test
    fun blackBotCanComputeOpeningMove() {
        val engine = ChessEngine()
        engine.makeMove(
            Move(Position.fromAlgebraic("e2"), Position.fromAlgebraic("e4"), engine.getPiece(Position.fromAlgebraic("e2"))!!)
        )
        val move = engine.computeBestMove(PieceColor.BLACK)
        assertTrue(move != null)
        assertEquals(PieceColor.BLACK, move!!.piece.color)
    }
}
