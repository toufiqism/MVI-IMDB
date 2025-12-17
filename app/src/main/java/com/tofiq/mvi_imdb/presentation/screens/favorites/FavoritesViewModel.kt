package com.tofiq.mvi_imdb.presentation.screens.favorites

import androidx.lifecycle.viewModelScope
import com.tofiq.mvi_imdb.domain.model.Movie
import com.tofiq.mvi_imdb.domain.usecase.GetFavoritesUseCase
import com.tofiq.mvi_imdb.domain.usecase.ToggleFavoriteUseCase
import com.tofiq.mvi_imdb.presentation.base.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Favorites screen following MVI architecture.
 * Handles loading and managing favorite movies.
 * 
 * Requirements: 5.4, 8.1, 8.2
 * - Displays all saved favorite movies from local database
 * - Allows removing movies from favorites
 * - Processes intents and emits immutable states
 */
@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val getFavoritesUseCase: GetFavoritesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : MviViewModel<FavoritesIntent, FavoritesState>() {

    private val _state = MutableStateFlow(FavoritesState.Initial)
    override val state: StateFlow<FavoritesState> = _state.asStateFlow()

    init {
        processIntent(FavoritesIntent.LoadFavorites)
    }

    override fun processIntent(intent: FavoritesIntent) {
        when (intent) {
            is FavoritesIntent.LoadFavorites -> loadFavorites()
            is FavoritesIntent.RemoveFavorite -> removeFavorite(intent.movie)
        }
    }

    /**
     * Load all favorite movies from local database.
     * Requirements: 5.4 - Displays all saved favorite movies
     */
    private fun loadFavorites() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            getFavoritesUseCase().collect { favorites ->
                _state.update {
                    it.copy(
                        favorites = favorites.toImmutableList(),
                        isLoading = false,
                        isEmpty = favorites.isEmpty()
                    )
                }
            }
        }
    }

    /**
     * Remove a movie from favorites.
     * Requirements: 5.3 - Removes movie from favorites when toggled
     */
    private fun removeFavorite(movie: Movie) {
        viewModelScope.launch {
            toggleFavoriteUseCase(movie)
            // The favorites list will be automatically updated via the Flow
        }
    }
}
