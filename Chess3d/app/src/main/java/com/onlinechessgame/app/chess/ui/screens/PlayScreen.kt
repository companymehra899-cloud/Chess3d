package com.onlinechessgame.app.chess.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Token
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.onlinechessgame.app.chess.data.local.UserProfileEntity
import com.onlinechessgame.app.chess.data.repository.ChessRepository
import com.onlinechessgame.app.chess.model.GameStatus
import com.onlinechessgame.app.chess.model.PieceColor
import com.onlinechessgame.app.chess.ui.components.BoardThemeStyle
import com.onlinechessgame.app.chess.ui.components.ChessBoard3D
import com.onlinechessgame.app.chess.ui.components.FloatingChatButton
import com.onlinechessgame.app.chess.ui.components.GameChatSheet
import com.onlinechessgame.app.chess.ui.components.GameEndResultDialog
import com.onlinechessgame.app.chess.ui.components.PawnPromotionDialog
import com.onlinechessgame.app.chess.ui.components.PlayerCard3D
import com.onlinechessgame.app.chess.ui.components.Avatar3DModalDialog
import com.onlinechessgame.app.chess.ui.viewmodel.AppTab
import com.onlinechessgame.app.chess.ui.viewmodel.ChessGameUiState
import com.onlinechessgame.app.chess.ui.viewmodel.ChessViewModel
import com.onlinechessgame.app.chess.ui.viewmodel.MatchmakingState

@Composable
fun PlayScreen(
    viewModel: ChessViewModel,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.userProfile.collectAsState()
    val matchState by viewModel.matchmakingState.collectAsState()
    val gameState by viewModel.gameUiState.collectAsState()
    val searchingPlayer by viewModel.searchingCyclingPlayer.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0B0F17))
    ) {
        when (matchState) {
            MatchmakingState.IDLE -> {
                PlayLobbyView(
                    viewModel = viewModel,
                    profile = profile,
                    onStartMatchmaking = { viewModel.startQuickMatch() }
                )
            }
            MatchmakingState.SEARCHING, MatchmakingState.FOUND -> {
                SearchingOpponentView(
                    matchState = matchState,
                    cyclingPlayer = searchingPlayer,
                    onCancel = { viewModel.cancelMatchmaking() }
                )
            }
            MatchmakingState.IN_GAME -> {
                ActiveGameView(
                    viewModel = viewModel,
                    profile = profile,
                    gameState = gameState
                )
            }
        }
    }
}

