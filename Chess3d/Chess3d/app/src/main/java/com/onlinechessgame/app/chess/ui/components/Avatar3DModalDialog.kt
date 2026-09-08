package com.onlinechessgame.app.chess.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.onlinechessgame.app.chess.model.AvatarCategory
import com.onlinechessgame.app.chess.model.AvatarItem
import com.onlinechessgame.app.chess.model.AvatarRarity

@Composable
fun ModalRibbonHeader(
    title: String,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        // Title Text with Wing Emojis
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "🪽 ",
                fontSize = 18.sp
            )
            Text(
                text = title.uppercase(),
                color = Color(0xFFFDE68A),
                fontWeight = FontWeight.Black,
                fontSize = 16.sp,
                letterSpacing = 1.2.sp,
                textAlign = TextAlign.Center
            )
            Text(
                text = " 🪽",
                fontSize = 18.sp
            )
        }

        // Close Button (Top Right Dark Circle with Gold Border)
        IconButton(
            onClick = onClose,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(36.dp)
                .background(Color(0xFF0F172A), CircleShape)
                .border(1.5.dp, Color(0xFFFBBF24), CircleShape)
                .testTag("close_modal_button")
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun AvatarCircularPortrait(
    avatar: AvatarItem,
    isEquipped: Boolean = false,
    isLocked: Boolean = false,
    hasGlowSunburst: Boolean = false,
    hasGreenSparkle: Boolean = false,
    sizeDp: Int = 64,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "avatar_portrait_anim")
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowScale"
    )
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    Box(
        modifier = modifier.size(sizeDp.dp),
        contentAlignment = Alignment.Center
    ) {
        // Sunburst / Glow
        if (hasGlowSunburst || isEquipped) {
            Box(
                modifier = Modifier
                    .size((sizeDp * 1.15f).dp)
                    .scale(glowScale)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                (if (isEquipped) Color(0xFFFBBF24) else Color(0xFF38BDF8)).copy(alpha = glowAlpha),
                                Color.Transparent
                            )
                        ),
                        CircleShape
                    )
            )
        }

        // Circular Frame
        Box(
            modifier = Modifier
                .size(sizeDp.dp)
                .clip(CircleShape)
                .background(Color(0xFF0F172A))
                .border(
                    if (isEquipped) 3.dp else 2.dp,
                    if (isEquipped) Color(0xFFFBBF24) else Color(0xFF475569),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (avatar.resId != null) {
                Image(
                    painter = painterResource(id = avatar.resId),
                    contentDescription = avatar.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text(
                    text = avatar.iconEmoji ?: "♟️",
                    fontSize = (sizeDp * 0.5f).sp
                )
            }

            // Locked Overlay
            if (isLocked) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.65f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked",
                        tint = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.size((sizeDp * 0.35f).dp)
                    )
                }
            }
        }

        // Equipped Badge
        if (isEquipped) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size((sizeDp * 0.35f).dp)
                    .background(Color(0xFF10B981), CircleShape)
                    .border(1.5.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Equipped",
                    tint = Color.White,
                    modifier = Modifier.size((sizeDp * 0.22f).dp)
                )
            }
        }
    }
}

