package com.example.evergreen.ui.theme.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.evergreen.data.EverGreenViewModel
import com.example.evergreen.models.UserModel
import com.example.evergreen.navigation.*
import com.example.evergreen.ui.theme.*
import java.util.Calendar

// ─── Data classes ─────────────────────────────────────────────────────────────

data class EcoEvent(
    val title:    String,
    val date:     String,
    val location: String,
    val category: String
)

data class MilestoneBadge(
    val name:       String,
    val icon:       ImageVector,
    val isUnlocked: Boolean
)

// ─── Dashboard Screen ─────────────────────────────────────────────────────────
@Composable
fun DashboardScreen(
    navController: NavController,
    vm: EverGreenViewModel = viewModel()
) {
    val user          by vm.user.collectAsState()
    val carbonEntries by vm.carbonEntries.collectAsState()

    LaunchedEffect(Unit) {
        vm.loadUser()
        vm.loadCarbonEntries()
        vm.loadHabits()
    }

    val points    = user?.totalPoints ?: 0
    val savedCo2  = vm.getSavedCo2Today()
    val hour      = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greeting  = when {
        hour < 12 -> "Good morning ☀️"
        hour < 17 -> "Good afternoon 🌤️"
        else      -> "Good evening 🌙"
    }

    val events = listOf(
        EcoEvent("Tree Planting 🌳",    "10 May", "Karura Forest", "Nature"),
        EcoEvent("River Clean-up 🧹",   "15 May", "Nairobi River", "Community"),
        EcoEvent("Climate Workshop 🎤", "20 May", "Campus Hall",   "Education")
    )

    DashboardContent(
        user           = user,
        userGreeting   = greeting,
        streak         = vm.streak,
        savedCo2       = savedCo2,
        trees          = vm.getTreesEquivalent(savedCo2),
        carKm          = vm.getCarKmEquivalent(savedCo2),
        tips           = carbonEntries.lastOrNull()
                            ?.let { vm.getRecommendations(it) }
                            ?: listOf("Log your first carbon entry to get personalised tips!"),
        points         = points,
        level          = vm.getLevel(points),
        pointsToNext   = vm.getPointsToNextLevel(points),
        levelProgress  = vm.getLevelProgress(points),
        nextLevelLabel = vm.getNextLevelLabel(points),
        events         = events,
        navController  = navController
    )
}

