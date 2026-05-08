package com.example.evergreen.ui.theme.screens.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.evergreen.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsScreen(navController: NavController) {
    var dailyReminder by remember { mutableStateOf(true) }
    var weeklyReport by remember { mutableStateOf(false) }
    var communityAlerts by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Notifications", fontSize = 18.sp, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = EverGreenSurface
                )
            )
        },
        containerColor = EverGreenSurface
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                "Preferences",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = CarbonGrayLight,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column {
                    NotificationToggleItem(
                        title = "Daily Logging Reminder",
                        description = "Get notified if you forget to log your footprint.",
                        isEnabled = dailyReminder,
                        onToggle = { dailyReminder = it }
                    )
                    HorizontalDivider(thickness = 0.5.dp, color = EverGreenPale)
                    NotificationToggleItem(
                        title = "Weekly Eco Report",
                        description = "A summary of your carbon reduction progress.",
                        isEnabled = weeklyReport,
                        onToggle = { weeklyReport = it }
                    )
                    HorizontalDivider(thickness = 0.5.dp, color = EverGreenPale)
                    NotificationToggleItem(
                        title = "Community Alerts",
                        description = "News about local sustainability events.",
                        isEnabled = communityAlerts,
                        onToggle = { communityAlerts = it }
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationToggleItem(
    title: String,
    description: String,
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Medium, color = CarbonGray, fontSize = 14.sp)
            Text(description, color = CarbonGrayLight, fontSize = 11.sp, lineHeight = 16.sp)
        }
        Switch(
            checked = isEnabled,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = EverGreenPrimary
            )
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NotificationSettingsPreview() {
    EverGreenTheme {
        NotificationSettingsScreen(navController = rememberNavController())
    }
}
