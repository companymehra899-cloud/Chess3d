package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.chess.data.local.ChessDatabase
import com.example.chess.data.repository.ChessRepository
import com.example.chess.engine.ChessEngine
import com.example.chess.model.Move
import com.example.chess.model.PieceColor
import com.example.chess.model.PieceType
import com.example.chess.model.Position
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  private lateinit var database: ChessDatabase
  private lateinit var repository: ChessRepository

  @Before
  fun setup() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    database = Room.inMemoryDatabaseBuilder(context, ChessDatabase::class.java)
      .allowMainThreadQueries()
      .build()
    repository = ChessRepository(database.chessDao())
  }

  @After
  fun tearDown() {
    database.close()
  }

  @Test
  fun testAppNameIsChess3D() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Chess 3D", appName)
  }

  @Test
  fun testInitialBonusTokensAndPoints() = runBlocking {
    repository.ensureProfileExists()
    val profile = repository.userProfileFlow.first()
    assertNotNull(profile)
    // Every user gets free bonus 1000 tokens
    assertEquals(1000, profile!!.tokens)
    assertEquals(1200, profile.ratingPoints)
  }

  @Test
  fun testWinAwardsTenPointsAndBonusTokens() = runBlocking {
    repository.ensureProfileExists()
    repository.recordWin(opponentName = "Viktor", opponentFlag = "🇳🇴", opponentRating = 1200, moves = 28)
    val profile = repository.userProfileFlow.first()
    assertNotNull(profile)
    // On win: get 10 points (+10), +50 bonus tokens
    assertEquals(1210, profile!!.ratingPoints)
    assertEquals(1050, profile.tokens)
    assertEquals(1, profile.wins)
  }

  @Test
  fun testLossDeductsTenPoints() = runBlocking {
    repository.ensureProfileExists()
    repository.recordLoss(opponentName = "Magnus", opponentFlag = "🇳🇴", opponentRating = 1200, moves = 35)
    val profile = repository.userProfileFlow.first()
    assertNotNull(profile)
    // On loss: lose 10 points (-10)
    assertEquals(1190, profile!!.ratingPoints)
    assertEquals(1, profile.losses)
  }

  @Test
  fun testChessEngineMoveExecution() {
    val engine = ChessEngine()
    val pawn = engine.getPiece(Position(6, 4)) // e2 pawn
    assertNotNull(pawn)
    assertEquals(PieceType.PAWN, pawn!!.type)
    assertEquals(PieceColor.WHITE, pawn.color)

    // Move e2 to e4
    val move = Move(Position(6, 4), Position(4, 4), pawn)
    val success = engine.makeMove(move)
    assertTrue(success)
    assertEquals(PieceColor.BLACK, engine.currentTurn)
  }
}
