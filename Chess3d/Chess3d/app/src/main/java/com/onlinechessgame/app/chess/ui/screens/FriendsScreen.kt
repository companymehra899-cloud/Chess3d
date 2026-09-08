package com.onlinechessgame.app.chess.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.onlinechessgame.app.chess.data.local.FriendEntity
import com.onlinechessgame.app.chess.data.repository.ChessRepository
import com.onlinechessgame.app.chess.model.AvatarItem
import com.onlinechessgame.app.chess.ui.viewmodel.ChessViewModel

@Composable
fun FriendsScreen(
    viewModel: ChessViewModel,
    modifier: Modifier = Modifier
) {
    val acceptedFriends by viewModel.acceptedFriends.collectAsState()
    val incomingRequests by viewModel.incomingFriendRequests.collectAsState()
    val outgoingRequests by viewModel.outgoingFriendRequests.collectAsState()
    val pendingCount by viewModel.pendingRequestsCount.collectAsState()
    val actionMessage by viewModel.friendActionMessage.collectAsState()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var friendToRemove by remember { mutableStateOf<FriendEntity?>(null) }

    val tabs = listOf(
        "Friends (${acceptedFriends.size})",
        "Requests (${incomingRequests.size + outgoingRequests.size})",
        "Add & Discover"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F172A),
                        Color(0xFF0B0F19),
                        Color(0xFF05070D)
                    )
                )
            )
            .testTag("friends_screen")
    ) {
        // Top Header
        Surface(
            color = Color(0xFF1E293B).copy(alpha = 0.7f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Friends & Network",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Challenge friends & connect worldwide",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp
                        )
                    }

                    if (pendingCount > 0) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color(0xFFEF4444),
                            modifier = Modifier.testTag("pending_requests_badge")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "$pendingCount new request${if (pendingCount > 1) "s" else ""}",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Tab Row
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = Color.Transparent,
                    contentColor = Color(0xFF38BDF8),
                    indicator = { tabPositions ->
                        SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            color = Color(0xFF38BDF8)
                        )
                    },
                    divider = {}
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = {
                                if (index == 1 && pendingCount > 0) {
                                    BadgedBox(badge = {
                                        Badge(containerColor = Color(0xFFEF4444)) {
                                            Text(pendingCount.toString())
                                        }
                                    }) {
                                        Text(
                                            text = title,
                                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 13.sp,
                                            color = if (selectedTabIndex == index) Color(0xFF38BDF8) else Color(0xFF94A3B8)
                                        )
                                    }
                                } else {
                                    Text(
                                        text = title,
                                        fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 13.sp,
                                        color = if (selectedTabIndex == index) Color(0xFF38BDF8) else Color(0xFF94A3B8)
                                    )
                                }
                            },
                            modifier = Modifier.testTag("friends_tab_$index")
                        )
                    }
                }
            }
        }

        // Action feedback message banner
        AnimatedVisibility(
            visible = actionMessage != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Surface(
                color = Color(0xFF1E293B),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38BDF8)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = actionMessage ?: "",
                        color = Color(0xFFE2E8F0),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = { viewModel.clearFriendActionMessage() },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Dismiss",
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // Tab Content
        Box(modifier = Modifier.weight(1f)) {
            when (selectedTabIndex) {
                0 -> AcceptedFriendsTab(
                    friends = acceptedFriends,
                    onChallenge = { friend -> viewModel.challengeFriend(friend) },
                    onRemove = { friend -> friendToRemove = friend },
                    onNavigateToAdd = { selectedTabIndex = 2 }
                )
                1 -> RequestsTab(
                    incoming = incomingRequests,
                    outgoing = outgoingRequests,
                    onAccept = { req -> viewModel.acceptFriendRequest(req) },
                    onDecline = { req -> viewModel.declineFriendRequest(req) },
                    onCancelOutgoing = { req -> viewModel.cancelOutgoingRequest(req) }
                )
                2 -> AddAndDiscoverTab(
                    onSendRequest = { username -> viewModel.sendFriendRequest(username) },
                    existingFriends = acceptedFriends.map { it.username }.toSet() +
                            outgoingRequests.map { it.username }.toSet()
                )
            }
        }
    }

    // Confirmation dialog for removing friend
    friendToRemove?.let { friend ->
        AlertDialog(
            onDismissRequest = { friendToRemove = null },
            title = { Text("Remove Friend") },
            text = { Text("Are you sure you want to remove ${friend.username} from your friends list?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.removeFriend(friend)
                        friendToRemove = null
                    }
                ) {
                    Text("Remove", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { friendToRemove = null }) {
                    Text("Cancel")
                }
            },
            containerColor = Color(0xFF1E293B),
            titleContentColor = Color.White,
            textContentColor = Color(0xFFCBD5E1)
        )
    }
}

