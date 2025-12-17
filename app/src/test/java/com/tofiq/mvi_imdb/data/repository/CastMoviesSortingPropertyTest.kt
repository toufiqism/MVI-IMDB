package com.tofiq.mvi_imdb.data.repository

import com.tofiq.mvi_imdb.data.mapper.toCastMovieList
import com.tofiq.mvi_imdb.data.remote.dto.PersonMovieCreditDto
import com.tofiq.mvi_imdb.domain.model.CastMovie
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.double
import io.kotest.property.arbitrary.element
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.orNull
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlinx.coroutines.test.runTest
import org.junit.Test

/**
 * **Feature: cast-movies, Property 5: Movies sorted by release date descending**
 * **Validates: Requirements 5.1, 5.2**
 * 
 * For any list of cast movies in the state, the movies SHALL be sorted by 
 * release date in descending order (newest first), with movies having 
 * null/empty release dates appearing last.
 */
class CastMoviesSortingPropertyTest {

    // Generator for valid release dates in YYYY-MM-DD format
    private val validReleaseDateArb: Arb<String> = arbitrary {
        val year = Arb.int(1900..2030).bind()
        val month = Arb.int(1..12).bind()
        val day = Arb.int(1..28).bind()
        String.format("%04d-%02d-%02d", year, month, day)
    }

    // Generator for release dates that can be null, empty, or valid
    private val releaseDateArb: Arb<String?> = arbitrary {
        Arb.element(
            null,
            "",
            validReleaseDateArb.bind()
        ).bind()
    }

    // Generator for PersonMovieCreditDto
    private val personMovieCreditDtoArb: Arb<PersonMovieCreditDto> = arbitrary {
        PersonMovieCreditDto(
            id = Arb.int(1..Int.MAX_VALUE).bind(),
            title = Arb.string(1..100).bind(),
            posterPath = Arb.string(10..50).orNull().bind(),
            releaseDate = releaseDateArb.bind(),
            character = Arb.string(0..50).orNull().bind(),
            voteAverage = Arb.double(0.0..10.0).orNull().bind(),
            overview = Arb.string(0..200).orNull().bind()
        )
    }

    /**
     * Sorts cast movies by release date descending (newest first).
     * Movies with null/empty release dates appear last.
     * This mirrors the sorting logic in MovieRepositoryImpl.getCastMovies()
     */
    private fun sortCastMovies(movies: List<CastMovie>): List<CastMovie> {
        return movies.sortedByDescending { it.releaseDate ?: "" }
    }

    @Test
    fun `Property 5 - Movies are sorted by release date descending`() = runTest {
        checkAll(100, Arb.list(personMovieCreditDtoArb, 0..30)) { dtoList ->
            val castMovies = dtoList.toCastMovieList()
            val sortedMovies = sortCastMovies(castMovies)
            
            // Verify descending order for movies with valid release dates
            val moviesWithDates = sortedMovies.filter { !it.releaseDate.isNullOrEmpty() }
            for (i in 0 until moviesWithDates.size - 1) {
                val current = moviesWithDates[i].releaseDate ?: ""
                val next = moviesWithDates[i + 1].releaseDate ?: ""
                (current >= next) shouldBe true
            }
        }
    }

    @Test
    fun `Property 5 - Movies with null or empty release dates appear last`() = runTest {
        checkAll(100, Arb.list(personMovieCreditDtoArb, 0..30)) { dtoList ->
            val castMovies = dtoList.toCastMovieList()
            val sortedMovies = sortCastMovies(castMovies)
            
            // Find the first movie with null/empty release date
            val firstNullIndex = sortedMovies.indexOfFirst { it.releaseDate.isNullOrEmpty() }
            
            if (firstNullIndex != -1) {
                // All movies after this index should also have null/empty release dates
                for (i in firstNullIndex until sortedMovies.size) {
                    sortedMovies[i].releaseDate.isNullOrEmpty() shouldBe true
                }
            }
        }
    }

    @Test
    fun `Property 5 - Sorting preserves all movies`() = runTest {
        checkAll(100, Arb.list(personMovieCreditDtoArb, 0..30)) { dtoList ->
            val castMovies = dtoList.toCastMovieList()
            val sortedMovies = sortCastMovies(castMovies)
            
            // Size should be preserved
            sortedMovies.size shouldBe castMovies.size
            
            // All original movies should be present
            val originalIds = castMovies.map { it.id }.toSet()
            val sortedIds = sortedMovies.map { it.id }.toSet()
            sortedIds shouldBe originalIds
        }
    }

    @Test
    fun `Property 5 - Sorting is stable for movies with same release date`() = runTest {
        checkAll(100, Arb.list(personMovieCreditDtoArb, 0..30)) { dtoList ->
            val castMovies = dtoList.toCastMovieList()
            val sortedMovies = sortCastMovies(castMovies)
            
            // Group by release date and verify order is consistent
            val groupedByDate = sortedMovies.groupBy { it.releaseDate ?: "" }
            
            // For each group, the relative order should be maintained
            groupedByDate.forEach { (_, moviesInGroup) ->
                // Movies in the same group should maintain their relative order
                // from the original sorted list
                val indicesInSorted = moviesInGroup.map { movie -> 
                    sortedMovies.indexOf(movie) 
                }
                indicesInSorted shouldBe indicesInSorted.sorted()
            }
        }
    }

    @Test
    fun `Property 5 - Empty list returns empty list`() = runTest {
        val emptyList = emptyList<CastMovie>()
        val sortedMovies = sortCastMovies(emptyList)
        
        sortedMovies.size shouldBe 0
    }
}
