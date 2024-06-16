package com.example.runpath.ui.theme.Maps

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.example.runpath.ui.theme.GeneralCustomization.Pink40
import com.example.runpath.ui.theme.GeneralCustomization.Pink80
import com.example.runpath.ui.theme.GeneralCustomization.Purple40
import com.example.runpath.ui.theme.GeneralCustomization.Purple80
import com.example.runpath.ui.theme.GeneralCustomization.PurpleGrey40
import com.example.runpath.ui.theme.GeneralCustomization.PurpleGrey80
import com.example.runpath.ui.theme.GeneralCustomization.Typography

// culorile temei aplicatiei pentru dark mode
private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)
// respectiv pentru light mode
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
fun RunPathTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // culoarea dinamica pentru versiunile Android 12 si mai noi
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        // verificarea daca dispozitivul ruleaza Android 12 sau o versiune mai noua
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        // selectarea temei in functie de modul dispozitivului
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    // schimbarea culorii barei de status in functie de tema
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }
    // aplicarea temei
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}