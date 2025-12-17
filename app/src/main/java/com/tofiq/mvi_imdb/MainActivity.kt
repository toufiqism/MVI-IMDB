package com.tofiq.mvi_imdb

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.rememberNavBackStack
import com.tofiq.mvi_imdb.presentation.navigation.AppNavigation
import com.tofiq.mvi_imdb.presentation.navigation.FavoritesRoute
import com.tofiq.mvi_imdb.presentation.navigation.HomeRoute
import com.tofiq.mvi_imdb.presentation.navigation.NavRoute
import com.tofiq.mvi_imdb.presentation.navigation.SearchRoute
import com.tofiq.mvi_imdb.ui.theme.MVIIMDBTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main activity for the Movie App.
 *
 * Requirements: 2.1, 7.1
 * - Displays category tabs for navigation (via bottom navigation)
 * - Uses Navigation3 for screen transitions
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MVIIMDBTheme {
                MovieApp()
            }
        }
    }
}

/**
 * Bottom navigation item data class.
 */
private data class BottomNavItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val route: NavRoute
)


/**
 * Main app composable with bottom navigation and Navigation3 content.
 */
@Composable
private fun MovieApp() {
    val backStack = rememberNavBackStack(HomeRoute)
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }

    val bottomNavItems = listOf(
        BottomNavItem(
            title = "Home",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            route = HomeRoute
        ),
        BottomNavItem(
            title = "Search",
            selectedIcon = Icons.Filled.Search,
            unselectedIcon = Icons.Outlined.Search,
            route = SearchRoute
        ),
        BottomNavItem(
            title = "Favorites",
            selectedIcon = Icons.Filled.Favorite,
            unselectedIcon = Icons.Outlined.FavoriteBorder,
            route = FavoritesRoute
        )
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedTabIndex == index,
                        onClick = {
                            if (selectedTabIndex != index) {
                                selectedTabIndex = index
                                // Clear the back stack and navigate to the selected tab
                                backStack.clear()
                                backStack.add(item.route)
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = if (selectedTabIndex == index) {
                                    item.selectedIcon
                                } else {
                                    item.unselectedIcon
                                },
                                contentDescription = item.title
                            )
                        },
                        label = { Text(text = item.title) }
                    )
                }
            }
        }
    ) { innerPadding ->
        AppNavigation(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            backStack = backStack,
            onNavigate = { route -> backStack.add(route) },
            onBack = { backStack.removeLastOrNull() }
        )
    }
}
