package com.example.evergreen.ui.theme.screens.dashboard

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.evergreen.data.AuthViewModel
import com.example.evergreen.data.EverGreenViewModel
import com.example.evergreen.models.UserModel
import com.example.evergreen.navigation.Routes
import com.example.evergreen.ui.theme.*

@Composable
fun ProfileScreen(
    navController: NavController,
    vm: EverGreenViewModel = viewModel(),
    vmAuth: AuthViewModel = viewModel()
) {

    val user by vm.user.collectAsState()
    val carbonEntries by vm.carbonEntries.collectAsState()

    LaunchedEffect(Unit) {
        vm.loadUser()
        vm.loadCarbonEntries()
        vm.loadHabits()
    }

    LaunchedEffect(Unit) {
        vmAuth.navigationEvent.collect { route ->
            navController.navigate(route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    val points = user?.totalPoints ?: 0
    val level = vm.getLevel(points)
    val levelProgress = vm.getLevelProgress(points)
    val nextLevel = vm.getNextLevelLabel(points)
    val totalEntries = carbonEntries.size
    val totalCo2 = carbonEntries.sumOf { it.totalEmission }

    val milestoneBadges = listOf(

        // 🌱 First entry
        MilestoneBadge(
            name = "🌱 Starter",
            icon = Icons.Default.Eco,
            isUnlocked = totalEntries >= 1
        ),

        // 🔥 7-day streak
        MilestoneBadge(
            name = "🔥 Consistent",
            icon = Icons.Default.Whatshot,
            isUnlocked = vm.streak >= 7
        ),

        // 🚶 Avoid driving
        MilestoneBadge(
            name = "🚶 Eco Walker",
            icon = Icons.AutoMirrored.Filled.DirectionsWalk,
            isUnlocked = vm.getCarKmEquivalent(totalCo2) >= 50
        ),

        // 💡 Energy saver
        MilestoneBadge(
            name = "💡 Energy Saver",
            icon = Icons.Default.Lightbulb,
            isUnlocked = totalEntries >= 10
        ),

        // ♻ Recycler
        MilestoneBadge(
            name = "♻ Recycler",
            icon = Icons.Default.Recycling,
            isUnlocked = vm.habitsCompletedCount >= 5
        ),

        // 🌳 Tree Guardian
        MilestoneBadge(
            name = "🌳 Tree Guardian",
            icon = Icons.Default.Park,
            isUnlocked = totalCo2 >= 100
        ),

        // 🌍 Planet Hero
        MilestoneBadge(
            name = "🌍 Planet Hero",
            icon = Icons.Default.Public,
            isUnlocked = points >= 1000
        )
    )

    ProfileContent(
        user = user,
        points = points,
        level = level,
        levelProgress = levelProgress,
        nextLevel = nextLevel,
        totalEntries = totalEntries,
        totalCo2 = totalCo2,
        streak = vm.streak,
        badges = milestoneBadges,
        onLogout = { vmAuth.logoutUser() },
        onUploadImage = { uri -> vm.uploadProfileImage(uri) },
        navController = navController
    )
}

@Composable
fun ProfileContent(
    user: UserModel?,
    points: Int,
    level: String,
    levelProgress: Float,
    nextLevel: String,
    totalEntries: Int,
    totalCo2: Double,
    streak: Int,
    badges: List<MilestoneBadge>,
    onLogout: () -> Unit,
    onUploadImage: (Uri) -> Unit,
    navController: NavController
) {

    var showLogoutDialog by remember { mutableStateOf(false) }

    var selectedImageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            onUploadImage(uri)
        }
    }

    if (showLogoutDialog) {

        AlertDialog(
            onDismissRequest = {
                showLogoutDialog = false
            },

            shape = RoundedCornerShape(20.dp),

            containerColor = Color.White,

            icon = {
                Icon(
                    Icons.AutoMirrored.Filled.ExitToApp,
                    null,
                    tint = EverGreenPrimary
                )
            },

            title = {
                Text(
                    "Sign out?",
                    fontWeight = FontWeight.Bold
                )
            },

            text = {
                Text(
                    "You'll need to login again to continue."
                )
            },

            confirmButton = {

                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    }
                ) {
                    Text(
                        "Sign out",
                        color = Color.Red
                    )
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {
                        showLogoutDialog = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        bottomBar = {
            EverGreenBottomBar(
                navController,
                Routes.PROFILE
            )
        },

        containerColor = EverGreenSurface
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {

            // ───────────────── HEADER ─────────────────

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(EverGreenPrimary)
                    .padding(
                        horizontal = 20.dp,
                        vertical = 24.dp
                    )
            ) {

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // PROFILE IMAGE

                    Box(
                        modifier = Modifier.size(100.dp),
                        contentAlignment = Alignment.BottomEnd
                    ) {

                        if (
                            selectedImageUri != null ||
                            !user?.profileImageUrl.isNullOrEmpty()
                        ) {

                            AsyncImage(
                                model = selectedImageUri
                                    ?: user?.profileImageUrl,

                                contentDescription = null,

                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape)
                                    .border(
                                        3.dp,
                                        Color.White.copy(alpha = 0.4f),
                                        CircleShape
                                    )
                            )

                        } else {

                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape)
                                    .background(EverGreenAccent)
                                    .border(
                                        3.dp,
                                        Color.White.copy(alpha = 0.4f),
                                        CircleShape
                                    ),

                                contentAlignment = Alignment.Center
                            ) {

                                Text(
                                    text = user?.username
                                        ?.take(1)
                                        ?.uppercase() ?: "U",

                                    fontSize = 34.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        FloatingActionButton(
                            onClick = {
                                launcher.launch("image/*")
                            },

                            modifier = Modifier.size(32.dp),

                            containerColor = EverGreenPrimary
                        ) {

                            Icon(
                                Icons.Default.Edit,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    Text(
                        text = user?.username ?: "Eco User",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = user?.email ?: "",
                        fontSize = 12.sp,
                        color = EverGreenLight
                    )

                    Spacer(Modifier.height(10.dp))

                    // BIO

                    Text(
                        text = if (user?.bio.isNullOrBlank())
                            "No bio added yet 🌱"
                        else
                            user?.bio ?: "",

                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(14.dp))

                    // LEVEL BADGE

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White.copy(alpha = 0.15f)
                    ) {

                        Text(
                            text = level,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(
                                horizontal = 14.dp,
                                vertical = 6.dp
                            )
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = {
                            navController.navigate(Routes.EDIT_PROFILE)
                        },

                        shape = RoundedCornerShape(20.dp),

                        border = BorderStroke(
                            1.dp,
                            Color.White.copy(alpha = 0.5f)
                        ),

                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White
                        )
                    ) {

                        Icon(
                            Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )

                        Spacer(Modifier.width(6.dp))

                        Text("Edit Profile")
                    }
                }
            }

            // ───────────────── BODY ─────────────────

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                // STATS

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {

                    ProfileStatCard(
                        modifier = Modifier.weight(1f),
                        label = "Points",
                        value = "$points",
                        icon = Icons.Default.Star,
                        iconTint = LeafGold
                    )

                    ProfileStatCard(
                        modifier = Modifier.weight(1f),
                        label = "Streak",
                        value = "${streak}d",
                        icon = Icons.Default.Whatshot,
                        iconTint = Color(0xFFEF5350)
                    )

                    ProfileStatCard(
                        modifier = Modifier.weight(1f),
                        label = "Entries",
                        value = "$totalEntries",
                        icon = Icons.Default.Description,
                        iconTint = EverGreenPrimary
                    )
                }

                // LEVEL PROGRESS

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {

                            Text(
                                "Level Progress",
                                fontWeight = FontWeight.SemiBold
                            )

                            Text(
                                nextLevel,
                                color = EverGreenPrimary,
                                fontSize = 12.sp
                            )
                        }

                        Spacer(Modifier.height(12.dp))

                        LinearProgressIndicator(
                            progress = { levelProgress },

                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),

                            color = EverGreenPrimary,
                            trackColor = EverGreenPale,
                            strokeCap = StrokeCap.Round
                        )

                        Spacer(Modifier.height(6.dp))

                        Text(
                            "${(levelProgress * 100).toInt()}% to next level",
                            fontSize = 11.sp,
                            color = CarbonGrayLight
                        )
                    }
                }

                // CARBON SUMMARY

                Card(
                    modifier = Modifier.fillMaxWidth(),

                    shape = RoundedCornerShape(16.dp),

                    colors = CardDefaults.cardColors(
                        containerColor = EverGreenPale
                    )
                ) {

                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),

                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(EverGreenPrimary),

                            contentAlignment = Alignment.Center
                        ) {

                            Icon(
                                Icons.Default.Eco,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }

                        Spacer(Modifier.width(14.dp))

                        Column {

                            Text(
                                text = "${"%.1f".format(totalCo2)} kg CO₂",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = EverGreenPrimary
                            )

                            Text(
                                "Total emissions logged",
                                fontSize = 12.sp,
                                color = CarbonGrayLight
                            )
                        }
                    }
                }

                // MILESTONES

                Text(
                    text = "Recent Milestones",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = CarbonGray
                )

                MilestoneRow(
                    badges = badges
                )

                // ACCOUNT

                ProfileSectionLabel("Account")

                ProfileMenuCard {

                    ProfileMenuItem(
                        icon = Icons.Default.Person,
                        label = "Edit Profile"
                    ) {
                        navController.navigate(Routes.EDIT_PROFILE)
                    }

                    HorizontalDivider(
                        thickness = 0.5.dp,
                        color = EverGreenPale
                    )

                    ProfileMenuItem(
                        icon = Icons.Default.Notifications,
                        label = "Notifications"
                    ) {
                        navController.navigate(Routes.NOTIFICATIONS)
                    }

                    HorizontalDivider(
                        thickness = 0.5.dp,
                        color = EverGreenPale
                    )

                    ProfileMenuItem(
                        icon = Icons.Default.Lock,
                        label = "Change Password"
                    ) {

                    }
                }

                // APP

                ProfileSectionLabel("App")

                ProfileMenuCard {

                    ProfileMenuItem(
                        icon = Icons.Default.BarChart,
                        label = "Carbon History"
                    ) {
                        navController.navigate(Routes.ADD_CARBON)
                    }

                    HorizontalDivider(
                        thickness = 0.5.dp,
                        color = EverGreenPale
                    )

                    ProfileMenuItem(
                        icon = Icons.Default.CheckCircle,
                        label = "My Habits"
                    ) {
                        navController.navigate(Routes.HABITS)
                    }

                    HorizontalDivider(
                        thickness = 0.5.dp,
                        color = EverGreenPale
                    )

                    ProfileMenuItem(
                        icon = Icons.Default.Lightbulb,
                        label = "Recommendations"
                    ) {
                        navController.navigate(Routes.RECOMMENDATIONS)
                    }
                }

                // ABOUT

                ProfileSectionLabel("About")

                ProfileMenuCard {

                    ProfileMenuItem(
                        icon = Icons.Default.Info,
                        label = "About EverGreen"
                    ) {

                    }

                    HorizontalDivider(
                        thickness = 0.5.dp,
                        color = EverGreenPale
                    )

                    ProfileMenuItem(
                        icon = Icons.Default.Shield,
                        label = "Privacy Policy"
                    ) {

                    }
                }

                // LOGOUT

                OutlinedButton(
                    onClick = {
                        showLogoutDialog = true
                    },

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),

                    shape = RoundedCornerShape(12.dp),

                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.Red
                    ),

                    border = BorderStroke(
                        1.dp,
                        Color.Red.copy(alpha = 0.4f)
                    )
                ) {

                    Icon(
                        Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(Modifier.width(8.dp))

                    Text(
                        "Sign Out",
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(Modifier.height(6.dp))

                Text(
                    text = "EverGreen v1.0.0 • Track. Reduce. Sustain.",
                    fontSize = 10.sp,
                    color = CarbonGrayLight,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// HELPER COMPOSABLES
// ─────────────────────────────────────────────────────────────

@Composable
fun ProfileStatCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    icon: ImageVector,
    iconTint: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = BorderStroke(1.dp, EverGreenPale)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = CarbonGray
            )

            Text(
                text = label,
                fontSize = 11.sp,
                color = CarbonGrayLight
            )
        }
    }
}

@Composable
fun ProfileSectionLabel(label: String) {
    Text(
        text = label,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = CarbonGrayLight,
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
    )
}

@Composable
fun ProfileMenuCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = BorderStroke(1.dp, EverGreenPale)
    ) {
        Column(content = content)
    }
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = EverGreenPrimary,
            modifier = Modifier.size(20.dp)
        )

        Spacer(Modifier.width(14.dp))

        Text(
            text = label,
            modifier = Modifier.weight(1f),
            fontSize = 14.sp,
            color = CarbonGray
        )

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = CarbonGrayLight.copy(alpha = 0.5f),
            modifier = Modifier.size(18.dp)
        )
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
            badges        = emptyList(),
            onLogout      = {},
            onUploadImage = {},
            navController = rememberNavController()
        )
    }
}
