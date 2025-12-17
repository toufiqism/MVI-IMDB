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
import io.kotest.property.arbitrary.enum
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.orNull
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
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
 * **Feature: tmdb-movie-app, Property 5: Category Selection Updates State**
 * **Validates: Requirements 2.2, 2.4**
 * 
 * For any category selection, the HomeState SHALL update selectedCategory to match 
 * the selected category and trigger a new data fetch.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelCategoryPropertyTest {

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

    private val categoryArb: Arb<Category> = Arb.enum<Category>()


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
    fun `Property 5 - Category selection updates selectedCategory in state`() = runTest(testDispatcher) {
        checkAll(100, categoryArb, Arb.list(movieArb, 0..10)) { targetCategory, movies ->
            // Setup repository to return movies for any category
            every { repository.getMovies(any(), any()) } returns flowOf(Resource.Success(movies))
            
            getMoviesUseCase = GetMoviesUseCase(repository)
            val viewModel = HomeViewModel(getMoviesUseCase)
            
            advanceUntilIdle()
            
            // Select the target category
            viewModel.processIntent(HomeIntent.SelectCategory(targetCategory))
            advanceUntilIdle()
            
            // Verify selectedCategory matches the selected category
            viewModel.state.value.selectedCategory shouldBe targetCategory
        }
    }

    @Test
    fun `Property 5 - Category selection triggers new data fetch`() = runTest(testDispatcher) {
        checkAll(100, categoryArb, Arb.list(movieArb, 0..10), Arb.list(movieArb, 0..10)) { targetCategory, initialMovies, newMovies ->
            // Skip if target category is POPULAR (same as initial)
            if (targetCategory == Category.POPULAR) return@checkAll
            
            repository = mockk()
            every { repository.getMovies(Category.POPULAR, 1) } returns flowOf(Resource.Success(initialMovies))
            every { repository.getMovies(targetCategory, 1) } returns flowOf(Resource.Success(newMovies))
            
            getMoviesUseCase = GetMoviesUseCase(repository)
            val viewModel = HomeViewModel(getMoviesUseCase)
            
            advanceUntilIdle()
            
            // Select the target category
            viewModel.processIntent(HomeIntent.SelectCategory(targetCategory))
            advanceUntilIdle()
            
            // Verify that getMovies was called for the new category
            verify { repository.getMovies(targetCategory, 1) }
            
            // Verify movies are updated to the new category's movies
            viewModel.state.value.movies shouldBe newMovies
        }
    }

    @Test
    fun `Property 5 - Category selection resets pagination to page 1`() = runTest(testDispatcher) {
        checkAll(100, categoryArb, Arb.list(movieArb, 1..5), Arb.list(movieArb, 1..5), Arb.list(movieArb, 1..5)) { targetCategory, page1, page2, newCategoryMovies ->
            // Skip if target category is POPULAR (same as initial)
            if (targetCategory == Category.POPULAR) return@checkAll
            
            repository = mockk()
            every { repository.getMovies(Category.POPULAR, 1) } returns flowOf(Resource.Success(page1))
            every { repository.getMovies(Category.POPULAR, 2) } returns flowOf(Resource.Success(page2))
            every { repository.getMovies(targetCategory, 1) } returns flowOf(Resource.Success(newCategoryMovies))
            
            getMoviesUseCase = GetMoviesUseCase(repository)
            val viewModel = HomeViewModel(getMoviesUseCase)
            
            advanceUntilIdle()
            
            // Load next page to advance pagination
            viewModel.processIntent(HomeIntent.LoadNextPage)
            advanceUntilIdle()
            
            viewModel.state.value.currentPage shouldBe 2
            
            // Select new category
            viewModel.processIntent(HomeIntent.SelectCategory(targetCategory))
            advanceUntilIdle()
            
            // Verify pagination is reset to page 1
            viewModel.state.value.currentPage shouldBe 1
        }
    }

    @Test
    fun `Property 5 - Selecting same category does not trigger new fetch`() = runTest(testDispatcher) {
        checkAll(100, Arb.list(movieArb, 0..10)) { movies ->
            repository = mockk()
            every { repository.getMovies(Category.POPULAR, 1) } returns flowOf(Resource.Success(movies))
            
            getMoviesUseCase = GetMoviesUseCase(repository)
            val viewModel = HomeViewModel(getMoviesUseCase)
            
            advanceUntilIdle()
            
            // Select the same category (POPULAR is default)
            viewModel.processIntent(HomeIntent.SelectCategory(Category.POPULAR))
            advanceUntilIdle()
            
            // Verify getMovies was only called once (initial load)
            verify(exactly = 1) { repository.getMovies(Category.POPULAR, 1) }
        }
    }
}
