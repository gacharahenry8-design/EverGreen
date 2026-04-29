package com.example.evergreen.models

data class CarbonEntry(
    var id: String = "",
    var userId: String = "",
    val transportEmission: Double = 0.0,
    val electricityEmission: Double = 0.0,
    val foodEmission: Double = 0.0,
    val totalEmission: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis()
)