@Composable
fun Avatar3DModalDialog(
    avatar: AvatarItem,
    isEquipped: Boolean,
    isUnlocked: Boolean = true,
    playerLevel: Int = 15,
    playerGems: Int = 650,
    onEquip: () -> Unit = {},
    onBuy: () -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.82f))
                .clickable { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .width(310.dp)
                    .padding(16.dp)
                    .clickable(enabled = false) {}
                    .testTag("avatar_preview_dialog_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.5.dp, Color(0xFFFBBF24)),
                elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header Ribbon: Blue with Gold Border and Title "PREVIEW"
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color(0xFF2563EB), Color(0xFF1D4ED8))
                                )
                            )
                            .border(BorderStroke(1.dp, Color(0xFFFBBF24))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "PREVIEW",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            letterSpacing = 1.2.sp
                        )

                        // Close Button Top Right
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = 8.dp)
                                .size(30.dp)
                                .background(Color(0xFF1E3A8A), CircleShape)
                                .border(1.5.dp, Color(0xFFFBBF24), CircleShape)
                                .testTag("close_preview_modal_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Portrait with gold border & equipped checkmark badge
                        Box(
                            modifier = Modifier.size(96.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(92.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF0F172A))
                                    .border(2.5.dp, Color(0xFFFBBF24), CircleShape)
                            ) {
                                if (avatar.resId != null) {
                                    Image(
                                        painter = painterResource(id = avatar.resId),
                                        contentDescription = avatar.title,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Text(
                                        text = avatar.iconEmoji ?: "♟️",
                                        fontSize = 44.sp,
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                }
                            }

                            // Green Checkmark Circle Badge at Bottom Right if Equipped
                            if (isEquipped) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .size(28.dp)
                                        .background(Color(0xFF10B981), CircleShape)
                                        .border(2.dp, Color.White, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Equipped",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Green EQUIPPED Pill Badge under portrait if equipped
                        if (isEquipped) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF10B981)
                            ) {
                                Text(
                                    text = "EQUIPPED",
                                    color = Color.White,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.sp,
                                    letterSpacing = 0.5.sp,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        // Avatar Title (Bold Dark Text)
                        Text(
                            text = avatar.title.uppercase(),
                            color = Color(0xFF0F172A),
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            letterSpacing = 0.5.sp,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Price / Tag Badge (Light green outlined FREE badge or Gem Badge)
                        if (avatar.costGems > 0) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFFFFBEB),
                                border = BorderStroke(1.2.dp, Color(0xFFF59E0B))
                            ) {
                                Text(
                                    text = "💎 ${avatar.costGems} GEMS",
                                    color = Color(0xFFD97706),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 3.dp)
                                )
                            }
                        } else {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFECFDF5),
                                border = BorderStroke(1.2.dp, Color(0xFF10B981))
                            ) {
                                Text(
                                    text = "FREE",
                                    color = Color(0xFF059669),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Biography / Description
                        Text(
                            text = avatar.description ?: "Master of tactical foresight and strategic precision.",
                            color = Color(0xFF475569),
                            fontSize = 12.5.sp,
                            lineHeight = 16.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Bottom Action Button
                        if (isEquipped) {
                            Button(
                                onClick = onDismiss,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFECFDF5)),
                                border = BorderStroke(1.5.dp, Color(0xFF10B981)),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color(0xFF059669),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "CURRENTLY EQUIPPED",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp,
                                    color = Color(0xFF059669)
                                )
                            }
                        } else if (isUnlocked) {
                            Button(
                                onClick = onEquip,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("equip_button")
                            ) {
                                Text(
                                    text = "EQUIP",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp,
                                    letterSpacing = 0.5.sp,
                                    color = Color.White
                                )
                            }
                        } else if (avatar.costGems > 0) {
                            val canAfford = playerGems >= avatar.costGems
                            Button(
                                onClick = { if (canAfford) onBuy() },
                                enabled = canAfford,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (canAfford) Color(0xFFF59E0B) else Color(0xFF94A3B8)
                                ),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("buy_button")
                            ) {
                                Text(
                                    text = "PURCHASE FOR 💎 ${avatar.costGems} GEMS",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp,
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

@Composable
fun AvatarSelectionModalDialog(
    allAvatars: List<AvatarItem>,
    selectedAvatarId: String,
    playerLevel: Int = 15,
    playerGems: Int = 650,
    isAvatarUnlocked: (String) -> Boolean,
    onSelectAvatar: (String) -> Unit,
    onBuyAvatar: (AvatarItem) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedCategoryTab by remember { mutableIntStateOf(0) }
    var activeModalAvatar by remember { mutableStateOf<AvatarItem?>(null) }

    val categoryTabs = listOf(
        3 to "👦 BOYS / MEN",
        4 to "👧 GIRLS / WOMEN",
        5 to "🐾 ANIMALS",
        6 to "👑 KINGS / ROYALTY"
    )

    val filteredAvatars = remember(selectedCategoryTab, allAvatars) {
        when (selectedCategoryTab) {
            1 -> allAvatars.filter { it.costGems == 0 || it.category == AvatarCategory.FREE }
            2 -> allAvatars.filter { it.costGems > 0 || it.category == AvatarCategory.PREMIUM }
            3 -> allAvatars.filter {
                (it.gender == "Boy" || it.gender == "Man" || it.category == AvatarCategory.REALISTIC_MAN)
            }
            4 -> allAvatars.filter {
                (it.gender == "Girl" || it.gender == "Woman" || it.category == AvatarCategory.REALISTIC_WOMAN)
            }
            5 -> allAvatars.filter {
                it.gender == "Animal" || it.category == AvatarCategory.ANIMALS ||
                        it.title.contains("Wolf", ignoreCase = true) || it.title.contains("Lion", ignoreCase = true) ||
                        it.title.contains("Tiger", ignoreCase = true) || it.title.contains("Eagle", ignoreCase = true) ||
                        it.title.contains("Owl", ignoreCase = true) || it.title.contains("Fox", ignoreCase = true) ||
                        it.title.contains("Bear", ignoreCase = true) || it.title.contains("Panther", ignoreCase = true) ||
                        it.title.contains("Dragon", ignoreCase = true)
            }
            6 -> allAvatars.filter {
                it.title.contains("King", ignoreCase = true) || it.title.contains("Queen", ignoreCase = true) ||
                        it.title.contains("Empress", ignoreCase = true) || it.title.contains("Emperor", ignoreCase = true) ||
                        it.title.contains("Royal", ignoreCase = true) || it.title.contains("Prince", ignoreCase = true) ||
                        it.title.contains("Princess", ignoreCase = true) || it.title.contains("Imperial", ignoreCase = true) ||
                        it.title.contains("Knight", ignoreCase = true)
            }
            else -> allAvatars
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.85f))
                .clickable { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .width(360.dp)
                    .fillMaxHeight(0.92f)
                    .padding(vertical = 12.dp, horizontal = 8.dp)
                    .clickable(enabled = false) {}
                    .testTag("avatar_selection_dialog_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0A0F1D)),
                border = BorderStroke(
                    2.dp,
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFFBBF24),
                            Color(0xFF38BDF8),
                            Color(0xFFFBBF24)
                        )
                    )
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                ) {
                    // Header Ribbon with Title and Close Button
                    ModalRibbonHeader(
                        title = "AVATAR SELECTION",
                        onClose = onDismiss
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Horizontal Category Tabs Bar (Matching the Screenshots)
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        items(categoryTabs) { (idx, label) ->
                            val isSelected = selectedCategoryTab == idx
                            Surface(
                                modifier = Modifier
                                    .clickable { selectedCategoryTab = idx }
                                    .testTag("avatar_tab_$label"),
                                shape = RoundedCornerShape(14.dp),
                                color = if (isSelected) Color(0xFFFBBF24) else Color(0xFF1E293B),
                                border = if (isSelected) BorderStroke(1.5.dp, Color(0xFFFDE68A)) else BorderStroke(1.dp, Color(0xFF334155))
                            ) {
                                Text(
                                    text = label,
                                    color = if (isSelected) Color(0xFF0F172A) else Color(0xFF94A3B8),
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.SemiBold,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 4-Column Avatar Card Grid (Matching Reference Screenshots)
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentPadding = PaddingValues(bottom = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filteredAvatars) { avatar ->
                            val isEquipped = avatar.id == selectedAvatarId
                            val isUnlocked = isAvatarUnlocked(avatar.id)

                            Box(
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(Color(0xFF1E293B))
                                    .border(
                                        width = if (isEquipped) 2.5.dp else 1.2.dp,
                                        color = if (isEquipped) Color(0xFF22C55E) else if (avatar.rarity == AvatarRarity.LEGENDARY || avatar.costGems > 0) Color(0xFFFBBF24) else Color(0xFF334155),
                                        shape = RoundedCornerShape(18.dp)
                                    )
                                    .clickable { activeModalAvatar = avatar }
                                    .testTag("avatar_grid_item_${avatar.id}"),
                                contentAlignment = Alignment.Center
                            ) {
                                // Avatar Image filling the card
                                if (avatar.resId != null) {
                                    Image(
                                        painter = painterResource(id = avatar.resId),
                                        contentDescription = avatar.title,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Text(
                                        text = avatar.iconEmoji ?: "♟️",
                                        fontSize = 32.sp
                                    )
                                }

                                // Green checkmark badge at the top-right corner if equipped
                                if (isEquipped) {
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(top = 5.dp, end = 5.dp)
                                            .size(18.dp)
                                            .background(Color(0xFF10B981), CircleShape)
                                            .border(1.2.dp, Color.White, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(11.dp)
                                        )
                                    }
                                }

                                // Bottom Badge Overlay (Matching Screenshots: "🔒 Lv.1", "FREE", "💎 600")
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .padding(bottom = 3.dp)
                                ) {
                                    if (!isEquipped) {
                                        if (isUnlocked) {
                                            // Compact Green FREE Badge
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = Color(0xFF16A34A),
                                                border = BorderStroke(0.8.dp, Color(0xFF4ADE80))
                                            ) {
                                                Text(
                                                    text = "FREE",
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Black,
                                                    fontSize = 7.5.sp,
                                                    letterSpacing = 0.5.sp,
                                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                                )
                                            }
                                        } else if (avatar.costGems > 0) {
                                            // Gem Cost Badge
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = Color(0xEE0F172A),
                                                border = BorderStroke(0.8.dp, Color(0xFFFBBF24))
                                            ) {
                                                Text(
                                                    text = "💎 ${avatar.costGems}",
                                                    color = Color(0xFFFDE68A),
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 7.5.sp,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                )
                                            }
                                        } else {
                                            // Level Lock Badge (e.g. 🔒 Lv.1)
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = Color(0xEE0F172A),
                                                border = BorderStroke(0.8.dp, Color(0xFFFBBF24))
                                            ) {
                                                Text(
                                                    text = "🔒 Lv.${avatar.requiredLevel}",
                                                    color = Color(0xFFFDE68A),
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 7.5.sp,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Bottom Gem Unlock Banner (Exact Match from User's Screenshots!)
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .testTag("avatar_buy_banner"),
                        color = Color(0xFF0F172A),
                        border = BorderStroke(1.5.dp, Color(0xFF38BDF8))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "💎",
                                    fontSize = 20.sp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Unlock Stylish 3D\nAvatars with Gems!",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    lineHeight = 14.sp
                                )
                            }

                            Button(
                                onClick = { selectedCategoryTab = 2 }, // Switch to BUY / SHOP tab
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "BUY AVATARS",
                                    color = Color(0xFF0F172A),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Interactive 3D Modal or Locked Avatar Sub-Menu Dialog
    if (activeModalAvatar != null) {
        val avatar = activeModalAvatar!!
        val isUnlocked = isAvatarUnlocked(avatar.id)
        val isEquipped = avatar.id == selectedAvatarId

        if (!isUnlocked && avatar.costGems == 0) {
            LockedAvatarModalDialog(
                avatar = avatar,
                onDismiss = { activeModalAvatar = null }
            )
        } else {
            Avatar3DModalDialog(
                avatar = avatar,
                isEquipped = isEquipped,
                isUnlocked = isUnlocked,
                playerLevel = playerLevel,
                playerGems = playerGems,
                onEquip = {
                    onSelectAvatar(avatar.id)
                    activeModalAvatar = null
                },
                onBuy = {
                    onBuyAvatar(avatar)
                    activeModalAvatar = null
                },
                onDismiss = { activeModalAvatar = null }
            )
        }
    }
}

@Composable
fun LockedAvatarModalDialog(
    avatar: AvatarItem,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.82f))
                .clickable { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .width(310.dp)
                    .padding(16.dp)
                    .clickable(enabled = false) {}
                    .testTag("locked_avatar_dialog_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.5.dp, Color(0xFFFBBF24)),
                elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header Ribbon: Blue with Gold Border and Title "LOCKED AVATAR"
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color(0xFF2563EB), Color(0xFF1D4ED8))
                                )
                            )
                            .border(BorderStroke(1.dp, Color(0xFFFBBF24))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "LOCKED AVATAR",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            letterSpacing = 1.2.sp
                        )

                        // Close Button Top Right
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = 8.dp)
                                .size(30.dp)
                                .background(Color(0xFF1E3A8A), CircleShape)
                                .border(1.5.dp, Color(0xFFFBBF24), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Portrait with lock overlay on bottom right
                        Box(
                            modifier = Modifier.size(96.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(92.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF0F172A))
                                    .border(2.dp, Color(0xFFFBBF24), CircleShape)
                            ) {
                                if (avatar.resId != null) {
                                    Image(
                                        painter = painterResource(id = avatar.resId),
                                        contentDescription = avatar.title,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Text(
                                        text = avatar.iconEmoji ?: "♟️",
                                        fontSize = 44.sp,
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                }
                            }

                            // Gold Lock Circle Badge at Bottom Right
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .size(30.dp)
                                    .background(Color(0xFFF59E0B), CircleShape)
                                    .border(1.5.dp, Color(0xFF0F172A), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Locked",
                                    tint = Color(0xFF0F172A),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Avatar Title (Bold Dark Text)
                        Text(
                            text = avatar.title.uppercase(),
                            color = Color(0xFF0F172A),
                            fontWeight = FontWeight.Black,
                            fontSize = 17.sp,
                            letterSpacing = 0.5.sp,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Subtitle
                        Text(
                            text = "This avatar is locked. Unlock it to use in the game.",
                            color = Color(0xFF64748B),
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Requirement Card Box (Light Blue/Gray Box)
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp, horizontal = 16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "REQUIREMENT",
                                    color = Color(0xFFEA580C),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text("👑 ", fontSize = 16.sp)
                                    Text(
                                        text = "Reach Level ${avatar.requiredLevel}",
                                        color = Color(0xFF0F172A),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // OK Button
                        Button(
                            onClick = onDismiss,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("locked_avatar_ok_button")
                        ) {
                            Text(
                                text = "OK",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
