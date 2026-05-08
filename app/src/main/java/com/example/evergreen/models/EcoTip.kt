package com.example.evergreen.models

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class TipCategory { ALL, TRANSPORT, FOOD, ENERGY }

data class EcoTip(
    val id:          Int,
    val title:       String,
    val description: String,
    val category:    TipCategory,
    val impact:      String,
    val iconBg:      Color,
    val iconTint:    Color,
    val icon:        ImageVector
)
