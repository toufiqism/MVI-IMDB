package com.tofiq.mvi_imdb.presentation.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.tofiq.mvi_imdb.domain.model.Movie
import com.tofiq.mvi_imdb.domain.repository.MovieRepository
import com.tofiq.mvi_imdb.domain.usecase.GetMovieDetailUseCase
import com.tofiq.mvi_imdb.domain.usecase.ToggleFavoriteUseCase
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
 * ViewModel for the Detail screen following MVI architecture.
 * Handles movie detail loading and favorite toggle functionality.
 * 
 * Requirements: 4.1, 5.1, 5.2, 5.3
 * - Navigates to and displays movie's detailed information
 * - Displays a favorite toggle button
 * - Persists movie to favorites when toggled on
 * - Removes movie from favorites when toggled off
 */
@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getMovieDetailUseCase: GetMovieDetailUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val repository: MovieRepository,
    savedStateHandle: SavedStateHandle
) : MviViewModel<DetailIntent, DetailState>() {

    private val _state = MutableStateFlow(DetailState.Initial)
    override val state: StateFlow<DetailState> = _state.asStateFlow()

    private val movieId: Int = savedStateHandle.get<Int>("movieId") ?: -1

    init {
        if (movieId != -1) {
            processIntent(DetailIntent.LoadDetail(movieId))
        }
    }

    override fun processIntent(intent: DetailIntent) {
        when (intent) {
            is DetailIntent.LoadDetail -> loadMovieDetail(intent.movieId)
            is DetailIntent.ToggleFavorite -> toggleFavorite()
            is DetailIntent.Retry -> retry()
        }
    }


    /**
     * Load movie details for the given movie ID.
     * Requirements: 4.1 - WHEN the user taps on a movie item THEN the Movie_Detail_Screen 
     * SHALL navigate to and display the movie's detailed information
     */
    private fun loadMovieDetail(movieId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null, movieId = movieId) }
            
            getMovieDetailUseCase(movieId).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true, error = null) }
                    }
                    is Resource.Success -> {
                        // Check if movie is favorite and update the detail
                        val isFavorite = repository.isFavorite(movieId)
                        val movieDetail = resource.data.copy(isFavorite = isFavorite)
                        _state.update {
                            it.copy(
                                movieDetail = movieDetail,
                                isLoading = false,
                                error = null
                            )
                        }
                    }
                    is Resource.Error -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = resource.message
                            )
                        }
                    }
                }
            }
        }
    }

    /**
     * Toggle the favorite status of the current movie.
     * Requirements: 5.2, 5.3 - Toggle favorite adds/removes movie from favorites
     */
    private fun toggleFavorite() {
        val currentDetail = state.value.movieDetail ?: return
        
        viewModelScope.launch {
            // Create a Movie from MovieDetail for the toggle operation
            val movie = Movie(
                id = currentDetail.id,
                title = currentDetail.title,
                posterPath = currentDetail.posterPath,
                backdropPath = currentDetail.backdropPath,
                releaseDate = currentDetail.releaseDate,
                voteAverage = currentDetail.voteAverage,
                overview = currentDetail.overview,
                isFavorite = currentDetail.isFavorite,
                releaseYear = currentDetail.releaseYear,
                formattedRating = currentDetail.formattedRating
            )
            
            toggleFavoriteUseCase(movie)
            
            // Update state with new favorite status
            val newFavoriteStatus = !currentDetail.isFavorite
            _state.update {
                it.copy(
                    movieDetail = currentDetail.copy(isFavorite = newFavoriteStatus)
                )
            }
        }
    }

    /**
     * Retry loading after an error.
     * Requirements: 4.5 - IF movie details fail to load THEN the Movie_Detail_Screen 
     * SHALL display an error state with retry option
     */
    private fun retry() {
        val currentMovieId = state.value.movieId ?: return
        _state.update { it.copy(error = null) }
        loadMovieDetail(currentMovieId)
    }
}
