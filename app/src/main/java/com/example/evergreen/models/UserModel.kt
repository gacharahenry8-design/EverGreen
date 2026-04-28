package com.example.evergreen.models

data class UserModel(
    val id: String = "",
    val username: String = "",
    val email: String = "",
    val totalPoints: Int = 0,
    val level: String = ""
)
