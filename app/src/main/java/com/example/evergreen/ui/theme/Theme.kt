package com.example.evergreen.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.evergreen.R


// ─────────────────────────────────────────────────────────────────────────────
// Light Color Scheme
// ─────────────────────────────────────────────────────────────────────────────
private val LightColorScheme = lightColorScheme(
    primary              = EverGreenPrimary,
    onPrimary            = SurfaceCard,
    primaryContainer     = EverGreenLight,
    onPrimaryContainer   = EverGreenDark,

    secondary            = EverGreenAccent,
    onSecondary          = EverGreenDark,
    secondaryContainer   = EverGreenPale,
    onSecondaryContainer = EverGreenPrimary,

    tertiary             = LeafGold,
    onTertiary           = SurfaceCard,
    tertiaryContainer    = LeafGoldLight,
    onTertiaryContainer  = Color(0xFF4E2600),

    background           = EverGreenSurface,
    onBackground         = CarbonGray,

    surface              = SurfaceCard,
    onSurface            = CarbonGray,
    surfaceVariant       = EverGreenPale,
    onSurfaceVariant     = CarbonGrayLight,

    error                = StatusDanger,
    onError              = SurfaceCard,
    errorContainer       = Color(0xFFFFDAD6),
    onErrorContainer     = Color(0xFF410002),

    outline              = CarbonGrayPale,
    outlineVariant       = EverGreenLight,
)

// ─────────────────────────────────────────────────────────────────────────────
// Dark Color Scheme
// ─────────────────────────────────────────────────────────────────────────────
private val DarkColorScheme = darkColorScheme(
    primary              = EverGreenAccent,
    onPrimary            = EverGreenDark,
    primaryContainer     = EverGreenPrimary,
    onPrimaryContainer   = EverGreenPale,

    secondary            = EverGreenLight,
    onSecondary          = EverGreenDark,
    secondaryContainer   = EverGreenMid,
    onSecondaryContainer = EverGreenPale,

    tertiary             = LeafGold,
    onTertiary           = DarkSurface,
    tertiaryContainer    = Color(0xFF4E2600),
    onTertiaryContainer  = LeafGoldLight,

    background           = DarkSurface,
    onBackground         = DarkOnSurface,

    surface              = DarkSurfaceCard,
    onSurface            = DarkOnSurface,
    surfaceVariant       = DarkSurfaceVariant,
    onSurfaceVariant     = CarbonGrayLight,

    error                = Color(0xFFFFB4AB),
    onError              = Color(0xFF690005),
    errorContainer       = Color(0xFF93000A),
    onErrorContainer     = Color(0xFFFFDAD6),

    outline              = CarbonGrayLight,
    outlineVariant       = DarkSurfaceVariant,
)

// ─────────────────────────────────────────────────────────────────────────────
// App Theme
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun EverGreenTheme(
    darkTheme: Boolean    = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,          // false = always use brand colors
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else      -> LightColorScheme
    }

    // Status bar matches the app header color
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = Typography,
        content     = content
    )
}
// ─────────────────────────────────────────────────────────────────────────────
// Font Family
// Using Google Fonts via downloadable fonts (no .ttf files needed)
// Add these to res/font/ OR use the Google Fonts Gradle plugin
// ─────────────────────────────────────────────────────────────────────────────

// Option A — if you add .ttf files to res/font/:
// val PoppinsFamily = FontFamily(
//     Font(R.font.poppins_regular,  FontWeight.Normal),
//     Font(R.font.poppins_medium,   FontWeight.Medium),
//     Font(R.font.poppins_semibold, FontWeight.SemiBold),
//     Font(R.font.poppins_bold,     FontWeight.Bold)
// )

// Option B — system default (safe fallback, works immediately with no setup)
val EverGreenFontFamily = FontFamily.Default

// ─────────────────────────────────────────────────────────────────────────────
// Typography Scale
// Material 3 type roles mapped to EverGreen's visual hierarchy
// ─────────────────────────────────────────────────────────────────────────────
val Typography = Typography(

    // ── Display — large hero text (not used often) ────────────────────────────
    displayLarge = TextStyle(
        fontFamily = EverGreenFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize   = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontFamily = EverGreenFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize   = 45.sp,
        lineHeight = 52.sp
    ),
    displaySmall = TextStyle(
        fontFamily = EverGreenFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize   = 36.sp,
        lineHeight = 44.sp
    ),

    // ── Headline — screen titles, section headers ─────────────────────────────
    headlineLarge = TextStyle(
        fontFamily = EverGreenFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize   = 32.sp,
        lineHeight = 40.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = EverGreenFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize   = 28.sp,
        lineHeight = 36.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = EverGreenFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize   = 24.sp,
        lineHeight = 32.sp
    ),

    // ── Title — card titles, dialog headings, top bars ────────────────────────
    titleLarge = TextStyle(
        fontFamily = EverGreenFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize   = 22.sp,
        lineHeight = 28.sp
    ),
    titleMedium = TextStyle(
        fontFamily = EverGreenFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize   = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontFamily = EverGreenFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize   = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),

    // ── Body — main readable text, form content ───────────────────────────────
    bodyLarge = TextStyle(
        fontFamily = EverGreenFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize   = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = EverGreenFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize   = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = EverGreenFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize   = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),

    // ── Label — buttons, tabs, chips, badges ──────────────────────────────────
    labelLarge = TextStyle(
        fontFamily = EverGreenFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize   = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = EverGreenFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize   = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = EverGreenFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize   = 10.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)

