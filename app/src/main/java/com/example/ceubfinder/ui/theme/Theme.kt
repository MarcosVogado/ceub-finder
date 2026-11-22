package com.example.ceubfinder.ui.theme

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
    primary = CeubMagentaLight,
    secondary = CeubPurpleLight,
    tertiary = CeubMagenta,
    background = Color(0xFF1C171F),
    surface = Color(0xFF251D29),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFFE6E1E6),
    onSurface = Color(0xFFE6E1E6)
)

private val LightColorScheme = lightColorScheme(
    primary = CeubPurple,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEFE8F3),
    onPrimaryContainer = Color(0xFF251D29),
    secondary = CeubMagenta,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFCE8F6),
    onSecondaryContainer = Color(0xFF330B27),
    tertiary = CeubMagentaLight,
    background = CeubBackground,
    onBackground = CeubTextPrimary,
    surface = CeubSurface,
    onSurface = CeubTextPrimary,
    surfaceVariant = CeubSurfaceVariant,
    onSurfaceVariant = CeubTextSecondary
)

@Composable
fun CEUBFinderTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color disabled by default to maintain CEUB institutional identity
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
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