@Composable
private fun PlayLobbyView(
    viewModel: ChessViewModel,
    profile: UserProfileEntity?,
    onStartMatchmaking: () -> Unit
) {
    val friends by viewModel.acceptedFriends.collectAsState()
    val pendingCount by viewModel.pendingRequestsCount.collectAsState()
    val selectedTimeMinutes by viewModel.selectedTimeControlMinutes.collectAsState()

    var show3DModalForHeroAvatar by remember { mutableStateOf(false) }
    var showCustomTimeDialog by remember { mutableStateOf(false) }

    val avatarItem = remember(profile?.selectedAvatarId) {
        ChessRepository.getAvailableAvatars().find { it.id == profile?.selectedAvatarId }
            ?: ChessRepository.getAvailableAvatars().first()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        // Top Status & Sound Controls
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Live Status Badge
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF0C1A30),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF10B981))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "1,420 PLAYERS ONLINE",
                            color = Color(0xFF34D399),
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.4.sp
                        )
                    }
                }

                // Quick Sound Toggle (Settings icon removed as requested)
                val soundOn = profile?.soundEffects != false
                Surface(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .clickable { viewModel.toggleSound(!soundOn) },
                    shape = CircleShape,
                    color = Color(0xFF1E293B),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (soundOn) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                            contentDescription = "Toggle Sound",
                            tint = if (soundOn) Color(0xFF38BDF8) else Color(0xFF94A3B8),
                            modifier = Modifier.size(19.dp)
                        )
                    }
                }
            }
        }

        // Hero Profile & Tokens Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("user_profile_hero_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF111A2E)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E3A8A).copy(alpha = 0.7f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // User Avatar & Name
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { show3DModalForHeroAvatar = true }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1E293B))
                                .border(2.dp, Color(0xFFF59E0B), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (avatarItem.resId != null) {
                                Image(
                                    painter = painterResource(id = avatarItem.resId),
                                    contentDescription = "User Avatar",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Text(avatarItem.iconEmoji.ifEmpty { "♟️" }, fontSize = 26.sp)
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = profile?.username ?: "Guest Player",
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 16.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = profile?.countryFlag ?: "🇺🇸", fontSize = 15.sp)
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            // Token Value under username (replaces avatar model name)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🪙", fontSize = 13.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${profile?.tokens ?: 1000} Tokens",
                                    color = Color(0xFFFBBF24),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }

                    // Rating Badge (Gold Glassmorphic)
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFB45309).copy(alpha = 0.25f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.6f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 11.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFFBBF24),
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "${profile?.ratingPoints ?: 1200}",
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }

        // Featured Online Matchmaking Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("quick_online_match_card"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF111E36)),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF38BDF8).copy(alpha = 0.5f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF142852), Color(0xFF0B1324))
                            )
                        )
                        .padding(18.dp)
                ) {
                    Column {
                        // Title row with icon and live badge
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFF0284C7).copy(alpha = 0.25f),
                                    modifier = Modifier.size(44.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38BDF8))
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Public,
                                            contentDescription = null,
                                            tint = Color(0xFF38BDF8),
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = "Play Online Match",
                                        color = Color.White,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 18.sp
                                    )
                                    Text(
                                        text = "Live opponents • 3D Chess Board",
                                        color = Color(0xFF94A3B8),
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF2563EB).copy(alpha = 0.25f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF60A5FA).copy(alpha = 0.4f))
                            ) {
                                Text(
                                    text = "⚡ FAST",
                                    color = Color(0xFF93C5FD),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Match Stakes & Time Format info
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF0C1322), RoundedCornerShape(12.dp))
                                .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(12.dp))
                                .padding(vertical = 10.dp, horizontal = 12.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("WIN MATCH", color = Color(0xFF34D399), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Text("+10 PTS", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("LOSE MATCH", color = Color(0xFFF87171), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Text("-10 PTS", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
                            }
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { showCustomTimeDialog = true }
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("FORMAT", color = Color(0xFF60A5FA), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Icon(Icons.Default.Tune, contentDescription = "Edit Time", tint = Color(0xFF60A5FA), modifier = Modifier.size(10.dp))
                                }
                                Text("$selectedTimeMinutes MIN", color = Color(0xFF38BDF8), fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Time Format Selector Pills (5 min, 10 min, 15 min, Custom)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            listOf(5, 10, 15).forEach { timeOption ->
                                val isSelected = selectedTimeMinutes == timeOption
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { viewModel.setTimeControlMinutes(timeOption) }
                                        .testTag("time_option_${timeOption}min"),
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) Color(0xFF2563EB) else Color(0xFF0C1322),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isSelected) Color(0xFF60A5FA) else Color(0xFF334155)
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier.padding(vertical = 6.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "${timeOption}m",
                                            color = if (isSelected) Color.White else Color(0xFFCBD5E1),
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 13.sp
                                        )
                                        Text(
                                            text = when (timeOption) {
                                                5 -> "Rapid"
                                                10 -> "Rapid"
                                                else -> "Classic"
                                            },
                                            color = if (isSelected) Color(0xFF93C5FD) else Color(0xFF64748B),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }

                            // Custom time button
                            val isCustom = selectedTimeMinutes !in listOf(5, 10, 15)
                            Surface(
                                modifier = Modifier
                                    .weight(1.1f)
                                    .clickable { showCustomTimeDialog = true }
                                    .testTag("time_option_custom"),
                                shape = RoundedCornerShape(10.dp),
                                color = if (isCustom) Color(0xFF2563EB) else Color(0xFF0C1322),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isCustom) Color(0xFF60A5FA) else Color(0xFF334155)
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 6.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = if (isCustom) "${selectedTimeMinutes}m" else "Custom",
                                        color = if (isCustom) Color.White else Color(0xFFCBD5E1),
                                        fontWeight = if (isCustom) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = "⚙️ Choose",
                                        color = if (isCustom) Color(0xFF93C5FD) else Color(0xFF64748B),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Large CTA Matchmaking Button
                        Button(
                            onClick = onStartMatchmaking,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("start_quick_match_button"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2563EB)
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Find Match ($selectedTimeMinutes Min)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }

        // Section Title: Game Modes & Features
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Game Modes & Features",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = "Quick Launch",
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp
                )
            }
        }

        // Row 1: Puzzles & Friends
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Puzzles & Tactics Card
                Card(
                    onClick = { viewModel.setTab(AppTab.PUZZLES) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("quick_access_puzzles_card"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F231D)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.45f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF10B981).copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🧩", fontSize = 20.sp)
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFF10B981).copy(alpha = 0.25f)
                            ) {
                                Text(
                                    text = "DAILY",
                                    color = Color(0xFF34D399),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Chess Puzzles",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Tactics & Mate in 2",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )
                    }
                }

                // Friends & Club Card
                Card(
                    onClick = { viewModel.setTab(AppTab.FRIENDS) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("quick_access_friends_card"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1B38)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF6366F1).copy(alpha = 0.45f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF6366F1).copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("👥", fontSize = 20.sp)
                            }
                            if (pendingCount > 0) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFEF4444)
                                ) {
                                    Text(
                                        text = "$pendingCount NEW",
                                        color = Color.White,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            } else {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFF6366F1).copy(alpha = 0.25f)
                                ) {
                                    Text(
                                        text = "INVITE",
                                        color = Color(0xFFA5B4FC),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Play Friends",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${friends.size} Friends • Chat",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Career Performance Stats
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Career Performance",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = "FIDE Rating Stats",
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "Wins",
                    value = "${profile?.wins ?: 0}",
                    color = Color(0xFF10B981),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Losses",
                    value = "${profile?.losses ?: 0}",
                    color = Color(0xFFEF4444),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Draws",
                    value = "${profile?.draws ?: 0}",
                    color = Color(0xFF64748B),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Win Rate",
                    value = run {
                        val w = profile?.wins ?: 0
                        val total = w + (profile?.losses ?: 0) + (profile?.draws ?: 0)
                        if (total == 0) "0%" else "${(w * 100) / total}%"
                    },
                    color = Color(0xFF38BDF8),
                    modifier = Modifier.weight(1.1f)
                )
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
    }

    // Hero 3D Avatar Modal Dialog
    // Custom Time Control Dialog
    if (showCustomTimeDialog) {
        var tempMinutes by remember { mutableStateOf(selectedTimeMinutes.toFloat()) }
        AlertDialog(
            onDismissRequest = { showCustomTimeDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Timer, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Select Time Control", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Choose match duration for each player:",
                        color = Color(0xFF94A3B8),
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Big prominent time display
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF0F172A),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF3B82F6)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { tempMinutes = (tempMinutes - 1f).coerceAtLeast(1f) },
                                modifier = Modifier
                                    .size(38.dp)
                                    .background(Color(0xFF1E293B), CircleShape)
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = Color.White)
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${tempMinutes.toInt()} MIN",
                                    color = Color.White,
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = when {
                                        tempMinutes <= 2 -> "⚡ Bullet Chess"
                                        tempMinutes <= 5 -> "⏱️ Blitz Chess"
                                        tempMinutes <= 15 -> "⏳ Rapid Chess"
                                        else -> "♟️ Classical Chess"
                                    },
                                    color = Color(0xFF38BDF8),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            IconButton(
                                onClick = { tempMinutes = (tempMinutes + 1f).coerceAtMost(60f) },
                                modifier = Modifier
                                    .size(38.dp)
                                    .background(Color(0xFF1E293B), CircleShape)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Increase", tint = Color.White)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Slider for fast picking 1 to 60 mins
                    Slider(
                        value = tempMinutes,
                        onValueChange = { tempMinutes = it },
                        valueRange = 1f..60f,
                        steps = 58,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF38BDF8),
                            activeTrackColor = Color(0xFF2563EB),
                            inactiveTrackColor = Color(0xFF334155)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Quick presets row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(1, 3, 5, 10, 15, 30).forEach { preset ->
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { tempMinutes = preset.toFloat() },
                                shape = RoundedCornerShape(8.dp),
                                color = if (tempMinutes.toInt() == preset) Color(0xFF2563EB) else Color(0xFF1E293B),
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (tempMinutes.toInt() == preset) Color(0xFF60A5FA) else Color(0xFF334155))
                            ) {
                                Text(
                                    text = "${preset}m",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.setTimeControlMinutes(tempMinutes.toInt())
                        showCustomTimeDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Apply (${tempMinutes.toInt()} Min)", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showCustomTimeDialog = false },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Cancel", color = Color(0xFF94A3B8))
                }
            },
            containerColor = Color(0xFF131D31),
            shape = RoundedCornerShape(18.dp)
        )
    }

    if (show3DModalForHeroAvatar) {
        Avatar3DModalDialog(
            avatar = avatarItem,
            isEquipped = true,
            onEquip = { },
            onDismiss = { show3DModalForHeroAvatar = false }
        )
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFF0C1322),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(color)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = title.uppercase(),
                    color = Color(0xFF94A3B8),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                color = color,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
