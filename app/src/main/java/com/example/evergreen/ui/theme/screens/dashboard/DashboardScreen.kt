package com.example.evergreen.ui.theme.screens.dashboard

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.evergreen.data.EverGreenViewModel
import com.example.evergreen.models.CarbonEntry
import com.example.evergreen.models.UserModel
import com.example.evergreen.navigation.*
import com.example.evergreen.ui.theme.*
import java.util.Calendar

@Composable
fun DashboardScreen(
    navController: NavController,
    vm: EverGreenViewModel = viewModel()
) {
    val user by vm.user.collectAsState()
    val carbonEntries by vm.carbonEntries.collectAsState()

    // Load data on first composition
    LaunchedEffect(Unit) {
        vm.loadUser()
        vm.loadCarbonEntries()
        vm.loadHabits()
    }

    val points = user?.totalPoints ?: 0
    val savedCo2 = vm.getSavedCo2Today()

    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greeting = when {
        hour < 12 -> "Good morning ☀️"
        hour < 17 -> "Good afternoon 🌤️"
        else      -> "Good evening 🌙"
    }

    DashboardContent(
        user = user,
        userGreeting = greeting,
        streak = vm.streak,
        score = vm.getSustainabilityScore(),
        weekly = vm.weeklyEmissions,
        savedCo2 = savedCo2,
        trees = vm.getTreesEquivalent(savedCo2),
        carKm = vm.getCarKmEquivalent(savedCo2),
        tips = carbonEntries.lastOrNull()?.let { vm.getRecommendations(it) }
            ?: listOf("Log your first carbon entry to get personalised tips!"),
        points = points,
        level = vm.getLevel(points),
        pointsToNext = vm.getPointsToNextLevel(points),
        levelProgress = vm.getLevelProgress(points),
        nextLevelLabel = vm.getNextLevelLabel(points),
        navController = navController
    )
}

