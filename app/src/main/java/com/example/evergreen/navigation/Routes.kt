package com.example.evergreen.navigation

/**
 * Defines all navigation paths for the EverGreen app.
 */
object Routes {
    // Auth Flow
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"

    // Main App Flow
    const val DASHBOARD = "dashboard"
    const val ADD_CARBON = "add_carbon"
    const val HABITS = "habits"
    const val RECOMMENDATIONS = "recommendations"
    const val PROFILE = "profile"

    // Detail Screens (Using curly braces for dynamic arguments)
    const val CARBON_DETAIL = "carbon_detail/{entryId}"
    const val HABIT_DETAIL = "habit_detail/{habitId}"
    const val BADGE_DETAIL = "badge_detail/{badgeId}"

    // Helper functions to create routes with arguments
    fun passCarbonId(id: String): String = "carbon_detail/$id"
    fun passHabitId(id: String): String = "habit_detail/$id"
    fun passBadgeId(id: String): String = "badge_detail/$id"
}