package com.riapebriand.assesment2.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun Assesment2Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
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


enum class AppTheme(val displayName: String) {
    PINK("Pink Pastel"),
    BLUE("Biru Pastel"),
    YELLOW("Kuning Pastel")
}

val pinkColorScheme: ColorScheme = lightColorScheme(
    primary = Color(0xFFF48FB1),
    primaryContainer = Color(0xFFFCE4EC),
    onPrimary = Color.Black
)

val blueColorScheme: ColorScheme = lightColorScheme(
    primary = Color(0xFF90CAF9),
    primaryContainer = Color(0xFFE3F2FD),
    onPrimary = Color.Black
)

val yellowColorScheme: ColorScheme = lightColorScheme(
    primary = Color(0xFFFFF176),
    primaryContainer = Color(0xFFFFF9C4),
    onPrimary = Color.Black
)
