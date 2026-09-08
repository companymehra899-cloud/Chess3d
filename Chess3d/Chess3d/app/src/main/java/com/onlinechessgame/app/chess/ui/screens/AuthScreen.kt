package com.onlinechessgame.app.chess.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.onlinechessgame.app.R
import com.onlinechessgame.app.chess.model.Country
import com.onlinechessgame.app.chess.model.DEFAULT_COUNTRIES
import com.onlinechessgame.app.chess.ui.viewmodel.ChessViewModel

data class StarterAvatar(
    val id: String,
    val name: String,
    val resId: Int
)

@Composable
fun AuthScreen(
    viewModel: ChessViewModel,
    modifier: Modifier = Modifier
) {
    val isAuthLoading by viewModel.isAuthLoading.collectAsState()
    val authError by viewModel.authErrorMessage.collectAsState()
    val authSuccess by viewModel.authSuccessMessage.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Login, 1: Sign Up, 2: Play as Guest
    val focusManager = LocalFocusManager.current

    // Login Form State
    var loginUsername by remember { mutableStateOf("") }
    var loginPassword by remember { mutableStateOf("") }
    var showLoginPassword by remember { mutableStateOf(false) }

    // Sign Up Form State
    var signupUsername by remember { mutableStateOf("") }
    var signupPassword by remember { mutableStateOf("") }
    var showSignupPassword by remember { mutableStateOf(false) }
    var selectedCountry by remember { mutableStateOf(DEFAULT_COUNTRIES.first()) }
    var showCountryPicker by remember { mutableStateOf(false) }

    val starterAvatars = remember {
        listOf(
            StarterAvatar("champion_boy", "Champion Boy", R.drawable.img_avatar_champion_boy),
            StarterAvatar("champion_girl", "Champion Girl", R.drawable.img_avatar_champion_girl),
            StarterAvatar("stylish_man_free", "Stylish Man", R.drawable.img_avatar_stylish_man),
            StarterAvatar("modern_woman_free", "Modern Woman", R.drawable.img_avatar_modern_woman),
            StarterAvatar("british_woman_free", "British Woman", R.drawable.img_avatar_british_woman),
            StarterAvatar("simple_man_free", "Simple Man", R.drawable.img_avatar_simple_man),
            StarterAvatar("champion_man_free", "Champion Man", R.drawable.img_avatar_champion_man),
            StarterAvatar("cool_guy", "Cool Guy", R.drawable.img_avatar_man_1),
            StarterAvatar("scholar_girl", "Scholar Girl", R.drawable.img_avatar_woman_1),
            StarterAvatar("explorer_boy", "Explorer Boy", R.drawable.img_avatar_african_boy),
            StarterAvatar("master_aarav", "Master Aarav", R.drawable.img_avatar_indian_boy),
            StarterAvatar("imperial_empress", "Imperial Empress", R.drawable.img_avatar_imperial_empress),
            StarterAvatar("master_lukas", "Master Lukas", R.drawable.img_avatar_german_boy)
        )
    }
    var selectedAvatarId by remember { mutableStateOf(starterAvatars.first().id) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF070A10),
                        Color(0xFF0F172A),
                        Color(0xFF0B0F17)
                    )
                )
            )
            .testTag("auth_screen")
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFF1E293B).copy(alpha = 0.8f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                        modifier = Modifier.clickable { viewModel.playAsGuest() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Skip / Guest", fontSize = 11.sp, color = Color(0xFF38BDF8), fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("➔", fontSize = 11.sp, color = Color(0xFF38BDF8))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // App Logo & Header Banner
            item {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(Color(0xFF1E3A8A), Color(0xFF0F172A))
                            )
                        )
                        .border(2.dp, Color(0xFFF59E0B), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Chess 3D Logo",
                        tint = Color(0xFFFDE68A),
                        modifier = Modifier.size(42.dp)
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "CHESS 3D ARENA",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFFDE68A),
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Compete, Solve Puzzles & Climb Leaderboards",
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8),
                    modifier = Modifier.padding(top = 4.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Tab Selector: Log In / Sign Up / Play as Guest
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF131D31),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = Color.Transparent,
                        contentColor = Color(0xFF38BDF8),
                        indicator = { tabPositions ->
                            SecondaryIndicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                height = 3.dp,
                                color = Color(0xFFF59E0B)
                            )
                        },
                        divider = {}
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = {
                                selectedTab = 0
                                viewModel.clearAuthMessages()
                            },
                            text = {
                                Text(
                                    "Log In",
                                    fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 13.sp,
                                    color = if (selectedTab == 0) Color(0xFFFDE68A) else Color(0xFF94A3B8)
                                )
                            },
                            modifier = Modifier.testTag("auth_tab_login")
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = {
                                selectedTab = 1
                                viewModel.clearAuthMessages()
                            },
                            text = {
                                Text(
                                    "Sign Up",
                                    fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 13.sp,
                                    color = if (selectedTab == 1) Color(0xFFFDE68A) else Color(0xFF94A3B8)
                                )
                            },
                            modifier = Modifier.testTag("auth_tab_signup")
                        )
                        Tab(
                            selected = selectedTab == 2,
                            onClick = {
                                selectedTab = 2
                                viewModel.clearAuthMessages()
                            },
                            text = {
                                Text(
                                    "Guest",
                                    fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 13.sp,
                                    color = if (selectedTab == 2) Color(0xFF38BDF8) else Color(0xFF94A3B8)
                                )
                            },
                            modifier = Modifier.testTag("auth_tab_guest")
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Error & Success Feedback Banners
            item {
                AnimatedVisibility(
                    visible = authError != null,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0x33EF4444)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Error",
                                tint = Color(0xFFF87171),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = authError ?: "",
                                color = Color(0xFFFCA5A5),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                AnimatedVisibility(
                    visible = authSuccess != null,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0x3310B981)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Success",
                                tint = Color(0xFF34D399),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = authSuccess ?: "",
                                color = Color(0xFF6EE7B7),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Tab Content
            when (selectedTab) {
                // ==========================================
                // 1. LOG IN TAB
                // ==========================================
                0 -> {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_card"),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF131D31)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E3A8A))
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Welcome Back!",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Enter your username and password to access your saved progress.",
                                    fontSize = 12.sp,
                                    color = Color(0xFF94A3B8),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(top = 4.dp, bottom = 18.dp)
                                )

                                // Username Input
                                OutlinedTextField(
                                    value = loginUsername,
                                    onValueChange = { loginUsername = it },
                                    label = { Text("Username") },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Person,
                                            contentDescription = null,
                                            tint = Color(0xFF38BDF8)
                                        )
                                    },
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("auth_login_username"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF38BDF8),
                                        unfocusedBorderColor = Color(0xFF334155),
                                        focusedContainerColor = Color(0xFF0F172A),
                                        unfocusedContainerColor = Color(0xFF0F172A),
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    ),
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Text,
                                        imeAction = ImeAction.Next
                                    )
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                // Password Input
                                OutlinedTextField(
                                    value = loginPassword,
                                    onValueChange = { loginPassword = it },
                                    label = { Text("Password") },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Lock,
                                            contentDescription = null,
                                            tint = Color(0xFF38BDF8)
                                        )
                                    },
                                    trailingIcon = {
                                        IconButton(onClick = { showLoginPassword = !showLoginPassword }) {
                                            Icon(
                                                imageVector = if (showLoginPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                                contentDescription = "Toggle password visibility",
                                                tint = Color(0xFF94A3B8)
                                            )
                                        }
                                    },
                                    visualTransformation = if (showLoginPassword) VisualTransformation.None else PasswordVisualTransformation(),
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("auth_login_password"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF38BDF8),
                                        unfocusedBorderColor = Color(0xFF334155),
                                        focusedContainerColor = Color(0xFF0F172A),
                                        unfocusedContainerColor = Color(0xFF0F172A),
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    ),
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Password,
                                        imeAction = ImeAction.Done
                                    ),
                                    keyboardActions = KeyboardActions(
                                        onDone = {
                                            focusManager.clearFocus()
                                            viewModel.login(loginUsername, loginPassword)
                                        }
                                    )
                                )

                                Spacer(modifier = Modifier.height(20.dp))

                                // Login Button
                                Button(
                                    onClick = {
                                        focusManager.clearFocus()
                                        viewModel.login(loginUsername, loginPassword)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .testTag("auth_login_button"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                                    enabled = !isAuthLoading && loginUsername.isNotBlank() && loginPassword.isNotBlank()
                                ) {
                                    if (isAuthLoading) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(24.dp),
                                            color = Color.Black,
                                            strokeWidth = 2.dp
                                        )
                                    } else {
                                        Text(
                                            text = "Log In & Play",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = Color.Black
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Don't have an account? ",
                                        fontSize = 12.sp,
                                        color = Color(0xFF94A3B8)
                                    )
                                    Text(
                                        text = "Sign Up",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF38BDF8),
                                        modifier = Modifier.clickable {
                                            selectedTab = 1
                                            viewModel.clearAuthMessages()
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // ==========================================
                // 2. SIGN UP TAB
                // ==========================================
                1 -> {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("signup_card"),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF131D31)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E3A8A))
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Create Free Account",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "No email or verification required! Get 1000 🪙 + 650 💎 immediately.",
                                    fontSize = 12.sp,
                                    color = Color(0xFF94A3B8),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                                )

                                // Username Input
                                OutlinedTextField(
                                    value = signupUsername,
                                    onValueChange = { signupUsername = it },
                                    label = { Text("Choose Username") },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Person,
                                            contentDescription = null,
                                            tint = Color(0xFF38BDF8)
                                        )
                                    },
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("auth_signup_username"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF38BDF8),
                                        unfocusedBorderColor = Color(0xFF334155),
                                        focusedContainerColor = Color(0xFF0F172A),
                                        unfocusedContainerColor = Color(0xFF0F172A),
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    ),
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Text,
                                        imeAction = ImeAction.Next
                                    )
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // Password Input
                                OutlinedTextField(
                                    value = signupPassword,
                                    onValueChange = { signupPassword = it },
                                    label = { Text("Choose Password") },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Lock,
                                            contentDescription = null,
                                            tint = Color(0xFF38BDF8)
                                        )
                                    },
                                    trailingIcon = {
                                        IconButton(onClick = { showSignupPassword = !showSignupPassword }) {
                                            Icon(
                                                imageVector = if (showSignupPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                                contentDescription = "Toggle password visibility",
                                                tint = Color(0xFF94A3B8)
                                            )
                                        }
                                    },
                                    visualTransformation = if (showSignupPassword) VisualTransformation.None else PasswordVisualTransformation(),
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("auth_signup_password"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF38BDF8),
                                        unfocusedBorderColor = Color(0xFF334155),
                                        focusedContainerColor = Color(0xFF0F172A),
                                        unfocusedContainerColor = Color(0xFF0F172A),
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    ),
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Password,
                                        imeAction = ImeAction.Done
                                    )
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                // Country Selection
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFF0F172A))
                                        .border(1.dp, Color(0xFF334155), RoundedCornerShape(12.dp))
                                        .clickable { showCountryPicker = !showCountryPicker }
                                        .padding(horizontal = 14.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = selectedCountry.flag, fontSize = 20.sp)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = "Country",
                                                fontSize = 10.sp,
                                                color = Color(0xFF94A3B8)
                                            )
                                            Text(
                                                text = selectedCountry.name,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color.White
                                            )
                                        }
                                    }
                                    Text(
                                        text = if (showCountryPicker) "▲" else "▼",
                                        color = Color(0xFF38BDF8),
                                        fontSize = 12.sp
                                    )
                                }

                                // Expandable Country Picker List
                                AnimatedVisibility(visible = showCountryPicker) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 8.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color(0xFF0B1120))
                                            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(12.dp))
                                            .padding(8.dp)
                                    ) {
                                        LazyColumn(modifier = Modifier.heightIn(max = 220.dp)) {
                                            items(DEFAULT_COUNTRIES) { c ->
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .background(if (selectedCountry.code == c.code) Color(0xFF1E293B) else Color.Transparent)
                                                        .clickable {
                                                            selectedCountry = c
                                                            showCountryPicker = false
                                                        }
                                                        .padding(horizontal = 10.dp, vertical = 8.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(text = c.flag, fontSize = 18.sp)
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(
                                                        text = c.name,
                                                        fontSize = 12.sp,
                                                        color = Color.White,
                                                        fontWeight = if (selectedCountry.code == c.code) FontWeight.Bold else FontWeight.Normal
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Choose Avatar
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = "CHOOSE STARTER AVATAR",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF94A3B8)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        items(starterAvatars) { avatar ->
                                            val isSelected = selectedAvatarId == avatar.id
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                modifier = Modifier.clickable { selectedAvatarId = avatar.id }
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(54.dp)
                                                        .clip(CircleShape)
                                                        .background(Color(0xFF1E293B))
                                                        .border(
                                                            width = if (isSelected) 3.dp else 1.dp,
                                                            color = if (isSelected) Color(0xFFF59E0B) else Color(0xFF334155),
                                                            shape = CircleShape
                                                        ),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Image(
                                                        painter = painterResource(id = avatar.resId),
                                                        contentDescription = avatar.name,
                                                        modifier = Modifier.fillMaxSize(),
                                                        contentScale = ContentScale.Crop
                                                    )
                                                }
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = avatar.name,
                                                    fontSize = 10.sp,
                                                    color = if (isSelected) Color(0xFFFDE68A) else Color(0xFF94A3B8),
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(22.dp))

                                // Sign Up Submit Button
                                Button(
                                    onClick = {
                                        focusManager.clearFocus()
                                        viewModel.signUp(
                                            username = signupUsername,
                                            password = signupPassword,
                                            countryCode = selectedCountry.code,
                                            countryName = selectedCountry.name,
                                            countryFlag = selectedCountry.flag,
                                            avatarId = selectedAvatarId
                                        )
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .testTag("auth_signup_button"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                    enabled = !isAuthLoading && signupUsername.isNotBlank() && signupPassword.isNotBlank()
                                ) {
                                    if (isAuthLoading) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(24.dp),
                                            color = Color.White,
                                            strokeWidth = 2.dp
                                        )
                                    } else {
                                        Text(
                                            text = "Create Account & Play ♟️",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = Color.White
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Already have an account? ",
                                        fontSize = 12.sp,
                                        color = Color(0xFF94A3B8)
                                    )
                                    Text(
                                        text = "Log In",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF38BDF8),
                                        modifier = Modifier.clickable {
                                            selectedTab = 0
                                            viewModel.clearAuthMessages()
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // ==========================================
                // 3. PLAY AS GUEST TAB
                // ==========================================
                2 -> {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("guest_card"),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF131D31)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF0284C7))
                        ) {
                            Column(
                                modifier = Modifier.padding(22.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF0C4A6E))
                                        .border(2.dp, Color(0xFF38BDF8), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.SportsEsports,
                                        contentDescription = "Guest Mode",
                                        tint = Color(0xFF38BDF8),
                                        modifier = Modifier.size(34.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Text(
                                    text = "Play Instantly as Guest",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )

                                Text(
                                    text = "Jump straight into chess battles without any password or registration required.",
                                    fontSize = 12.sp,
                                    color = Color(0xFF94A3B8),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(top = 4.dp, bottom = 18.dp)
                                )

                                // Highlights list
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFF0F172A))
                                        .padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    GuestFeatureRow("⚡", "Zero wait — 1-tap instant match play")
                                    GuestFeatureRow("🪙", "1000 Free Bonus Starter Tokens included")
                                    GuestFeatureRow("💎", "650 Free Starter Gems for custom avatars")
                                    GuestFeatureRow("📱", "Stats and match history stored on your device")
                                }

                                Spacer(modifier = Modifier.height(22.dp))

                                Button(
                                    onClick = { viewModel.playAsGuest() },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp)
                                        .testTag("auth_guest_button"),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                                    enabled = !isAuthLoading
                                ) {
                                    if (isAuthLoading) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(24.dp),
                                            color = Color.White,
                                            strokeWidth = 2.dp
                                        )
                                    } else {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.PlayArrow,
                                                contentDescription = null,
                                                tint = Color.White
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "Start Playing as Guest",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}

@Composable
private fun GuestFeatureRow(icon: String, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = icon, fontSize = 14.sp)
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            fontSize = 12.sp,
            color = Color(0xFFE2E8F0),
            fontWeight = FontWeight.Medium
        )
    }
}
