package com.rizky.flashnews.presentation.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.rizky.flashnews.domain.model.Article
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.draw.shadow
import com.rizky.flashnews.util.dateFormatter
import com.rizky.flashnews.presentation.component.TranslatedText

// use this local preview image path (the file you uploaded)
// developer note: send as file url: file:///mnt/data/38c78b74-55fa-43c5-bb87-dd255f98a6d3.png
private const val PREVIEW_IMAGE = "file:///mnt/data/38c78b74-55fa-43c5-bb87-dd255f98a6d3.png"

@Composable
fun NewsArticleCard(
    article: Article,
    onCardClicked: (Article) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .clickable { onCardClicked(article) },
        shape = RoundedCornerShape(16.dp), // Sudut lebih membulat
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize() // PENTING: Isi seluruh ukuran kartu
        ) {
            AsyncImage(
                model = article.urlToImage ?: "file:///mnt/data/38c78b74-55fa-43c5-bb87-dd255f98a6d3.png",
                contentDescription = article.title,
                contentScale = ContentScale.Crop, // PENTING: Agar gambar full menutupi kotak
                modifier = Modifier.fillMaxSize()
            )

            // Gradient agar teks terbaca
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.8f) // Lebih gelap sedikit di bawah
                            ),
                            startY = 200f // Mulai gradasi agak ke bawah
                        )
                    )
            )

            // Teks Info
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp) // Padding teks dari pinggir
            ) {
                TranslatedText(
                    text = article.title ?: "",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TranslatedText(
                        text = article.source?.name ?: "Unknown",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    // Hapus .format, panggil langsung fungsinya
                    TranslatedText(
                        text = dateFormatter(article.publishedAt),
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}