@Composable
private fun AcceptedFriendsTab(
    friends: List<FriendEntity>,
    onChallenge: (FriendEntity) -> Unit,
    onRemove: (FriendEntity) -> Unit,
    onNavigateToAdd: () -> Unit
) {
    if (friends.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "👥", fontSize = 48.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "No Friends Yet",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Add other chess players to challenge them to direct 3D matches anytime!",
                color = Color(0xFF94A3B8),
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = onNavigateToAdd,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                modifier = Modifier.testTag("find_friends_button")
            ) {
                Text("Discover Players", fontWeight = FontWeight.Bold)
            }
        }
    } else {
        val onlineCount = friends.count { it.status == "ONLINE" }
        val inGameCount = friends.count { it.status == "IN_GAME" }
        val offlineCount = friends.count { it.status == "OFFLINE" }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Status Summary Chip Bar
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF1E293B).copy(alpha = 0.5f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StatusIndicator(color = Color(0xFF10B981), label = "$onlineCount Online")
                        StatusIndicator(color = Color(0xFFF59E0B), label = "$inGameCount In Match")
                        StatusIndicator(color = Color(0xFF64748B), label = "$offlineCount Offline")
                    }
                }
            }

            items(friends, key = { it.id }) { friend ->
                FriendCard(
                    friend = friend,
                    onChallenge = { onChallenge(friend) },
                    onRemove = { onRemove(friend) }
                )
            }
        }
    }
}

