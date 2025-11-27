package com.rizky.flashnews.util

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

fun dateFormatter(inputDateTime: String?): String {
    // 1. Cek jika input null/kosong, langsung return string kosong
    if (inputDateTime.isNullOrEmpty()) return ""

    return try {
        // Format input dari API (ISO Standard)
        val inputFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        val dateTime = OffsetDateTime.parse(inputDateTime, inputFormatter)
        val outputFormatter = DateTimeFormatter
            .ofPattern("dd MMM yyyy", Locale.getDefault())

        dateTime.format(outputFormatter)
    } catch (e: Exception) {
        // Jika gagal parsing, kembalikan tanggal asli (dipotong 10 karakter pertama saja biar tidak panjang)
        inputDateTime.take(10)
    }
}