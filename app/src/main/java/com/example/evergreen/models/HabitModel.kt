package com.example.evergreen.models

data class HabitModel(
    var id: String = "",
    var userId: String = "",
    val name: String = "",
    val points: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)
