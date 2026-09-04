package com.onlinechessgame.app.chess.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.onlinechessgame.app.R
import com.onlinechessgame.app.chess.data.repository.ChessRepository
import com.onlinechessgame.app.chess.model.ChessPieceColorTheme
import com.onlinechessgame.app.chess.model.ChessPieceStyle
import com.onlinechessgame.app.chess.model.Country
import com.onlinechessgame.app.chess.model.DEFAULT_COUNTRIES
import com.onlinechessgame.app.chess.model.Piece
import com.onlinechessgame.app.chess.model.PieceColor
import com.onlinechessgame.app.chess.model.PieceType
import com.onlinechessgame.app.chess.ui.components.AvatarSelectionModalDialog
import com.onlinechessgame.app.chess.ui.components.BoardThemeStyle
import com.onlinechessgame.app.chess.ui.components.ChessPiece3D
import com.onlinechessgame.app.chess.ui.viewmodel.ChessViewModel

@Composable
fun ProfileScreen(
    viewModel: ChessViewModel,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.userProfile.collectAsState()
    val matchHistory by viewModel.gameUiState.collectAsState()

    var showEditNameDialog by remember { mutableStateOf(false) }
    var editNameInput by remember { mutableStateOf("") }
    var showCountryDialog by remember { mutableStateOf(false) }
    var showFullAvatarSelectionModal by remember { mutableStateOf(false) }
    var showRulesModal by remember { mutableStateOf(false) }
    var showPrivacyModal by remember { mutableStateOf(false) }
    var showPieceColorDialog by remember { mutableStateOf(false) }
    var showPieceStyleDialog by remember { mutableStateOf(false) }
    var showBoardThemeDialog by remember { mutableStateOf(false) }

    val referralCode by viewModel.userReferralCode.collectAsState()
    val referralCount by viewModel.referralCount.collectAsState()
    val isReferralRedeemed by viewModel.isReferralRedeemed.collectAsState()
    val referralStatusMessage by viewModel.referralStatusMessage.collectAsState()

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var inputFriendCode by remember { mutableStateOf("") }
    var isEnteringCode by remember { mutableStateOf(false) }

    val allAvatars = remember { ChessRepository.getAvailableAvatars() }
    val currentAvatar = remember(profile?.selectedAvatarId) {
        allAvatars.find { it.id == profile?.selectedAvatarId } ?: allAvatars.first()
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0B0F17))
            .padding(horizontal = 16.dp)
            .testTag("profile_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        // Top Profile Banner Card with Level, XP, Gems & Avatar Customizer
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("profile_banner_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131D31)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E3A8A))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Big Avatar Circle with Level Badge
                    Box(
                        modifier = Modifier
                            .size(92.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(88.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1E293B))
                                .border(
                                    3.dp,
                                    Brush.linearGradient(
                                        listOf(Color(0xFFFDE68A), Color(0xFFF59E0B), Color(0xFF38BDF8))
                                    ),
                                    CircleShape
                                )
                                .clickable { showFullAvatarSelectionModal = true }
                                .testTag("active_avatar_circle"),
                            contentAlignment = Alignment.Center
                        ) {
                            if (currentAvatar.resId != null) {
                                Image(
                                    painter = painterResource(id = currentAvatar.resId),
                                    contentDescription = "Active Avatar",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Text(currentAvatar.iconEmoji.ifEmpty { "👑" }, fontSize = 42.sp)
                            }
                        }

                        // Level badge pill (Bottom Center)
                        Surface(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .offset(y = 6.dp),
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFF59E0B),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFFDE68A))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("★", color = Color(0xFF0F172A), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Lv. ${profile?.level ?: 15}",
                                    color = Color(0xFF0F172A),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Username + Edit button
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = profile?.username ?: "PlayerOne",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        IconButton(
                            onClick = {
                                editNameInput = profile?.username ?: ""
                                showEditNameDialog = true
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Name",
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // XP Progress Bar
                    val currentXp = profile?.xp ?: 1200
                    val maxXp = profile?.maxXp ?: 2000
                    val xpProgress = (currentXp.toFloat() / maxXp.toFloat()).coerceIn(0f, 1f)

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("EXPERIENCE", color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text("$currentXp / $maxXp XP", color = Color(0xFFFDE68A), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF0F172A))
                                .border(0.5.dp, Color(0xFF334155), RoundedCornerShape(4.dp))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(xpProgress)
                                    .height(8.dp)
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(Color(0xFF38BDF8), Color(0xFF2563EB))
                                        )
                                    )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Country Flag & Change Avatar Buttons Row
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF1E293B),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                            modifier = Modifier
                                .clickable { showCountryDialog = true }
                                .testTag("change_country_chip")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = profile?.countryFlag ?: "🇺🇸", fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = profile?.countryName ?: "United States",
                                    color = Color(0xFFE2E8F0),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF1E3A8A).copy(alpha = 0.4f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38BDF8)),
                            modifier = Modifier
                                .clickable { showFullAvatarSelectionModal = true }
                                .testTag("btn_open_avatar_selection_modal")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("🪽", fontSize = 13.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Avatars",
                                    color = Color(0xFF38BDF8),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Gems & Tokens & Rating summary row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF0F172A), RoundedCornerShape(14.dp))
                            .padding(vertical = 10.dp, horizontal = 12.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("GEMS", color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("💎", fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "${profile?.gems ?: 650}",
                                    color = Color(0xFF34D399),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .height(28.dp)
                                .width(1.dp)
                                .background(Color(0xFF334155))
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("TOKENS", color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🪙", fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "${profile?.tokens ?: 1000}",
                                    color = Color(0xFFFBBF24),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .height(28.dp)
                                .width(1.dp)
                                .background(Color(0xFF334155))
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("RATING", color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "${profile?.ratingPoints ?: 1200}",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }
                }
            }
        }

        // =========================================================================
        // CUSTOMIZATION CONTROLS: CHESS PIECE COLOR, CHESS PIECES & CHESS BOARD
        // =========================================================================
        item {
            val activePieceColorKey = profile?.pieceColor ?: "CLASSIC"
            val activePieceStyleKey = profile?.pieceStyle ?: "TOURNAMENT_PLASTIC"
            val activeBoardThemeKey = profile?.boardTheme ?: "SIMPLE_GREEN_BUFF"

            val currentPieceColor = ChessPieceColorTheme.fromKey(activePieceColorKey)
            val currentPieceStyle = ChessPieceStyle.entries.find { it.id == activePieceStyleKey }
                ?: ChessPieceStyle.TOURNAMENT_PLASTIC
            val currentBoardTheme = BoardThemeStyle.fromKey(activeBoardThemeKey)

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // 0. Chess Pieces Color Horizontal Row Button (Right above Chess Pieces)
                Card(
                    onClick = { showPieceColorDialog = true },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    border = BorderStroke(1.dp, Color(0xFF334155)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("row_chess_piece_color")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(currentPieceColor.swatchColor.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                                    .border(1.dp, currentPieceColor.swatchColor.copy(alpha = 0.8f), RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .background(currentPieceColor.swatchColor, CircleShape)
                                        .border(1.dp, Color.White.copy(alpha = 0.6f), CircleShape)
                                )
                            }
                            Column {
                                Text(
                                    text = "Chess Pieces Color",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = currentPieceColor.title,
                                    color = Color(0xFF38BDF8),
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = "Open Chess Pieces Color Dialog",
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                // 1. Chess Pieces Horizontal Row Button
                Card(
                    onClick = { showPieceStyleDialog = true },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    border = BorderStroke(1.dp, Color(0xFF334155)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("row_chess_pieces")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color(0xFF0284C7).copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                                    .border(1.dp, Color(0xFF38BDF8).copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("♟️", fontSize = 20.sp)
                            }
                            Column {
                                Text(
                                    text = "Chess Pieces",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = currentPieceStyle.title,
                                    color = Color(0xFF38BDF8),
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = "Open Chess Pieces Dialog",
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                // 2. Chess Board Horizontal Row Button (Right below Chess Pieces)
                Card(
                    onClick = { showBoardThemeDialog = true },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    border = BorderStroke(1.dp, Color(0xFF334155)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("row_chess_board")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            // Dual Square Color Swatch
                            Row(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                            ) {
                                Box(modifier = Modifier.weight(1f).fillMaxHeight().background(currentBoardTheme.lightSquare))
                                Box(modifier = Modifier.weight(1f).fillMaxHeight().background(currentBoardTheme.darkSquare))
                            }
                            Column {
                                Text(
                                    text = "Chess Board",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = currentBoardTheme.title,
                                    color = Color(0xFF38BDF8),
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = "Open Chess Board Dialog",
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }



        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("settings_audio_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131D31)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E3A8A).copy(alpha = 0.8f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("⚙️", fontSize = 18.sp)
                        Text(
                            text = "Game Settings & Audio",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }

                    HorizontalDivider(color = Color(0xFF1E293B))

                    // 1. Sound Effects Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFF1E293B),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(if (profile?.soundEffects != false) "🔊" else "🔇", fontSize = 16.sp)
                                }
                            }
                            Column {
                                Text(
                                    text = "Sound Effects",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Move, capture & checkmate sounds",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Switch(
                            checked = profile?.soundEffects != false,
                            onCheckedChange = { viewModel.toggleSound(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF2563EB),
                                uncheckedThumbColor = Color(0xFF94A3B8),
                                uncheckedTrackColor = Color(0xFF1E293B)
                            ),
                            modifier = Modifier.testTag("switch_sound_effects")
                        )
                    }

                    // 2. Haptic Vibration Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFF1E293B),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("📳", fontSize = 16.sp)
                                }
                            }
                            Column {
                                Text(
                                    text = "Haptic Vibration",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Tactile feedback on piece drops",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Switch(
                            checked = profile?.hapticFeedback != false,
                            onCheckedChange = { viewModel.toggleHaptic(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF2563EB),
                                uncheckedThumbColor = Color(0xFF94A3B8),
                                uncheckedTrackColor = Color(0xFF1E293B)
                            ),
                            modifier = Modifier.testTag("switch_haptic_feedback")
                        )
                    }

                    // 3. Auto-Queen Promotion Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFF1E293B),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("👑", fontSize = 16.sp)
                                }
                            }
                            Column {
                                Text(
                                    text = "Auto-Queen Promotion",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Instantly promote 8th-rank pawns",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Switch(
                            checked = profile?.autoQueenPromotion != false,
                            onCheckedChange = { viewModel.toggleAutoQueen(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF2563EB),
                                uncheckedThumbColor = Color(0xFF94A3B8),
                                uncheckedTrackColor = Color(0xFF1E293B)
                            ),
                            modifier = Modifier.testTag("switch_auto_queen")
                        )
                    }
                }
            }
        }

        // --- NEW FEATURES: RULES & PRIVACY GUIDES ---
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("help_rules_privacy_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131D31)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E3A8A).copy(alpha = 0.8f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "📚 Guides & Policies",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )

                    HorizontalDivider(color = Color(0xFF1E293B))

                    // 1. How to Play Chess (Rules & Strategy)
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFF1E293B),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showRulesModal = true }
                            .testTag("btn_how_to_play_rules")
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFFD97706).copy(alpha = 0.2f),
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text("♟️", fontSize = 20.sp)
                                    }
                                }
                                Column {
                                    Text(
                                        text = "How to Play Chess (Rules)",
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Learn piece moves, castling, en passant & tactics",
                                        color = Color(0xFFCBD5E1),
                                        fontSize = 11.sp,
                                        maxLines = 1
                                    )
                                }
                            }
                            Text("➔", color = Color(0xFFFDE68A), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }

                    // 2. Privacy Policy & Security
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFF1E293B),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showPrivacyModal = true }
                            .testTag("btn_privacy_policy")
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFF10B981).copy(alpha = 0.2f),
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text("🛡️", fontSize = 20.sp)
                                    }
                                }
                                Column {
                                    Text(
                                        text = "Privacy & Fair Play Policy",
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Offline local encryption, zero tracking & guest safety",
                                        color = Color(0xFFCBD5E1),
                                        fontSize = 11.sp,
                                        maxLines = 1
                                    )
                                }
                            }
                            Text("➔", color = Color(0xFF34D399), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }

                    // 3. Account & Local Session Control
                    val isGuest = profile?.isGuest != false
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFF1E293B),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isGuest) Color(0xFF0284C7) else Color(0xFFD97706)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.logout() }
                            .testTag("btn_account_logout")
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = if (isGuest) Color(0xFF0284C7).copy(alpha = 0.2f) else Color(0xFFD97706).copy(alpha = 0.2f),
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(if (isGuest) "👤" else "🔑", fontSize = 20.sp)
                                    }
                                }
                                Column {
                                    Text(
                                        text = if (isGuest) "Playing as Guest" else "Logged in as ${profile?.username}",
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = if (isGuest) "Tap to Sign Up or Log In to your account" else "Tap to Switch User or Log Out",
                                        color = Color(0xFF94A3B8),
                                        fontSize = 11.sp,
                                        maxLines = 1
                                    )
                                }
                            }
                            Text(if (isGuest) "Sign In ➔" else "Log Out ➔", color = if (isGuest) Color(0xFF38BDF8) else Color(0xFFFBBF24), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    // Info footer
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Grandmaster 3D Chess • v2.4.0 • Local Room DB",
                            color = Color(0xFF64748B),
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // =========================================================================
        // COMPACT SHARE & REFER CARD (AT THE VERY BOTTOM OF PROFILE)
        // =========================================================================
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("profile_share_refer_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131D31)),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Brush.horizontalGradient(listOf(Color(0xFF0EA5E9), Color(0xFF10B981)))
                )
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Header row: Title + Bonus Badge
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("🎁", fontSize = 18.sp)
                            Column {
                                Text(
                                    text = "Refer & Earn Bonus",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "Earn +500 Tokens per invited friend",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF10B981).copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = "$referralCount Invited",
                                color = Color(0xFF34D399),
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    // Compact Referral Code Box with Copy & Share
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF0B0F17),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "YOUR CODE",
                                    color = Color(0xFF64748B),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = referralCode,
                                    color = Color(0xFF38BDF8),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.testTag("profile_referral_code_text")
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Button(
                                    onClick = {
                                        clipboardManager.setText(AnnotatedString(referralCode))
                                        Toast.makeText(context, "Copied: $referralCode", Toast.LENGTH_SHORT).show()
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.5f)),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier
                                        .height(34.dp)
                                        .testTag("profile_copy_referral_code_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy",
                                        tint = Color(0xFF38BDF8),
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Copy", color = Color(0xFF38BDF8), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = {
                                        val shareMessage = "♟️ Play 3D Chess Master with me! Use my referral code: $referralCode to get +250 FREE Bonus Tokens! 🎁 Download: https://chess3d.game/refer?code=$referralCode"
                                        val sendIntent = Intent().apply {
                                            action = Intent.ACTION_SEND
                                            putExtra(Intent.EXTRA_TEXT, shareMessage)
                                            type = "text/plain"
                                        }
                                        context.startActivity(Intent.createChooser(sendIntent, "Share Referral Code"))
                                        viewModel.recordShareApp()
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier
                                        .height(34.dp)
                                        .testTag("profile_share_app_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = "Share",
                                        tint = Color.White,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Share", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // Redeem Friend's Code Section (Short)
                    if (isReferralRedeemed) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF34D399),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Referral bonus claimed (+250 Tokens)",
                                color = Color(0xFF34D399),
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    } else {
                        if (!isEnteringCode) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { isEnteringCode = true }
                                    .testTag("profile_toggle_enter_referral_button"),
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF0F172A),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CardGiftcard,
                                        contentDescription = null,
                                        tint = Color(0xFFFBBF24),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Have a Friend's Code? Redeem here",
                                        color = Color(0xFFFDE68A),
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = inputFriendCode,
                                    onValueChange = { inputFriendCode = it.uppercase() },
                                    placeholder = { Text("CHESS-XXXX-XXX", color = Color(0xFF64748B), fontSize = 11.sp) },
                                    singleLine = true,
                                    textStyle = TextStyle(color = Color.White, fontSize = 12.sp, fontFamily = FontFamily.Monospace),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .testTag("profile_input_referral_field"),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF38BDF8),
                                        unfocusedBorderColor = Color(0xFF334155),
                                        focusedContainerColor = Color(0xFF0B0F17),
                                        unfocusedContainerColor = Color(0xFF0B0F17)
                                    ),
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                    keyboardActions = KeyboardActions(onDone = {
                                        if (inputFriendCode.isNotBlank()) {
                                            viewModel.redeemReferralCode(inputFriendCode)
                                        }
                                    })
                                )

                                Spacer(modifier = Modifier.width(6.dp))

                                Button(
                                    onClick = {
                                        if (inputFriendCode.isNotBlank()) {
                                            viewModel.redeemReferralCode(inputFriendCode)
                                        }
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                    modifier = Modifier
                                        .height(44.dp)
                                        .testTag("profile_submit_referral_code_button")
                                ) {
                                    Text("Claim", color = Color.White, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // Compact Status Alert Banner if present
                    referralStatusMessage?.let { (msg, isSuccess) ->
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(6.dp),
                            color = if (isSuccess) Color(0xFF064E3B).copy(alpha = 0.5f) else Color(0xFF881337).copy(alpha = 0.5f),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSuccess) Color(0xFF10B981) else Color(0xFFF43F5E)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = msg,
                                    color = if (isSuccess) Color(0xFFA7F3D0) else Color(0xFFFECDD3),
                                    fontSize = 11.sp,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = { viewModel.clearReferralStatusMessage() },
                                    modifier = Modifier.size(18.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Dismiss",
                                        tint = Color.White.copy(alpha = 0.7f),
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }

    // Edit Username Dialog
    if (showEditNameDialog) {
        AlertDialog(
            onDismissRequest = { showEditNameDialog = false },
            title = { Text("Edit Username", color = Color.White) },
            text = {
                OutlinedTextField(
                    value = editNameInput,
                    onValueChange = { editNameInput = it },
                    label = { Text("New Username") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF38BDF8)
                    )
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateUsername(editNameInput)
                        showEditNameDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showEditNameDialog = false }) {
                    Text("Cancel")
                }
            },
            containerColor = Color(0xFF1E293B)
        )
    }

    // Country Selector Dialog (70+ Countries with Search)
    if (showCountryDialog) {
        var countrySearchQuery by remember { mutableStateOf("") }
        val filteredCountryList = remember(countrySearchQuery) {
            if (countrySearchQuery.isBlank()) {
                DEFAULT_COUNTRIES
            } else {
                DEFAULT_COUNTRIES.filter {
                    it.name.contains(countrySearchQuery, ignoreCase = true) ||
                    it.code.contains(countrySearchQuery, ignoreCase = true)
                }
            }
        }

        AlertDialog(
            onDismissRequest = { showCountryDialog = false },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Select Country", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF2563EB).copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = "${DEFAULT_COUNTRIES.size} Countries",
                            color = Color(0xFF38BDF8),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = countrySearchQuery,
                        onValueChange = { countrySearchQuery = it },
                        placeholder = { Text("Search country (e.g. India, USA, UK)...", fontSize = 12.sp, color = Color(0xFF64748B)) },
                        leadingIcon = { Text("🔍", fontSize = 14.sp) },
                        trailingIcon = {
                            if (countrySearchQuery.isNotEmpty()) {
                                IconButton(onClick = { countrySearchQuery = "" }, modifier = Modifier.size(24.dp)) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color(0xFF94A3B8), modifier = Modifier.size(16.dp))
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF38BDF8),
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedContainerColor = Color(0xFF0F172A),
                            unfocusedContainerColor = Color(0xFF0F172A)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(filteredCountryList) { country ->
                            val isSelected = country.code == profile?.countryCode
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) Color(0xFF2563EB).copy(alpha = 0.35f) else Color(0xFF0F172A).copy(alpha = 0.6f),
                                border = if (isSelected) BorderStroke(1.dp, Color(0xFF38BDF8)) else BorderStroke(0.5.dp, Color(0xFF1E293B)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.selectCountry(country)
                                        showCountryDialog = false
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(country.flag, fontSize = 22.sp)
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = country.name,
                                                color = Color.White,
                                                fontSize = 14.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                            )
                                            Text(
                                                text = country.code,
                                                color = Color(0xFF64748B),
                                                fontSize = 10.sp
                                            )
                                        }
                                    }
                                    if (isSelected) {
                                        Text("✓", color = Color(0xFF38BDF8), fontWeight = FontWeight.Black, fontSize = 16.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                OutlinedButton(
                    onClick = { showCountryDialog = false },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF94A3B8))
                ) {
                    Text("Close")
                }
            },
            containerColor = Color(0xFF1E293B),
            shape = RoundedCornerShape(20.dp)
        )
    }

    // Full 15-Avatar Selection Modal Dialog (Matching the reference layout)
    if (showFullAvatarSelectionModal) {
        AvatarSelectionModalDialog(
            allAvatars = allAvatars,
            selectedAvatarId = profile?.selectedAvatarId ?: "champion_boy",
            playerLevel = profile?.level ?: 15,
            playerGems = profile?.gems ?: 650,
            isAvatarUnlocked = { id -> viewModel.isAvatarUnlocked(id) },
            onSelectAvatar = { id -> viewModel.selectAvatar(id) },
            onBuyAvatar = { avatar -> viewModel.buyAvatarWithGems(avatar) },
            onDismiss = { showFullAvatarSelectionModal = false }
        )
    }

    // Interactive Chess Rules Dialog (How to Play Chess)
    if (showRulesModal) {
        ChessRulesModalDialog(onDismiss = { showRulesModal = false })
    }

    // Privacy & Security Policy Dialog
    if (showPrivacyModal) {
        ChessPrivacyModalDialog(onDismiss = { showPrivacyModal = false })
    }

    // Chess Pieces Color Modal Dialog
    if (showPieceColorDialog) {
        ChessPieceColorModalDialog(
            currentColorId = profile?.pieceColor ?: "CLASSIC",
            onSelectColor = { id -> viewModel.updatePieceColor(id) },
            onDismiss = { showPieceColorDialog = false }
        )
    }

    // Chess Pieces Style Modal Dialog
    if (showPieceStyleDialog) {
        ChessPiecesStyleModalDialog(
            currentStyleId = profile?.pieceStyle ?: "TOURNAMENT_PLASTIC",
            onSelectStyle = { id -> viewModel.updatePieceStyle(id) },
            onDismiss = { showPieceStyleDialog = false }
        )
    }

    // Chess Board Theme Modal Dialog (Normal & 3D Boards)
    if (showBoardThemeDialog) {
        ChessBoardThemeModalDialog(
            currentThemeId = profile?.boardTheme ?: "SIMPLE_GREEN_BUFF",
            onSelectTheme = { id -> viewModel.updateBoardTheme(id) },
            onDismiss = { showBoardThemeDialog = false }
        )
    }
}