private fun SearchingOpponentView(
    matchState: MatchmakingState,
    cyclingPlayer: com.onlinechessgame.app.chess.model.OnlinePlayer?,
    onCancel: () -> Unit
) {
    val transition = rememberInfiniteTransition(label = "radar")
    val rotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .testTag("matchmaking_searching_view"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Radar scanning animation
        Box(
            modifier = Modifier.size(180.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val r = size.minDimension / 2f
                // Concentric circles
                drawCircle(color = Color(0xFF1E3A8A).copy(alpha = 0.3f), radius = r, style = Stroke(width = 1.5f))
                drawCircle(color = Color(0xFF1E3A8A).copy(alpha = 0.45f), radius = r * 0.7f, style = Stroke(width = 1.5f))
                drawCircle(color = Color(0xFF1E3A8A).copy(alpha = 0.6f), radius = r * 0.4f, style = Stroke(width = 1.5f))
            }

            // Rotating sweep
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .rotate(rotation)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val r = size.minDimension / 2f
                    drawLine(
                        brush = Brush.linearGradient(
                            listOf(Color(0xFF38BDF8), Color.Transparent),
                            start = Offset(r, r),
                            end = Offset(r * 2f, r)
                        ),
                        start = Offset(r, r),
                        end = Offset(r * 2f, r),
                        strokeWidth = 3f
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.Public,
                contentDescription = null,
                tint = Color(0xFF38BDF8),
                modifier = Modifier.size(52.dp)
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = if (matchState == MatchmakingState.FOUND) "OPPONENT FOUND!" else "Searching for Opponent...",
            color = if (matchState == MatchmakingState.FOUND) Color(0xFF34D399) else Color.White,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 22.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (matchState == MatchmakingState.FOUND) "Match starting in a moment..." else "Scanning global chess pool (1000 - 1500 ELO)...",
            color = Color(0xFF94A3B8),
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Cycling Opponent Card preview
        if (cyclingPlayer != null) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                border = androidx.compose.foundation.BorderStroke(
                    1.5.dp,
                    if (matchState == MatchmakingState.FOUND) Color(0xFF10B981) else Color(0xFF334155)
                ),
                modifier = Modifier.fillMaxWidth(0.85f)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF0F172A))
                            .border(1.5.dp, Color(0xFF38BDF8), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (cyclingPlayer.avatar.resId != null) {
                            Image(
                                painter = painterResource(id = cyclingPlayer.avatar.resId),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text(cyclingPlayer.avatar.iconEmoji.ifEmpty { "♟️" }, fontSize = 24.sp)
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = cyclingPlayer.username,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(cyclingPlayer.country.flag, fontSize = 15.sp)
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${cyclingPlayer.country.name} • ${cyclingPlayer.rating} PTS",
                            color = Color(0xFF38BDF8),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (matchState != MatchmakingState.FOUND) {
            OutlinedButton(
                onClick = onCancel,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF94A3B8)),
                modifier = Modifier.testTag("cancel_matchmaking_button")
            ) {
                Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Cancel Matchmaking")
            }
        }
    }
}

@Composable
private fun ActiveGameView(
    viewModel: ChessViewModel,
    profile: UserProfileEntity?,
    gameState: ChessGameUiState
) {
    var showResignConfirm by remember { mutableStateOf(false) }

    val userAvatar = remember(profile?.selectedAvatarId) {
        ChessRepository.getAvailableAvatars().find { it.id == profile?.selectedAvatarId }
            ?: ChessRepository.getAvailableAvatars().first()
    }

    val selectedTheme = remember(profile?.boardTheme) {
        BoardThemeStyle.fromKey(profile?.boardTheme)
    }

    val pieceStyle = remember(profile?.pieceStyle) {
        profile?.pieceStyle ?: "TOURNAMENT_PLASTIC"
    }

    val pieceColorTheme = remember(profile?.pieceColor) {
        profile?.pieceColor ?: "CLASSIC"
    }
    val isometricTable = !selectedTheme.isSimple

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 6.dp, vertical = 4.dp)
            .testTag("active_chess_game_view")
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isometricTable) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "PLAYER 1 (YOU)",
                            color = Color(0xFFE2E8F0),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.2.sp
                        )
                        Text(
                            text = String.format("%02d:%02d", gameState.playerTimeSeconds / 60, gameState.playerTimeSeconds % 60),
                            color = Color(0xFFF3C96B),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "PLAYER 2 (AI)",
                            color = Color(0xFFE2E8F0),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.2.sp
                        )
                        Text(
                            text = String.format("%02d:%02d", gameState.opponentTimeSeconds / 60, gameState.opponentTimeSeconds % 60),
                            color = Color(0xFFD8D4CC),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else if (gameState.opponent != null) {
                PlayerCard3D(
                    username = gameState.opponent.username,
                    countryFlag = gameState.opponent.country.flag,
                    rating = gameState.opponent.rating,
                    avatar = gameState.opponent.avatar,
                    isCurrentTurn = gameState.currentTurn == PieceColor.BLACK,
                    timeRemainingSeconds = gameState.opponentTimeSeconds,
                    capturedPieces = gameState.capturedByBlack,
                    playerColor = PieceColor.BLACK
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            ChessBoard3D(
                board = gameState.board,
                selectedPosition = gameState.selectedPosition,
                legalMoves = gameState.legalMoves,
                lastMove = gameState.lastMove,
                isCheck = gameState.isCheck,
                kingInCheckPos = gameState.kingInCheckPos,
                boardTheme = selectedTheme,
                pieceStyle = pieceStyle,
                pieceColorTheme = pieceColorTheme,
                showCoordinates = false,
                flipped = false,
                onSquareClick = { pos -> viewModel.onSquareClicked(pos) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(6.dp))

            if (!isometricTable) {
                PlayerCard3D(
                    username = profile?.username ?: "You",
                    countryFlag = profile?.countryFlag ?: "🇺🇸",
                    rating = profile?.ratingPoints ?: 1200,
                    avatar = userAvatar,
                    isCurrentTurn = gameState.currentTurn == PieceColor.WHITE,
                    timeRemainingSeconds = gameState.playerTimeSeconds,
                    capturedPieces = gameState.capturedByWhite,
                    playerColor = PieceColor.WHITE
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 4. Game Action Bar (Offer Draw, Resign, Chat) - Right below Player Card
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { viewModel.offerDraw() },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF94A3B8)),
                        modifier = Modifier.testTag("draw_offer_button")
                    ) {
                        Text("Offer Draw", fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = { showResignConfirm = true },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFF87171)),
                        modifier = Modifier.testTag("resign_button")
                    ) {
                        Text("Resign", fontSize = 12.sp)
                    }
                }

                // Floating / In-Game Chat Button with unread badge
                FloatingChatButton(
                    unreadCount = gameState.unreadChatCount,
                    onClick = { viewModel.toggleChatSheet(true) }
                )
            }
        }

        // Integrated Chat Modal Bottom Sheet
        if (gameState.opponent != null) {
            GameChatSheet(
                isOpen = gameState.isChatOpen,
                opponentName = gameState.opponent.username,
                messages = gameState.chatMessages,
                onSendMessage = { text -> viewModel.sendPlayerChatMessage(text) },
                onDismiss = { viewModel.toggleChatSheet(false) }
            )
        }

        // Pawn Promotion Dialog
        if (gameState.pendingPromotionMove != null) {
            PawnPromotionDialog(
                color = gameState.playerColor,
                onSelect = { pieceType -> viewModel.onPromotionSelected(pieceType) }
            )
        }

        val isFriendSent by viewModel.friendRequestSentToOpponent.collectAsState()

        // Game End Dialog (+10 / -10 points, tokens bonus)
        if (gameState.showGameEndDialog) {
            GameEndResultDialog(
                status = gameState.gameStatus,
                playerColor = gameState.playerColor,
                opponentName = gameState.opponent?.username ?: "Opponent",
                onRematch = {
                    viewModel.startQuickMatch()
                },
                onMenu = {
                    viewModel.cancelMatchmaking()
                },
                onAddFriend = {
                    viewModel.sendFriendRequestToCurrentOpponent()
                },
                isFriendRequestSent = isFriendSent
            )
        }

        // Resign Confirm Dialog
        if (showResignConfirm) {
            AlertDialog(
                onDismissRequest = { showResignConfirm = false },
                title = { Text("Resign Game?") },
                text = { Text("Resigning will result in a defeat, loss of 10 rating points, and loss of 25 tokens.") },
                confirmButton = {
                    Button(
                        onClick = {
                            showResignConfirm = false
                            viewModel.resignMatch()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                    ) {
                        Text("Yes, Resign")
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = { showResignConfirm = false }) {
                        Text("Continue Playing")
                    }
                },
                containerColor = Color(0xFF1E293B)
            )
        }
    }
}
