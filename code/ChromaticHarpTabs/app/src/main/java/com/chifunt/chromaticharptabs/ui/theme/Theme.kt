package com.chifunt.chromaticharptabs.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = Silver200,
    onPrimary = Navy900,
    primaryContainer = Navy700,
    onPrimaryContainer = Silver100,
    secondary = Mist100,
    onSecondary = Navy900,
    secondaryContainer = Navy600,
    onSecondaryContainer = Silver100,
    tertiary = Silver300,
    onTertiary = Navy900,
    error = ErrorRed,
    onError = Silver100,
    errorContainer = ErrorContainerDark,
    onErrorContainer = Silver100,
    background = Navy900,
    onBackground = Silver100,
    surface = Navy800,
    onSurface = Silver100,
    surfaceVariant = Navy700,
    onSurfaceVariant = Silver200,
    outline = Mist200
)

private val LightColorScheme = lightColorScheme(
    primary = Navy700,
    onPrimary = Silver100,
    primaryContainer = Silver200,
    onPrimaryContainer = Navy900,
    secondary = Navy600,
    onSecondary = Silver100,
    secondaryContainer = Silver200,
    onSecondaryContainer = Navy900,
    tertiary = Navy700,
    onTertiary = Silver100,
    error = ErrorRed,
    onError = Silver100,
    background = Silver100,
    onBackground = Navy900,
    surface = Silver200,
    onSurface = Navy900,
    surfaceVariant = Silver300,
    onSurfaceVariant = Navy800,
    outline = Mist200
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
