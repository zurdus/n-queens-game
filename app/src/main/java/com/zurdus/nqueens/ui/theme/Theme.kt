package com.zurdus.nqueens.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = RoyalViolet,
    onPrimary = OnRoyalViolet,
    primaryContainer = RoyalVioletContainer,
    onPrimaryContainer = OnRoyalVioletContainer,
    secondary = BurnishedGold,
    onSecondary = OnBurnishedGold,
    secondaryContainer = BurnishedGoldContainer,
    onSecondaryContainer = OnBurnishedGoldContainer,
    tertiary = PuzzleTeal,
    onTertiary = OnPuzzleTeal,
    tertiaryContainer = PuzzleTealContainer,
    onTertiaryContainer = OnPuzzleTealContainer,
    background = WarmBackground,
    onBackground = Ink,
    surface = WarmBackground,
    onSurface = Ink,
    surfaceContainer = WarmSurfaceContainer,
    surfaceContainerHighest = WarmSurfaceContainerHighest,
    onSurfaceVariant = InkVariant,
    outline = Outline,
    outlineVariant = OutlineVariant,
    error = Error,
    onError = Color.White,
    errorContainer = ErrorContainer,
    onErrorContainer = Ink,
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkRoyalViolet,
    onPrimary = OnDarkRoyalViolet,
    primaryContainer = DarkRoyalVioletContainer,
    onPrimaryContainer = OnDarkRoyalVioletContainer,
    secondary = DarkBurnishedGold,
    onSecondary = OnDarkBurnishedGold,
    secondaryContainer = DarkBurnishedGoldContainer,
    onSecondaryContainer = OnDarkBurnishedGoldContainer,
    tertiary = DarkPuzzleTeal,
    onTertiary = OnDarkPuzzleTeal,
    tertiaryContainer = DarkPuzzleTealContainer,
    onTertiaryContainer = OnDarkPuzzleTealContainer,
    background = DarkBackground,
    onBackground = DarkInk,
    surface = DarkBackground,
    onSurface = DarkInk,
    surfaceContainer = DarkSurfaceContainer,
    surfaceContainerHighest = DarkSurfaceContainerHighest,
    onSurfaceVariant = DarkInkVariant,
    outline = DarkOutline,
    outlineVariant = DarkOutlineVariant,
    error = DarkError,
    onError = OnDarkError,
    errorContainer = DarkErrorContainer,
    onErrorContainer = OnDarkErrorContainer,
)

@Immutable
data class BoardColors(
    val lightSquare: Color,
    val darkSquare: Color,
)

private val LocalBoardColors = staticCompositionLocalOf {
    BoardColors(
        lightSquare = LightBoardSquare,
        darkSquare = DarkBoardSquare,
    )
}

val MaterialTheme.boardColors: BoardColors
    @Composable
    @ReadOnlyComposable
    get() = LocalBoardColors.current

@Composable
fun NQueensTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val boardColors = if (darkTheme) {
        BoardColors(
            lightSquare = LightBoardSquareInDarkTheme,
            darkSquare = DarkBoardSquareInDarkTheme,
        )
    } else {
        BoardColors(
            lightSquare = LightBoardSquare,
            darkSquare = DarkBoardSquare,
        )
    }

    androidx.compose.runtime.CompositionLocalProvider(
        LocalBoardColors provides boardColors,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = NQueensTypography,
            content = content,
        )
    }
}
