package com.example.evergreen.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.evergreen.data.EverGreenViewModel
import com.example.evergreen.models.UserModel
import com.example.evergreen.navigation.Routes
import com.example.evergreen.ui.screens.dashboard.EverGreenBottomBar
import com.example.evergreen.ui.theme.*
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ProfileScreen(
    navController: NavController,
    vm: EverGreenViewModel = viewModel()
) {
    val user by vm.user.collectAsState()
    val carbonEntries by vm.carbonEntries.collectAsState()

    LaunchedEffect(Unit) {
        vm.loadUser()
        vm.loadCarbonEntries()
    }

    val points        = user?.totalPoints ?: 0
    val level         = vm.getLevel(points)
    val levelProgress = vm.getLevelProgress(points)
    val nextLevel     = vm.getNextLevelLabel(points)
    val totalEntries  = carbonEntries.size
    val totalCo2      = carbonEntries.sumOf { it.totalEmission }

    ProfileContent(
        user          = user,
        points        = points,
        level         = level,
        levelProgress = levelProgress,
        nextLevel     = nextLevel,
        totalEntries  = totalEntries,
        totalCo2      = totalCo2,
        streak        = vm.streak,
        navController = navController
    )
}

@Composable
fun ProfileContent(
    user:          UserModel?,
    points:        Int,
    level:         String,
    levelProgress: Float,
    nextLevel:     String,
    totalEntries:  Int,
    totalCo2:      Double,
    streak:        Int,
    navController: NavController
) {
    var showLogoutDialog by remember { mutableStateOf(false) }

    // ── Logout dialog ─────────────────────────────────────────────────────────
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            shape            = RoundedCornerShape(20.dp),
            containerColor   = Color.White,
            icon = {
                Icon(
                    Icons.AutoMirrored.Filled.ExitToApp,
                    null,
                    tint = EverGreenPrimary,
                    modifier = Modifier.size(28.dp)
                )
            },
            title = {
                Text(
                    "Sign out?",
                    fontWeight = FontWeight.SemiBold,
                    color      = CarbonGray
                )
            },
            text = {
                Text(
                    "You'll need to log in again to access your eco data.",
                    fontSize = 13.sp,
                    color    = CarbonGrayLight
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }) {
                    Text("Sign out", color = Color(0xFFDC2626), fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel", color = CarbonGrayLight)
                }
            }
        )
    }

    Scaffold(
        bottomBar      = { EverGreenBottomBar(navController, Routes.PROFILE) },
        containerColor = EverGreenSurface
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {

            // ── Header banner ─────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(EverGreenPrimary)
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()) {

                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(EverGreenAccent)
                            .border(3.dp, Color.White.copy(alpha = 0.4f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text       = user?.username?.take(1)?.uppercase() ?: "U",
                            fontSize   = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color      = Color.White
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text       = user?.username ?: "Eco User",
                        fontSize   = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = Color.White
                    )
                    Text(
                        text     = user?.email ?: "",
                        fontSize = 12.sp,
                        color    = EverGreenLight
                    )

                    Spacer(Modifier.height(12.dp))

                    // Level badge
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0x33FFFFFF)
                    ) {
                        Text(
                            text     = level,
                            fontSize = 12.sp,
                            color    = Color.White,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp)
                        )
                    }
                }
            }

            Column(
                modifier            = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                // ── Stats row ─────────────────────────────────────────────────
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ProfileStatCard(
                        modifier = Modifier.weight(1f),
                        label    = "Points",
                        value    = "$points",
                        icon     = Icons.Default.Star,
                        iconTint = LeafGold
                    )
                    ProfileStatCard(
                        modifier = Modifier.weight(1f),
                        label    = "Streak",
                        value    = "${streak}d",
                        icon     = Icons.Default.Whatshot,
                        iconTint = Color(0xFFEF5350)
                    )
                    ProfileStatCard(
                        modifier = Modifier.weight(1f),
                        label    = "Entries",
                        value    = "$totalEntries",
                        icon     = Icons.Default.Description,
                        iconTint = EverGreenPrimary
                    )
                }

                // ── Level progress card ───────────────────────────────────────
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(16.dp),
                    colors   = CardDefaults.cardColors(containerColor = Color.White),
                    border   = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier              = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment     = Alignment.CenterVertically
                        ) {
                            Text(
                                text       = "Level progress",
                                fontSize   = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color      = CarbonGray
                            )
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = LeafGoldLight
                            ) {
                                Text(
                                    text     = nextLevel,
                                    fontSize = 10.sp,
                                    color    = LeafGold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                        Spacer(Modifier.height(10.dp))
                        LinearProgressIndicator(
                            progress   = { levelProgress },
                            modifier   = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color      = EverGreenPrimary,
                            trackColor = EverGreenPale,
                            strokeCap  = StrokeCap.Round
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text     = "${"%.0f".format(levelProgress * 100)}% to next level",
                            fontSize = 11.sp,
                            color    = CarbonGrayLight
                        )
                    }
                }

                // ── Carbon summary card ───────────────────────────────────────
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(16.dp),
                    colors   = CardDefaults.cardColors(containerColor = EverGreenPale),
                    border   = CardDefaults.outlinedCardBorder()
                ) {
                    Row(
                        modifier          = Modifier.padding(16.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(EverGreenPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Eco,
                                null,
                                tint     = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(Modifier.width(14.dp))
                        Column {
                            Text(
                                text       = "${"%.1f".format(totalCo2)} kg CO₂",
                                fontSize   = 20.sp,
                                fontWeight = FontWeight.SemiBold,
                                color      = EverGreenPrimary
                            )
                            Text(
                                text     = "Total emissions logged",
                                fontSize = 12.sp,
                                color    = CarbonGrayLight
                            )
                        }
                    }
                }

                // ── Section: Account ──────────────────────────────────────────
                ProfileSectionLabel("Account")

                ProfileMenuCard {
                    ProfileMenuItem(
                        icon  = Icons.Default.Person,
                        label = "Edit profile",
                        onClick = { /* TODO: navigate to edit profile */ }
                    )
                    HorizontalDivider(thickness = 0.5.dp, color = EverGreenPale)
                    ProfileMenuItem(
                        icon  = Icons.Default.Notifications,
                        label = "Notifications",
                        onClick = { /* TODO */ }
                    )
                    HorizontalDivider(thickness = 0.5.dp, color = EverGreenPale)
                    ProfileMenuItem(
                        icon  = Icons.Default.Lock,
                        label = "Change password",
                        onClick = { /* TODO */ }
                    )
                }

                // ── Section: App ──────────────────────────────────────────────
                ProfileSectionLabel("App")

                ProfileMenuCard {
                    ProfileMenuItem(
                        icon  = Icons.Default.BarChart,
                        label = "My carbon history",
                        onClick = { navController.navigate(Routes.ADD_CARBON) }
                    )
                    HorizontalDivider(thickness = 0.5.dp, color = EverGreenPale)
                    ProfileMenuItem(
                        icon  = Icons.Default.CheckCircle,
                        label = "My habits",
                        onClick = { navController.navigate(Routes.HABITS) }
                    )
                    HorizontalDivider(thickness = 0.5.dp, color = EverGreenPale)
                    ProfileMenuItem(
                        icon  = Icons.Default.Lightbulb,
                        label = "Recommendations",
                        onClick = { navController.navigate(Routes.RECOMMENDATIONS) }
                    )
                }

                // ── Section: About ────────────────────────────────────────────
                ProfileSectionLabel("About")

                ProfileMenuCard {
                    ProfileMenuItem(
                        icon  = Icons.Default.Info,
                        label = "About EverGreen",
                        onClick = { /* TODO */ }
                    )
                    HorizontalDivider(thickness = 0.5.dp, color = EverGreenPale)
                    ProfileMenuItem(
                        icon  = Icons.Default.Shield,
                        label = "Privacy policy",
                        onClick = { /* TODO */ }
                    )
                }

                // ── Sign out button ───────────────────────────────────────────
                Spacer(Modifier.height(4.dp))

                OutlinedButton(
                    onClick  = { showLogoutDialog = true },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape    = RoundedCornerShape(12.dp),
                    colors   = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFDC2626)
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp, Color(0xFFDC2626).copy(alpha = 0.4f)
                    )
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ExitToApp,
                        null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Sign out",
                        fontSize   = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.3.sp
                    )
                }

                Spacer(Modifier.height(8.dp))

                Text(
                    text     = "EverGreen v1.0.0  •  Track. Reduce. Sustain.",
                    fontSize = 10.sp,
                    color    = CarbonGrayLight,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

// ── Reusable components ───────────────────────────────────────────────────────

@Composable
private fun ProfileSectionLabel(text: String) {
    Text(
        text       = text,
        fontSize   = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color      = CarbonGrayLight,
        letterSpacing = 0.8.sp,
        modifier   = Modifier.padding(top = 4.dp, start = 4.dp)
    )
}

@Composable
private fun ProfileMenuCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(containerColor = Color.White),
        border   = CardDefaults.outlinedCardBorder()
    ) {
        Column(content = content)
    }
}

