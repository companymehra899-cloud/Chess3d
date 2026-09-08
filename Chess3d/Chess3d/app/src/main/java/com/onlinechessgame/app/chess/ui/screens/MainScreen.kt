package com.onlinechessgame.app.chess.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.SportsEsports
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.onlinechessgame.app.chess.ui.viewmodel.AppTab
import com.onlinechessgame.app.chess.ui.viewmodel.ChessViewModel
import com.onlinechessgame.app.chess.ui.viewmodel.MatchmakingState

@Composable
fun MainScreen(
    viewModel: ChessViewModel,
    modifier: Modifier = Modifier
) {
    val isAuthCompleted by viewModel.isAuthCompleted.collectAsState()
    val currentTab by viewModel.currentTab.collectAsState()
    val matchState by viewModel.matchmakingState.collectAsState()
    val pendingRequestsCount by viewModel.pendingRequestsCount.collectAsState()

    if (!isAuthCompleted) {
        AuthScreen(viewModel = viewModel, modifier = modifier)
        return
    }

    // Hide bottom navigation bar when inside an active game so player has full screen focus
    val showBottomNav = matchState != MatchmakingState.IN_GAME

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFF0B0F17),
        bottomBar = {
            if (showBottomNav) {
                NavigationBar(
                    modifier = Modifier
                        .drawBehind {
                            drawLine(
                                color = Color(0xFF1E293B),
                                start = Offset(0f, 0f),
                                end = Offset(size.width, 0f),
                                strokeWidth = 1.dp.toPx()
                            )
                        }
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .testTag("main_bottom_nav_bar"),
                    containerColor = Color(0xFF0C1322),
                    contentColor = Color.White
                ) {
                    // Play Tab
                    NavigationBarItem(
                        selected = currentTab == AppTab.PLAY,
                        onClick = { viewModel.setTab(AppTab.PLAY) },
                        icon = {
                            Icon(
                                imageVector = if (currentTab == AppTab.PLAY) Icons.Filled.SportsEsports else Icons.Outlined.SportsEsports,
                                contentDescription = "Play",
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = { Text("Play", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF38BDF8),
                            selectedTextColor = Color(0xFF38BDF8),
                            indicatorColor = Color(0xFF1E293B),
                            unselectedIconColor = Color(0xFF94A3B8),
                            unselectedTextColor = Color(0xFF94A3B8)
                        ),
                        modifier = Modifier.testTag("nav_tab_play")
                    )

                    // Friends Tab with Badge for pending requests
                    NavigationBarItem(
                        selected = currentTab == AppTab.FRIENDS,
                        onClick = { viewModel.setTab(AppTab.FRIENDS) },
                        icon = {
                            if (pendingRequestsCount > 0) {
                                BadgedBox(badge = {
                                    Badge(containerColor = Color(0xFFEF4444)) {
                                        Text(pendingRequestsCount.toString())
                                    }
                                }) {
                                    Icon(
                                        imageVector = if (currentTab == AppTab.FRIENDS) Icons.Filled.People else Icons.Outlined.People,
                                        contentDescription = "Friends",
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            } else {
                                Icon(
                                    imageVector = if (currentTab == AppTab.FRIENDS) Icons.Filled.People else Icons.Outlined.People,
                                    contentDescription = "Friends",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        },
                        label = { Text("Friends", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF38BDF8),
                            selectedTextColor = Color(0xFF38BDF8),
                            indicatorColor = Color(0xFF1E293B),
                            unselectedIconColor = Color(0xFF94A3B8),
                            unselectedTextColor = Color(0xFF94A3B8)
                        ),
                        modifier = Modifier.testTag("nav_tab_friends")
                    )

                    // Game History Tab
                    NavigationBarItem(
                        selected = currentTab == AppTab.HISTORY,
                        onClick = { viewModel.setTab(AppTab.HISTORY) },
                        icon = {
                            Icon(
                                imageVector = if (currentTab == AppTab.HISTORY) Icons.Filled.History else Icons.Outlined.History,
                                contentDescription = "History",
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = { Text("History", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF38BDF8),
                            selectedTextColor = Color(0xFF38BDF8),
                            indicatorColor = Color(0xFF1E293B),
                            unselectedIconColor = Color(0xFF94A3B8),
                            unselectedTextColor = Color(0xFF94A3B8)
                        ),
                        modifier = Modifier.testTag("nav_tab_history")
                    )

                    // Leaderboard Tab
                    NavigationBarItem(
                        selected = currentTab == AppTab.LEADERBOARD,
                        onClick = { viewModel.setTab(AppTab.LEADERBOARD) },
                        icon = {
                            Icon(
                                imageVector = if (currentTab == AppTab.LEADERBOARD) Icons.Filled.EmojiEvents else Icons.Outlined.EmojiEvents,
                                contentDescription = "Leaders",
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = { Text("Leaders", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF38BDF8),
                            selectedTextColor = Color(0xFF38BDF8),
                            indicatorColor = Color(0xFF1E293B),
                            unselectedIconColor = Color(0xFF94A3B8),
                            unselectedTextColor = Color(0xFF94A3B8)
                        ),
                        modifier = Modifier.testTag("nav_tab_leaders")
                    )

                    // Profile Tab
                    NavigationBarItem(
                        selected = currentTab == AppTab.PROFILE,
                        onClick = { viewModel.setTab(AppTab.PROFILE) },
                        icon = {
                            Icon(
                                imageVector = if (currentTab == AppTab.PROFILE) Icons.Filled.AccountCircle else Icons.Outlined.AccountCircle,
                                contentDescription = "Profile",
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = { Text("Profile", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF38BDF8),
                            selectedTextColor = Color(0xFF38BDF8),
                            indicatorColor = Color(0xFF1E293B),
                            unselectedIconColor = Color(0xFF94A3B8),
                            unselectedTextColor = Color(0xFF94A3B8)
                        ),
                        modifier = Modifier.testTag("nav_tab_profile")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "tab_transition"
            ) { target ->
                when (target) {
                    AppTab.PLAY -> PlayScreen(viewModel = viewModel)
                    AppTab.FRIENDS -> FriendsScreen(viewModel = viewModel)
                    AppTab.HISTORY -> GameHistoryScreen(viewModel = viewModel)
                    AppTab.PUZZLES -> PuzzlesScreen(viewModel = viewModel)
                    AppTab.LEADERBOARD -> LeaderboardScreen(viewModel = viewModel)
                    AppTab.PROFILE -> ProfileScreen(viewModel = viewModel)
                    AppTab.SETTINGS -> SettingsScreen(viewModel = viewModel)
                }
            }
        }
    }
}
