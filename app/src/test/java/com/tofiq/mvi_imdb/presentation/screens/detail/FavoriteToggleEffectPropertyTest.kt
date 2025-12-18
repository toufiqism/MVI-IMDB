package com.tofiq.mvi_imdb.presentation.screens.detail

import com.tofiq.mvi_imdb.domain.model.Cast
import com.tofiq.mvi_imdb.domain.model.Movie
import com.tofiq.mvi_imdb.domain.model.MovieDetail
import com.tofiq.mvi_imdb.domain.repository.MovieRepository
import com.tofiq.mvi_imdb.domain.usecase.GetMovieDetailUseCase
import com.tofiq.mvi_imdb.domain.usecase.ToggleFavoriteUseCase
import com.tofiq.mvi_imdb.util.Resource
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.double
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.orNull
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Test
import androidx.lifecycle.SavedStateHandle

/**
 * **Feature: mvi-effects, Property 4: Favorite Toggle Effect Emission**
 * **Validates: Requirements 5.4**
 * 
 * For any favorite toggle intent processed by DetailViewModel, the ViewModel 
 * SHALL emit a ShowMessage effect confirming the action.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class FavoriteToggleEffectPropertyTest {

    private val castArb: Arb<Cast> = arbitrary {
        Cast(
            id = Arb.int(1..Int.MAX_VALUE).bind(),
            name = Arb.string(1..50).bind(),
            character = Arb.string(1..50).bind(),
            profilePath = Arb.string(10..50).orNull().bind()
        )
    }

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

    private val movieDetailArb: Arb<MovieDetail> = arbitrary {
        MovieDetail(
            id = Arb.int(1..Int.MAX_VALUE).bind(),
            title = Arb.string(1..100).bind(),
            posterPath = Arb.string(10..50).orNull().bind(),
            backdropPath = Arb.string(10..50).orNull().bind(),
            releaseDate = Arb.string(0..10).bind(),
            runtime = Arb.int(1..300).orNull().bind(),
            genres = Arb.list(Arb.string(1..20), 0..5).bind().toImmutableList(),
            voteAverage = Arb.double(0.0..10.0).bind(),
            voteCount = Arb.int(0..Int.MAX_VALUE).bind(),
            overview = Arb.string(0..500).bind(),
            cast = Arb.list(castArb, 0..10).bind().toImmutableList(),
            similarMovies = Arb.list(movieArb, 0..10).bind().toImmutableList(),
            isFavorite = Arb.boolean().bind()
        )
    }

    private fun createViewModel(
        movieDetail: MovieDetail,
        movieId: Int = movieDetail.id
    ): DetailViewModel {
        val getMovieDetailUseCase: GetMovieDetailUseCase = mockk()
        val toggleFavoriteUseCase: ToggleFavoriteUseCase = mockk()
        val repository: MovieRepository = mockk()
        val savedStateHandle = SavedStateHandle(mapOf("movieId" to movieId))

        coEvery { getMovieDetailUseCase(movieId) } returns flowOf(Resource.Success(movieDetail))
        coEvery { repository.isFavorite(movieId) } returns movieDetail.isFavorite
        coEvery { toggleFavoriteUseCase(any()) } returns Unit

        return DetailViewModel(
            getMovieDetailUseCase = getMovieDetailUseCase,
            toggleFavoriteUseCase = toggleFavoriteUseCase,
            repository = repository,
            savedStateHandle = savedStateHandle
        )
    }

    @Test
    fun `Property 4 - favorite toggle emits ShowMessage effect`() = runTest {
        checkAll(100, movieDetailArb) { movieDetail ->
            val viewModel = createViewModel(movieDetail)
            val effects = mutableListOf<DetailEffect>()

            val job = launch {
                viewModel.effect.toList(effects)
            }

            // Wait for initial load to complete
            advanceUntilIdle()

            // Toggle favorite
            viewModel.processIntent(DetailIntent.ToggleFavorite)
            advanceUntilIdle()

            // Verify ShowMessage effect was emitted
            val showMessageEffects = effects.filterIsInstance<DetailEffect.ShowMessage>()
            showMessageEffects.size shouldBe 1
            
            // Verify the message contains the movie title
            val message = showMessageEffects.first().message
            (message.contains(movieDetail.title)) shouldBe true

            job.cancel()
        }
    }

    @Test
    fun `Property 4 - adding to favorites shows added message`() = runTest {
        checkAll(100, movieDetailArb) { baseMovieDetail ->
            // Ensure movie is NOT a favorite initially
            val movieDetail = baseMovieDetail.copy(isFavorite = false)
            val viewModel = createViewModel(movieDetail)
            val effects = mutableListOf<DetailEffect>()

            val job = launch {
                viewModel.effect.toList(effects)
            }

            // Wait for initial load
            advanceUntilIdle()

            // Toggle favorite (should add to favorites)
            viewModel.processIntent(DetailIntent.ToggleFavorite)
            advanceUntilIdle()

            // Verify message indicates added to favorites
            val showMessageEffects = effects.filterIsInstance<DetailEffect.ShowMessage>()
            showMessageEffects.size shouldBe 1
            showMessageEffects.first().message.contains("added to favorites") shouldBe true

            job.cancel()
        }
    }

    @Test
    fun `Property 4 - removing from favorites shows removed message`() = runTest {
        checkAll(100, movieDetailArb) { baseMovieDetail ->
            // Ensure movie IS a favorite initially
            val movieDetail = baseMovieDetail.copy(isFavorite = true)
            val viewModel = createViewModel(movieDetail)
            val effects = mutableListOf<DetailEffect>()

            val job = launch {
                viewModel.effect.toList(effects)
            }

            // Wait for initial load
            advanceUntilIdle()

            // Toggle favorite (should remove from favorites)
            viewModel.processIntent(DetailIntent.ToggleFavorite)
            advanceUntilIdle()

            // Verify message indicates removed from favorites
            val showMessageEffects = effects.filterIsInstance<DetailEffect.ShowMessage>()
            showMessageEffects.size shouldBe 1
            showMessageEffects.first().message.contains("removed from favorites") shouldBe true

            job.cancel()
        }
    }
}
