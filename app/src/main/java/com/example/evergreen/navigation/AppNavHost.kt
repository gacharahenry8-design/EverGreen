package com.example.evergreen.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.evergreen.ui.screens.auth.RegisterScreen
import com.example.evergreen.ui.theme.screens.splash.SplashScreen
import com.example.evergreen.ui.screens.auth.RegisterScreen
@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Routes.SPLASH
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        // --- Splash Screen ---
        composable(Routes.SPLASH) {
            SplashScreen(navController = navController)
        }

        // --- Auth Screens ---
        composable(Routes.LOGIN) {
            // Swap with LoginScreen(navController) once created
            PlaceholderScreen("Login")
        }

        composable(Routes.REGISTER) {
            RegisterScreen(navController = navController)
        }

        // --- Main App Screens ---
        composable(Routes.DASHBOARD) {
            PlaceholderScreen("Dashboard")
        }

        composable(Routes.ADD_CARBON) {
            PlaceholderScreen("Add Carbon Entry")
        }

        composable(Routes.HABITS) {
            PlaceholderScreen("Habit Tracker")
        }

        composable(Routes.RECOMMENDATIONS) {
            PlaceholderScreen("Recommendations")
        }

        composable(Routes.PROFILE) {
            PlaceholderScreen("User Profile")
        }

        // --- Detail Screens (Passing Arguments) ---
        composable(
            route = Routes.CARBON_DETAIL,
            arguments = listOf(navArgument("entryId") { type = NavType.StringType })
        ) { backStackEntry ->
            val entryId = backStackEntry.arguments?.getString("entryId") ?: ""
            PlaceholderScreen("Carbon Detail: $entryId")
        }

        composable(
            route = Routes.HABIT_DETAIL,
            arguments = listOf(navArgument("habitId") { type = NavType.StringType })
        ) { backStackEntry ->
            val habitId = backStackEntry.arguments?.getString("habitId") ?: ""
            PlaceholderScreen("Habit Detail: $habitId")
        }

        composable(
            route = Routes.BADGE_DETAIL,
            arguments = listOf(navArgument("badgeId") { type = NavType.StringType })
        ) { backStackEntry ->
            val badgeId = backStackEntry.arguments?.getString("badgeId") ?: ""
            PlaceholderScreen("Badge Detail: $badgeId")
        }
    }
}

/**
 * A temporary placeholder to use while screens are being developed.
 */
@Composable
private fun PlaceholderScreen(name: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$name Screen coming soon",
            fontSize = 18.sp,
            color = Color(0xFF2E7D32) // A forest green color
        )
    }
}