package com.rizky.flashnews.ui.theme

import androidx.compose.ui.graphics.Color

// --- DOMINANT COLORS (Primary Brand) ---
// Warna Biru Royal yang profesional untuk News App
val RoyalBlueLight = Color(0xFF0061A4)
val RoyalBlueDark = Color(0xFF9FCAFF)  // Versi lebih terang untuk mode gelap agar tidak redup

// --- SECONDARY COLORS (Accents) ---
// Warna pendukung (misal untuk tombol sekunder atau float)
val TealLight = Color(0xFF006972)
val TealDark = Color(0xFF4DD8E7)

// --- NEUTRAL / BACKGROUND COLORS (PENTING UNTUK SEARCH) ---

// Mode Terang (Light Mode)
val LightBackground = Color(0xFFFBFCFE) // Putih sedikit kebiruan, tidak putih joss (agar mata rileks)
val LightSurface = Color(0xFFFBFCFE)    // Warna kartu/card
val LightSurfaceVariant = Color(0xFFDFE2EB) // Warna Kolom Search Bar (Abu-abu muda)
val LightOnSurfaceVariant = Color(0xFF43474E) // Warna Teks di dalam Search Bar (Abu-abu gelap) -> PASTI KELIHATAN

// Mode Gelap (Dark Mode)
val DarkBackground = Color(0xFF191C1E) // Hitam keabu-abuan (bukan hitam pekat OLED, lebih elegan)
val DarkSurface = Color(0xFF191C1E)
val DarkSurfaceVariant = Color(0xFF43474E) // Warna Kolom Search Bar (Abu-abu gelap)
val DarkOnSurfaceVariant = Color(0xFFC3C7CF) // Warna Teks di dalam Search Bar (Putih tulang) -> PASTI KELIHATAN

// Warna Error (Standar Material)
val ErrorLight = Color(0xFFBA1A1A)
val ErrorDark = Color(0xFFFFB4AB)