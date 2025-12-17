package com.tofiq.mvi_imdb.util

sealed class Resource<out T> {
    data class Success<T>(val data: T) : Resource<T>()
    data class Error<T>(val message: String, val data: T? = null) : Resource<T>()
    data object Loading : Resource<Nothing>()
    
    val isSuccess: Boolean get() = this is Success
    val isError: Boolean get() = this is Error
    val isLoading: Boolean get() = this is Loading
    
    fun <R> map(transform: (T) -> R): Resource<R> = when (this) {
        is Success -> Success(transform(data))
        is Error -> Error(message, data?.let(transform))
        is Loading -> Loading
    }
    
    fun getOrNull(): T? = when (this) {
        is Success -> data
        is Error -> data
        is Loading -> null
    }
    
    fun getOrThrow(): T = when (this) {
        is Success -> data
        is Error -> throw IllegalStateException(message)
        is Loading -> throw IllegalStateException("Resource is still loading")
    }
}
