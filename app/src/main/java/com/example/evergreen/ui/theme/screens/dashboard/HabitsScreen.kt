package com.example.evergreen.ui.theme.screens.dashboard

// UI & Layout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Navigation
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

import com.example.evergreen.data.EverGreenViewModel
import com.example.evergreen.navigation.Routes
import com.example.evergreen.ui.theme.*

// ─────────────────────────────────────────────────────────────
// HABIT MODEL
// ─────────────────────────────────────────────────────────────

data class Habit(
    val id: Int,
    val name: String,
    val icon: String,
    val category: String,
    val isCompleted: Boolean = false,
    val streak: Int = 0
)

// ─────────────────────────────────────────────────────────────
// MAIN SCREEN
// ─────────────────────────────────────────────────────────────

@Composable
fun HabitsScreen(
    navController: NavController,
    vm: EverGreenViewModel = viewModel()
) {

    val carbonEntries by vm.carbonEntries.collectAsState()

    LaunchedEffect(Unit) {
        vm.loadCarbonEntries()
    }

    // Generate habits dynamically from carbon data
    val habits = remember(carbonEntries) {
        generateHabitsFromCarbon(vm)
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(EverGreenPrimary)
                    .padding(
                        top = 40.dp,
                        start = 20.dp,
                        end = 20.dp,
                        bottom = 20.dp
                    )
            ) {

                Text(
                    text = "My Green Habits",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "Personalized from your carbon activity 🌍",
                    fontSize = 14.sp,
                    color = EverGreenLight
                )
            }
        },

        bottomBar = {
            EverGreenBottomBar(navController, Routes.HABITS)
        },

        containerColor = EverGreenSurface

    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),

            contentPadding = PaddingValues(16.dp),

            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // SUMMARY CARD
            item {
                HabitSummaryCard(habits)
            }

            // TITLE
            item {
                Text(
                    text = "Recommended Habits",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = CarbonGray
                )
            }

            // HABITS
            items(habits) { habit ->

                HabitItemRow(
                    habit = habit,
                    onToggle = {
                        // Future implementation
                    }
                )
            }

            item {
                Spacer(Modifier.height(80.dp))
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// GENERATE HABITS FROM USER CARBON DATA
// ─────────────────────────────────────────────────────────────

fun generateHabitsFromCarbon(
    vm: EverGreenViewModel
): List<Habit> {

    val latest = vm.carbonEntries.value.lastOrNull()

    // No data yet
    if (latest == null) {

        return listOf(
            Habit(
                id = 1,
                name = "Log your first carbon activity",
                icon = "🌱",
                category = "General"
            )
        )
    }

    val habits = mutableListOf<Habit>()

    // ── TRANSPORT ─────────────────────

    if (latest.transportEmission > 20) {

        habits.add(
            Habit(
                id = 1,
                name = "Use public transport today",
                icon = "🚌",
                category = "Transport"
            )
        )

        habits.add(
            Habit(
                id = 2,
                name = "Walk short distances",
                icon = "🚶",
                category = "Transport"
            )
        )
    }

    // ── ELECTRICITY ──────────────────

    if (latest.electricityEmission > 15) {

        habits.add(
            Habit(
                id = 3,
                name = "Switch off unused lights",
                icon = "💡",
                category = "Electricity"
            )
        )

        habits.add(
            Habit(
                id = 4,
                name = "Reduce appliance usage",
                icon = "🔌",
                category = "Electricity"
            )
        )
    }

    // ── FOOD ─────────────────────────

    if (latest.foodEmission > 10) {

        habits.add(
            Habit(
                id = 5,
                name = "Eat a plant-based meal",
                icon = "🥗",
                category = "Food"
            )
        )
    }

    // ── GENERAL ──────────────────────

    habits.add(
        Habit(
            id = 6,
            name = "Carry a reusable bottle",
            icon = "💧",
            category = "Lifestyle"
        )
    )

    habits.add(
        Habit(
            id = 7,
            name = "Avoid single-use plastics",
            icon = "♻️",
            category = "Lifestyle"
        )
    )

    return habits
}

// ─────────────────────────────────────────────────────────────
// HABIT ITEM
// ─────────────────────────────────────────────────────────────

@Composable
fun HabitItemRow(
    habit: Habit,
    onToggle: () -> Unit
) {

    Card(
        shape = RoundedCornerShape(16.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),

        modifier = Modifier.fillMaxWidth()
    ) {

        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),

            verticalAlignment = Alignment.CenterVertically,

            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = habit.icon,
                    fontSize = 24.sp
                )

                Spacer(Modifier.width(12.dp))

                Column {

                    Text(
                        text = habit.name,
                        fontWeight = FontWeight.SemiBold,
                        color = CarbonGray
                    )

                    Spacer(Modifier.height(2.dp))

                    Text(
                        text = habit.category,
                        fontSize = 12.sp,
                        color = CarbonGrayLight
                    )
                }
            }

            IconButton(
                onClick = onToggle,

                colors = IconButtonDefaults.iconButtonColors(
                    containerColor =
                        if (habit.isCompleted)
                            EverGreenPrimary
                        else
                            EverGreenPale
                )
            ) {

                Icon(
                    imageVector =
                        if (habit.isCompleted)
                            Icons.Default.Check
                        else
                            Icons.Default.Add,

                    contentDescription = null,

                    tint =
                        if (habit.isCompleted)
                            Color.White
                        else
                            EverGreenPrimary
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// SUMMARY CARD
// ─────────────────────────────────────────────────────────────

@Composable
fun HabitSummaryCard(
    habits: List<Habit>
) {

    val completedCount = habits.count { it.isCompleted }

    val progress =
        if (habits.isNotEmpty())
            completedCount.toFloat() / habits.size
        else
            0f

    Card(
        colors = CardDefaults.cardColors(
            containerColor = EverGreenAccent
        ),

        shape = RoundedCornerShape(16.dp)
    ) {

        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),

            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "Habit Progress",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "$completedCount of ${habits.size} completed",
                    color = Color.White.copy(alpha = 0.8f)
                )
            }

            Box(
                contentAlignment = Alignment.Center
            ) {

                CircularProgressIndicator(
                    progress = { progress },

                    color = Color.White,

                    trackColor = Color.White.copy(alpha = 0.2f),

                    strokeWidth = 6.dp,

                    modifier = Modifier.size(50.dp)
                )

                Text(
                    text = "${(progress * 100).toInt()}%",
                    fontSize = 10.sp,
                    color = Color.White
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// PREVIEW
// ─────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
fun HabitsScreenPreview() {

    EverGreenTheme {

        HabitsScreen(
            navController = rememberNavController()
        )
    }
}