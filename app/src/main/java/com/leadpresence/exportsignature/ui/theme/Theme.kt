package com.leadpresence.exportsignature.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

/**
 * Custom Light Color Scheme based on brand palette:
 * - Primary: Deep Navy (#003049)
 * - Secondary: Vibrant Red (#C1121F)
 * - Tertiary: Sky Blue (#669BBC)
 * - Background: Warm Cream (#FDF0D5)
 * - Surface: Pure White (#FFFFFF)
 */
private val LightColorScheme = lightColorScheme(
    // Primary color - used for primary actions, highlights
    primary = PrimaryDark,              // #003049 - Deep Navy
    onPrimary = TextOnPrimary,          // #FFFFFF - White text on primary
    primaryContainer = PrimaryLight,    // #669BBC - Light blue container variant
    onPrimaryContainer = PrimaryDark,   // #003049 - Dark text on light container

    // Secondary color - complementary accent color
    secondary = SecondaryRed,           // #C1121F - Vibrant Red
    onSecondary = TextOnPrimary,        // #FFFFFF - White text on secondary
    secondaryContainer = Color(0xFFFDE0E0), // Light red tint for containers
    onSecondaryContainer = SecondaryDarkRed, // #780000 - Dark text on light container

    // Tertiary color - additional accent
    tertiary = PrimaryLight,            // #669BBC - Sky Blue
    onTertiary = TextOnPrimary,         // #FFFFFF - White text
    tertiaryContainer = Color(0xFFD8E4F0), // Light blue tint
    onTertiaryContainer = PrimaryDark,  // #003049 - Dark text on light container

    // Background - main app background
    background = BackgroundCream,       // #FDF0D5 - Warm Cream
    onBackground = TextDark,            // #1A1A1A - Dark text on background

    // Surface - cards, sheets, elevated components
    surface = SurfaceWhite,             // #FFFFFF - Pure white
    onSurface = TextDark,               // #1A1A1A - Dark text on surface
    surfaceVariant = SurfaceLight,      // #F8F6F0 - Off-white variant
    onSurfaceVariant = TextMedium,      // #4A4A4A - Medium text on surface variant

    // Outline & borders
    outline = BorderMedium,             // #D0CCB8 - Medium border
    outlineVariant = BorderLight,       // #E8E6DF - Light border

    // Error states
    error = ErrorRed,                   // #C1121F - Red for errors
    onError = TextOnPrimary,            // #FFFFFF - White on error
    errorContainer = Color(0xFFFDE0E0), // Light error background
    onErrorContainer = ErrorDark,       // #780000 - Dark text on error container

    // Scrim - for modals and overlays
    scrim = Color(0xFF000000).copy(alpha = 0.32f), // Black with 32% opacity

    // Inverse colors - for reversed backgrounds
    inverseSurface = TextDark,          // #1A1A1A
    inverseOnSurface = TextOnPrimary,   // #FFFFFF
    inversePrimary = PrimaryLight       // #669BBC
)

/**
 * Optional: Dark color scheme for future dark mode support
 */
private val DarkColorScheme = darkColorScheme(
    primary = PrimaryLight,             // #669BBC - Light blue becomes primary in dark
    onPrimary = TextDark,               // #1A1A1A - Dark text
    primaryContainer = PrimaryDark,     // #003049 - Navy container
    onPrimaryContainer = PrimaryLight,  // #669BBC - Light text

    secondary = SecondaryRed,           // #C1121F - Red stays same
    onSecondary = TextOnPrimary,        // #FFFFFF - White text
    secondaryContainer = SecondaryDarkRed, // #780000 - Dark container
    onSecondaryContainer = Color(0xFFFFB4B4), // Light text

    tertiary = PrimaryLight,            // #669BBC
    onTertiary = TextDark,              // #1A1A1A
    tertiaryContainer = PrimaryDark,    // #003049
    onTertiaryContainer = PrimaryLight, // #669BBC

    background = Color(0xFF0F0E0A),     // Very dark background
    onBackground = Color(0xFFF8F6F0),   // Light text

    surface = Color(0xFF1A1918),        // Dark surface
    onSurface = Color(0xFFF8F6F0),      // Light text
    surfaceVariant = Color(0xFF2F2D27), // Slightly lighter surface
    onSurfaceVariant = Color(0xFFD0CCB8), // Light-medium text

    outline = Color(0xFF8B8680),        // Medium outline
    outlineVariant = Color(0xFF4F4D47), // Darker outline

    error = ErrorRed,                   // #C1121F
    onError = TextDark,                 // #1A1A1A
    errorContainer = SecondaryDarkRed,  // #780000
    onErrorContainer = Color(0xFFFFB4B4), // Light text

    scrim = Color(0xFF000000).copy(alpha = 0.40f),
    inverseSurface = Color(0xFFF8F6F0),
    inverseOnSurface = Color(0xFF1A1918),
    inversePrimary = PrimaryDark        // #003049
)

/**
 * Main Theme Composable - Supports light/dark mode and dynamic colors (Android 12+)
 *
 * @param darkTheme Whether to use dark theme (defaults to system preference)
 * @param dynamicColor Whether to use dynamic colors on Android 12+ (defaults to true)
 * @param content The composable content to apply theme to
 */
@Composable
fun JustMySignTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        // Dynamic colors available on Android 12+ (S and above)
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) {
                dynamicDarkColorScheme(context)
            } else {
                dynamicLightColorScheme(context)
            }
        }
        // Fallback to custom schemes
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}

/**
 * Light Theme Only - Force light theme without system preference
 * Useful if your app should always be light
 */
@Composable
fun JustMySignLightTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}

/**
 * Utility function to check if dark theme is active
 */
@Composable
fun isDarkThemeEnabled(): Boolean = isSystemInDarkTheme()