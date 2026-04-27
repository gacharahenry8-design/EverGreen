package com.example.evergreen.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController

// ─────────────────────────────────────────────────────────────────────────────
// Route Constants
// Single source of truth for every screen route in the app.
// Usage: navController.navigate(Routes.DASHBOARD)
// ─────────────────────────────────────────────────────────────────────────────
object Routes {

    // ── Auth flow ─────────────────────────────────────────────────────────────
    const val SPLASH    = "splash"
    const val LOGIN     = "login"
    const val REGISTER  = "register"

    // ── Main app ──────────────────────────────────────────────────────────────
    const val DASHBOARD       = "dashboard"
    const val ADD_CARBON      = "add_carbon"
    const val HABITS          = "habits"
    const val RECOMMENDATIONS = "recommendations"
    const val PROFILE         = "profile"

    // ── Detail screens (with arguments) ───────────────────────────────────────
    // Usage → navigate:  navController.goToCarbonDetail("entry123")
    //       → define:    composable(Routes.CARBON_DETAIL) { backStackEntry -> ... }
    const val CARBON_DETAIL = "carbon_detail/{entryId}"
    fun carbonDetail(entryId: String) = "carbon_detail/$entryId"

    const val HABIT_DETAIL  = "habit_detail/{habitId}"
    fun habitDetail(habitId: String) = "habit_detail/$habitId"

    const val BADGE_DETAIL  = "badge_detail/{badgeId}"
    fun badgeDetail(badgeId: String) = "badge_detail/$badgeId"
}

// ─────────────────────────────────────────────────────────────────────────────
// Bottom Navigation Items
// Pass `bottomNavItems` to your bottom nav bar composable
// ─────────────────────────────────────────────────────────────────────────────
data class BottomNavItem(
    val label: String,
    val route: String,
    val icon:  ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(
        label = "Home",
        route = Routes.DASHBOARD,
        icon  = Icons.Filled.Home
    ),
    BottomNavItem(
        label = "Log",
        route = Routes.ADD_CARBON,
        icon  = Icons.Filled.Add
    ),
    BottomNavItem(
        label = "Habits",
        route = Routes.HABITS,
        icon  = Icons.Filled.CheckCircle
    ),
    BottomNavItem(
        label = "Tips",
        route = Routes.RECOMMENDATIONS,
        icon  = Icons.Filled.Lightbulb
    ),
    BottomNavItem(
        label = "Profile",
        route = Routes.PROFILE,
        icon  = Icons.Filled.AccountCircle
    )
)

// ─────────────────────────────────────────────────────────────────────────────
// NavController Extension Functions
// Clean helpers so screens never hardcode navigation logic
// ─────────────────────────────────────────────────────────────────────────────

// After login/register — clear back stack so Back doesn't return to auth screens
fun NavController.navigateToDashboard() {
    navigate(Routes.DASHBOARD) {
        popUpTo(Routes.SPLASH) { inclusive = true }
    }
}

// After logout — clear entire back stack so Back doesn't return to the app
fun NavController.navigateToLogin() {
    navigate(Routes.LOGIN) {
        popUpTo(0) { inclusive = true }
    }
}

// Login ↔ Register — replace each other, never stack
fun NavController.navigateToRegister() {
    navigate(Routes.REGISTER) {
        popUpTo(Routes.LOGIN) { inclusive = true }
    }
}

fun NavController.navigateToLoginFromRegister() {
    navigate(Routes.LOGIN) {
        popUpTo(Routes.REGISTER) { inclusive = true }
    }
}

// Standard push navigations (back stack preserved)
fun NavController.goToAddCarbon()        = navigate(Routes.ADD_CARBON)
fun NavController.goToHabits()           = navigate(Routes.HABITS)
fun NavController.goToRecommendations()  = navigate(Routes.RECOMMENDATIONS)
fun NavController.goToProfile()          = navigate(Routes.PROFILE)

// Detail screen navigations
fun NavController.goToCarbonDetail(entryId: String) =
    navigate(Routes.carbonDetail(entryId))

fun NavController.goToHabitDetail(habitId: String) =
    navigate(Routes.habitDetail(habitId))

fun NavController.goToBadgeDetail(badgeId: String) =
    navigate(Routes.badgeDetail(badgeId))