// ─── Dashboard Content ────────────────────────
@Composable
fun DashboardContent(
    user: UserModel?,
    userGreeting: String,
    streak: Int,
    savedCo2: Double,
    trees: Double,
    carKm: Double,
    tips: List<String>,
    points: Int,
    level: String,
    pointsToNext: Int,
    levelProgress: Float,
    nextLevelLabel: String,
    events: List<EcoEvent>,
    navController: NavController
) {
    Scaffold(
        bottomBar = { EverGreenBottomBar(navController, Routes.DASHBOARD) },
        containerColor = EverGreenSurface,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.goToAddCarbon() },
                containerColor = EverGreenPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {

            // ── HEADER ─────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(EverGreenPrimary)
                    .padding(20.dp)
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(userGreeting, fontSize = 12.sp, color = EverGreenLight)
                        Text(
                            "${user?.username ?: "there"} 🌿",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🔥 $streak", color = Color.White)
                        Spacer(Modifier.width(10.dp))

                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(EverGreenAccent)
                                .clickable { navController.goToProfile() },
                            contentAlignment = Alignment.Center
                        ) {
                            if (!user?.profileImageUrl.isNullOrEmpty()) {
                                AsyncImage(
                                    model = user?.profileImageUrl,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Text(
                                    user?.username?.take(1)?.uppercase() ?: "U",
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                // ── LEVEL CARD (MAIN FEATURE) ─────────────────
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(Modifier.padding(16.dp)) {

                        Text("Your Level", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = CarbonGrayLight)

                        Spacer(Modifier.height(6.dp))

                        Text(
                            level,
                            fontSize = 20.sp,
                            color = EverGreenPrimary,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(Modifier.height(10.dp))

                        LinearProgressIndicator(
                            progress = { levelProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = EverGreenPrimary,
                            trackColor = EverGreenPale,
                            strokeCap = StrokeCap.Round
                        )

                        Spacer(Modifier.height(6.dp))

                        Text(
                            "$pointsToNext pts to reach $nextLevelLabel",
                            fontSize = 11.sp,
                            color = CarbonGrayLight
                        )
                    }
                }

                // ── ECO IMPACT ───────────────────────────────
                Text("Eco Impact Today", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = CarbonGray)

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    QuickStatCard(
                        modifier = Modifier.weight(1f),
                        label = "Trees",
                        value = "%.2f".format(trees),
                        sub = "🌳 saved"
                    )
                    QuickStatCard(
                        modifier = Modifier.weight(1f),
                        label = "Driving",
                        value = "%.1f".format(carKm),
                        sub = "km avoided 🚗"
                    )
                }

                // ── DAILY CHALLENGE ─────────────────────────
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = EverGreenPale)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("🌱 Daily Challenge", fontWeight = FontWeight.Bold, color = EverGreenPrimary, fontSize = 13.sp)
                        Spacer(Modifier.height(4.dp))
                        Text("Avoid plastic bottles today", fontSize = 12.sp, color = CarbonGray)

                        Spacer(Modifier.height(10.dp))

                        Button(
                            onClick = { },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EverGreenPrimary)
                        ) {
                            Text("Mark Done", fontSize = 12.sp)
                        }
                    }
                }

                // ── MILESTONES ─────────────────────────────────────────────────
                Text(
                    text       = "Recent Milestones",
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = CarbonGray
                )
                MilestoneRow(
                    badges = listOf(
                        MilestoneBadge("Starter", Icons.Default.Eco,            true),
                        MilestoneBadge("Walker",  Icons.AutoMirrored.Filled.DirectionsWalk, true),
                        MilestoneBadge("Saver",   Icons.Default.Savings,        false),
                        MilestoneBadge("Global",  Icons.Default.Public,         false)
                    )
                )

                // ── EVENTS ────────────────────────────────
                Text("Events", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = CarbonGray)

                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(events) { event ->
                        EventCard(event)
                    }
                }

                // ── TIPS ─────────────────────────────────
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Quick Tips", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = CarbonGray)
                            Text(
                                text     = "See all",
                                fontSize = 11.sp,
                                color    = EverGreenPrimary,
                                modifier = Modifier.clickable { navController.goToRecommendations() }
                            )
                        }

                        Spacer(Modifier.height(10.dp))

                        tips.take(3).forEach { tip ->
                            Text("• $tip", fontSize = 12.sp, color = CarbonGray, lineHeight = 18.sp)
                            Spacer(Modifier.height(4.dp))
                        }
                    }
                }

                // ── ACTION BUTTONS ───────────────────────
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = { navController.goToAddCarbon() },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EverGreenPrimary)
                    ) {
                        Icon(Icons.Filled.Add, null, Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Log Carbon", fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = { navController.goToHabits() },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = EverGreenPrimary)
                    ) {
                        Icon(Icons.Filled.CheckCircle, null, Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Habits", fontSize = 12.sp)
                    }
                }

                Spacer(Modifier.height(80.dp))
            }
        }
    }
}

// ── Shared bottom nav bar ─────────────────────────────────────────────────────
@Composable
fun EverGreenBottomBar(
    navController: NavController, 
    currentRoute: String,
    vm: EverGreenViewModel = viewModel()
) {
    val user by vm.user.collectAsState()
    
    NavigationBar(containerColor = Color.White, tonalElevation = 0.dp) {
        bottomNavItems.forEach { item ->
            val isSelected = currentRoute == item.route
            NavigationBarItem(
                selected = isSelected,
                onClick  = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(Routes.DASHBOARD) { saveState = true }
                            launchSingleTop = true
                            restoreState    = true
                        }
                    }
                },
                icon  = {
                    if (item.route == Routes.PROFILE && !user?.profileImageUrl.isNullOrEmpty()) {
                        AsyncImage(
                            model = user?.profileImageUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .border(
                                    width = 1.5.dp,
                                    color = if (isSelected) EverGreenPrimary else Color.Transparent,
                                    shape = CircleShape
                                ),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(item.icon, item.label, Modifier.size(22.dp))
                    }
                },
                label  = { Text(item.label, fontSize = 10.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor   = EverGreenPrimary,
                    selectedTextColor   = EverGreenPrimary,
                    unselectedIconColor = CarbonGrayLight,
                    unselectedTextColor = CarbonGrayLight,
                    indicatorColor      = EverGreenPale
                )
            )
        }
    }
}

