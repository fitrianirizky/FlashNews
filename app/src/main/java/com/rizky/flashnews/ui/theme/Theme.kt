package com.rizky.flashnews.ui.theme

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Skema Warna Gelap
private val DarkColorScheme = darkColorScheme(
    primary = RoyalBlueDark,
    onPrimary = Color(0xFF003258), // Teks di atas tombol biru
    primaryContainer = Color(0xFF00497D),
    onPrimaryContainer = Color(0xFFD1E4FF),

    secondary = TealDark,
    onSecondary = Color(0xFF00363C),

    background = DarkBackground,
    onBackground = Color(0xFFE2E2E6), // Warna teks utama di background gelap (Putih gading)

    surface = DarkSurface,
    onSurface = Color(0xFFE2E2E6),

    // INI KUNCI AGAR SEARCH BAR TERLIHAT JELAS DI MODE GELAP
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,

    error = ErrorDark
)

// Skema Warna Terang
private val LightColorScheme = lightColorScheme(
    primary = RoyalBlueLight,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD1E4FF),
    onPrimaryContainer = Color(0xFF001D36),

    secondary = TealLight,
    onSecondary = Color.White,

    background = LightBackground,
    onBackground = Color(0xFF191C1E), // Warna teks utama di background terang (Hampir hitam)

    surface = LightSurface,
    onSurface = Color(0xFF191C1E),

    // INI KUNCI AGAR SEARCH BAR TERLIHAT JELAS DI MODE TERANG
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,

    error = ErrorLight
)

@Composable
fun FlashNewsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Ubah dynamicColor ke FALSE jika ingin warna konsisten biru (tidak mengikuti wallpaper HP user)
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

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Mengatur warna Status Bar agar menyatu dengan aplikasi
            window.statusBarColor = colorScheme.background.toArgb()
            // Mengatur ikon status bar (jam/baterai) jadi gelap/terang otomatis
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Pastikan file Type.kt Anda ada
        content = content
    )
}