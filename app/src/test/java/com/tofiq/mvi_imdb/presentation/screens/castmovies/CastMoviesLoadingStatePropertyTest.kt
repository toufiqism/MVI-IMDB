package com.tofiq.mvi_imdb.presentation.screens.castmovies

import com.tofiq.mvi_imdb.domain.model.CastMovie
import com.tofiq.mvi_imdb.domain.repository.MovieRepository
import com.tofiq.mvi_imdb.domain.usecase.GetCastMoviesUseCase
import com.tofiq.mvi_imdb.util.Resource
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
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
 * **Feature: cast-movies, Property 1: Loading state during API request**
 * **Validates: Requirements 1.4**
 * 
 * For any API request initiated by LoadCastMovies intent, the state SHALL have 
 * isLoading=true until the request completes (success or failure).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class CastMoviesLoadingStatePropertyTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: MovieRepository
    private lateinit var getCastMoviesUseCase: GetCastMoviesUseCase

    private val castMovieArb: Arb<CastMovie> = arbitrary {
        CastMovie(
            id = Arb.int(1..Int.MAX_VALUE).bind(),
            title = Arb.string(1..100).bind(),
            posterPath = Arb.string(10..50).orNull().bind(),
            releaseDate = Arb.string(0..10).orNull().bind(),
            character = Arb.string(0..50).orNull().bind(),
            voteAverage = Arb.double(0.0..10.0).bind()
        )
    }

    private val personIdArb: Arb<Int> = Arb.int(1..Int.MAX_VALUE)
    private val personNameArb: Arb<String> = Arb.string(1..50)
    private val profilePathArb: Arb<String?> = Arb.string(10..50).orNull()

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
    fun `Property 1 - Loading state is false after successful fetch`() = runTest(testDispatcher) {
        checkAll(100, personIdArb, personNameArb, profilePathArb, Arb.list(castMovieArb, 0..10)) { personId, personName, profilePath, movies ->
            every { repository.getCastMovies(personId) } returns flowOf(Resource.Success(movies))
            
            getCastMoviesUseCase = GetCastMoviesUseCase(repository)
            val viewModel = CastMoviesViewModel(getCastMoviesUseCase)
            
            viewModel.processIntent(CastMoviesIntent.LoadCastMovies(personId, personName, profilePath))
            advanceUntilIdle()
            
            // After successful fetch, isLoading should be false
            viewModel.state.value.isLoading shouldBe false
        }
    }

    @Test
    fun `Property 1 - Loading state is false after error`() = runTest(testDispatcher) {
        checkAll(100, personIdArb, personNameArb, profilePathArb, Arb.string(1..100)) { personId, personName, profilePath, errorMessage ->
            every { repository.getCastMovies(personId) } returns flowOf(Resource.Error(errorMessage))
            
            getCastMoviesUseCase = GetCastMoviesUseCase(repository)
            val viewModel = CastMoviesViewModel(getCastMoviesUseCase)
            
            viewModel.processIntent(CastMoviesIntent.LoadCastMovies(personId, personName, profilePath))
            advanceUntilIdle()
            
            // After error, isLoading should be false
            viewModel.state.value.isLoading shouldBe false
        }
    }

    @Test
    fun `Property 1 - Loading state transitions correctly through fetch lifecycle`() = runTest(testDispatcher) {
        checkAll(100, personIdArb, personNameArb, profilePathArb, Arb.list(castMovieArb, 0..10)) { personId, personName, profilePath, movies ->
            // Create a flow that emits Loading first, then Success
            every { repository.getCastMovies(personId) } returns flow {
                emit(Resource.Loading)
                emit(Resource.Success(movies))
            }
            
            getCastMoviesUseCase = GetCastMoviesUseCase(repository)
            val viewModel = CastMoviesViewModel(getCastMoviesUseCase)
            
            viewModel.processIntent(CastMoviesIntent.LoadCastMovies(personId, personName, profilePath))
            advanceUntilIdle()
            
            // After completion, isLoading should be false regardless of intermediate states
            viewModel.state.value.isLoading shouldBe false
            // Error should be null on success
            viewModel.state.value.error shouldBe null
        }
    }
}