@Composable
private fun ProfileMenuItem(
    icon:    ImageVector,
    label:   String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(9.dp))
                    .background(EverGreenPale),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = icon,
                    contentDescription = null,
                    tint               = EverGreenPrimary,
                    modifier           = Modifier.size(17.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Text(
                text     = label,
                fontSize = 14.sp,
                color    = CarbonGray
            )
        }
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            null,
            tint     = CarbonGrayLight,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun ProfileStatCard(
    modifier: Modifier = Modifier,
    label:    String,
    value:    String,
    icon:     ImageVector,
    iconTint: Color
) {
    Card(
        modifier = modifier,
        shape    = RoundedCornerShape(14.dp),
        colors   = CardDefaults.cardColors(containerColor = Color.White),
        border   = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier            = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, null, tint = iconTint, modifier = Modifier.size(20.dp))
            Spacer(Modifier.height(6.dp))
            Text(
                text       = value,
                fontSize   = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color      = CarbonGray
            )
            Text(text = label, fontSize = 10.sp, color = CarbonGrayLight)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfileScreenPreview() {
    EverGreenTheme {
        ProfileContent(
            user          = UserModel(username = "Eco Hero", email = "eco@example.com", totalPoints = 450),
            points        = 450,
            level         = "Eco Saver 🌿",
            levelProgress = 0.75f,
            nextLevel     = "Green Champion 🌳",
            totalEntries  = 12,
            totalCo2      = 48.5,
            streak        = 7,
            navController = rememberNavController()
        )
    }
}