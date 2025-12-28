package com.tofiq.mvi_imdb.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tofiq.mvi_imdb.domain.model.Category
import com.tofiq.mvi_imdb.ui.theme.AnimationSpecs
import com.tofiq.mvi_imdb.ui.theme.VibrantBlue
import com.tofiq.mvi_imdb.ui.theme.VibrantPink
import com.tofiq.mvi_imdb.ui.theme.VibrantPurple

/**
 * Enhanced category tabs with animated pill indicator and smooth transitions.
 * 
 * Features:
 * - Pill-shaped gradient indicator
 * - Scale animation on selection
 * - Color transitions for selected/unselected states
 * - Bouncy spring animations
 * 
 * Optimized for:
 * - Recomposition stability with remembered callbacks
 * - Smooth animations via graphicsLayer
 * - Configuration change survival
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

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        ScrollableTabRow(
            selectedTabIndex = selectedIndex,
            edgePadding = 16.dp,
            modifier = Modifier.fillMaxWidth(),
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onSurface,
            indicator = { tabPositions ->
                if (selectedIndex < tabPositions.size) {
                    // Animated gradient pill indicator
                    val currentTabPosition = tabPositions[selectedIndex]
                    
                    Box(
                        modifier = Modifier
                            .tabIndicatorOffset(currentTabPosition)
                            .height(40.dp)
                            .padding(horizontal = 4.dp, vertical = 4.dp)
                            .clip(RoundedCornerShape(20.dp))
//                            .background(
//                                brush = Brush.horizontalGradient(
//                                    colors = listOf(
//                                        VibrantBlue,
//                                        VibrantPurple
//                                    )
//                                )
//                            )
                    )
                }
            },
            divider = {} // Remove divider for cleaner look
        ) {
            categories.forEachIndexed { index, category ->
                val isSelected = category == selectedCategory
                
                // Remember click callback for each category
                val onClick = remember(category) {
                    { onCategorySelected(category) }
                }
                
                // Animate text color
                val textColor by animateColorAsState(
                    targetValue = if (isSelected) {
                        Color.White
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    animationSpec = tween(AnimationSpecs.SHORT_DURATION),
                    label = "tabTextColor"
                )
                
                // Animate scale for selection
                val scale by animateFloatAsState(
                    targetValue = if (isSelected) 1.05f else 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "tabScale"
                )
                
                // Animate font weight
                val fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                
                Tab(
                    selected = isSelected,
                    onClick = onClick,
                    modifier = Modifier.height(48.dp),
                    text = {
                        Text(
                            text = category.displayName,
                            color = textColor,
                            fontWeight = fontWeight,
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                            }
                        )
                    }
                )
            }
        }
    }
}

/**
 * Alternative chip-style category tabs for horizontal scrolling.
 * Each category is displayed as a standalone chip.
 */
@Composable
fun CategoryChips(
    selectedCategory: Category,
    onCategorySelected: (Category) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = remember { Category.entries }
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        categories.forEach { category ->
            val isSelected = category == selectedCategory
            
            val onClick = remember(category) {
                { onCategorySelected(category) }
            }
            
            CategoryChip(
                text = category.displayName,
                isSelected = isSelected,
                onClick = onClick
            )
        }
    }
}

/**
 * Individual category chip with press animation.
 */
@Composable
private fun CategoryChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    
    // Background color animation
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        },
        animationSpec = tween(AnimationSpecs.SHORT_DURATION),
        label = "chipBg"
    )
    
    // Text color animation
    val textColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.onPrimary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        animationSpec = tween(AnimationSpecs.SHORT_DURATION),
        label = "chipText"
    )
    
    // Scale animation
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = AnimationSpecs.BouncySpring,
        label = "chipScale"
    )
    
    // Elevation animation
    val elevation by animateDpAsState(
        targetValue = if (isSelected) 4.dp else 0.dp,
        animationSpec = tween(AnimationSpecs.SHORT_DURATION),
        label = "chipElevation"
    )
    
    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                shadowElevation = elevation.toPx()
            }
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}
