package com.leadpresence.exportsignature.ui.theme

import androidx.compose.ui.graphics.Color

// Primary Brand Colors - Navy & Burgundy
val PrimaryDark = Color(0xFF003049)      // Deep Navy - Primary
val PrimaryLight = Color(0xFF669BBC)     // Sky Blue - Primary variant/Light
val SecondaryRed = Color(0xFFC1121F)     // Vibrant Red - Accent
val SecondaryDarkRed = Color(0xFF780000) // Deep Burgundy - Dark accent

// Background & Surface Colors
val BackgroundCream = Color(0xFFFDF0D5)  // Warm Cream - Main background
val SurfaceWhite = Color(0xFFFFFFFF)     // Pure White - Cards, surfaces
val SurfaceLight = Color(0xFFF8F6F0)     // Off-white - Secondary surfaces

// Text Colors
val TextDark = Color(0xFF1A1A1A)         // Near Black - Primary text
val TextMedium = Color(0xFF4A4A4A)       // Dark Gray - Secondary text
val TextLight = Color(0xFF7A7A7A)        // Medium Gray - Tertiary text
val TextOnPrimary = Color(0xFFFFFFFF)    // White - Text on primary color

// Semantic Colors
val ErrorRed = Color(0xFFC1121F)         // Use SecondaryRed
val ErrorDark = Color(0xFF780000)        // Use SecondaryDarkRed
val SuccessGreen = Color(0xFF4CAF50)     // Success state
val WarningOrange = Color(0xFFFF9800)    // Warning state
val InfoBlue = Color(0xFF2196F3)         // Info state

// Border & Divider Colors
val BorderLight = Color(0xFFE8E6DF)      // Light border for inputs
val BorderMedium = Color(0xFFD0CCB8)     // Medium border
val DividerColor = Color(0xFFE0DCC8)     // Divider lines

// Additional Utilities
val DisabledGray = Color(0xFFC4C4C4)     // Disabled state
val HintGray = Color(0xFF9E9E9E)         // Hint text
val OverlayDark = Color(0xFF000000).copy(alpha = 0.12f) // Overlay tint
val OverlayLight = Color(0xFFFFFFFF).copy(alpha = 0.08f) // Light overlay

// Gradient Colors (for enhanced UI elements)
val GradientStart = PrimaryDark
val GradientEnd = PrimaryLight
val AccentGradientStart = SecondaryDarkRed
val AccentGradientEnd = SecondaryRed