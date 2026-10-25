package com.example.ui.theme

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

private val DarkColorScheme = darkColorScheme(
    primary = SamityNavyLight,
    onPrimary = Color.White,
    primaryContainer = SamityNavyDark,
    onPrimaryContainer = Color(0xFFD0E4FF),
    secondary = SamityEmeraldLight,
    onSecondary = Color.Black,
    secondaryContainer = SamityEmerald,
    onSecondaryContainer = Color.White,
    tertiary = SamityGoldLight,
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkSurfaceVariant,
    onBackground = Color(0xFFECEFF1),
    onSurface = Color(0xFFECEFF1)
)

private val LightColorScheme = lightColorScheme(
    primary = SamityNavyPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0ECFD),
    onPrimaryContainer = SamityNavyDark,
    secondary = SamityEmerald,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFDCFCE7),
    onSecondaryContainer = Color(0xFF064E3B),
    tertiary = SamityGold,
    background = SlateBackground,
    surface = SlateSurface,
    surfaceVariant = Color(0xFFF1F5F9),
    onBackground = SlateTextPrimary,
    onSurface = SlateTextPrimary,
    outline = SlateBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep branded financial colors for consistency
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
