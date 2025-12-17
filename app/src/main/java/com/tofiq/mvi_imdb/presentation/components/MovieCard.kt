package com.tofiq.mvi_imdb.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.tofiq.mvi_imdb.domain.model.Movie
import com.tofiq.mvi_imdb.util.Constants

/**
 * Reusable movie card composable displaying poster, title, year, and rating.
 * Optimized for recomposition by using stable parameters and remembered values.
 * Uses fixed height for info section to ensure uniform card heights in grids.
 * 
 * Requirements: 1.5 - WHEN displaying a movie item THEN the Movie_List_Screen 
 * SHALL show the movie poster, title, release year, and rating
 */
@Composable
fun MovieCard(
    movie: Movie,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Remember the poster URL to avoid recalculation
    val posterUrl = remember(movie.posterPath) {
        Constants.getPosterUrl(movie.posterPath)
    }
    
    // Remember the info text to avoid string concatenation on recomposition
    val infoText = remember(movie.releaseYear, movie.formattedRating) {
        "${movie.releaseYear} • ★ ${movie.formattedRating}"
    }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            // Movie poster
            AsyncImage(
                model = posterUrl,
                contentDescription = movie.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f / 3f)
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop
            )
            
            // Movie info - fixed height to ensure uniform card sizes
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .height(48.dp)
            ) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    minLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    text = infoText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }
    }
}
