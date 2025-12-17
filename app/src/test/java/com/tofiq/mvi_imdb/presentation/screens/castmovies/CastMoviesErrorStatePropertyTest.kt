package com.tofiq.mvi_imdb.presentation.screens.castmovies

import com.tofiq.mvi_imdb.domain.repository.MovieRepository
import com.tofiq.mvi_imdb.domain.usecase.GetCastMoviesUseCase
import com.tofiq.mvi_imdb.util.Resource
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldNotBeEmpty
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.orNull
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
 * **Feature: cast-movies, Property 2: Error state on API failure**
 * **Validates: Requirements 1.5**
 * 
 * For any failed API request, the state SHALL have error set to a non-null message 
 * and isLoading=false.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class CastMoviesErrorStatePropertyTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: MovieRepository
    private lateinit var getCastMoviesUseCase: GetCastMoviesUseCase

    private val personIdArb: Arb<Int> = Arb.int(1..Int.MAX_VALUE)
    private val personNameArb: Arb<String> = Arb.string(1..50)
    private val profilePathArb: Arb<String?> = Arb.string(10..50).orNull()
    private val errorMessageArb: Arb<String> = Arb.string(1..100)

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
    fun `Property 2 - Error state has non-null error message on API failure`() = runTest(testDispatcher) {
        checkAll(100, personIdArb, personNameArb, profilePathArb, errorMessageArb) { personId, personName, profilePath, errorMessage ->
            every { repository.getCastMovies(personId) } returns flowOf(Resource.Error(errorMessage))
            
            getCastMoviesUseCase = GetCastMoviesUseCase(repository)
            val viewModel = CastMoviesViewModel(getCastMoviesUseCase)
            
            viewModel.processIntent(CastMoviesIntent.LoadCastMovies(personId, personName, profilePath))
            advanceUntilIdle()
            
            // Error should be set to the error message
            viewModel.state.value.error shouldNotBe null
            viewModel.state.value.error!!.shouldNotBeEmpty()
        }
    }

    @Test
    fun `Property 2 - Loading is false when error state is set`() = runTest(testDispatcher) {
        checkAll(100, personIdArb, personNameArb, profilePathArb, errorMessageArb) { personId, personName, profilePath, errorMessage ->
            every { repository.getCastMovies(personId) } returns flowOf(Resource.Error(errorMessage))
            
            getCastMoviesUseCase = GetCastMoviesUseCase(repository)
            val viewModel = CastMoviesViewModel(getCastMoviesUseCase)
            
            viewModel.processIntent(CastMoviesIntent.LoadCastMovies(personId, personName, profilePath))
            advanceUntilIdle()
            
            // isLoading should be false when there's an error
            viewModel.state.value.isLoading shouldBe false
        }
    }

    @Test
    fun `Property 2 - Error message matches the API error`() = runTest(testDispatcher) {
        checkAll(100, personIdArb, personNameArb, profilePathArb, errorMessageArb) { personId, personName, profilePath, errorMessage ->
            every { repository.getCastMovies(personId) } returns flowOf(Resource.Error(errorMessage))
            
            getCastMoviesUseCase = GetCastMoviesUseCase(repository)
            val viewModel = CastMoviesViewModel(getCastMoviesUseCase)
            
            viewModel.processIntent(CastMoviesIntent.LoadCastMovies(personId, personName, profilePath))
            advanceUntilIdle()
            
            // Error message should match the API error
            viewModel.state.value.error shouldBe errorMessage
        }
    }
}
