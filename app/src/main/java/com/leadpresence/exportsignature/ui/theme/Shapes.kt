package com.leadpresence.exportsignature.ui.theme


import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Material 3 Shapes system
 * Defines corner radius for different component categories
 */
val Shapes = Shapes(
    // Extra small - small interactive elements (chips, small buttons)
    extraSmall = RoundedCornerShape(4.dp),

    // Small - dialogs, cards, snackbars
    small = RoundedCornerShape(8.dp),

    // Medium - FABs, default buttons, text fields
    medium = RoundedCornerShape(12.dp),

    // Large - prominent containers, sheets
    large = RoundedCornerShape(16.dp),

    // Extra large - bottom sheets, modals
    extraLarge = RoundedCornerShape(28.dp)
)

/**
 * Additional custom corner radius values for consistency
 * Use these for custom components
 */
object CustomShapes {
    val xs = RoundedCornerShape(2.dp)      // Minimal rounding
    val sm = RoundedCornerShape(6.dp)      // Small
    val md = RoundedCornerShape(12.dp)     // Medium (default button)
    val lg = RoundedCornerShape(16.dp)     // Large (cards)
    val xl = RoundedCornerShape(20.dp)     // Extra large
    val xxl = RoundedCornerShape(28.dp)    // Extra extra large (bottom sheet)

    // Pill shapes (max border radius for full pill)
    val pill = RoundedCornerShape(50.dp)

    // Top-only rounding
    val topOnly = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)

    // Bottom-only rounding (for bottom sheets)
    val bottomOnly = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp)

    // Corner-specific shapes
    val topStartOnly = RoundedCornerShape(topStart = 16.dp)
    val topEndOnly = RoundedCornerShape(topEnd = 16.dp)
    val bottomStartOnly = RoundedCornerShape(bottomStart = 16.dp)
    val bottomEndOnly = RoundedCornerShape(bottomEnd = 16.dp)

    // Asymmetric shapes
    val asymmetric = RoundedCornerShape(
        topStart = 16.dp,
        topEnd = 8.dp,
        bottomStart = 8.dp,
        bottomEnd = 16.dp
    )
}