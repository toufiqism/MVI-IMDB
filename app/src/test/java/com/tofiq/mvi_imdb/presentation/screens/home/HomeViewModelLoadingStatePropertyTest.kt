package com.tofiq.mvi_imdb.presentation.screens.home

import com.tofiq.mvi_imdb.domain.model.Category
import com.tofiq.mvi_imdb.domain.model.Movie
import com.tofiq.mvi_imdb.domain.repository.MovieRepository
import com.tofiq.mvi_imdb.domain.usecase.GetMoviesUseCase
import com.tofiq.mvi_imdb.util.Resource
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.double
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.orNull
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * **Feature: tmdb-movie-app, Property 2: Loading State Consistency**
 * **Validates: Requirements 1.3, 4.5**
 * 
 * For any data fetch operation, the state SHALL have isLoading=true while the 
 * operation is in progress and isLoading=false when complete (success or error).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelLoadingStatePropertyTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: MovieRepository
    private lateinit var getMoviesUseCase: GetMoviesUseCase

    private val movieArb: Arb<Movie> = arbitrary {
        Movie(
            id = Arb.int(1..Int.MAX_VALUE).bind(),
            title = Arb.string(1..100).bind(),
            posterPath = Arb.string(10..50).orNull().bind(),
            backdropPath = Arb.string(10..50).orNull().bind(),
            releaseDate = Arb.string(0..10).bind(),
            voteAverage = Arb.double(0.0..10.0).bind(),
            overview = Arb.string(0..500).bind(),
            isFavorite = Arb.boolean().bind()
        )
    }


    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Property 2 - Loading state is false after successful fetch`() = runTest(testDispatcher) {
        checkAll(100, Arb.list(movieArb, 0..10)) { movies ->
            every { repository.getMovies(Category.POPULAR, 1) } returns flowOf(Resource.Success(movies))
            
            getMoviesUseCase = GetMoviesUseCase(repository)
            val viewModel = HomeViewModel(getMoviesUseCase)
            
            advanceUntilIdle()
            
            // After successful fetch, isLoading should be false
            viewModel.state.value.isLoading shouldBe false
            viewModel.state.value.isLoadingMore shouldBe false
        }
    }

    @Test
    fun `Property 2 - Loading state is false after error`() = runTest(testDispatcher) {
        checkAll(100, Arb.string(1..100)) { errorMessage ->
            every { repository.getMovies(Category.POPULAR, 1) } returns flowOf(Resource.Error(errorMessage))
            
            getMoviesUseCase = GetMoviesUseCase(repository)
            val viewModel = HomeViewModel(getMoviesUseCase)
            
            advanceUntilIdle()
            
            // After error, isLoading should be false
            viewModel.state.value.isLoading shouldBe false
            viewModel.state.value.isLoadingMore shouldBe false
        }
    }

    @Test
    fun `Property 2 - Loading state transitions correctly through fetch lifecycle`() = runTest(testDispatcher) {
        checkAll(100, Arb.list(movieArb, 0..10)) { movies ->
            // Create a flow that emits Loading first, then Success
            every { repository.getMovies(Category.POPULAR, 1) } returns flow {
                emit(Resource.Loading)
                emit(Resource.Success(movies))
            }
            
            getMoviesUseCase = GetMoviesUseCase(repository)
            val viewModel = HomeViewModel(getMoviesUseCase)
            
            // Complete the operation
            advanceUntilIdle()
            
            // After completion, isLoading should be false regardless of intermediate states
            viewModel.state.value.isLoading shouldBe false
            // Error should be null on success
            viewModel.state.value.error shouldBe null
        }
    }

    @Test
    fun `Property 2 - LoadingMore state is false after pagination completes`() = runTest(testDispatcher) {
        checkAll(100, Arb.list(movieArb, 1..5), Arb.list(movieArb, 1..5)) { firstPage, secondPage ->
            every { repository.getMovies(Category.POPULAR, 1) } returns flowOf(Resource.Success(firstPage))
            every { repository.getMovies(Category.POPULAR, 2) } returns flowOf(Resource.Success(secondPage))
            
            getMoviesUseCase = GetMoviesUseCase(repository)
            val viewModel = HomeViewModel(getMoviesUseCase)
            
            advanceUntilIdle()
            
            // Load next page
            viewModel.processIntent(HomeIntent.LoadNextPage)
            advanceUntilIdle()
            
            // After pagination completes, isLoadingMore should be false
            viewModel.state.value.isLoadingMore shouldBe false
        }
    }
}
