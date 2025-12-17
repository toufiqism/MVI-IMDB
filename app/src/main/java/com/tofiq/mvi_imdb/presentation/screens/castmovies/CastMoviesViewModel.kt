package com.tofiq.mvi_imdb.presentation.screens.castmovies

import androidx.lifecycle.viewModelScope
import com.tofiq.mvi_imdb.domain.usecase.GetCastMoviesUseCase
import com.tofiq.mvi_imdb.presentation.base.MviViewModel
import com.tofiq.mvi_imdb.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Cast Movies screen following MVI architecture.
 * Handles loading movies for a specific actor/person.
 * 
 * Requirements: 1.2, 1.4, 1.5
 * - Fetches movie credits from TMDB API using Person_ID
 * - Displays loading indicator during API request
 * - Displays error message with retry option on failure
 */
@HiltViewModel
class CastMoviesViewModel @Inject constructor(
    private val getCastMoviesUseCase: GetCastMoviesUseCase
) : MviViewModel<CastMoviesIntent, CastMoviesState>() {

    private val _state = MutableStateFlow(CastMoviesState.Initial)
    override val state: StateFlow<CastMoviesState> = _state.asStateFlow()

    override fun processIntent(intent: CastMoviesIntent) {
        when (intent) {
            is CastMoviesIntent.LoadCastMovies -> loadCastMovies(
                personId = intent.personId,
                personName = intent.personName,
                profilePath = intent.profilePath
            )
            is CastMoviesIntent.Retry -> retry()
        }
    }

    /**
     * Load movies for the given person/actor.
     * Requirements: 1.2 - Fetch movie credits from TMDB API using Person_ID
     * Requirements: 1.4 - Display loading indicator during API request
     */
    private fun loadCastMovies(personId: Int, personName: String, profilePath: String?) {
        viewModelScope.launch {
            _state.update { 
                it.copy(
                    isLoading = true, 
                    error = null, 
                    personId = personId,
                    personName = personName,
                    profilePath = profilePath
                ) 
            }
            
            getCastMoviesUseCase(personId).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true, error = null) }
                    }
                    is Resource.Success -> {
                        _state.update {
                            it.copy(
                                movies = resource.data.toImmutableList(),
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
     * Retry loading after an error.
     * Requirements: 1.5 - Display error message with retry option
     */
    private fun retry() {
        val currentState = state.value
        val personId = currentState.personId ?: return
        _state.update { it.copy(error = null) }
        loadCastMovies(personId, currentState.personName, currentState.profilePath)
    }
}
