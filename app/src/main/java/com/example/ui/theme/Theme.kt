package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val QuizFightColorScheme = darkColorScheme(
    primary = ArcadeGold,
    onPrimary = ArcadeVoid,
    primaryContainer = ArcadeAmber,
    onPrimaryContainer = ArcadeVoid,
    secondary = ArcadeCyan,
    onSecondary = ArcadeVoid,
    secondaryContainer = ArcadePanel,
    onSecondaryContainer = ArcadeCream,
    tertiary = ArcadeEmerald,
    onTertiary = ArcadeVoid,
    background = ArcadeVoid,
    onBackground = ArcadeCream,
    surface = ArcadeSurface,
    onSurface = ArcadeCream,
    surfaceVariant = ArcadePanel,
    onSurfaceVariant = ArcadeMutedText,
    error = ArcadeCrimson,
    onError = ArcadeCream,
    outline = ArcadePanelBorderLight
)

@Composable
fun QuizFightTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = QuizFightColorScheme,
        typography = QuizFightTypography,
        content = content
    )
}