// ==========================================
// COMPREHENSIVE CHESS RULES MODAL DIALOG
// ==========================================
@Composable
fun ChessRulesModalDialog(onDismiss: () -> Unit) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Objective 🏆", "Pieces ♟️", "Special ⚡", "Tactics 🧠")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .height(580.dp)
                .clip(RoundedCornerShape(24.dp))
                .testTag("dialog_chess_rules"),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("📖", fontSize = 22.sp)
                        Column {
                            Text(
                                text = "How to Play Chess",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp
                            )
                            Text(
                                text = "Complete Official Rules & Master Guide",
                                color = Color(0xFF38BDF8),
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Text("✕", color = Color(0xFF94A3B8), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Tab Switcher
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color(0xFF1E293B),
                    contentColor = Color(0xFF38BDF8),
                    indicator = { tabPositions ->
                        SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = Color(0xFF38BDF8)
                        )
                    },
                    modifier = Modifier.clip(RoundedCornerShape(12.dp))
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 11.sp,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Scrollable Content
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    when (selectedTab) {
                        0 -> {
                            // Objective & Game Flow
                            item {
                                RuleInfoCard(
                                    title = "🎯 The Main Goal: Checkmate",
                                    icon = "👑",
                                    accentColor = Color(0xFFF59E0B),
                                    description = "The ultimate goal in chess is to checkmate your opponent's King. Checkmate happens when the enemy King is attacked (in 'Check') and has no legal escape moves."
                                )
                            }
                            item {
                                RuleInfoCard(
                                    title = "⚔️ Turns & Moves",
                                    icon = "⏱️",
                                    accentColor = Color(0xFF38BDF8),
                                    description = "White always moves first, followed by Black. Players take alternating turns moving exactly one piece (except during Castling). You cannot pass your turn."
                                )
                            }
                            item {
                                RuleInfoCard(
                                    title = "🤝 Draws & Stalemates",
                                    icon = "⚖️",
                                    accentColor = Color(0xFF10B981),
                                    description = "• Stalemate: The player whose turn it is has no legal moves and is NOT in check.\n• 50-Move Rule: 50 moves without a pawn move or capture.\n• Threefold Repetition: The exact same board position occurs 3 times."
                                )
                            }
                        }
                        1 -> {
                            // Pieces & Movements
                            item {
                                RuleInfoCard(
                                    title = "King (♔ / ♚)",
                                    icon = "👑",
                                    accentColor = Color(0xFFF59E0B),
                                    description = "Moves 1 square in any direction (horizontally, vertically, or diagonally). The most vital piece — if checkmated, the game is lost!"
                                )
                            }
                            item {
                                RuleInfoCard(
                                    title = "Queen (♕ / ♛)",
                                    icon = "💎",
                                    accentColor = Color(0xFFEC4899),
                                    description = "Moves any number of vacant squares in any straight or diagonal direction. The most powerful attacking piece on the board."
                                )
                            }
                            item {
                                RuleInfoCard(
                                    title = "Rook (♖ / ♜)",
                                    icon = "🏰",
                                    accentColor = Color(0xFF38BDF8),
                                    description = "Moves any number of vacant squares horizontally or vertically along ranks and files. Powerful in open endgames."
                                )
                            }
                            item {
                                RuleInfoCard(
                                    title = "Bishop (♗ / ♝)",
                                    icon = "🎯",
                                    accentColor = Color(0xFF8B5CF6),
                                    description = "Moves any number of vacant squares diagonally. Each player starts with one light-squared and one dark-squared Bishop."
                                )
                            }
                            item {
                                RuleInfoCard(
                                    title = "Knight (♘ / ♞)",
                                    icon = "🐴",
                                    accentColor = Color(0xFF10B981),
                                    description = "Moves in an 'L' shape (2 squares in one direction, then 1 square perpendicular). The only piece that can jump over other pieces!"
                                )
                            }
                            item {
                                RuleInfoCard(
                                    title = "Pawn (♙ / ♟)",
                                    icon = "🛡️",
                                    accentColor = Color(0xFF64748B),
                                    description = "Moves 1 square forward (or 2 squares on its very first move). Captures diagonally 1 square forward. Reaching the opposite end triggers Pawn Promotion!"
                                )
                            }
                        }
                        2 -> {
                            // Special Rules
                            item {
                                RuleInfoCard(
                                    title = "🏰 Castling (King Safety)",
                                    icon = "🛡️",
                                    accentColor = Color(0xFF38BDF8),
                                    description = "The King moves two squares toward a Rook, and that Rook hops over the King. Rules:\n1. King & Rook must not have moved yet.\n2. No pieces between them.\n3. King cannot castle out of, through, or into Check."
                                )
                            }
                            item {
                                RuleInfoCard(
                                    title = "⚡ En Passant (In Passing)",
                                    icon = "💨",
                                    accentColor = Color(0xFFF59E0B),
                                    description = "If an enemy pawn moves forward 2 squares past your pawn's attack square, your pawn can capture it as if it only moved 1 square forward. Must be done immediately on the very next turn!"
                                )
                            }
                            item {
                                RuleInfoCard(
                                    title = "👑 Pawn Promotion",
                                    icon = "✨",
                                    accentColor = Color(0xFF10B981),
                                    description = "When a pawn marches all the way to the 8th rank, it is instantly promoted into a Queen, Rook, Bishop, or Knight of your choice."
                                )
                            }
                        }
                        3 -> {
                            // Grandmaster Tactics & Tips
                            item {
                                RuleInfoCard(
                                    title = "1. Control the Center (e4, d4, e5, d5)",
                                    icon = "🎯",
                                    accentColor = Color(0xFFF59E0B),
                                    description = "Pieces in the center control more squares and can quickly mobilize to both the Kingside and Queenside flanks."
                                )
                            }
                            item {
                                RuleInfoCard(
                                    title = "2. Develop Minor Pieces Early",
                                    icon = "🐴",
                                    accentColor = Color(0xFF38BDF8),
                                    description = "Bring Knights and Bishops out in the first 5-8 moves before launching premature Queen attacks."
                                )
                            }
                            item {
                                RuleInfoCard(
                                    title = "3. Castle Early for King Safety",
                                    icon = "🏰",
                                    accentColor = Color(0xFF10B981),
                                    description = "A King stuck in the open center is vulnerable to sudden tactical pins and mating attacks. Castle within the first 10 moves!"
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Close Button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Got It! Let's Play", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ==========================================
// PRIVACY & SECURITY POLICY MODAL DIALOG
// ==========================================
@Composable
fun ChessPrivacyModalDialog(onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .height(540.dp)
                .clip(RoundedCornerShape(24.dp))
                .testTag("dialog_chess_privacy"),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("🛡️", fontSize = 22.sp)
                        Column {
                            Text(
                                text = "Privacy & Fair Play",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp
                            )
                            Text(
                                text = "Your data safety & gameplay integrity",
                                color = Color(0xFF34D399),
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Text("✕", color = Color(0xFF94A3B8), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Scrollable Content
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        RuleInfoCard(
                            title = "🔒 100% Offline Local Data Storage",
                            icon = "💾",
                            accentColor = Color(0xFF38BDF8),
                            description = "Your match history, ratings, solved tactical puzzles, unlocked avatars, and board themes are encrypted locally on your device via modern Room SQLite DB. No personal identifiable data is sold or shared."
                        )
                    }
                    item {
                        RuleInfoCard(
                            title = "👤 Guest & Anonymous Safety",
                            icon = "🕶️",
                            accentColor = Color(0xFF10B981),
                            description = "You are not required to provide personal phone numbers, contacts, or real email addresses. You can customize your player display name, country flag, and avatar at any time with complete freedom."
                        )
                    }
                    item {
                        RuleInfoCard(
                            title = "⚔️ Fair Play & Anti-Cheat System",
                            icon = "⚖️",
                            accentColor = Color(0xFFF59E0B),
                            description = "To ensure a rewarding competitive environment, games are evaluated by our deterministic heuristic engine to detect non-human external computational assistance and promote true sportsmanship."
                        )
                    }
                    item {
                        RuleInfoCard(
                            title = "🎮 Audio & Haptic Controls",
                            icon = "🎛️",
                            accentColor = Color(0xFFEC4899),
                            description = "Audio playback and haptic vibration motors are strictly controlled by your preferences in the Profile settings menu and only activate upon deliberate piece moves."
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Close Button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Understood & Agreed", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// Reusable Helper Card for Rules & Privacy Items
@Composable
private fun RuleInfoCard(
    title: String,
    icon: String,
    accentColor: Color,
    description: String
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFF1E293B),
        border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(icon, fontSize = 16.sp)
                Text(
                    text = title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = description,
                color = Color(0xFFCBD5E1),
                fontSize = 11.sp,
                lineHeight = 16.sp
            )
        }
    }
}

// ==========================================
// CHESS PIECES COLOR MODAL DIALOG
// ==========================================
@Composable
fun ChessPieceColorModalDialog(
    currentColorId: String,
    onSelectColor: (String) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight()
                .clip(RoundedCornerShape(24.dp))
                .testTag("dialog_chess_pieces_color"),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            border = BorderStroke(1.5.dp, Color(0xFF38BDF8).copy(alpha = 0.6f))
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFF0284C7).copy(alpha = 0.2f), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🎨", fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Select Chess Pieces Color",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF94A3B8))
                    }
                }

                HorizontalDivider(color = Color(0xFF1E293B))

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    ChessPieceColorTheme.entries.forEach { theme ->
                        val isSelected = currentColorId == theme.id
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isSelected) Color(0xFF1E293B) else Color(0xFF131D31),
                            border = BorderStroke(
                                if (isSelected) 1.5.dp else 1.dp,
                                if (isSelected) Color(0xFF38BDF8) else Color(0xFF334155)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSelectColor(theme.id)
                                    onDismiss()
                                }
                                .testTag("modal_piece_color_${theme.id}")
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(30.dp)
                                            .background(theme.swatchColor, CircleShape)
                                            .border(1.5.dp, Color.White.copy(alpha = 0.8f), CircleShape)
                                    )

                                    Column {
                                        Text(
                                            text = theme.title,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = theme.subtitle,
                                            color = Color(0xFF94A3B8),
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                if (isSelected) {
                                    Surface(
                                        shape = RoundedCornerShape(20.dp),
                                        color = Color(0xFF0284C7)
                                    ) {
                                        Text(
                                            text = "EQUIPPED",
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// CHESS PIECES STYLE MODAL DIALOG
// ==========================================
@Composable
fun ChessPiecesStyleModalDialog(
    currentStyleId: String,
    onSelectStyle: (String) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight()
                .clip(RoundedCornerShape(24.dp))
                .testTag("dialog_chess_pieces_style"),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            border = BorderStroke(1.5.dp, Color(0xFF38BDF8).copy(alpha = 0.6f))
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFF0284C7).copy(alpha = 0.2f), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("♟️", fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Select Chess Pieces Style",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF94A3B8))
                    }
                }

                HorizontalDivider(color = Color(0xFF1E293B))

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    ChessPieceStyle.entries.forEach { style ->
                        val isSelected = currentStyleId == style.id
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isSelected) Color(0xFF1E293B) else Color(0xFF131D31),
                            border = BorderStroke(
                                if (isSelected) 1.5.dp else 1.dp,
                                if (isSelected) Color(0xFF38BDF8) else Color(0xFF334155)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSelectStyle(style.id)
                                    onDismiss()
                                }
                                .testTag("modal_piece_style_${style.id}")
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = style.title,
                                            color = if (isSelected) Color(0xFF38BDF8) else Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = if (isSelected) Color(0xFF0284C7).copy(alpha = 0.3f) else Color(0xFF334155)
                                        ) {
                                            Text(
                                                text = style.badge,
                                                color = if (isSelected) Color(0xFF7DD3FC) else Color(0xFF94A3B8),
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = style.description,
                                        color = Color(0xFF94A3B8),
                                        fontSize = 11.sp,
                                        lineHeight = 14.sp
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Box(
                                    modifier = Modifier
                                        .size(22.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) Color(0xFF38BDF8) else Color.Transparent)
                                        .border(2.dp, if (isSelected) Color(0xFF38BDF8) else Color(0xFF64748B), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isSelected) {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = Color(0xFF090D16),
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// CHESS BOARD THEME MODAL DIALOG (NORMAL & 3D BOARDS)
// ==========================================
@Composable
fun ChessBoardThemeModalDialog(
    currentThemeId: String,
    onSelectTheme: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedCategory by remember { mutableIntStateOf(0) } // 0 = Normal Boards, 1 = 3D Boards

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .height(540.dp)
                .clip(RoundedCornerShape(24.dp))
                .testTag("dialog_chess_board_theme"),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            border = BorderStroke(1.5.dp, Color(0xFF38BDF8).copy(alpha = 0.6f))
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFF0284C7).copy(alpha = 0.2f), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🪵", fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Select Chess Board Theme",
                            color = Color(0xFF38BDF8),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF94A3B8))
                    }
                }

                // 2 Options Tabs (Normal Boards vs 3D Boards)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF131D31), RoundedCornerShape(12.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val categories = listOf("Normal Boards", "3D Boards")
                    categories.forEachIndexed { index, title ->
                        val isSelected = selectedCategory == index
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedCategory = index },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) Color(0xFF0284C7) else Color.Transparent
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = title,
                                    color = if (isSelected) Color.White else Color(0xFF94A3B8),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(color = Color(0xFF1E293B))

                val themes = BoardThemeStyle.entries.filter { if (selectedCategory == 0) it.isSimple else !it.isSimple }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(themes) { theme ->
                        val isSelected = currentThemeId == theme.id
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) Color(0xFF1E293B) else Color(0xFF131D31),
                            border = BorderStroke(
                                if (isSelected) 1.5.dp else 1.dp,
                                if (isSelected) Color(0xFF38BDF8) else Color(0xFF334155)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSelectTheme(theme.id)
                                    onDismiss()
                                }
                                .testTag("modal_board_theme_${theme.id}")
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Row(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                    ) {
                                        Box(modifier = Modifier.weight(1f).fillMaxHeight().background(theme.lightSquare))
                                        Box(modifier = Modifier.weight(1f).fillMaxHeight().background(theme.darkSquare))
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column {
                                        Text(
                                            text = theme.title,
                                            color = if (isSelected) Color(0xFF38BDF8) else Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.5.sp
                                        )
                                        Text(
                                            text = theme.subtitle,
                                            color = Color(0xFF94A3B8),
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) Color(0xFF38BDF8) else Color.Transparent)
                                        .border(2.dp, if (isSelected) Color(0xFF38BDF8) else Color(0xFF64748B), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isSelected) {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = Color(0xFF090D16),
                                            modifier = Modifier.size(13.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
