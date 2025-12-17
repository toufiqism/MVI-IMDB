package com.tofiq.mvi_imdb.data.mapper

import com.tofiq.mvi_imdb.data.remote.dto.PersonMovieCreditDto
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldNotBeEmpty
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.double
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.orNull
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlinx.coroutines.test.runTest
import org.junit.Test

/**
 * **Feature: cast-movies, Property 4: Movie display completeness**
 * **Validates: Requirements 1.3, 4.1, 4.2**
 * 
 * For any successfully loaded cast movie list, each CastMovie in the state 
 * SHALL have a non-empty title, and the character field SHALL be preserved 
 * from the API response.
 */
class CastMovieMapperPropertyTest {

    // Generator for PersonMovieCreditDto with non-null title (valid API response)
    private val validPersonMovieCreditDtoArb: Arb<PersonMovieCreditDto> = arbitrary {
        PersonMovieCreditDto(
            id = Arb.int(1..Int.MAX_VALUE).bind(),
            title = Arb.string(1..100).bind(), // Non-null title for valid movies
            posterPath = Arb.string(10..50).orNull().bind(),
            releaseDate = Arb.string(0..10).orNull().bind(),
            character = Arb.string(0..50).orNull().bind(),
            voteAverage = Arb.double(0.0..10.0).orNull().bind(),
            overview = Arb.string(0..500).orNull().bind()
        )
    }

    // Generator for PersonMovieCreditDto with null title (edge case)
    private val nullTitlePersonMovieCreditDtoArb: Arb<PersonMovieCreditDto> = arbitrary {
        PersonMovieCreditDto(
            id = Arb.int(1..Int.MAX_VALUE).bind(),
            title = null,
            posterPath = Arb.string(10..50).orNull().bind(),
            releaseDate = Arb.string(0..10).orNull().bind(),
            character = Arb.string(0..50).orNull().bind(),
            voteAverage = Arb.double(0.0..10.0).orNull().bind(),
            overview = Arb.string(0..500).orNull().bind()
        )
    }

    @Test
    fun `Property 4 - CastMovie has non-empty title when DTO title is non-null`() = runTest {
        checkAll(100, validPersonMovieCreditDtoArb) { dto ->
            val castMovie = dto.toCastMovie()
            
            // Title should be non-empty when DTO has non-null title
            castMovie.title.shouldNotBeEmpty()
            castMovie.title shouldBe dto.title
        }
    }

    @Test
    fun `Property 4 - Character field is preserved from API response`() = runTest {
        checkAll(100, validPersonMovieCreditDtoArb) { dto ->
            val castMovie = dto.toCastMovie()
            
            // Character should be preserved exactly as received from API
            castMovie.character shouldBe dto.character
        }
    }

    @Test
    fun `Property 4 - All required fields are mapped correctly`() = runTest {
        checkAll(100, validPersonMovieCreditDtoArb) { dto ->
            val castMovie = dto.toCastMovie()
            
            // Required fields should be preserved
            castMovie.id shouldBe dto.id
            castMovie.title shouldBe dto.title
            castMovie.posterPath shouldBe dto.posterPath
            castMovie.releaseDate shouldBe dto.releaseDate
            castMovie.voteAverage shouldBe (dto.voteAverage ?: 0.0)
        }
    }

    @Test
    fun `Property 4 - Release year is correctly extracted from release date`() = runTest {
        checkAll(100, validPersonMovieCreditDtoArb) { dto ->
            val castMovie = dto.toCastMovie()
            
            // Release year should be extracted from first 4 characters of release date
            val expectedYear = dto.releaseDate?.takeIf { it.length >= 4 }?.substring(0, 4) ?: ""
            castMovie.releaseYear shouldBe expectedYear
        }
    }

    @Test
    fun `Property 4 - List mapping preserves all items`() = runTest {
        checkAll(100, Arb.list(validPersonMovieCreditDtoArb, 0..20)) { dtoList ->
            val castMovies = dtoList.toCastMovieList()
            
            castMovies.size shouldBe dtoList.size
            castMovies.forEachIndexed { index, castMovie ->
                castMovie.id shouldBe dtoList[index].id
                castMovie.title shouldBe dtoList[index].title
                castMovie.character shouldBe dtoList[index].character
            }
        }
    }

    @Test
    fun `Property 4 - Null title from DTO results in empty string`() = runTest {
        checkAll(100, nullTitlePersonMovieCreditDtoArb) { dto ->
            val castMovie = dto.toCastMovie()
            
            // When DTO title is null, CastMovie title should be empty string
            castMovie.title shouldBe ""
        }
    }
}
