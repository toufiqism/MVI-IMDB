package com.tofiq.mvi_imdb.presentation.components

import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tofiq.mvi_imdb.domain.model.Category

/**
 * Reusable category tabs composable for movie categories.
 * Optimized for recomposition with remembered callbacks.
 * 
 * Requirements: 2.1 - WHEN the user navigates to the home screen THEN the Movie_List_Screen 
 * SHALL display category tabs for Popular, Top Rated, Upcoming, and Now Playing
 */
@Composable
fun CategoryTabs(
    selectedCategory: Category,
    onCategorySelected: (Category) -> Unit,
    modifier: Modifier = Modifier
) {
    // Remember categories list to avoid recreating on each recomposition
    val categories = remember { Category.entries }
    val selectedIndex = categories.indexOf(selectedCategory)

    ScrollableTabRow(
        selectedTabIndex = selectedIndex,
        edgePadding = 16.dp,
        modifier = modifier
    ) {
        categories.forEach { category ->
            // Remember click callback for each category
            val onClick = remember(category) {
                { onCategorySelected(category) }
            }
            Tab(
                selected = category == selectedCategory,
                onClick = onClick,
                text = { Text(category.displayName) }
            )
        }
    }
}
