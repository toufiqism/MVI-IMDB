package com.tofiq.mvi_imdb.presentation.screens.home

import androidx.lifecycle.viewModelScope
import com.tofiq.mvi_imdb.domain.model.Category
import com.tofiq.mvi_imdb.domain.usecase.GetMoviesUseCase
import com.tofiq.mvi_imdb.presentation.base.MviViewModel
import com.tofiq.mvi_imdb.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Home screen following MVI architecture.
 * Handles movie loading, pagination, and category switching.
 * 
 * Requirements: 1.1, 1.2, 2.2, 8.1, 8.2
 * - Displays popular movies on launch
 * - Supports pagination for loading more movies
 * - Allows switching between categories
 * - Processes intents and emits immutable states
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getMoviesUseCase: GetMoviesUseCase
) : MviViewModel<HomeIntent, HomeState>() {

    private val _state = MutableStateFlow(HomeState.Initial)
    override val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        processIntent(HomeIntent.LoadMovies)
    }

    override fun processIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.LoadMovies -> loadMovies()
            is HomeIntent.SelectCategory -> selectCategory(intent.category)
            is HomeIntent.LoadNextPage -> loadNextPage()
            is HomeIntent.Retry -> retry()
        }
    }


    /**
     * Load movies for the current category from page 1.
     * Clears existing movies and starts fresh.
     */
    private fun loadMovies() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            
            getMoviesUseCase(state.value.selectedCategory, 1).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true, error = null) }
                    }
                    is Resource.Success -> {
                        _state.update {
                            it.copy(
                                movies = resource.data,
                                isLoading = false,
                                error = null,
                                currentPage = 1,
                                hasMorePages = resource.data.isNotEmpty()
                            )
                        }
                    }
                    is Resource.Error -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = resource.message,
                                movies = resource.data ?: it.movies
                            )
                        }
                    }
                }
            }
        }
    }

    /**
     * Select a different category and load its movies.
     * Requirements: 2.2, 2.4 - Category selection updates state and triggers fetch
     */
    private fun selectCategory(category: Category) {
        if (category == state.value.selectedCategory) return
        
        _state.update {
            it.copy(
                selectedCategory = category,
                movies = emptyList(),
                currentPage = 1,
                hasMorePages = true,
                error = null
            )
        }
        loadMovies()
    }

    /**
     * Load the next page of movies for pagination.
     * Requirements: 1.2 - Pagination preserves existing movies
     */
    private fun loadNextPage() {
        val currentState = state.value
        if (currentState.isLoading || currentState.isLoadingMore || !currentState.hasMorePages) return
        
        viewModelScope.launch {
            _state.update { it.copy(isLoadingMore = true) }
            
            val nextPage = currentState.currentPage + 1
            getMoviesUseCase(currentState.selectedCategory, nextPage).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoadingMore = true) }
                    }
                    is Resource.Success -> {
                        _state.update {
                            val existingIds = it.movies.map { movie -> movie.id }.toSet()
                            val newMovies = resource.data.filter { movie -> movie.id !in existingIds }
                            it.copy(
                                movies = it.movies + newMovies,
                                isLoadingMore = false,
                                currentPage = nextPage,
                                hasMorePages = resource.data.isNotEmpty()
                            )
                        }
                    }
                    is Resource.Error -> {
                        _state.update {
                            it.copy(
                                isLoadingMore = false,
                                error = resource.message
                            )
                        }
                    }
                }
            }
        }
    }

    /**
     * Retry loading after an error.
     */
    private fun retry() {
        _state.update { it.copy(error = null) }
        loadMovies()
    }
}