@Composable
fun DashboardContent(
    user: UserModel?,
    userGreeting: String,
    streak: Int,
    score: Int,
    weekly: List<Double>,
    savedCo2: Double,
    trees: Double,
    carKm: Double,
    tips: List<String>,
    points: Int,
    level: String,
    pointsToNext: Int,
    levelProgress: Float,
    nextLevelLabel: String,
    navController: NavController
) {
    Scaffold(
        bottomBar = { EverGreenBottomBar(navController, Routes.DASHBOARD) },
        containerColor = EverGreenSurface
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {

            // ── Header ────────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(EverGreenPrimary)
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier          = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {

                        Text(
                            text     = userGreeting,
                            fontSize = 12.sp,
                            color    = EverGreenLight
                        )
                        Text(
                            text       = "${user?.username ?: "there"} 🌿",
                            fontSize   = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color      = Color.White
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0x33FFFFFF)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("🔥", fontSize = 14.sp)
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = "$streak",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(Modifier.width(12.dp))
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(EverGreenAccent)
                                .clickable { navController.goToProfile() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text       = user?.username?.take(1)?.uppercase() ?: "U",
                                fontSize   = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color      = Color.White
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                // ── Sustainability score card ──────────────────────────────────
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(16.dp),
                    colors   = CardDefaults.cardColors(containerColor = Color.White),
                    border   = CardDefaults.outlinedCardBorder()
                ) {
                    Column(
                        modifier            = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text     = "Sustainability score",
                            fontSize = 12.sp,
                            color    = CarbonGrayLight
                        )
                        Spacer(Modifier.height(8.dp))
                        ScoreRing(score = score)
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text     = "Better than ${score - 7}% of users",
                            fontSize = 11.sp,
                            color    = CarbonGrayLight
                        )
                        Spacer(Modifier.height(6.dp))
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = EverGreenPale
                        ) {
                            Text(
                                text     = level,
                                fontSize = 12.sp,
                                color    = EverGreenPrimary,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // ── Weekly chart card ─────────────────────────────────────────
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
                                text       = "Weekly emissions",
                                fontSize   = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color      = CarbonGray
                            )
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = EverGreenPale
                            ) {
                                Text(
                                    text     = "-12% this week",
                                    fontSize = 10.sp,
                                    color    = EverGreenPrimary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        WeeklyBarChart(emissions = weekly)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text     = "kg CO₂ per day",
                            fontSize = 10.sp,
                            color    = CarbonGrayLight
                        )
                    }
                }

                // ── Quick stats row ───────────────────────────────────────────
                Text(
                    text = "Eco-Impact Today",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = CarbonGray,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickStatCard(
                        modifier = Modifier.weight(1f),
                        label    = "Trees Equivalent",
                        value    = "%.2f".format(trees),
                        sub      = "Trees saved 🌳"
                    )
                    QuickStatCard(
                        modifier = Modifier.weight(1f),
                        label    = "Driving Equivalent",
                        value    = "%.1f".format(carKm),
                        sub      = "km not driven 🚗"
                    )
                }

                // ── Milestones ───────────────────────────────────────────────
                Text(
                    text = "Recent Milestones",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = CarbonGray,
                    modifier = Modifier.padding(top = 8.dp)
                )
                MilestoneRow(
                    badges = listOf(
                        Badge("Starter", Icons.Default.Eco, true),
                        Badge("Walker", Icons.Default.DirectionsWalk, true),
                        Badge("Saver", Icons.Default.Savings, false),
                        Badge("Global", Icons.Default.Public, false)
                    )
                )

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
                                    text     = "$pointsToNext pts to go",
                                    fontSize = 10.sp,
                                    color    = LeafGold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress    = { levelProgress },
                            modifier    = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color       = EverGreenPrimary,
                            trackColor  = EverGreenPale,
                            strokeCap   = StrokeCap.Round
                        )
                        Spacer(Modifier.height(6.dp))
                        Row(
                            modifier              = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text     = level,
                                fontSize = 10.sp,
                                color    = CarbonGrayLight
                            )
                            Text(
                                text     = nextLevelLabel,
                                fontSize = 10.sp,
                                color    = CarbonGrayLight
                            )
                        }
                    }
                }

                // ── Quick tips card ───────────────────────────────────────────
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
                                text       = "Quick tips",
                                fontSize   = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color      = CarbonGray
                            )
                            Text(
                                text     = "See all",
                                fontSize = 11.sp,
                                color    = EverGreenPrimary,
                                modifier = Modifier.clickable {
                                    navController.goToRecommendations()
                                }
                            )
                        }
                        Spacer(Modifier.height(10.dp))
                        tips.take(3).forEachIndexed { index, tip ->
                            Row(
                                modifier          = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 5.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    modifier = Modifier
                                        .padding(top = 4.dp, end = 10.dp)
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (index == 0) EverGreenAccent else EverGreenLight
                                        )
                                )
                                Text(
                                    text     = tip,
                                    fontSize = 12.sp,
                                    color    = CarbonGray,
                                    lineHeight = 18.sp
                                )
                            }
                            if (index < tips.take(3).lastIndex) {
                                HorizontalDivider(
                                    thickness = 0.5.dp,
                                    color     = EverGreenPale
                                )
                            }
                        }
                    }
                }

                // ── Quick action buttons ──────────────────────────────────────
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick  = { navController.goToAddCarbon() },
                        modifier = Modifier.weight(1f),
                        shape    = RoundedCornerShape(12.dp),
                        colors   = ButtonDefaults.buttonColors(
                            containerColor = EverGreenPrimary,
                            contentColor   = Color.White
                        )
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Log carbon", fontSize = 12.sp)
                    }
                    OutlinedButton(
                        onClick  = { navController.goToHabits() },
                        modifier = Modifier.weight(1f),
                        shape    = RoundedCornerShape(12.dp),
                        colors   = ButtonDefaults.outlinedButtonColors(
                            contentColor = EverGreenPrimary
                        )
                    ) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Habits", fontSize = 12.sp)
                    }
                }

                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

