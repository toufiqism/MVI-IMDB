package com.tofiq.mvi_imdb.presentation.screens

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.rememberNavBackStack
import com.tofiq.mvi_imdb.data.local.SettingsDataStore
import com.tofiq.mvi_imdb.presentation.navigation.AppNavigation
import com.tofiq.mvi_imdb.presentation.navigation.FavoritesRoute
import com.tofiq.mvi_imdb.presentation.navigation.HomeRoute
import com.tofiq.mvi_imdb.presentation.navigation.NavRoute
import com.tofiq.mvi_imdb.presentation.navigation.SearchRoute
import com.tofiq.mvi_imdb.presentation.navigation.SettingsRoute
import com.tofiq.mvi_imdb.ui.theme.AnimationSpecs
import com.tofiq.mvi_imdb.ui.theme.VibrantBlue
import com.tofiq.mvi_imdb.ui.theme.VibrantGreen
import com.tofiq.mvi_imdb.ui.theme.VibrantPink
import com.tofiq.mvi_imdb.ui.theme.VibrantPurple

/**
 * Bottom navigation item data class.
 * Uses stable fields for optimal recomposition.
 */
private data class BottomNavItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val route: NavRoute,
    val accentColor: Color
)

/**
 * Main app composable with enhanced bottom navigation.
 * 
 * Features:
 * - Animated icon transitions with scale and bounce
 * - Gradient indicator for selected items
 * - Glass morphism effect on navigation bar
 * - Configuration change survival with rememberSaveable
 * 
 * Optimized for:
 * - Recomposition stability
 * - Configuration change handling
 * - Smooth 60fps animations
 */
@Composable
fun MovieApp(
    settingsDataStore: SettingsDataStore
) {
    val backStack = rememberNavBackStack(HomeRoute)
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }

    val bottomNavItems = listOf(
        BottomNavItem(
            title = "Home",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            route = HomeRoute,
            accentColor = VibrantBlue
        ),
        BottomNavItem(
            title = "Search",
            selectedIcon = Icons.Filled.Search,
            unselectedIcon = Icons.Outlined.Search,
            route = SearchRoute,
            accentColor = VibrantPurple
        ),
        BottomNavItem(
            title = "Favorites",
            selectedIcon = Icons.Filled.Favorite,
            unselectedIcon = Icons.Outlined.FavoriteBorder,
            route = FavoritesRoute,
            accentColor = VibrantPink
        ),
        BottomNavItem(
            title = "Settings",
            selectedIcon = Icons.Filled.Settings,
            unselectedIcon = Icons.Outlined.Settings,
            route = SettingsRoute,
            accentColor = VibrantGreen
        )
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            EnhancedNavigationBar(
                items = bottomNavItems,
                selectedIndex = selectedTabIndex,
                onItemSelected = { index, item ->
                    if (selectedTabIndex != index) {
                        selectedTabIndex = index
                        // Clear the back stack and navigate to the selected tab
                        backStack.clear()
                        backStack.add(item.route)
                    }
                }
            )
        }
    ) { innerPadding ->
        AppNavigation(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            backStack = backStack,
            onNavigate = { route -> backStack.add(route) },
            onBack = { backStack.removeLastOrNull() },
            settingsDataStore = settingsDataStore
        )
    }
}

/**
 * Enhanced navigation bar with professional animations.
 */
@Composable
private fun EnhancedNavigationBar(
    items: List<BottomNavItem>,
    selectedIndex: Int,
    onItemSelected: (Int, BottomNavItem) -> Unit
) {
    NavigationBar(
        modifier = Modifier
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            )
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 0.dp
    ) {
        items.forEachIndexed { index, item ->
            val isSelected = selectedIndex == index
            
            // Animate icon scale with bouncy spring
            val iconScale by animateFloatAsState(
                targetValue = if (isSelected) 1.15f else 1f,
                animationSpec = spring(
                    dampingRatio = 0.5f,
                    stiffness = 300f
                ),
                label = "navIconScale"
            )
            
            // Animate icon offset for "pop" effect
            val iconOffset by animateDpAsState(
                targetValue = if (isSelected) (-4).dp else 0.dp,
                animationSpec = spring(
                    dampingRatio = 0.6f,
                    stiffness = 400f
                ),
                label = "navIconOffset"
            )
            
            // Animate icon rotation for subtle effect
            val iconRotation by animateFloatAsState(
                targetValue = if (isSelected) 0f else 0f,
                animationSpec = tween(AnimationSpecs.SHORT_DURATION),
                label = "navIconRotation"
            )
            
            // Animate alpha for unselected items
            val iconAlpha by animateFloatAsState(
                targetValue = if (isSelected) 1f else 0.7f,
                animationSpec = tween(AnimationSpecs.SHORT_DURATION),
                label = "navIconAlpha"
            )
            
            NavigationBarItem(
                selected = isSelected,
                onClick = { onItemSelected(index, item) },
                icon = {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        // Selection indicator
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(
                                        brush = Brush.radialGradient(
                                            colors = listOf(
                                                item.accentColor.copy(alpha = 0.2f),
                                                Color.Transparent
                                            )
                                        ),
                                        shape = CircleShape
                                    )
                            )
                        }
                        
                        Icon(
                            imageVector = if (isSelected) {
                                item.selectedIcon
                            } else {
                                item.unselectedIcon
                            },
                            contentDescription = item.title,
                            modifier = Modifier
                                .size(24.dp)
                                .offset(y = iconOffset)
                                .graphicsLayer {
                                    scaleX = iconScale
                                    scaleY = iconScale
                                    rotationZ = iconRotation
                                    alpha = iconAlpha
                                },
                            tint = if (isSelected) {
                                item.accentColor
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                },
                label = {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) {
                            item.accentColor
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = VibrantBlue,
                    selectedTextColor = VibrantBlue,
                    indicatorColor = Color.Transparent,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}
