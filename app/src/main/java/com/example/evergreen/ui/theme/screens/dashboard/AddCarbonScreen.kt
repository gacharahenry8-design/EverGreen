package com.example.evergreen.ui.theme.screens.dashboard

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
import com.example.evergreen.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCarbonScreen(
    navController: NavController,
    vm: EverGreenViewModel = viewModel()
) {
    // ── Transport State ────────────────────────────────────────────────────────
    var transportKm         by remember { mutableStateOf("") }
    var transportMode       by remember { mutableStateOf("Car") }
    var returnTrip          by remember { mutableStateOf(false) }
    var numberOfPassengers  by remember { mutableStateOf("1") }

    // ── Electricity State ──────────────────────────────────────────────────────
    var electricityUsageLevel by remember { mutableStateOf("Medium") }
    var acHoursUsed           by remember { mutableStateOf("") }
    var usedRenewableEnergy   by remember { mutableStateOf(false) }
    val electricityLevels     = listOf("Low", "Medium", "High")
    val baseElectricityKwh = when (electricityUsageLevel) {
        "Low"    -> 3.0
        "Medium" -> 7.0
        "High"   -> 18.0
        else     -> 7.0
    }
    val acEmission         = (acHoursUsed.toDoubleOrNull() ?: 0.0) * 1.2 // ~1.2 kWh/hr AC
    val renewableDiscount  = if (usedRenewableEnergy) 0.4 else 1.0
    val electricityKwh     = (baseElectricityKwh + acEmission) * renewableDiscount

    // ── Food State ─────────────────────────────────────────────────────────────
    var meatMeals         by remember { mutableStateOf("") }
    var dairyServings     by remember { mutableStateOf("") }
    var foodWasteLevel    by remember { mutableStateOf("None") }
    var locallySourced    by remember { mutableStateOf(false) }
    val foodWasteLevels   = listOf("None", "Some", "Lots")

    // ── Shopping & Consumption State ───────────────────────────────────────────
    var newClothingItems    by remember { mutableStateOf("") }
    var onlineOrdersCount   by remember { mutableStateOf("") }
    var usedOrSecondhand    by remember { mutableStateOf(false) }

    // ── Water State ────────────────────────────────────────────────────────────
    var showerMinutes       by remember { mutableStateOf("") }
    var showerCount         by remember { mutableStateOf("1") }
    var ranLaundry          by remember { mutableStateOf(false) }
    var coldWashOnly        by remember { mutableStateOf(false) }

    // ── Waste & Recycling State ────────────────────────────────────────────────
    var recycledToday       by remember { mutableStateOf(false) }
    var compostedToday      by remember { mutableStateOf(false) }
    var singleUsePlastics   by remember { mutableStateOf("0") }
    val plasticLevels       = listOf("0", "1–2", "3–5", "5+")

    // ── Loading / Success ──────────────────────────────────────────────────────
    var isLoading           by remember { mutableStateOf(false) }
    var showSuccess         by remember { mutableStateOf(false) }

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
    val passengerCount       = numberOfPassengers.toIntOrNull()?.coerceAtLeast(1) ?: 1
    val tripMultiplier       = if (returnTrip) 2.0 else 1.0
    val transportEmission    = ((transportKm.toDoubleOrNull() ?: 0.0) * transportFactor * tripMultiplier) / passengerCount

    val electricityEmission  = electricityKwh * 0.5

    val foodWasteMultiplier  = when (foodWasteLevel) { "Some" -> 1.15; "Lots" -> 1.35; else -> 1.0 }
    val localFoodDiscount    = if (locallySourced) 0.9 else 1.0
    val meatEmission         = (meatMeals.toIntOrNull() ?: 0) * 3.3
    val dairyEmission        = (dairyServings.toIntOrNull() ?: 0) * 0.9
    val foodEmission         = (meatEmission + dairyEmission) * foodWasteMultiplier * localFoodDiscount

    val clothingEmission     = (newClothingItems.toIntOrNull() ?: 0) * 10.0 * (if (usedOrSecondhand) 0.1 else 1.0)
    val deliveryEmission     = (onlineOrdersCount.toIntOrNull() ?: 0) * 0.5

    val showerEmission       = (showerMinutes.toDoubleOrNull() ?: 0.0) *
            (showerCount.toIntOrNull() ?: 1) * 0.034
    val laundryEmission      = if (ranLaundry) (if (coldWashOnly) 0.3 else 0.7) else 0.0

    val recycleOffset        = if (recycledToday) -0.5 else 0.0
    val compostOffset        = if (compostedToday) -0.3 else 0.0
    val plasticEmission      = when (singleUsePlastics) { "1–2" -> 0.1; "3–5" -> 0.3; "5+" -> 0.6; else -> 0.0 }

    val totalEmission = maxOf(
        0.0,
        transportEmission + electricityEmission + foodEmission +
                clothingEmission + deliveryEmission +
                showerEmission + laundryEmission +
                recycleOffset + compostOffset + plasticEmission
    )

    val scorePreview = vm.getSustainabilityScore(totalEmission).coerceIn(0, 100)

    val transportModes = listOf("Car", "Bus", "Train", "Motorcycle", "Walking", "Cycling")

    Scaffold(
        bottomBar      = { EverGreenBottomBar(navController, Routes.ADD_CARBON) },
        containerColor = EverGreenSurface,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Log Carbon",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
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
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Today's footprint", fontSize = 11.sp, color = EverGreenLight)
                        Text(
                            "${"%.2f".format(totalEmission)} kg CO₂",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Box(modifier = Modifier.size(60.dp), contentAlignment = Alignment.Center) {
                        androidx.compose.foundation.Canvas(Modifier.size(60.dp)) {
                            drawArc(
                                color = Color.White.copy(alpha = 0.2f),
                                startAngle = 120f, sweepAngle = 300f, useCenter = false,
                                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 6f)
                            )
                            drawArc(
                                color = Color.White,
                                startAngle = 120f,
                                sweepAngle = (scorePreview / 100f) * 300f,
                                useCenter = false,
                                style = androidx.compose.ui.graphics.drawscope.Stroke(
                                    width = 6f,
                                    cap = StrokeCap.Round
                                )
                            )
                        }
                        Text(
                            "$scorePreview",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // ══════════════════════════════════════════════════════════════
                // 1. TRANSPORT
                // ══════════════════════════════════════════════════════════════
                SectionLabel(Icons.Default.DirectionsCar, "Transport", transportEmission)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(
                        Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("How did you travel?", fontSize = 12.sp, color = CarbonGrayLight)

                        // Mode chips — row 1
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            transportModes.take(3).forEach { mode ->
                                FilterChip(
                                    selected = transportMode == mode,
                                    onClick  = { transportMode = mode },
                                    label    = { Text(mode, fontSize = 11.sp) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                        // Mode chips — row 2
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            transportModes.drop(3).forEach { mode ->
                                FilterChip(
                                    selected = transportMode == mode,
                                    onClick  = { transportMode = mode },
                                    label    = { Text(mode, fontSize = 11.sp) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        CarbonTextField(
                            value         = transportKm,
                            onValueChange = { transportKm = it },
                            label         = "Distance covered (km)",
                            icon          = Icons.Default.Route,
                            suffix        = "km"
                        )

                        // Return trip toggle
                        CarbonToggleRow(
                            icon    = Icons.Default.SwapHoriz,
                            label   = "Return / round trip?",
                            checked = returnTrip,
                            onCheckedChange = { returnTrip = it }
                        )

                        // Carpooling (only relevant for Car/Motorcycle)
                        if (transportMode == "Car" || transportMode == "Motorcycle") {
                            Text(
                                "Number of passengers (incl. you)",
                                fontSize = 12.sp,
                                color = CarbonGrayLight
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf("1", "2", "3", "4+").forEach { n ->
                                    FilterChip(
                                        selected = numberOfPassengers == n,
                                        onClick  = { numberOfPassengers = n },
                                        label    = { Text(n, fontSize = 11.sp) }
                                    )
                                }
                            }
                            if (numberOfPassengers != "1") {
                                Text(
                                    "Great — carpooling reduces your share of emissions!",
                                    fontSize = 11.sp,
                                    color = EverGreenPrimary
                                )
                            }
                        }
                    }
                }

                // ══════════════════════════════════════════════════════════════
                // 2. ELECTRICITY
                // ══════════════════════════════════════════════════════════════
                SectionLabel(Icons.Default.ElectricBolt, "Electricity Usage", electricityEmission)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(
                        Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("General usage level today", fontSize = 12.sp, color = CarbonGrayLight)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            electricityLevels.forEach { level ->
                                FilterChip(
                                    selected = electricityUsageLevel == level,
                                    onClick  = { electricityUsageLevel = level },
                                    label    = { Text(level, fontSize = 11.sp) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        Text(
                            text = when (electricityUsageLevel) {
                                "Low"    -> "Basic: Lights, phone charging, laptop (est. 3 kWh)"
                                "Medium" -> "Standard: Fridge, TV, lighting, fans (est. 7 kWh)"
                                "High"   -> "Heavy: AC usage, water heater, oven (est. 18 kWh)"
                                else     -> ""
                            },
                            fontSize = 11.sp,
                            color    = EverGreenPrimary,
                            modifier = Modifier.padding(top = 2.dp)
                        )

                        // AC hours
                        CarbonTextField(
                            value         = acHoursUsed,
                            onValueChange = { acHoursUsed = it },
                            label         = "Air conditioning hours used",
                            icon          = Icons.Default.AcUnit,
                            suffix        = "hrs"
                        )

                        // Renewable energy
                        CarbonToggleRow(
                            icon            = Icons.Default.WbSunny,
                            label           = "Used solar / renewable energy today?",
                            checked         = usedRenewableEnergy,
                            onCheckedChange = { usedRenewableEnergy = it }
                        )
                        if (usedRenewableEnergy) {
                            Text(
                                "40% emission reduction applied for renewable use.",
                                fontSize = 11.sp,
                                color = EverGreenPrimary
                            )
                        }
                    }
                }

                // ══════════════════════════════════════════════════════════════
                // 3. FOOD & DIET
                // ══════════════════════════════════════════════════════════════
                SectionLabel(Icons.Default.Restaurant, "Food & Diet", foodEmission)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(
                        Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CarbonTextField(
                            value         = meatMeals,
                            onValueChange = { meatMeals = it },
                            label         = "Meat-based meals today",
                            icon          = Icons.Default.SetMeal,
                            suffix        = "meals",
                            keyboardType  = KeyboardType.Number
                        )

                        CarbonTextField(
                            value         = dairyServings,
                            onValueChange = { dairyServings = it },
                            label         = "Dairy servings today (milk, cheese…)",
                            icon          = Icons.Default.LocalDrink,
                            suffix        = "servings",
                            keyboardType  = KeyboardType.Number
                        )

                        // Food waste
                        Text("How much food did you waste?", fontSize = 12.sp, color = CarbonGrayLight)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            foodWasteLevels.forEach { level ->
                                FilterChip(
                                    selected = foodWasteLevel == level,
                                    onClick  = { foodWasteLevel = level },
                                    label    = { Text(level, fontSize = 11.sp) }
                                )
                            }
                        }
                        if (foodWasteLevel != "None") {
                            Text(
                                "Food waste increases your footprint — try planning meals!",
                                fontSize = 11.sp,
                                color = Color(0xFFE65100)
                            )
                        }

                        // Locally sourced
                        CarbonToggleRow(
                            icon            = Icons.Default.Storefront,
                            label           = "Ate locally sourced food today?",
                            checked         = locallySourced,
                            onCheckedChange = { locallySourced = it }
                        )
                    }
                }

                // ══════════════════════════════════════════════════════════════
                // 4. WATER USE
                // ══════════════════════════════════════════════════════════════
                SectionLabel(Icons.Default.WaterDrop, "Water Use", showerEmission + laundryEmission)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(
                        Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CarbonTextField(
                            value         = showerMinutes,
                            onValueChange = { showerMinutes = it },
                            label         = "Shower duration",
                            icon          = Icons.Default.Shower,
                            suffix        = "min"
                        )

                        Text("Number of showers today", fontSize = 12.sp, color = CarbonGrayLight)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("1", "2", "3+").forEach { n ->
                                FilterChip(
                                    selected = showerCount == n,
                                    onClick  = { showerCount = n },
                                    label    = { Text(n, fontSize = 11.sp) }
                                )
                            }
                        }

                        CarbonToggleRow(
                            icon            = Icons.Default.LocalLaundryService,
                            label           = "Did laundry today?",
                            checked         = ranLaundry,
                            onCheckedChange = { ranLaundry = it }
                        )
                        if (ranLaundry) {
                            CarbonToggleRow(
                                icon            = Icons.Default.AcUnit,
                                label           = "Cold wash only?",
                                checked         = coldWashOnly,
                                onCheckedChange = { coldWashOnly = it }
                            )
                        }
                    }
                }

                // ══════════════════════════════════════════════════════════════
                // 5. SHOPPING & CONSUMPTION
                // ══════════════════════════════════════════════════════════════
                SectionLabel(
                    Icons.Default.ShoppingBag,
                    "Shopping & Consumption",
                    clothingEmission + deliveryEmission
                )
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(
                        Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CarbonTextField(
                            value         = newClothingItems,
                            onValueChange = { newClothingItems = it },
                            label         = "New clothing items bought",
                            icon          = Icons.Default.Checkroom,
                            suffix        = "items",
                            keyboardType  = KeyboardType.Number
                        )

                        CarbonToggleRow(
                            icon            = Icons.Default.Recycling,
                            label           = "Were they used / second-hand?",
                            checked         = usedOrSecondhand,
                            onCheckedChange = { usedOrSecondhand = it }
                        )

                        CarbonTextField(
                            value         = onlineOrdersCount,
                            onValueChange = { onlineOrdersCount = it },
                            label         = "Online orders / deliveries today",
                            icon          = Icons.Default.LocalShipping,
                            suffix        = "orders",
                            keyboardType  = KeyboardType.Number
                        )
                    }
                }

                // ══════════════════════════════════════════════════════════════
                // 6. WASTE & RECYCLING
                // ══════════════════════════════════════════════════════════════
                SectionLabel(
                    Icons.Default.DeleteOutline,
                    "Waste & Recycling",
                    plasticEmission + recycleOffset + compostOffset
                )
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(
                        Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "Single-use plastics used today",
                            fontSize = 12.sp,
                            color = CarbonGrayLight
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            plasticLevels.forEach { level ->
                                FilterChip(
                                    selected = singleUsePlastics == level,
                                    onClick  = { singleUsePlastics = level },
                                    label    = { Text(level, fontSize = 11.sp) }
                                )
                            }
                        }

                        CarbonToggleRow(
                            icon            = Icons.Default.Recycling,
                            label           = "Recycled waste today? (−0.5 kg)",
                            checked         = recycledToday,
                            onCheckedChange = { recycledToday = it }
                        )

                        CarbonToggleRow(
                            icon            = Icons.Default.Compost,
                            label           = "Composted food scraps? (−0.3 kg)",
                            checked         = compostedToday,
                            onCheckedChange = { compostedToday = it }
                        )
                    }
                }

                // ── SAVE BUTTON ───────────────────────────────────────────────
                Button(
                    onClick = {
                        isLoading = true
                        vm.saveCarbonEntry(
                            transportEmission    = transportEmission,
                            electricityEmission  = electricityEmission,
                            foodEmission         = foodEmission,
                            totalEmission        = totalEmission,
                            onSuccess            = { isLoading = false; showSuccess = true },
                            onError              = { isLoading = false }
                        )
                    },
                    enabled  = totalEmission > 0 && !isLoading,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape    = RoundedCornerShape(14.dp),
                    colors   = ButtonDefaults.buttonColors(containerColor = EverGreenPrimary)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color       = Color.White,
                            modifier    = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Log Today's Impact", fontWeight = FontWeight.SemiBold)
                    }
                }

                if (showSuccess) {
                    Surface(
                        color    = Color(0xFFE8F5E9),
                        shape    = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CheckCircle, null, tint = EverGreenPrimary)
                            Spacer(Modifier.width(12.dp))
                            Text(
                                "Impact logged successfully! +10 Points",
                                fontSize = 13.sp,
                                color    = EverGreenPrimary
                            )
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))
            }
        }
    }
}