// ── Score ring composable ─────────────────────────────────────────────────────
@Composable
private fun ScoreRing(score: Int) {
    Box(
        modifier         = Modifier.size(90.dp),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.foundation.Canvas(modifier = Modifier.size(90.dp)) {
            val stroke = Stroke(width = 10f, cap = StrokeCap.Round)
            val sweep  = (score / 100f) * 300f
            drawArc(
                color      = EverGreenPale,
                startAngle = 120f,
                sweepAngle = 300f,
                useCenter  = false,
                style      = stroke
            )
            drawArc(
                color      = EverGreenPrimary,
                startAngle = 120f,
                sweepAngle = sweep,
                useCenter  = false,
                style      = stroke
            )
        }
        Text(
            text       = "$score",
            fontSize   = 24.sp,
            fontWeight = FontWeight.SemiBold,
            color      = EverGreenPrimary
        )
    }
}

// ── Weekly bar chart composable ───────────────────────────────────────────────
@Composable
private fun WeeklyBarChart(emissions: List<Double>) {
    val days    = listOf("M", "T", "W", "T", "F", "S", "S")
    val maxVal  = emissions.maxOrNull()?.takeIf { it > 0 } ?: 1.0
    val display = if (emissions.size < 7)
        List(7 - emissions.size) { 0.0 } + emissions
    else emissions.takeLast(7)

    Row(
        modifier          = Modifier
            .fillMaxWidth()
            .height(70.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        display.forEachIndexed { index, value ->
            Column(
                modifier            = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                val heightFraction = (value / maxVal).toFloat().coerceIn(0.05f, 1f)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(heightFraction)
                        .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                        .background(
                            if (index == display.lastIndex) EverGreenPrimary
                            else EverGreenAccent
                        )
                )
            }
        }
    }
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        days.forEach { day ->
            Text(
                text      = day,
                fontSize  = 10.sp,
                color     = CarbonGrayLight,
                modifier  = Modifier.weight(1f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

// ── Quick stat card composable ────────────────────────────────────────────────
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


// ── Shared bottom navigation bar ──────────────────────────────────────────────
@Composable
fun EverGreenBottomBar(navController: NavController, currentRoute: String) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 0.dp
    ) {
        bottomNavItems.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
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
                    Icon(
                        imageVector        = item.icon,
                        contentDescription = item.label,
                        modifier           = Modifier.size(22.dp)
                    )
                },
                label = {
                    Text(text = item.label, fontSize = 10.sp)
                },
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

// ── Milestones row composable ───────────────────────────────────────────────
data class Badge(val name: String, val icon: ImageVector, val isUnlocked: Boolean)

@Composable
fun MilestoneRow(badges: List<Badge>) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(badges) { badge ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (badge.isUnlocked) EverGreenPale else Color.White
                ),
                border = if (badge.isUnlocked) null else CardDefaults.outlinedCardBorder(),
                modifier = Modifier.width(85.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(if (badge.isUnlocked) EverGreenPrimary else EverGreenPale),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = badge.icon,
                            contentDescription = null,
                            tint = if (badge.isUnlocked) Color.White else CarbonGrayLight,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = badge.name,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (badge.isUnlocked) EverGreenPrimary else CarbonGrayLight
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardPreview() {
    EverGreenTheme {
        val navController = rememberNavController()
        DashboardContent(
            user = UserModel(username = "Eco Hero", totalPoints = 450),
            userGreeting = "Good morning ☀️",
            streak = 5,
            score = 82,
            weekly = listOf(12.0, 15.0, 10.0, 18.0, 9.0, 11.0, 8.5),
            savedCo2 = 4.5,
            trees = 0.23,
            carKm = 18.0,
            tips = listOf(
                "Try public transport twice a week — save up to 30 kg CO₂/month",
                "Switch to LED bulbs and unplug devices when idle",
                "Replace one meat meal per day with plant-based food"
            ),
            points = 450,
            level = "Green Champion 🌳",
            pointsToNext = 50,
            levelProgress = 0.8f,
            nextLevelLabel = "Planet Guardian 🌎",
            navController = navController
        )
    }
}
