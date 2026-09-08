package com.onlinechessgame.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.onlinechessgame.app.chess.ui.screens.MainScreen
import com.onlinechessgame.app.chess.ui.viewmodel.ChessViewModel
import com.onlinechessgame.app.ui.theme.Chess3DTheme

class MainActivity : ComponentActivity() {

  private val chessViewModel: ChessViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      Chess3DTheme {
        MainScreen(viewModel = chessViewModel)
      }
    }
  }
}
