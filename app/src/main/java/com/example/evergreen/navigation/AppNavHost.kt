package com.example.evergreen.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.evergreen.ui.theme.screens.splash.SplashScreen

// ─── App Nav Host ─────────────────────────────────────────────────────────────
@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController    = navController,
        startDestination = Routes.SPLASH
    ) {

        // ── Splash ────────────────────────────────────────────────────────────
        composable(Routes.SPLASH) {
            SplashScreen(navController = navController)
        }

        // ── Auth ──────────────────────────────────────────────────────────────
        composable(Routes.LOGIN) {
            // LoginScreen(navController = navController)
            PlaceholderScreen("Login")
        }

        composable(Routes.REGISTER) {
            com.example.evergreen.ui.theme.screens.signup.SignUpScreen(navController = navController)
        }

        // ── Main app ──────────────────────────────────────────────────────────
        composable(Routes.DASHBOARD) {
            // DashboardScreen(navController = navController)
            PlaceholderScreen("Dashboard")
        }

        composable(Routes.ADD_CARBON) {
            // AddCarbonScreen(navController = navController)
            PlaceholderScreen("Add Carbon")
        }

        composable(Routes.HABITS) {
            // HabitTrackerScreen(navController = navController)
            PlaceholderScreen("Habit Tracker")
        }

        composable(Routes.RECOMMENDATIONS) {
            // RecommendationsScreen(navController = navController)
            PlaceholderScreen("Recommendations")
        }

        composable(Routes.PROFILE) {
            // ProfileScreen(navController = navController)
            PlaceholderScreen("Profile")
        }

        // ── Detail screens (with arguments) ───────────────────────────────────
        composable(
            route     = Routes.CARBON_DETAIL,
            arguments = listOf(navArgument("entryId") { type = NavType.StringType })
        ) { backStackEntry ->
            val entryId = backStackEntry.arguments?.getString("entryId") ?: ""
            // CarbonDetailScreen(entryId = entryId, navController = navController)
            PlaceholderScreen("Carbon Detail: $entryId")
        }

        composable(
            route     = Routes.HABIT_DETAIL,
            arguments = listOf(navArgument("habitId") { type = NavType.StringType })
        ) { backStackEntry ->
            val habitId = backStackEntry.arguments?.getString("habitId") ?: ""
            // HabitDetailScreen(habitId = habitId, navController = navController)
            PlaceholderScreen("Habit Detail: $habitId")
        }

        composable(
            route     = Routes.BADGE_DETAIL,
            arguments = listOf(navArgument("badgeId") { type = NavType.StringType })
        ) { backStackEntry ->
            val badgeId = backStackEntry.arguments?.getString("badgeId") ?: ""
            // BadgeDetailScreen(badgeId = badgeId, navController = navController)
            PlaceholderScreen("Badge Detail: $badgeId")
        }
    }
}

// ─── Placeholder (delete each one as you build the real screen) ───────────────
@Composable
private fun PlaceholderScreen(name: String) {
    androidx.compose.foundation.layout.Box(
        modifier         = androidx.compose.ui.Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        androidx.compose.material3.Text(
            text     = "$name — coming soon",
            fontSize = androidx.compose.ui.unit.TextUnit(
                16f, androidx.compose.ui.unit.TextUnitType.Sp
            ),
            color    = androidx.compose.ui.graphics.Color(0xFF2E7D32)
        )
    }
}