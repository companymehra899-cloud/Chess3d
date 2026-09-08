package com.onlinechessgame.app.chess.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.onlinechessgame.app.chess.model.ChessPieceStyle
import com.onlinechessgame.app.chess.ui.components.BoardThemeStyle
import com.onlinechessgame.app.chess.ui.viewmodel.ChessViewModel

@Composable
fun SettingsScreen(
    viewModel: ChessViewModel,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.userProfile.collectAsState()
    val referralCode by viewModel.userReferralCode.collectAsState()
    val referralCount by viewModel.referralCount.collectAsState()
    val isReferralRedeemed by viewModel.isReferralRedeemed.collectAsState()
    val referralStatusMessage by viewModel.referralStatusMessage.collectAsState()

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var inputFriendCode by remember { mutableStateOf("") }
    var isEnteringCode by remember { mutableStateOf(false) }

    val simpleBoards = listOf(
        BoardThemeStyle.SIMPLE_GREEN_BUFF,
        BoardThemeStyle.SIMPLE_WOOD_MAPLE,
        BoardThemeStyle.SIMPLE_BLUE_ICE
    )

    val luxuryBoards = listOf(
        BoardThemeStyle.ISOMETRIC_TRUE_3D,
        BoardThemeStyle.MARSHALL_CLASSIC_3D,
        BoardThemeStyle.WALNUT_3D,
        BoardThemeStyle.CYBER_3D,
        BoardThemeStyle.MARBLE_3D,
        BoardThemeStyle.MIDNIGHT_3D,
        BoardThemeStyle.PREMIUM_3D
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0B0F17))
            .padding(horizontal = 16.dp)
            .testTag("settings_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        // Header Title
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = Color(0xFF38BDF8),
                    modifier = Modifier.size(26.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "App Settings",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                    Text(
                        text = "Visuals, Audio, Gameplay & Rules",
                        color = Color(0xFF94A3B8),
                        fontSize = 13.sp
                    )
                }
            }
        }

        // Chess Pieces Style Selector
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("♟️", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Chess Pieces Style",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "Choose between weighted plastic and luxury styles",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.5.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        for (style in ChessPieceStyle.entries) {
                            val isSelected = (profile?.pieceStyle ?: "TOURNAMENT_PLASTIC") == style.id
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) Color(0xFF0F172A) else Color(0xFF131D31),
                                border = androidx.compose.foundation.BorderStroke(
                                    if (isSelected) 2.dp else 1.dp,
                                    if (isSelected) Color(0xFF38BDF8) else Color(0xFF334155)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("settings_piece_style_${style.id}")
                                    .clickable { viewModel.updatePieceStyle(style.id) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = style.title,
                                                color = if (isSelected) Color(0xFF38BDF8) else Color.White,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                fontSize = 13.5.sp
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = Color(0xFF38BDF8).copy(alpha = 0.2f)
                                            ) {
                                                Text(
                                                    text = style.badge,
                                                    color = Color(0xFF7DD3FC),
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                        Text(
                                            text = style.subtitle,
                                            color = Color(0xFF94A3B8),
                                            fontSize = 11.sp
                                        )
                                    }

                                    if (isSelected) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = Color(0xFF10B981).copy(alpha = 0.2f)
                                        ) {
                                            Text(
                                                text = "Active",
                                                color = Color(0xFF34D399),
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
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

        // Chess Board Themes Selector (3 Normal Simple + 3D Luxury)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ColorLens, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Chess Board Themes",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "3 NORMAL SIMPLE BOARDS",
                        color = Color(0xFF38BDF8),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        for (themeStyle in simpleBoards) {
                            val isSelected = (profile?.boardTheme ?: "SIMPLE_GREEN_BUFF") == themeStyle.id
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) Color(0xFF0F172A) else Color(0xFF131D31),
                                border = androidx.compose.foundation.BorderStroke(
                                    if (isSelected) 2.dp else 1.dp,
                                    if (isSelected) Color(0xFF38BDF8) else Color(0xFF334155)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("theme_selector_${themeStyle.id}")
                                    .clickable { viewModel.updateBoardTheme(themeStyle.id) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Row(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                        ) {
                                            Box(modifier = Modifier.size(18.dp).background(themeStyle.lightSquare))
                                            Box(modifier = Modifier.size(18.dp).background(themeStyle.darkSquare))
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column {
                                            Text(
                                                text = themeStyle.title,
                                                color = Color.White,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                fontSize = 13.5.sp
                                            )
                                            Text(
                                                text = themeStyle.subtitle,
                                                color = Color(0xFF94A3B8),
                                                fontSize = 10.5.sp
                                            )
                                        }
                                    }

                                    if (isSelected) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = Color(0xFF10B981).copy(alpha = 0.2f)
                                        ) {
                                            Text(
                                                text = "Active",
                                                color = Color(0xFF34D399),
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "3D BOARDS",
                        color = Color(0xFFFDE68A),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        for (themeStyle in luxuryBoards) {
                            val isSelected = (profile?.boardTheme ?: "SIMPLE_GREEN_BUFF") == themeStyle.id || (profile?.boardTheme ?: "") == themeStyle.name
                            val isTrue3d = themeStyle.id == BoardThemeStyle.ISOMETRIC_TRUE_3D.id
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) Color(0xFF0F172A) else Color(0xFF131D31),
                                border = androidx.compose.foundation.BorderStroke(
                                    if (isSelected) 2.dp else 1.dp,
                                    if (isSelected) Color(0xFFFDE68A) else Color(0xFF334155)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("theme_selector_${themeStyle.id}")
                                    .clickable { viewModel.updateBoardTheme(themeStyle.id) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Row(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                        ) {
                                            Box(modifier = Modifier.size(18.dp).background(themeStyle.lightSquare))
                                            Box(modifier = Modifier.size(18.dp).background(themeStyle.darkSquare))
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = themeStyle.title,
                                                    color = Color.White,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                    fontSize = 13.5.sp
                                                )
                                                if (isTrue3d) {
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Surface(
                                                        shape = RoundedCornerShape(4.dp),
                                                        color = Color(0xFFFDE68A).copy(alpha = 0.18f)
                                                    ) {
                                                        Text(
                                                            text = "TRUE 3D",
                                                            color = Color(0xFFFDE68A),
                                                            fontSize = 9.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                        )
                                                    }
                                                }
                                            }
                                            Text(
                                                text = themeStyle.subtitle,
                                                color = Color(0xFF94A3B8),
                                                fontSize = 10.5.sp
                                            )
                                        }
                                    }

                                    if (isSelected) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = Color(0xFF10B981).copy(alpha = 0.2f)
                                        ) {
                                            Text(
                                                text = "Active",
                                                color = Color(0xFF34D399),
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
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

        // Gameplay Toggles Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Preferences",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Auto Queen toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Auto-Queen Promotion", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            Text("Instantly promote pawns to Queen on reaching back rank", color = Color(0xFF94A3B8), fontSize = 11.sp)
                        }
                        Switch(
                            checked = profile?.autoQueenPromotion ?: true,
                            onCheckedChange = { viewModel.toggleAutoQueen(it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF38BDF8), checkedTrackColor = Color(0xFF0284C7))
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Sound Effects toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Sound Effects", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = if (profile?.soundEffects != false) Color(0xFF10B981).copy(alpha = 0.2f) else Color(0xFFEF4444).copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = if (profile?.soundEffects != false) "ON" else "MUTED",
                                        color = if (profile?.soundEffects != false) Color(0xFF34D399) else Color(0xFFF87171),
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text("Piece move clicks, capture knocks, and victory alerts", color = Color(0xFF94A3B8), fontSize = 11.sp)
                        }
                        Switch(
                            checked = profile?.soundEffects ?: true,
                            onCheckedChange = { viewModel.toggleSound(it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF38BDF8), checkedTrackColor = Color(0xFF0284C7)),
                            modifier = Modifier.testTag("sound_effects_switch")
                        )
                    }

                    // Interactive Sound Preview Chips
                    if (profile?.soundEffects != false) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "PREVIEW AUDIO CLIPS",
                            color = Color(0xFF38BDF8),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF0F172A),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { viewModel.playMoveSound() }
                                    .testTag("preview_move_sound")
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("♟️ Move", color = Color.White, fontSize = 11.5.sp, fontWeight = FontWeight.Medium)
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF0F172A),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { viewModel.playCaptureSound() }
                                    .testTag("preview_capture_sound")
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("⚔️ Capture", color = Color(0xFFF87171), fontSize = 11.5.sp, fontWeight = FontWeight.Medium)
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF0F172A),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                                modifier = Modifier
                                    .weight(1.1f)
                                    .clickable { viewModel.playCheckmateSound() }
                                    .testTag("preview_checkmate_sound")
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("🏆 Checkmate", color = Color(0xFFFBBF24), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF0F172A),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                                modifier = Modifier
                                    .weight(0.9f)
                                    .clickable { viewModel.playClickSound() }
                                    .testTag("preview_click_sound")
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("🔘 Tap", color = Color(0xFF38BDF8), fontSize = 11.5.sp, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Haptic Feedback toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Haptic Feedback", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            Text("Vibrate on move and piece capture", color = Color(0xFF94A3B8), fontSize = 11.sp)
                        }
                        Switch(
                            checked = profile?.hapticFeedback ?: true,
                            onCheckedChange = { viewModel.toggleHaptic(it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF38BDF8), checkedTrackColor = Color(0xFF0284C7))
                        )
                    }
                }
            }
        }

        // ==========================================
        // SHARE & REFER CARD (NEW FEATURE)
        // ==========================================
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("settings_share_refer_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131D31)),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Brush.horizontalGradient(listOf(Color(0xFF0EA5E9), Color(0xFF10B981)))
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(Color(0xFF0284C7), Color(0xFF059669))
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Share",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Share & Refer",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp
                                )
                                Text(
                                    text = "Invite friends & earn bonus rewards",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 11.5.sp
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF10B981).copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("🎁", fontSize = 11.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "FREE REWARDS",
                                    color = Color(0xFF34D399),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 3 Reward stats chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF0F172A),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Friend Gets", color = Color(0xFF94A3B8), fontSize = 10.sp)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("+250 🪙", color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }

                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF0F172A),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("You Earn", color = Color(0xFF94A3B8), fontSize = 10.sp)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("+500 🪙", color = Color(0xFF34D399), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }

                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF0F172A),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Invited", color = Color(0xFF94A3B8), fontSize = 10.sp)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("$referralCount Friends", color = Color(0xFFFBBF24), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Unique Referral Code Container
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF0B0F17),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "YOUR REFERRAL CODE",
                                    color = Color(0xFF64748B),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = referralCode,
                                    color = Color(0xFF38BDF8),
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.testTag("referral_code_text")
                                )
                            }

                            Button(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(referralCode))
                                    Toast.makeText(context, "Referral code copied: $referralCode", Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.5f)),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("copy_referral_code_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy code",
                                    tint = Color(0xFF38BDF8),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Copy",
                                    color = Color(0xFF38BDF8),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Share App Button
                    Button(
                        onClick = {
                            val shareMessage = "♟️ Join me on 3D Chess Master for realistic 3D online matches!\n\n" +
                                    "Use my Referral Code: $referralCode to claim 250 FREE Bonus Tokens instantly! 🎁\n\n" +
                                    "Download & play now: https://chess3d.game/refer?code=$referralCode"

                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, shareMessage)
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, "Share & Refer Chess 3D")
                            context.startActivity(shareIntent)
                            viewModel.recordShareApp()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("share_app_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFF0284C7), Color(0xFF059669))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Share App & Invite Friends",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.5.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Enter a Friend's Referral Code Section
                    if (isReferralRedeemed) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF064E3B).copy(alpha = 0.3f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF059669).copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF34D399),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Friend referral code claimed! (+250 Tokens)",
                                    color = Color(0xFF34D399),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    } else {
                        if (!isEnteringCode) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { isEnteringCode = true }
                                    .testTag("toggle_enter_referral_button"),
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFF0F172A),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CardGiftcard,
                                        contentDescription = null,
                                        tint = Color(0xFFFBBF24),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Have a Friend's Referral Code? Redeem here",
                                        color = Color(0xFFFDE68A),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        } else {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF0F172A), RoundedCornerShape(10.dp))
                                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(10.dp))
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = "Redeem Friend's Code (+250 Tokens)",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedTextField(
                                        value = inputFriendCode,
                                        onValueChange = { inputFriendCode = it.uppercase() },
                                        placeholder = { Text("e.g. CHESS-9821-XYZ", color = Color(0xFF64748B), fontSize = 12.sp) },
                                        singleLine = true,
                                        textStyle = TextStyle(color = Color.White, fontSize = 13.sp, fontFamily = FontFamily.Monospace),
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("input_referral_field"),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = Color(0xFF38BDF8),
                                            unfocusedBorderColor = Color(0xFF334155),
                                            focusedContainerColor = Color(0xFF1E293B),
                                            unfocusedContainerColor = Color(0xFF1E293B)
                                        ),
                                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                        keyboardActions = KeyboardActions(onDone = {
                                            if (inputFriendCode.isNotBlank()) {
                                                viewModel.redeemReferralCode(inputFriendCode)
                                            }
                                        })
                                    )

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Button(
                                        onClick = {
                                            if (inputFriendCode.isNotBlank()) {
                                                viewModel.redeemReferralCode(inputFriendCode)
                                            }
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 10.dp),
                                        modifier = Modifier.testTag("submit_referral_code_button")
                                    ) {
                                        Text("Claim", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    // Status Message Banner if present
                    referralStatusMessage?.let { (msg, isSuccess) ->
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSuccess) Color(0xFF064E3B).copy(alpha = 0.5f) else Color(0xFF881337).copy(alpha = 0.5f),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSuccess) Color(0xFF10B981) else Color(0xFFF43F5E)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = msg,
                                    color = if (isSuccess) Color(0xFFA7F3D0) else Color(0xFFFECDD3),
                                    fontSize = 11.5.sp,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = { viewModel.clearReferralStatusMessage() },
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Dismiss",
                                        tint = Color.White.copy(alpha = 0.7f),
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Rules & Rewards Explanation Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131D31)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E3A8A))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFFFBBF24), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Chess 3D Economy Rules",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "• Every new user receives 1,000 Free Bonus Tokens upon registration.\n" +
                                "• Every Single Match Win awards +10 Rating Points & +50 Tokens.\n" +
                                "• Every Match Loss deducts -10 Rating Points & awards +10 Consolation Tokens.\n" +
                                "• Tactical Puzzles award +20 Tokens & +5 Rating Points on each solution.\n" +
                                "• Share & Refer Program awards +500 Tokens per invited friend & +250 Tokens on redeeming a referral code.",
                        color = Color(0xFFCBD5E1),
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
    }
}
