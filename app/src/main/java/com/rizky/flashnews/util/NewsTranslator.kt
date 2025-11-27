package com.rizky.flashnews.util

import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import java.util.Locale

object NewsTranslator {
    private var isModelDownloaded = false

    fun translateIfNeeded(text: String, onResult: (String) -> Unit) {
        // 1. Cek Bahasa HP User
        val deviceLanguage = Locale.getDefault().language
        // Kode 'in' atau 'id' adalah kode untuk Bahasa Indonesia
        val isIndonesian = deviceLanguage == "in" || deviceLanguage == "id"

        // Jika HP bukan Bahasa Indonesia, atau teks kosong, langsung kembalikan teks asli (Inggris)
        if (!isIndonesian || text.isEmpty()) {
            onResult(text)
            return
        }

        // 2. Siapkan Opsi Translator (Inggris -> Indonesia)
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.INDONESIAN)
            .build()
        val translator = Translation.getClient(options)

        // 3. Syarat Download (Bisa diset requireWifi() jika ingin hemat kuota user)
        val conditions = DownloadConditions.Builder()
            .build()

        // 4. Proses Translate
        // Cek apakah model bahasa sudah ada di HP?
        translator.downloadModelIfNeeded(conditions)
            .addOnSuccessListener {
                isModelDownloaded = true
                // Model siap, lakukan translate
                translator.translate(text)
                    .addOnSuccessListener { translatedText ->
                        onResult(translatedText)
                    }
                    .addOnFailureListener {
                        onResult(text) // Gagal translate, pakai teks asli
                    }
            }
            .addOnFailureListener {
                // Gagal download model (misal gak ada internet), pakai teks asli
                onResult(text)
            }
    }
}