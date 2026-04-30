package com.example.evergreen.ui.screens.carbon

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.evergreen.data.EverGreenViewModel
import com.example.evergreen.navigation.Routes
import com.example.evergreen.ui.screens.dashboard.EverGreenBottomBar
import com.example.evergreen.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCarbonScreen(
    navController: NavController,
    vm: EverGreenViewModel = viewModel()
) {
    // ── Form state ────────────────────────────────────────────────────────────
    var transportKm    by remember { mutableStateOf("") }
    var transportMode  by remember { mutableStateOf("Car") }
    var electricityKwh by remember { mutableStateOf("") }
    var meatMeals      by remember { mutableStateOf("") }
    var isLoading      by remember { mutableStateOf(false) }
    var showSuccess    by remember { mutableStateOf(false) }

    val transportModes = listOf("Car", "Bus", "Train", "Motorcycle", "Walking", "Cycling")
    var modeExpanded   by remember { mutableStateOf(false) }

    // ── Emission factors ──────────────────────────────────────────────────────
    val transportFactor = when (transportMode) {
        "Car"        -> 0.21
        "Bus"        -> 0.089
        "Train"      -> 0.041
        "Motorcycle" -> 0.114
        "Walking"    -> 0.0
        "Cycling"    -> 0.0
        else         -> 0.21
    }

    val transportEmission  = (transportKm.toDoubleOrNull() ?: 0.0) * transportFactor
    val electricityEmission = (electricityKwh.toDoubleOrNull() ?: 0.0) * 0.5
    val foodEmission        = (meatMeals.toIntOrNull() ?: 0) * 3.3
    val totalEmission       = transportEmission + electricityEmission + foodEmission

    // ── Score preview ─────────────────────────────────────────────────────────
    val scorePreview = vm.getSustainabilityScore(totalEmission).coerceIn(0, 100)

    Scaffold(
        bottomBar      = { EverGreenBottomBar(navController, Routes.ADD_CARBON) },
        containerColor = EverGreenSurface,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Log Carbon",
                        fontWeight = FontWeight.SemiBold,
                        fontSize   = 16.sp,
                        color      = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = EverGreenPrimary)
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {

            // ── Live score preview banner ─────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(EverGreenPrimary)
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "Today's footprint",
                            fontSize = 11.sp,
                            color    = EverGreenLight
                        )
                        Text(
                            "${"%.2f".format(totalEmission)} kg CO₂",
                            fontSize   = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color      = Color.White
                        )
                        Text(
                            when {
                                totalEmission == 0.0  -> "Enter your data below"
                                totalEmission < 5.0   -> "Great — below average! 🌱"
                                totalEmission < 15.0  -> "Moderate — room to improve 🌤️"
                                else                  -> "High — let's reduce this 🔴"
                            },
                            fontSize = 11.sp,
                            color    = EverGreenLight
                        )
                    }

                    // Mini score ring
                    Box(
                        modifier         = Modifier.size(68.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        androidx.compose.foundation.Canvas(Modifier.size(68.dp)) {
                            val stroke = androidx.compose.ui.graphics.drawscope.Stroke(
                                width = 8f, cap = StrokeCap.Round
                            )
                            drawArc(
                                color = Color.White.copy(alpha = 0.2f),
                                startAngle = 120f, sweepAngle = 300f,
                                useCenter = false, style = stroke
                            )
                            drawArc(
                                color = Color.White,
                                startAngle = 120f,
                                sweepAngle = (scorePreview / 100f) * 300f,
                                useCenter = false, style = stroke
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "$scorePreview",
                                fontSize   = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color      = Color.White
                            )
                            Text(
                                "score",
                                fontSize = 9.sp,
                                color    = EverGreenLight
                            )
                        }
                    }
                }
            }

            Column(
                modifier            = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                // ── Transport section ─────────────────────────────────────────
                SectionLabel(
                    icon  = Icons.Default.DirectionsCar,
                    title = "Transport",
                    emission = transportEmission
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(16.dp),
                    colors   = CardDefaults.cardColors(containerColor = Color.White),
                    border   = CardDefaults.outlinedCardBorder()
                ) {
                    Column(
                        modifier            = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Mode picker
                        Text("Transport mode", fontSize = 12.sp, color = CarbonGrayLight)
                        ExposedDropdownMenuBox(
                            expanded        = modeExpanded,
                            onExpandedChange = { modeExpanded = !modeExpanded }
                        ) {
                            OutlinedTextField(
                                value         = transportMode,
                                onValueChange = {},
                                readOnly      = true,
                                trailingIcon  = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(modeExpanded)
                                },
                                leadingIcon = {
                                    Icon(
                                        when (transportMode) {
                                            "Car"        -> Icons.Default.DirectionsCar
                                            "Bus"        -> Icons.Default.DirectionsBus
                                            "Train"      -> Icons.Default.Train
                                            "Motorcycle" -> Icons.Default.TwoWheeler
                                            "Walking"    -> Icons.Default.DirectionsWalk
                                            "Cycling"    -> Icons.Default.DirectionsBike
                                            else         -> Icons.Default.DirectionsCar
                                        },
                                        null,
                                        tint     = EverGreenPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                colors   = carbonFieldColors(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                shape    = RoundedCornerShape(10.dp)
                            )
                            ExposedDropdownMenu(
                                expanded        = modeExpanded,
                                onDismissRequest = { modeExpanded = false },
                                containerColor  = Color.White
                            ) {
                                transportModes.forEach { mode ->
                                    DropdownMenuItem(
                                        text    = { Text(mode, fontSize = 13.sp, color = CarbonGray) },
                                        onClick = {
                                            transportMode = mode
                                            modeExpanded  = false
                                        }
                                    )
                                }
                            }
                        }

                        // Distance input
                        CarbonTextField(
                            value         = transportKm,
                            onValueChange = { transportKm = it },
                            label         = "Distance (km)",
                            icon          = Icons.Default.Route,
                            suffix        = "km"
                        )

                        // Emission factor hint
                        if (transportFactor > 0) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(EverGreenPale)
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Info,
                                    null,
                                    tint     = EverGreenPrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    "$transportMode emits ${transportFactor} kg CO₂/km",
                                    fontSize = 11.sp,
                                    color    = EverGreenPrimary
                                )
                            }
                        }
                    }
                }

                // ── Electricity section ───────────────────────────────────────
                SectionLabel(
                    icon     = Icons.Default.ElectricBolt,
                    title    = "Electricity",
                    emission = electricityEmission
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(16.dp),
                    colors   = CardDefaults.cardColors(containerColor = Color.White),
                    border   = CardDefaults.outlinedCardBorder()
                ) {
                    Column(
                        modifier            = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CarbonTextField(
                            value         = electricityKwh,
                            onValueChange = { electricityKwh = it },
                            label         = "Units used (kWh)",
                            icon          = Icons.Default.OfflineBolt,
                            suffix        = "kWh"
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(EverGreenPale)
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Info, null,
                                tint     = EverGreenPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                "Kenya grid: 0.5 kg CO₂ per kWh",
                                fontSize = 11.sp,
                                color    = EverGreenPrimary
                            )
                        }
                    }
                }

                // ── Food section ──────────────────────────────────────────────
                SectionLabel(
                    icon     = Icons.Default.Restaurant,
                    title    = "Food",
                    emission = foodEmission
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(16.dp),
                    colors   = CardDefaults.cardColors(containerColor = Color.White),
                    border   = CardDefaults.outlinedCardBorder()
                ) {
                    Column(
                        modifier            = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CarbonTextField(
                            value         = meatMeals,
                            onValueChange = { meatMeals = it },
                            label         = "Meat meals today",
                            icon          = Icons.Default.SetMeal,
                            suffix        = "meals",
                            keyboardType  = KeyboardType.Number
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(EverGreenPale)
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Info, null,
                                tint     = EverGreenPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                "Each meat meal: ~3.3 kg CO₂",
                                fontSize = 11.sp,
                                color    = EverGreenPrimary
                            )
                        }
                    }
                }

                // ── Breakdown card ────────────────────────────────────────────
                if (totalEmission > 0) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape    = RoundedCornerShape(16.dp),
                        colors   = CardDefaults.cardColors(containerColor = EverGreenPale),
                        border   = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Breakdown",
                                fontSize   = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color      = CarbonGray
                            )
                            Spacer(Modifier.height(10.dp))
                            BreakdownRow("🚗 Transport", transportEmission, totalEmission)
                            Spacer(Modifier.height(6.dp))
                            BreakdownRow("⚡ Electricity", electricityEmission, totalEmission)
                            Spacer(Modifier.height(6.dp))
                            BreakdownRow("🍖 Food", foodEmission, totalEmission)
                            Spacer(Modifier.height(10.dp))
                            HorizontalDivider(color = EverGreenLight)
                            Spacer(Modifier.height(8.dp))
                            Row(
                                modifier              = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Total",
                                    fontSize   = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color      = CarbonGray
                                )
                                Text(
                                    "${"%.2f".format(totalEmission)} kg CO₂",
                                    fontSize   = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color      = EverGreenPrimary
                                )
                            }
                        }
                    }
                }

                // ── Save button ───────────────────────────────────────────────
                Button(
                    onClick = {
                        isLoading = true
                        vm.saveCarbonEntry(
                            transportEmission  = transportEmission,
                            electricityEmission = electricityEmission,
                            foodEmission       = foodEmission,
                            totalEmission      = totalEmission,
                            onSuccess = {
                                isLoading   = false
                                showSuccess = true
                            },
                            onError = { isLoading = false }
                        )
                    },
                    enabled  = totalEmission > 0 && !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape  = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor         = EverGreenPrimary,
                        disabledContainerColor = EverGreenPale,
                        contentColor           = Color.White,
                        disabledContentColor   = CarbonGrayLight
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color    = Color.White,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(Icons.Default.Save, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Save entry",
                            fontSize      = 14.sp,
                            fontWeight    = FontWeight.SemiBold,
                            letterSpacing = 0.3.sp
                        )
                    }
                }

                // ── Success snackbar ──────────────────────────────────────────
                if (showSuccess) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape    = RoundedCornerShape(12.dp),
                        colors   = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
                    ) {
                        Row(
                            modifier          = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                null,
                                tint     = EverGreenPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text(
                                    "Entry saved!",
                                    fontSize   = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color      = EverGreenPrimary
                                )
                                Text(
                                    "+10 points earned 🎉",
                                    fontSize = 11.sp,
                                    color    = CarbonGrayLight
                                )
                            }
                            Spacer(Modifier.weight(1f))
                            Text(
                                "✕",
                                fontSize = 14.sp,
                                color    = CarbonGrayLight,
                                modifier = Modifier.clickable { showSuccess = false }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

// ── Reusable composables ──────────────────────────────────────────────────────

@Composable
private fun SectionLabel(icon: ImageVector, title: String, emission: Double) {
    Row(
        modifier          = Modifier.padding(top = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(EverGreenPale),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = EverGreenPrimary, modifier = Modifier.size(15.dp))
        }
        Spacer(Modifier.width(8.dp))
        Text(
            text       = title,
            fontSize   = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color      = CarbonGray,
            modifier   = Modifier.weight(1f)
        )
        if (emission > 0) {
            Surface(shape = RoundedCornerShape(20.dp), color = EverGreenPale) {
                Text(
                    "${"%.2f".format(emission)} kg",
                    fontSize = 10.sp,
                    color    = EverGreenPrimary,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CarbonTextField(
    value:         String,
    onValueChange: (String) -> Unit,
    label:         String,
    icon:          ImageVector,
    suffix:        String,
    keyboardType:  KeyboardType = KeyboardType.Decimal
) {
    OutlinedTextField(
        value         = value,
        onValueChange = onValueChange,
        label         = { Text(label, fontSize = 12.sp) },
        leadingIcon   = {
            Icon(icon, null, tint = EverGreenPrimary, modifier = Modifier.size(18.dp))
        },
        suffix        = { Text(suffix, fontSize = 12.sp, color = CarbonGrayLight) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine    = true,
        modifier      = Modifier.fillMaxWidth(),
        shape         = RoundedCornerShape(10.dp),
        colors        = carbonFieldColors()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun carbonFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor          = CarbonGray,
    unfocusedTextColor        = CarbonGray,
    focusedContainerColor     = Color.White,
    unfocusedContainerColor   = EverGreenSurface,
    cursorColor               = EverGreenPrimary,
    focusedBorderColor        = EverGreenPrimary,
    unfocusedBorderColor      = Color(0xFFE5E7EB),
    focusedLabelColor         = EverGreenPrimary,
    unfocusedLabelColor       = CarbonGrayLight,
    focusedLeadingIconColor   = EverGreenPrimary,
    unfocusedLeadingIconColor = CarbonGrayLight
)

@Composable
private fun BreakdownRow(label: String, value: Double, total: Double) {
    val fraction = if (total > 0) (value / total).toFloat() else 0f
    Column {
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Text(label, fontSize = 12.sp, color = CarbonGray)
            Text(
                "${"%.2f".format(value)} kg",
                fontSize = 12.sp,
                color    = EverGreenPrimary,
                fontWeight = FontWeight.Medium
            )
        }
        Spacer(Modifier.height(4.dp))
        LinearProgressIndicator(
            progress   = { fraction },
            modifier   = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            color      = EverGreenPrimary,
            trackColor = EverGreenLight,
            strokeCap  = StrokeCap.Round
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AddCarbonScreenPreview() {
    EverGreenTheme {
        AddCarbonScreen(navController = rememberNavController())
    }
}