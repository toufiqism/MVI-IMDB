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
 * **Feature: cast-movies, Property 3: Actor info preservation**
 * **Validates: Requirements 2.1, 2.2**
 * 
 * For any LoadCastMovies intent with personName and profilePath, the resulting state 
 * SHALL contain the same personName and profilePath values.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class CastMoviesActorInfoPropertyTest {

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
    fun `Property 3 - Actor name is preserved in state after successful load`() = runTest(testDispatcher) {
        checkAll(100, personIdArb, personNameArb, profilePathArb, Arb.list(castMovieArb, 0..10)) { personId, personName, profilePath, movies ->
            every { repository.getCastMovies(personId) } returns flowOf(Resource.Success(movies))
            
            getCastMoviesUseCase = GetCastMoviesUseCase(repository)
            val viewModel = CastMoviesViewModel(getCastMoviesUseCase)
            
            viewModel.processIntent(CastMoviesIntent.LoadCastMovies(personId, personName, profilePath))
            advanceUntilIdle()
            
            // Actor name should be preserved in state
            viewModel.state.value.personName shouldBe personName
        }
    }

    @Test
    fun `Property 3 - Profile path is preserved in state after successful load`() = runTest(testDispatcher) {
        checkAll(100, personIdArb, personNameArb, profilePathArb, Arb.list(castMovieArb, 0..10)) { personId, personName, profilePath, movies ->
            every { repository.getCastMovies(personId) } returns flowOf(Resource.Success(movies))
            
            getCastMoviesUseCase = GetCastMoviesUseCase(repository)
            val viewModel = CastMoviesViewModel(getCastMoviesUseCase)
            
            viewModel.processIntent(CastMoviesIntent.LoadCastMovies(personId, personName, profilePath))
            advanceUntilIdle()
            
            // Profile path should be preserved in state
            viewModel.state.value.profilePath shouldBe profilePath
        }
    }

    @Test
    fun `Property 3 - Actor info is preserved even on error`() = runTest(testDispatcher) {
        checkAll(100, personIdArb, personNameArb, profilePathArb, Arb.string(1..100)) { personId, personName, profilePath, errorMessage ->
            every { repository.getCastMovies(personId) } returns flowOf(Resource.Error(errorMessage))
            
            getCastMoviesUseCase = GetCastMoviesUseCase(repository)
            val viewModel = CastMoviesViewModel(getCastMoviesUseCase)
            
            viewModel.processIntent(CastMoviesIntent.LoadCastMovies(personId, personName, profilePath))
            advanceUntilIdle()
            
            // Actor info should be preserved even when there's an error
            viewModel.state.value.personName shouldBe personName
            viewModel.state.value.profilePath shouldBe profilePath
        }
    }

    @Test
    fun `Property 3 - Person ID is preserved in state`() = runTest(testDispatcher) {
        checkAll(100, personIdArb, personNameArb, profilePathArb, Arb.list(castMovieArb, 0..10)) { personId, personName, profilePath, movies ->
            every { repository.getCastMovies(personId) } returns flowOf(Resource.Success(movies))
            
            getCastMoviesUseCase = GetCastMoviesUseCase(repository)
            val viewModel = CastMoviesViewModel(getCastMoviesUseCase)
            
            viewModel.processIntent(CastMoviesIntent.LoadCastMovies(personId, personName, profilePath))
            advanceUntilIdle()
            
            // Person ID should be preserved in state
            viewModel.state.value.personId shouldBe personId
        }
    }
}
