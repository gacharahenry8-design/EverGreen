package com.example.evergreen.ui.theme.screens.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.evergreen.navigation.Routes
import com.example.evergreen.navigation.navigateToDashboard
import com.example.evergreen.ui.theme.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import com.example.evergreen.ui.theme.EverGreenTheme

// ─── Splash Screen ────────────────────────────────────────────────────────────
@Composable
fun SplashScreen(navController: NavController) {

    // Animation states
    val logoScale    by rememberInfiniteTransition(label = "pulse")
        .animateFloat(
            initialValue   = 1f,
            targetValue    = 1.08f,
            animationSpec  = infiniteRepeatable(
                animation  = tween(1200, easing = EaseInOut),
                repeatMode = RepeatMode.Reverse
            ),
            label = "logoScale"
        )

    var logoAlpha    by remember { mutableStateOf(0f) }
    var taglineAlpha by remember { mutableStateOf(0f) }
    var titleAlpha   by remember { mutableStateOf(0f) }
    var subtitleAlpha by remember { mutableStateOf(0f) }
    var barAlpha     by remember { mutableStateOf(0f) }
    var progress     by remember { mutableStateOf(0f) }

    val logoScaleAnim    by animateFloatAsState(logoAlpha,    tween(600), label = "la")
    val taglineAlphaAnim by animateFloatAsState(taglineAlpha, tween(600), label = "ta")
    val titleAlphaAnim   by animateFloatAsState(titleAlpha,   tween(600), label = "ti")
    val subtitleAlphaAnim by animateFloatAsState(subtitleAlpha, tween(600), label = "sa")
    val barAlphaAnim     by animateFloatAsState(barAlpha,     tween(600), label = "ba")
    val progressAnim     by animateFloatAsState(
        progress,
        animationSpec = tween(2000, easing = FastOutSlowInEasing),
        label = "progress"
    )

    // Staggered entrance → navigate after load
    LaunchedEffect(Unit) {
        delay(100);  logoAlpha     = 1f
        delay(500);  taglineAlpha  = 1f
        delay(200);  titleAlpha    = 1f
        delay(200);  subtitleAlpha = 1f
        delay(200);  barAlpha      = 1f
        delay(100);  progress      = 1f
        delay(2200)

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            navController.navigateToDashboard()
        } else {
            navController.navigate(Routes.LOGIN) {
                popUpTo(Routes.SPLASH) { inclusive = true }
            }
        }
    }

    // ── Full-screen background ────────────────────────────────────────────────
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(EverGreenDark),
        contentAlignment = Alignment.Center
    ) {
        // Decorative corner leaves
        CornerLeaves()

        // ── Centre content ────────────────────────────────────────────────────
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // Logo ring (pulsing outer + inner circle + leaf icon)
            Box(
                modifier = Modifier
                    .alpha(logoScaleAnim)
                    .scale(logoScale)
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(EverGreenPrimary),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(EverGreenMid),
                    contentAlignment = Alignment.Center
                ) {
                    LeafIcon()
                }
            }

            Spacer(Modifier.height(20.dp))

            // "EST. 2025" tag
            Text(
                text      = "EST. 2026",
                fontSize  = 10.sp,
                fontWeight = FontWeight.Light,
                letterSpacing = 3.sp,
                color     = EverGreenLight,
                modifier  = Modifier.alpha(taglineAlphaAnim)
            )

            Spacer(Modifier.height(6.dp))

            // App name
            Text(
                text      = "EverGreen",
                fontSize  = 36.sp,
                fontWeight = FontWeight.SemiBold,
                color     = EverGreenPale,
                textAlign = TextAlign.Center,
                modifier  = Modifier.alpha(titleAlphaAnim)
            )

            Spacer(Modifier.height(6.dp))

            // Tagline
            Text(
                text      = "TRACK. REDUCE. SUSTAIN.",
                fontSize  = 10.sp,
                fontWeight = FontWeight.Light,
                letterSpacing = 1.5.sp,
                color     = EverGreenAccent,
                textAlign = TextAlign.Center,
                modifier  = Modifier.alpha(subtitleAlphaAnim)
            )

            Spacer(Modifier.height(48.dp))

            // Loading bar
            Column(
                modifier = Modifier
                    .alpha(barAlphaAnim)
                    .width(160.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LinearProgressIndicator(
                    progress       = { progressAnim },
                    modifier       = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .clip(RoundedCornerShape(1.dp)),
                    color          = EverGreenLight,
                    trackColor     = EverGreenPrimary,
                    strokeCap      = StrokeCap.Round
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text      = "Loading your impact...",
                    fontSize  = 10.sp,
                    fontWeight = FontWeight.Light,
                    letterSpacing = 0.5.sp,
                    color     = EverGreenAccent
                )
            }
        }
    }
}

// ── Decorative corner leaf shapes ────────────────────────────────────────────
@Composable
private fun BoxScope.CornerLeaves() {
    // Top-left
    Box(
        modifier = Modifier
            .size(100.dp)
            .align(Alignment.TopStart)
            .offset((-20).dp, (-20).dp)
            .clip(RoundedCornerShape(50, 0, 50, 0))
            .background(EverGreenPrimary.copy(alpha = 0.5f))
    )
    // Top-right
    Box(
        modifier = Modifier
            .size(65.dp)
            .align(Alignment.TopEnd)
            .offset(15.dp, 30.dp)
            .clip(RoundedCornerShape(0, 50, 0, 50))
            .background(EverGreenMid.copy(alpha = 0.35f))
    )
    // Bottom-right
    Box(
        modifier = Modifier
            .size(130.dp)
            .align(Alignment.BottomEnd)
            .offset(30.dp, 30.dp)
            .clip(RoundedCornerShape(50, 0, 50, 0))
            .background(EverGreenPrimary.copy(alpha = 0.45f))
    )
    // Bottom-left
    Box(
        modifier = Modifier
            .size(75.dp)
            .align(Alignment.BottomStart)
            .offset((-20).dp, (-10).dp)
            .clip(RoundedCornerShape(0, 50, 0, 50))
            .background(EverGreenMid.copy(alpha = 0.3f))
    )
}

// ── Simple leaf SVG icon drawn with Canvas/Compose shapes ────────────────────
@Composable
private fun LeafIcon() {
    // Using a simple Text emoji as placeholder — replace with
    // an actual SVG vector drawable (res/drawable/ic_leaf.xml) in production
    Text(
        text     = "\uD83C\uDF3F",   // 🌿
        fontSize = 26.sp,
        textAlign = TextAlign.Center
    )
}


@Preview(showBackground = true, name = "Light Mode")
@Composable
fun SplashScreenPreview() {
    EverGreenTheme(darkTheme = false) {
        // rememberNavController provides a fake controller for previewing
        val navController = rememberNavController()
        SplashScreen(navController = navController)
    }
}

@Preview(showBackground = true, name = "Dark Mode")
@Composable
fun SplashScreenDarkPreview() {
    EverGreenTheme(darkTheme = true) {
        val navController = rememberNavController()
        SplashScreen(navController = navController)
    }
}