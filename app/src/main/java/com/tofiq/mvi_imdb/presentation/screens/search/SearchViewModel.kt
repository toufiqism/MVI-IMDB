package com.tofiq.mvi_imdb.presentation.screens.search

import androidx.lifecycle.viewModelScope
import com.tofiq.mvi_imdb.domain.usecase.SearchMoviesUseCase
import com.tofiq.mvi_imdb.presentation.base.MviViewModel
import com.tofiq.mvi_imdb.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Search screen following MVI architecture.
 * Handles search query processing with debounce and pagination.
 * 
 * Requirements: 3.2, 3.3, 3.4, 3.5
 * - Searches movies when query is at least 2 characters
 * - Debounces search by 300ms
 * - Displays empty state when no results found
 * - Clears results when search is cleared
 */
@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchMoviesUseCase: SearchMoviesUseCase
) : MviViewModel<SearchIntent, SearchState>() {

    private val _state = MutableStateFlow(SearchState.Initial)
    override val state: StateFlow<SearchState> = _state.asStateFlow()

    private val queryFlow = MutableStateFlow("")
    private var searchJob: Job? = null

    init {
        setupQueryDebounce()
    }

    /**
     * Set up debounced search query processing.
     * Requirements: 3.2 - Search triggers after 300ms debounce with at least 2 characters
     */
    private fun setupQueryDebounce() {
        queryFlow
            .debounce(300)
            .distinctUntilChanged()
            .filter { it.length >= 2 }
            .onEach { query -> executeSearch(query) }
            .launchIn(viewModelScope)
    }

    override fun processIntent(intent: SearchIntent) {
        when (intent) {
            is SearchIntent.UpdateQuery -> updateQuery(intent.query)
            is SearchIntent.ClearSearch -> clearSearch()
            is SearchIntent.LoadNextPage -> loadNextPage()
        }
    }

    /**
     * Update the search query.
     * Requirements: 3.2 - Query must be at least 2 characters to trigger search
     */
    private fun updateQuery(query: String) {
        _state.update { it.copy(query = query) }
        queryFlow.value = query
        
        // If query is less than 2 characters, clear results but don't show empty state
        if (query.length < 2) {
            searchJob?.cancel()
            _state.update { 
                it.copy(
                    movies = emptyList(),
                    isLoading = false,
                    isEmpty = false,
                    error = null,
                    currentPage = 1,
                    hasMorePages = true
                )
            }
        }
    }

    /**
     * Execute the search query.
     * Requirements: 3.3 - Display matching movies in a list format
     */
    private fun executeSearch(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _state.update { 
                it.copy(
                    isLoading = true, 
                    error = null,
                    currentPage = 1,
                    hasMorePages = true
                ) 
            }
            
            searchMoviesUseCase(query, 1).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true, error = null) }
                    }
                    is Resource.Success -> {
                        val movies = resource.data
                        _state.update {
                            it.copy(
                                movies = movies,
                                isLoading = false,
                                error = null,
                                isEmpty = movies.isEmpty(),
                                currentPage = 1,
                                hasMorePages = movies.isNotEmpty()
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
     * Clear the search input and results.
     * Requirements: 3.5 - Clear search resets state to initial
     */
    private fun clearSearch() {
        searchJob?.cancel()
        queryFlow.value = ""
        _state.update { SearchState.Initial }
    }

    /**
     * Load the next page of search results.
     * Requirements: 3.3 - Supports pagination for search results
     */
    private fun loadNextPage() {
        val currentState = state.value
        if (currentState.isLoading || currentState.isLoadingMore || !currentState.hasMorePages) return
        if (currentState.query.length < 2) return
        
        viewModelScope.launch {
            _state.update { it.copy(isLoadingMore = true) }
            
            val nextPage = currentState.currentPage + 1
            searchMoviesUseCase(currentState.query, nextPage).collect { resource ->
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
}
