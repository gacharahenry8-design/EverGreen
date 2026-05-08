package com.example.evergreen.ui.theme.screens.dashboard

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.evergreen.data.EverGreenViewModel
import com.example.evergreen.navigation.Routes
import com.example.evergreen.ui.theme.*

// ─── Tip data models ──────────────────────────────────────────────────────────
// TipCategory.ALL is used for trend-based tips that span all categories
enum class TipCategory { ALL, TRANSPORT, FOOD, ENERGY }

data class EcoTip(
    val id:          Int,
    val title:       String,
    val description: String,
    val category:    TipCategory,
    val impact:      String,
    val iconBg:      Color,
    val iconTint:    Color,
    val icon:        ImageVector
)

// ─── Recommendations Screen ───────────────────────────────────────────────────
@Composable
fun RecommendationsScreen(
    navController: NavController,
    vm: EverGreenViewModel = viewModel()
) {
    val entries by vm.carbonEntries.collectAsState()

    LaunchedEffect(Unit) {
        vm.loadUser()
        vm.loadCarbonEntries()
    }

    val score = vm.getSustainabilityScore()

    // Generate tips locally based on carbon entries data from the ViewModel
    val allTips = remember(entries) {
        val tips = mutableListOf<EcoTip>()
        val latest = entries.lastOrNull()
        
        if (latest != null) {
            if (latest.transportEmission > 10.0) {
                tips.add(EcoTip(
                    id = 1,
                    title = "Use Public Transport",
                    description = "Your transport emissions are high. Try taking a bus or train today.",
                    category = TipCategory.TRANSPORT,
                    impact = "High Impact",
                    iconBg = EverGreenPale,
                    iconTint = EverGreenPrimary,
                    icon = Icons.Default.DirectionsBus
                ))
            }
            if (latest.foodEmission > 5.0) {
                tips.add(EcoTip(
                    id = 2,
                    title = "Try a Vegan Meal",
                    description = "Plant-based diets have a much lower carbon footprint. Give it a try!",
                    category = TipCategory.FOOD,
                    impact = "Medium Impact",
                    iconBg = LeafGoldLight,
                    iconTint = LeafGold,
                    icon = Icons.Default.Restaurant
                ))
            }
            if (latest.electricityEmission > 5.0) {
                tips.add(EcoTip(
                    id = 3,
                    title = "Energy Saving Mode",
                    description = "Switch off unnecessary lights and unplug idle electronics.",
                    category = TipCategory.ENERGY,
                    impact = "Daily Habit",
                    iconBg = Color(0xFFE3F2FD),
                    iconTint = Color(0xFF1565C0),
                    icon = Icons.Default.Bolt
                ))
            }
        }
        
        if (tips.isEmpty()) {
            tips.add(EcoTip(
                id = 0,
                title = "Keep it Green!",
                description = "Your footprint is looking great. Check back after your next entry.",
                category = TipCategory.ALL,
                impact = "Get started",
                iconBg = EverGreenPale,
                iconTint = EverGreenPrimary,
                icon = Icons.Default.Eco
            ))
        }
        tips
    }

    var selectedFilter by remember { mutableStateOf(TipCategory.ALL) }
    val doneTips       = remember { mutableStateMapOf<Int, Boolean>() }

    val filteredTips = if (selectedFilter == TipCategory.ALL) allTips
    else allTips.filter {
        it.category == selectedFilter || it.category == TipCategory.ALL
    }

    Scaffold(
        bottomBar      = { EverGreenBottomBar(navController, Routes.RECOMMENDATIONS) },
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
                Column {
                    Text(
                        text       = "Recommendations",
                        fontSize   = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = Color.White
                    )
                    Text(
                        text     = "Based on your actual carbon logs",
                        fontSize = 12.sp,
                        color    = EverGreenLight,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            Column(
                modifier            = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                // ── Score banner ──────────────────────────────────────────────
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(14.dp),
                    colors   = CardDefaults.cardColors(containerColor = EverGreenPrimary)
                ) {
                    Row(
                        modifier          = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text       = "$score",
                                fontSize   = 28.sp,
                                fontWeight = FontWeight.SemiBold,
                                color      = Color.White
                            )
                            Text(text = "your score", fontSize = 10.sp, color = EverGreenLight)
                        }
                        Spacer(Modifier.width(16.dp))
                        Box(
                            modifier = Modifier
                                .width(0.5.dp)
                                .height(44.dp)
                                .background(EverGreenMid)
                        )
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text(
                                text       = "Better than ${(score - 7).coerceAtLeast(0)}% of users",
                                fontSize   = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color      = Color.White
                            )
                            Text(
                                text     = if (entries.isEmpty())
                                    "Log carbon data to get personalised tips"
                                else
                                    "${filteredTips.size} personalised tips for you",
                                fontSize = 11.sp,
                                color    = EverGreenLight,
                                modifier = Modifier.padding(top = 3.dp)
                            )
                        }
                    }
                }

                // ── Empty state ───────────────────────────────────────────────
                if (entries.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape    = RoundedCornerShape(14.dp),
                        colors   = CardDefaults.cardColors(containerColor = SurfaceCard),
                        border   = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(
                            modifier            = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("📊", fontSize = 36.sp)
                            Spacer(Modifier.height(10.dp))
                            Text(
                                text       = "No carbon data yet",
                                fontSize   = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color      = CarbonGray
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text      = "Log your transport, food and electricity usage. EverGreen will then generate tips tailored to your actual footprint — not generic advice.",
                                fontSize  = 12.sp,
                                color     = CarbonGrayLight,
                                lineHeight = 18.sp
                            )
                            Spacer(Modifier.height(14.dp))
                            Button(
                                onClick = { navController.navigate(Routes.ADD_CARBON) },
                                shape   = RoundedCornerShape(10.dp),
                                colors  = ButtonDefaults.buttonColors(
                                    containerColor = EverGreenPrimary,
                                    contentColor   = Color.White
                                )
                            ) {
                                Text("Log carbon now", fontSize = 13.sp)
                            }
                        }
                    }
                } else {

                    // ── Data summary card ─────────────────────────────────────
                    val avgT = if (entries.isNotEmpty()) entries.map { it.transportEmission }.average() else 0.0
                    val avgF = if (entries.isNotEmpty()) entries.map { it.foodEmission }.average() else 0.0
                    val avgE = if (entries.isNotEmpty()) entries.map { it.electricityEmission }.average() else 0.0

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape    = RoundedCornerShape(14.dp),
                        colors   = CardDefaults.cardColors(containerColor = SurfaceCard),
                        border   = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text       = "Your average daily footprint",
                                fontSize   = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color      = CarbonGray,
                                modifier   = Modifier.padding(bottom = 10.dp)
                            )
                            Row(
                                modifier              = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FootprintTile(
                                    modifier = Modifier.weight(1f),
                                    label    = "Transport",
                                    value    = "${"%.1f".format(avgT)} kg",
                                    color    = EverGreenPrimary,
                                    bg       = EverGreenPale
                                )
                                FootprintTile(
                                    modifier = Modifier.weight(1f),
                                    label    = "Food",
                                    value    = "${"%.1f".format(avgF)} kg",
                                    color    = LeafGold,
                                    bg       = LeafGoldLight
                                )
                                FootprintTile(
                                    modifier = Modifier.weight(1f),
                                    label    = "Energy",
                                    value    = "${"%.1f".format(avgE)} kg",
                                    color    = Color(0xFF1565C0),
                                    bg       = Color(0xFFE3F2FD)
                                )
                            }
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text     = "Based on ${entries.size} log${if (entries.size > 1) "s" else ""}",
                                fontSize = 10.sp,
                                color    = CarbonGrayLight
                            )
                        }
                    }

                    // ── Filter chips ──────────────────────────────────────────
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TipCategory.entries.forEach { category ->
                            val isSelected = selectedFilter == category
                            FilterChip(
                                selected = isSelected,
                                onClick  = { selectedFilter = category },
                                label    = {
                                    Text(
                                        text     = category.name.lowercase()
                                            .replaceFirstChar { it.uppercase() },
                                        fontSize = 11.sp
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = EverGreenPrimary,
                                    selectedLabelColor     = Color.White,
                                    containerColor         = SurfaceCard,
                                    labelColor             = CarbonGrayLight
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    borderColor         = EverGreenLight,
                                    selectedBorderColor = EverGreenPrimary,
                                    borderWidth         = 0.5.dp,
                                    selectedBorderWidth = 0.5.dp,
                                    enabled             = true,
                                    selected            = isSelected
                                )
                            )
                        }
                    }

                    // ── Dynamic tip cards ─────────────────────────────────────
                    filteredTips.forEach { tip ->
                        val isDone  = doneTips[tip.id] == true
                        val bgColor by animateColorAsState(
                            targetValue   = if (isDone) EverGreenPale else SurfaceCard,
                            animationSpec = tween(300),
                            label         = "tipBg"
                        )

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .alpha(if (isDone) 0.6f else 1f),
                            shape    = RoundedCornerShape(14.dp),
                            colors   = CardDefaults.cardColors(containerColor = bgColor),
                            border   = CardDefaults.outlinedCardBorder()
                        ) {
                            Row(
                                modifier          = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                // Icon
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(tip.iconBg),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector        = tip.icon,
                                        contentDescription = tip.category.name,
                                        tint               = tip.iconTint,
                                        modifier           = Modifier.size(18.dp)
                                    )
                                }

                                Spacer(Modifier.width(10.dp))

                                // Text + pills
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text       = tip.title,
                                        fontSize   = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color      = CarbonGray
                                    )
                                    Spacer(Modifier.height(3.dp))
                                    Text(
                                        text       = tip.description,
                                        fontSize   = 11.sp,
                                        color      = CarbonGrayLight,
                                        lineHeight = 16.sp
                                    )
                                    Spacer(Modifier.height(6.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                                        if (tip.category != TipCategory.ALL) {
                                            Surface(
                                                shape = RoundedCornerShape(20.dp),
                                                color = tip.iconBg
                                            ) {
                                                Text(
                                                    text     = tip.category.name.lowercase()
                                                        .replaceFirstChar { it.uppercase() },
                                                    fontSize = 10.sp,
                                                    color    = tip.iconTint,
                                                    modifier = Modifier.padding(
                                                        horizontal = 7.dp, vertical = 2.dp
                                                    )
                                                )
                                            }
                                        }
                                        Surface(
                                            shape = RoundedCornerShape(20.dp),
                                            color = when {
                                                tip.impact.startsWith("High")  -> Color(0xFFFFEBEE)
                                                tip.impact == "Get started"    -> EverGreenPale
                                                else                           -> EverGreenPale
                                            }
                                        ) {
                                            Text(
                                                text     = tip.impact,
                                                fontSize = 10.sp,
                                                color    = when {
                                                    tip.impact.startsWith("High")  -> Color(0xFFC62828)
                                                    tip.impact == "Get started"    -> EverGreenPrimary
                                                    else                           -> EverGreenMid
                                                },
                                                modifier = Modifier.padding(
                                                    horizontal = 7.dp, vertical = 2.dp
                                                )
                                            )
                                        }
                                    }
                                }

                                Spacer(Modifier.width(8.dp))

                                // Done checkbox
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isDone) EverGreenPrimary
                                            else EverGreenLight.copy(alpha = 0.4f)
                                        )
                                        .clickable { doneTips[tip.id] = !isDone },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isDone) {
                                        Icon(
                                            imageVector        = Icons.Filled.Check,
                                            contentDescription = "Done",
                                            tint               = Color.White,
                                            modifier           = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // ── Completion banner ─────────────────────────────────────
                    val doneCount = doneTips.values.count { it }
                    if (doneCount > 0) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape    = RoundedCornerShape(14.dp),
                            colors   = CardDefaults.cardColors(containerColor = EverGreenPale),
                            border   = CardDefaults.outlinedCardBorder()
                        ) {
                            Row(
                                modifier          = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("🌿", fontSize = 20.sp)
                                Spacer(Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text       = "Great work!",
                                        fontSize   = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color      = EverGreenDark
                                    )
                                    Text(
                                        text     = "You've completed $doneCount tip${if (doneCount > 1) "s" else ""} today. Keep it up!",
                                        fontSize = 11.sp,
                                        color    = EverGreenPrimary
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

// ── Footprint tile composable ─────────────────────────────────────────────────
@Composable
private fun FootprintTile(
    modifier: Modifier,
    label:    String,
    value:    String,
    color:    Color,
    bg:       Color
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(bg)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = label, fontSize = 10.sp, color = color)
            Spacer(Modifier.height(3.dp))
            Text(
                text       = value,
                fontSize   = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color      = color
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FootprintTilePreview() {
    Row(
        modifier = Modifier.padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FootprintTile(
            modifier = Modifier.weight(1f),
            label = "Transport",
            value = "12.5 kg",
            color = EverGreenPrimary,
            bg = EverGreenPale
        )
        FootprintTile(
            modifier = Modifier.weight(1f),
            label = "Food",
            value = "8.2 kg",
            color = LeafGold,
            bg = LeafGoldLight
        )
    }
}

@Preview(showBackground = true, name = "Recommendations Screen")
@Composable
fun RecommendationsPreview() {
    val mockNavController = rememberNavController()
    RecommendationsScreen(navController = mockNavController)
}
