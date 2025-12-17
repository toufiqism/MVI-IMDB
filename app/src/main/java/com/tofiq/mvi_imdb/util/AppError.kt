package com.tofiq.mvi_imdb.util

/**
 * Sealed class representing different types of application errors.
 * Provides user-friendly error messages for display in the UI.
 * 
 * Requirements: 9.2 - WHEN an API error occurs THEN the Movie_Repository SHALL map HTTP errors to user-friendly error messages
 * Requirements: 9.4 - IF an unexpected error occurs THEN the Error_Handler SHALL log the error and display a generic error message
 */
sealed class AppError {
    
    /**
     * Represents network-related errors including HTTP errors.
     * @param message The error message from the network layer
     * @param code Optional HTTP status code
     */
    data class NetworkError(val message: String, val code: Int? = null) : AppError()
    
    /**
     * Represents database-related errors.
     * @param message The error message from the database layer
     */
    data class DatabaseError(val message: String) : AppError()
    
    /**
     * Represents JSON parsing or data transformation errors.
     * @param message The error message describing the parse failure
     */
    data class ParseError(val message: String) : AppError()
    
    /**
     * Represents any unexpected or unknown errors.
     */
    data object UnknownError : AppError()
    
    /**
     * Converts the error to a user-friendly message suitable for display in the UI.
     * Maps HTTP status codes to appropriate messages.
     * 
     * @return A user-friendly error message string
     */
    fun toUserMessage(): String = when (this) {
        is NetworkError -> when (code) {
            401 -> "Authentication failed. Please check API key."
            404 -> "Content not found."
            429 -> "Too many requests. Please try again later."
            in 500..599 -> "Server error. Please try again later."
            else -> message.ifEmpty { "Network error. Please check your connection." }
        }
        is DatabaseError -> "Failed to access local data."
        is ParseError -> "Failed to process data."
        is UnknownError -> "An unexpected error occurred."
    }
    
    companion object {
        /**
         * Creates an AppError from a Throwable.
         * Useful for converting exceptions to AppError instances.
         * 
         * @param throwable The exception to convert
         * @return An appropriate AppError instance
         */
        fun fromThrowable(throwable: Throwable): AppError {
            return when (throwable) {
                is java.net.UnknownHostException -> NetworkError("No internet connection")
                is java.net.SocketTimeoutException -> NetworkError("Connection timed out")
                is retrofit2.HttpException -> NetworkError(
                    message = throwable.message ?: "HTTP error",
                    code = throwable.code()
                )
                is kotlinx.serialization.SerializationException -> ParseError(throwable.message ?: "Serialization error")
                is com.google.gson.JsonSyntaxException -> ParseError(throwable.message ?: "JSON parse error")
                is android.database.sqlite.SQLiteException -> DatabaseError(throwable.message ?: "Database error")
                else -> UnknownError
            }
        }
    }
}
