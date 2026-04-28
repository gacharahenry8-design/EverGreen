package com.example.evergreen.ui.theme

import androidx.compose.ui.graphics.Color

// ─────────────────────────────────────────────────────────────────────────────
// EverGreen Brand Colors
// Referenced by Theme.kt — do not duplicate these anywhere else
// ─────────────────────────────────────────────────────────────────────────────

// ── Green ramp ────────────────────────────────────────────────────────────────
val EverGreenDark      = Color(0xFF1B5E20)  // deep forest   — splash bg, dark headers
val EverGreenPrimary   = Color(0xFF2E7D32)  // core green    — buttons, top bars
val EverGreenMid       = Color(0xFF388E3C)  // mid green     — logo ring, dividers
val EverGreenAccent    = Color(0xFF66BB6A)  // bright green  — icons, active tabs
val EverGreenLight     = Color(0xFFA5D6A7)  // pale green    — progress bars, tags
val EverGreenPale      = Color(0xFFE8F5E9)  // near white    — text on dark bg
val EverGreenSurface   = Color(0xFFF1F8E9)  // screen bg     — scaffold background

// ── Accent / semantic ─────────────────────────────────────────────────────────
val LeafGold           = Color(0xFFF9A825)  // amber         — badges, warnings
val LeafGoldLight      = Color(0xFFFFF8E1)  // amber tint    — warning backgrounds
val CarbonGray         = Color(0xFF37474F)  // dark slate    — body text
val CarbonGrayLight    = Color(0xFF78909C)  // mid slate     — secondary text
val CarbonGrayPale     = Color(0xFFECEFF1)  // light slate   — dividers, disabled

// ── Surface ───────────────────────────────────────────────────────────────────
val SurfaceCard        = Color(0xFFFFFFFF)  // white         — card backgrounds

// ── Status ────────────────────────────────────────────────────────────────────
val StatusDanger       = Color(0xFFE53935)  // red           — errors, alerts

// ── Dark mode surfaces ────────────────────────────────────────────────────────
val DarkSurface        = Color(0xFF121212)  // near black    — dark bg
val DarkSurfaceCard    = Color(0xFF1E1E1E)  // dark card     — elevated dark surface
val DarkSurfaceVariant = Color(0xFF2C2C2C)  // dark variant  — input fields on dark
val DarkOnSurface      = Color(0xFFE0E0E0)  // light gray    — text on dark bg