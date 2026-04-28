package com.example.evergreen.navigation

import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder

/**
 * Extension functions for NavController to handle common navigation patterns in EverGreen.
 */

fun NavController.navigateToDashboard() {
    this.navigate(Routes.DASHBOARD) {
        // Pop up to the start destination of the graph to
        // avoid building up a large stack of destinations
        // on the back stack as users select items
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
