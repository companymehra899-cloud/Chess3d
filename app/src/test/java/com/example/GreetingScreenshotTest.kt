package com.example

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import com.example.chess.engine.ChessEngine
import com.example.chess.ui.components.BoardThemeStyle
import com.example.chess.ui.components.ChessBoard3D
import com.example.ui.theme.Chess3DTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [34])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun chessBoard3d_screenshot() {
    val engine = ChessEngine()
    composeTestRule.setContent {
      Chess3DTheme {
        Box(
          modifier = Modifier
            .size(400.dp)
            .background(Color(0xFF0B0F17))
            .padding(16.dp)
        ) {
          ChessBoard3D(
            board = engine.board,
            selectedPosition = null,
            legalMoves = emptyList(),
            lastMove = null,
            isCheck = false,
            kingInCheckPos = null,
            boardTheme = BoardThemeStyle.WALNUT_3D,
            flipped = false,
            onSquareClick = {}
          )
        }
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}
