package com.chifunt.chromaticharptabs.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = RosePinePine,
    onPrimary = RosePineText,
    primaryContainer = RosePineHighlightMed,
    onPrimaryContainer = RosePineText,
    secondary = RosePineRose,
    onSecondary = RosePineBase,
    secondaryContainer = RosePineHighlightHigh,
    onSecondaryContainer = RosePineText,
    tertiary = RosePineFoam,
    onTertiary = RosePineBase,
    error = ErrorRedDark,
    onError = RosePineBase,
    errorContainer = ErrorContainerDark,
    onErrorContainer = RosePineText,
    background = RosePineBase,
    onBackground = RosePineText,
    surface = RosePineSurface,
    onSurface = RosePineText,
    surfaceVariant = RosePineOverlay,
    onSurfaceVariant = RosePineSubtle,
    outline = RosePineHighlightHigh
)

private val LightColorScheme = lightColorScheme(
    primary = RosePineDawnPine,
    onPrimary = RosePineDawnBase,
    primaryContainer = RosePineDawnHighlightMed,
    onPrimaryContainer = RosePineDawnText,
    secondary = RosePineDawnRose,
    onSecondary = RosePineDawnBase,
    secondaryContainer = RosePineDawnHighlightHigh,
    onSecondaryContainer = RosePineDawnText,
    tertiary = RosePineDawnFoam,
    onTertiary = RosePineDawnBase,
    error = ErrorRedLight,
    onError = RosePineDawnBase,
    errorContainer = RosePineDawnHighlightMed,
    onErrorContainer = RosePineDawnText,
    background = RosePineDawnBase,
    onBackground = RosePineDawnText,
    surface = RosePineDawnSurface,
    onSurface = RosePineDawnText,
    surfaceVariant = RosePineDawnOverlay,
    onSurfaceVariant = RosePineDawnSubtle,
    outline = RosePineDawnHighlightHigh
)

@Composable
fun ChromaticHarpTabsTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