// ── Reusable composables ───────────────────────────────────────────────────────

@Composable
private fun SectionLabel(icon: ImageVector, title: String, emission: Double) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = EverGreenPrimary, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(8.dp))
        Text(
            title,
            fontSize     = 14.sp,
            fontWeight   = FontWeight.Bold,
            color        = CarbonGray,
            modifier     = Modifier.weight(1f)
        )
        if (emission != 0.0) {
            val label = if (emission < 0)
                "${"%.2f".format(emission)} kg"
            else
                "+${"%.2f".format(emission)} kg"
            Text(
                label,
                fontSize   = 12.sp,
                color      = if (emission < 0) EverGreenPrimary else Color(0xFFE65100),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/** A row with an icon, label text, and a trailing Switch. */
@Composable
private fun CarbonToggleRow(
    icon: ImageVector,
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier          = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint     = EverGreenPrimary,
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            label,
            fontSize = 12.sp,
            color    = CarbonGray,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked         = checked,
            onCheckedChange = onCheckedChange,
            colors          = SwitchDefaults.colors(checkedThumbColor = EverGreenPrimary)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CarbonTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    suffix: String,
    keyboardType: KeyboardType = KeyboardType.Decimal
) {
    OutlinedTextField(
        value         = value,
        onValueChange = onValueChange,
        label         = { Text(label, fontSize = 12.sp) },
        leadingIcon   = { Icon(icon, null, tint = EverGreenPrimary, modifier = Modifier.size(18.dp)) },
        suffix        = { Text(suffix, fontSize = 12.sp, color = CarbonGrayLight) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine    = true,
        modifier      = Modifier.fillMaxWidth(),
        shape         = RoundedCornerShape(10.dp),
        colors        = OutlinedTextFieldDefaults.colors(
            focusedBorderColor   = EverGreenPrimary,
            unfocusedBorderColor = Color(0xFFE5E7EB)
        )
    )
}

@Preview(showBackground = true)
@Composable
fun AddCarbonPreview() {
    EverGreenTheme {
        AddCarbonScreen(navController = rememberNavController())
    }
}