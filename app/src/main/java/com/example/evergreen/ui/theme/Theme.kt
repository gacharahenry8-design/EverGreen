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
