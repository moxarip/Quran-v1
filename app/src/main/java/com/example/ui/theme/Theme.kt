package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = PolishGold,
    secondary = PolishLightGold,
    tertiary = LightSage,
    background = PolishBodyBg,
    surface = PolishSurfaceBg,
    onPrimary = PolishBodyBg,
    onSecondary = PolishBodyBg,
    onBackground = CreamWhite,
    onSurface = CreamWhite
  )

private val LightColorScheme =
  lightColorScheme(
    primary = IslamicEmerald,
    secondary = IslamicGold,
    tertiary = IslamicDarkGreen,
    background = CreamWhite,
    surface = LightSage,
    onPrimary = CreamWhite,
    onSecondary = DeepSlateBg,
    onBackground = DeepSlateBg,
    onSurface = DeepSlateBg
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Force Dark Theme for majestic spiritual design
  dynamicColor: Boolean = false, // Preserve our designer color scheme
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
