package dev.openstride.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * The app's colours.
 *
 * The palette is our own: the original app's orange is a trademark, so the
 * accent here is a different hue chosen to read as "outdoors" without
 * copying anything.
 */
private val LightColors =
    lightColorScheme(
        primary = Color(0xFF00696E),
        onPrimary = Color.White,
        primaryContainer = Color(0xFF6FF6FE),
        onPrimaryContainer = Color(0xFF002021),
        secondary = Color(0xFF4A6365),
        onSecondary = Color.White,
        secondaryContainer = Color(0xFFCCE8EA),
        onSecondaryContainer = Color(0xFF051F21),
        tertiary = Color(0xFF525E7D),
        onTertiary = Color.White,
        background = Color(0xFFFAFDFC),
        onBackground = Color(0xFF191C1C),
        surface = Color(0xFFFAFDFC),
        onSurface = Color(0xFF191C1C),
        surfaceVariant = Color(0xFFDAE4E5),
        onSurfaceVariant = Color(0xFF3F4949),
        surfaceContainerLowest = Color(0xFFFFFFFF),
        surfaceContainerLow = Color(0xFFF4F7F7),
        surfaceContainer = Color(0xFFEEF2F2),
        surfaceContainerHigh = Color(0xFFE8ECEC),
        surfaceContainerHighest = Color(0xFFE2E6E6),
        surfaceBright = Color(0xFFFAFDFC),
        surfaceDim = Color(0xFFDBDEDF),
        inverseSurface = Color(0xFF2D3131),
        inverseOnSurface = Color(0xFFEFF1F1),
        inversePrimary = Color(0xFF4CD9E1),
        outline = Color(0xFF6F7979),
        outlineVariant = Color(0xFFBFC8C9),
        error = Color(0xFFBA1A1A),
        onError = Color.White,
    )

private val DarkColors =
    darkColorScheme(
        primary = Color(0xFF4CD9E1),
        onPrimary = Color(0xFF003739),
        primaryContainer = Color(0xFF004F52),
        onPrimaryContainer = Color(0xFF6FF6FE),
        secondary = Color(0xFFB0CCCE),
        onSecondary = Color(0xFF1B3436),
        secondaryContainer = Color(0xFF324B4D),
        onSecondaryContainer = Color(0xFFCCE8EA),
        tertiary = Color(0xFFBAC6EA),
        onTertiary = Color(0xFF243047),
        background = Color(0xFF191C1C),
        onBackground = Color(0xFFE0E3E2),
        surface = Color(0xFF191C1C),
        onSurface = Color(0xFFE0E3E2),
        surfaceVariant = Color(0xFF3F4949),
        onSurfaceVariant = Color(0xFFBEC8C9),
        surfaceContainerLowest = Color(0xFF0C1212),
        surfaceContainerLow = Color(0xFF191C1C),
        surfaceContainer = Color(0xFF1D2020),
        surfaceContainerHigh = Color(0xFF272B2B),
        surfaceContainerHighest = Color(0xFF323636),
        surfaceBright = Color(0xFF3F4343),
        surfaceDim = Color(0xFF101414),
        inverseSurface = Color(0xFFE0E3E2),
        inverseOnSurface = Color(0xFF2D3131),
        inversePrimary = Color(0xFF00696E),
        outline = Color(0xFF899393),
        outlineVariant = Color(0xFF3F4949),
        error = Color(0xFFFFB4AB),
        onError = Color(0xFF690005),
    )

@Composable
fun OpenStrideTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) DarkColors else LightColors
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // The app draws edge to edge, so the bar is transparent and only
            // the icon contrast has to be set.
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }
    MaterialTheme(
        colorScheme = colors,
        content = content,
    )
}