@Composable
private fun StatusIndicator(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = label, color = Color(0xFFCBD5E1), fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun FriendCard(
    friend: FriendEntity,
    onChallenge: () -> Unit,
    onRemove: () -> Unit
) {
    val avatars = remember { ChessRepository.getAvailableAvatars() }
    val avatar = avatars.firstOrNull { it.id == friend.avatarId } ?: avatars.first()

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("friend_card_${friend.username}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar with Status Badge
            Box {
                AvatarDisplay(
                    avatar = avatar,
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                )
                val statusColor = when (friend.status) {
                    "ONLINE" -> Color(0xFF10B981)
                    "IN_GAME" -> Color(0xFFF59E0B)
                    else -> Color(0xFF64748B)
                }
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(statusColor)
                        .border(2.dp, Color(0xFF1E293B), CircleShape)
                        .align(Alignment.BottomEnd)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = friend.username,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = friend.countryFlag, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(3.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF0F172A)
                    ) {
                        Text(
                            text = "⭐ ${friend.rating}",
                            color = Color(0xFF38BDF8),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    val statusLabel = when (friend.status) {
                        "ONLINE" -> "Online"
                        "IN_GAME" -> "In a match"
                        else -> "Offline"
                    }
                    val statusColor = when (friend.status) {
                        "ONLINE" -> Color(0xFF34D399)
                        "IN_GAME" -> Color(0xFFFBBF24)
                        else -> Color(0xFF94A3B8)
                    }
                    Text(
                        text = statusLabel,
                        color = statusColor,
                        fontSize = 11.sp
                    )
                }
            }

            // Challenge Button
            Button(
                onClick = onChallenge,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.testTag("challenge_${friend.username}")
            ) {
                Text("Play ⚔️", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.width(4.dp))

            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remove friend",
                    tint = Color(0xFF64748B),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun RequestsTab(
    incoming: List<FriendEntity>,
    outgoing: List<FriendEntity>,
    onAccept: (FriendEntity) -> Unit,
    onDecline: (FriendEntity) -> Unit,
    onCancelOutgoing: (FriendEntity) -> Unit
) {
    val avatars = remember { ChessRepository.getAvailableAvatars() }

    if (incoming.isEmpty() && outgoing.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "📬", fontSize = 48.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "No Pending Requests",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "When players send you friend requests or when you add others, they will appear here.",
                color = Color(0xFF94A3B8),
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Incoming Requests Section
            if (incoming.isNotEmpty()) {
                item {
                    Text(
                        text = "Incoming Requests (${incoming.size})",
                        color = Color(0xFF38BDF8),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                items(incoming, key = { "in_${it.id}" }) { req ->
                    val avatar = avatars.firstOrNull { it.id == req.avatarId } ?: avatars.first()
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.5f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("incoming_request_${req.username}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AvatarDisplay(
                                avatar = avatar,
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = req.username,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = req.countryFlag, fontSize = 13.sp)
                                }
                                Text(
                                    text = "Rating: ${req.rating} • ${req.countryName}",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 11.sp
                                )
                            }

                            // Accept Button
                            Button(
                                onClick = { onAccept(req) },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("accept_${req.username}")
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Accept", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            // Decline Button
                            OutlinedButton(
                                onClick = { onDecline(req) },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444)),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("decline_${req.username}")
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Decline", modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }

            // Outgoing Requests Section
            if (outgoing.isNotEmpty()) {
                item {
                    Text(
                        text = "Sent Requests (${outgoing.size})",
                        color = Color(0xFF94A3B8),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                items(outgoing, key = { "out_${it.id}" }) { req ->
                    val avatar = avatars.firstOrNull { it.id == req.avatarId } ?: avatars.first()
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF131D2E)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("outgoing_request_${req.username}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AvatarDisplay(
                                avatar = avatar,
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = req.username,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = req.countryFlag, fontSize = 13.sp)
                                }
                                Text(
                                    text = "Pending response...",
                                    color = Color(0xFFF59E0B),
                                    fontSize = 11.sp
                                )
                            }

                            OutlinedButton(
                                onClick = { onCancelOutgoing(req) },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF94A3B8)),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("cancel_${req.username}")
                            ) {
                                Text("Cancel", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AddAndDiscoverTab(
    onSendRequest: (String) -> Unit,
    existingFriends: Set<String>
) {
    var searchInput by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    // Recommended Global Players for discovery
    val suggestedPlayers = remember {
        listOf(
            SuggestedPlayer("Magnus_Viking", "🇳🇴", "Norway", 1845, "custom_golden_king"),
            SuggestedPlayer("Hikaru_Blitz", "🇺🇸", "United States", 1790, "man_portrait_2"),
            SuggestedPlayer("Gukesh_Phenom", "🇮🇳", "India", 1765, "custom_golden_king"),
            SuggestedPlayer("Fabiano_Classic", "🇺🇸", "United States", 1740, "man_portrait_1"),
            SuggestedPlayer("Alireza_Speed", "🇫🇷", "France", 1715, "custom_cyber_rook"),
            SuggestedPlayer("Judit_Queen", "🇭🇺", "Hungary", 1680, "woman_portrait_2"),
            SuggestedPlayer("Ding_Dragon", "🇨🇳", "China", 1710, "custom_shadow_knight"),
            SuggestedPlayer("Nodirbek_Fast", "🇺🇿", "Uzbekistan", 1690, "custom_valiant_pawn")
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Add by username box
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Add Friend by Username",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Enter the exact player username to send a direct friend request.",
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = searchInput,
                            onValueChange = { searchInput = it },
                            placeholder = { Text("Username...", color = Color(0xFF64748B), fontSize = 13.sp) },
                            singleLine = true,
                            leadingIcon = {
                                Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF94A3B8))
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFF38BDF8),
                                unfocusedBorderColor = Color(0xFF475569),
                                focusedContainerColor = Color(0xFF0F172A),
                                unfocusedContainerColor = Color(0xFF0F172A)
                            ),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                            keyboardActions = KeyboardActions(
                                onSend = {
                                    if (searchInput.isNotBlank()) {
                                        onSendRequest(searchInput)
                                        searchInput = ""
                                        focusManager.clearFocus()
                                    }
                                }
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("friend_username_input")
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                if (searchInput.isNotBlank()) {
                                    onSendRequest(searchInput)
                                    searchInput = ""
                                    focusManager.clearFocus()
                                }
                            },
                            enabled = searchInput.isNotBlank(),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                            modifier = Modifier
                                .height(56.dp)
                                .testTag("send_friend_request_button")
                        ) {
                            Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }

        // Global Discover Section
        item {
            Text(
                text = "Suggested Worldwide Players",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }

        items(suggestedPlayers) { player ->
            val isAlreadyAdded = existingFriends.contains(player.username)
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = player.flag, fontSize = 28.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = player.username,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Rating: ${player.rating} • ${player.country}",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp
                        )
                    }

                    Button(
                        onClick = { onSendRequest(player.username) },
                        enabled = !isAlreadyAdded,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isAlreadyAdded) Color(0xFF334155) else Color(0xFF2563EB),
                            contentColor = if (isAlreadyAdded) Color(0xFF94A3B8) else Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("add_suggested_${player.username}")
                    ) {
                        Text(
                            text = if (isAlreadyAdded) "Requested" else "+ Add",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

private data class SuggestedPlayer(
    val username: String,
    val flag: String,
    val country: String,
    val rating: Int,
    val avatarId: String
)

@Composable
fun AvatarDisplay(
    avatar: AvatarItem,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = CircleShape,
        color = Color(0xFF1E293B),
        modifier = modifier
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            if (avatar.resId != null) {
                Image(
                    painter = painterResource(id = avatar.resId),
                    contentDescription = avatar.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text(
                    text = avatar.iconEmoji.ifEmpty { "♟️" },
                    fontSize = 20.sp
                )
            }
        }
    }
}