// ── Helper Composables ────────────────────────────────────────────────────────

@Composable
private fun QuickStatCard(
    modifier: Modifier = Modifier,
    label:    String,
    value:    String,
    sub:      String
) {
    Card(
        modifier = modifier,
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(containerColor = Color.White),
        border   = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = label, fontSize = 11.sp, color = CarbonGrayLight)
            Spacer(Modifier.height(4.dp))
            Text(
                text       = value,
                fontSize   = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color      = EverGreenPrimary
            )
            Text(text = sub, fontSize = 10.sp, color = CarbonGrayLight)
        }
    }
}

@Composable
fun EventCard(event: EcoEvent) {
    Card(
        shape    = RoundedCornerShape(16.dp),
        modifier = Modifier.width(220.dp),
        colors   = CardDefaults.cardColors(containerColor = Color.White),
        border   = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text       = event.title,
                fontWeight = FontWeight.SemiBold,
                fontSize   = 13.sp,
                color      = EverGreenPrimary
            )
            Spacer(Modifier.height(6.dp))
            Text("📅 ${event.date}",     fontSize = 11.sp, color = CarbonGrayLight)
            Text("📍 ${event.location}", fontSize = 11.sp, color = CarbonGrayLight)
            Spacer(Modifier.height(8.dp))
            Button(
                onClick  = { },
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(10.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = EverGreenPrimary)
            ) {
                Text("Join", fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun MilestoneRow(badges: List<MilestoneBadge>) {
    LazyRow(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(badges) { badge ->
            Card(
                shape    = RoundedCornerShape(12.dp),
                colors   = CardDefaults.cardColors(
                    containerColor = if (badge.isUnlocked) EverGreenPale else Color.White
                ),
                border   = if (badge.isUnlocked) null else CardDefaults.outlinedCardBorder(),
                modifier = Modifier.width(85.dp)
            ) {
                Column(
                    modifier            = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                if (badge.isUnlocked) EverGreenPrimary else EverGreenPale
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector        = badge.icon,
                            contentDescription = null,
                            tint               = if (badge.isUnlocked) Color.White else CarbonGrayLight,
                            modifier           = Modifier.size(20.dp)
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text       = badge.name,
                        fontSize   = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color      = if (badge.isUnlocked) EverGreenPrimary else CarbonGrayLight
                    )
                }
            }
        }
    }
}

// ── Preview ───────────────────────────────────────────────────────────────────
@Preview(showBackground = true)
@Composable
fun DashboardPreview() {
    EverGreenTheme {
        DashboardContent(
            user           = UserModel(username = "Eco Hero", totalPoints = 450),
            userGreeting   = "Good morning ☀️",
            streak         = 5,
            savedCo2       = 4.5,
            trees          = 0.23,
            carKm          = 18.0,
            tips           = listOf(
                "Try public transport twice a week — save up to 30 kg CO₂/month",
                "Switch to LED bulbs and unplug devices when idle",
                "Replace one meat meal per day with plant-based food"
            ),
            points         = 450,
            level          = "Green Champion 🌳",
            pointsToNext   = 50,
            levelProgress  = 0.8f,
            nextLevelLabel = "Planet Guardian 🌎",
            events         = listOf(
                EcoEvent("Tree Planting 🌳",    "10 May", "Karura Forest", "Nature"),
                EcoEvent("River Clean-up 🧹",   "15 May", "Nairobi River", "Community"),
                EcoEvent("Climate Workshop 🎤", "20 May", "Campus Hall",   "Education")
            ),
            navController = rememberNavController()
        )
    }
}
