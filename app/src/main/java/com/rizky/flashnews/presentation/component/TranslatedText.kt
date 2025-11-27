package com.rizky.flashnews.presentation.component

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import com.rizky.flashnews.util.NewsTranslator

@Composable
fun TranslatedText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    color: Color = Color.Unspecified,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip
) {
    // State untuk menampung teks yang akan ditampilkan
    // Awalnya berisi teks asli (Inggris) sambil nunggu proses translate
    var displayedText by remember { mutableStateOf(text) }

    // Setiap kali 'text' berubah (misal scroll list), jalankan fungsi translate
    LaunchedEffect(key1 = text) {
        NewsTranslator.translateIfNeeded(text) { result ->
            displayedText = result
        }
    }

    Text(
        text = displayedText,
        modifier = modifier,
        style = style,
        color = color,
        maxLines = maxLines,
        overflow = overflow
    )
}