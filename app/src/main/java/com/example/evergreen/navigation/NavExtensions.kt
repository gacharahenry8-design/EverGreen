package com.example.evergreen.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder

/**
 * Extension functions for NavController to handle common navigation patterns in EverGreen.
 */

fun NavController.navigateToDashboard() {
    this.navigate(Routes.DASHBOARD) {
        popUpTo(this@navigateToDashboard.graph.startDestinationId) {
            inclusive = true
        }
        launchSingleTop = true
    }
}

fun NavController.navigateToLoginFromRegister() {
    this.navigate(Routes.LOGIN) {
        popUpTo(Routes.REGISTER) { inclusive = true }
    }
}

fun NavController.goToProfile() {
    this.navigate(Routes.PROFILE)
}

fun NavController.goToRecommendations() {
    this.navigate(Routes.RECOMMENDATIONS)
}

fun NavController.goToAddCarbon() {
    this.navigate(Routes.ADD_CARBON)
}

fun NavController.goToHabits() {
    this.navigate(Routes.HABITS)
}

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

val bottomNavItems = listOf(
    BottomNavItem("Dashboard", Icons.Default.Dashboard, Routes.DASHBOARD),
    BottomNavItem("Habits", Icons.Default.Eco, Routes.HABITS),
    BottomNavItem("History", Icons.Default.History, Routes.CARBON_DETAIL.replace("/{entryId}", "")), // Fallback or specific route
    BottomNavItem("Profile", Icons.Default.Person, Routes.PROFILE)
)
