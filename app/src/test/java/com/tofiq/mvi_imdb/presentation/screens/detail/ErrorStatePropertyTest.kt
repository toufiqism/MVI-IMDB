package com.tofiq.mvi_imdb.presentation.screens.detail

import com.tofiq.mvi_imdb.util.AppError
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldNotBeEmpty
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.orNull
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlinx.coroutines.test.runTest
import org.junit.Test

/**
 * **Feature: tmdb-movie-app, Property 3: Error State Contains Message**
 * **Validates: Requirements 1.4, 9.2**
 * 
 * For any failed network request, the resulting state SHALL contain a non-empty error message.
 */
class ErrorStatePropertyTest {

    private val networkErrorArb: Arb<AppError.NetworkError> = arbitrary {
        AppError.NetworkError(
            message = Arb.string(0..100).bind(),
            code = Arb.int(100..599).orNull().bind()
        )
    }

    private val databaseErrorArb: Arb<AppError.DatabaseError> = arbitrary {
        AppError.DatabaseError(
            message = Arb.string(0..100).bind()
        )
    }

    private val parseErrorArb: Arb<AppError.ParseError> = arbitrary {
        AppError.ParseError(
            message = Arb.string(0..100).bind()
        )
    }

    @Test
    fun `Property 3 - NetworkError always produces non-empty user message`() = runTest {
        checkAll(100, networkErrorArb) { error ->
            val userMessage = error.toUserMessage()
            
            // Error message should never be null
            userMessage shouldNotBe null
            
            // Error message should never be empty
            userMessage.shouldNotBeEmpty()
        }
    }

    @Test
    fun `Property 3 - DatabaseError always produces non-empty user message`() = runTest {
        checkAll(100, databaseErrorArb) { error ->
            val userMessage = error.toUserMessage()
            
            userMessage shouldNotBe null
            userMessage.shouldNotBeEmpty()
        }
    }

    @Test
    fun `Property 3 - ParseError always produces non-empty user message`() = runTest {
        checkAll(100, parseErrorArb) { error ->
            val userMessage = error.toUserMessage()
            
            userMessage shouldNotBe null
            userMessage.shouldNotBeEmpty()
        }
    }

    @Test
    fun `Property 3 - UnknownError always produces non-empty user message`() = runTest {
        val error = AppError.UnknownError
        val userMessage = error.toUserMessage()
        
        userMessage shouldNotBe null
        userMessage.shouldNotBeEmpty()
    }

    @Test
    fun `Property 3 - HTTP error codes map to specific user messages`() = runTest {
        // Test specific HTTP error codes produce appropriate messages
        val errorCodes = listOf(401, 404, 429, 500, 502, 503)
        
        errorCodes.forEach { code ->
            val error = AppError.NetworkError(message = "", code = code)
            val userMessage = error.toUserMessage()
            
            userMessage shouldNotBe null
            userMessage.shouldNotBeEmpty()
        }
    }

    @Test
    fun `Property 3 - DetailState with error has non-empty error message`() = runTest {
        checkAll(100, networkErrorArb) { appError ->
            val errorMessage = appError.toUserMessage()
            val state = DetailState(
                movieDetail = null,
                isLoading = false,
                error = errorMessage,
                movieId = 1
            )
            
            // When state has an error, the error message should be non-empty
            state.error shouldNotBe null
            state.error!!.shouldNotBeEmpty()
            
            // Loading should be false when there's an error
            state.isLoading shouldBe false
        }
    }
}
