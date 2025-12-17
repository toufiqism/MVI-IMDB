package com.tofiq.mvi_imdb.data.remote

import com.tofiq.mvi_imdb.data.remote.api.TmdbApiService
import com.tofiq.mvi_imdb.data.remote.dto.CreditsResponse
import com.tofiq.mvi_imdb.data.remote.dto.MovieDetailDto
import com.tofiq.mvi_imdb.data.remote.dto.MovieListResponse
import com.tofiq.mvi_imdb.data.remote.dto.PersonMovieCreditsResponse
import com.tofiq.mvi_imdb.domain.model.Category
import com.tofiq.mvi_imdb.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteDataSource @Inject constructor(
    private val apiService: TmdbApiService
) {
    suspend fun getMovies(category: Category, page: Int): Resource<MovieListResponse> =
        safeApiCall {
            when (category) {
                Category.POPULAR -> apiService.getPopularMovies(page)
                Category.TOP_RATED -> apiService.getTopRatedMovies(page)
                Category.UPCOMING -> apiService.getUpcomingMovies(page)
                Category.NOW_PLAYING -> apiService.getNowPlayingMovies(page)
            }
        }

    suspend fun getMovieDetail(movieId: Int): Resource<MovieDetailDto> =
        safeApiCall { apiService.getMovieDetail(movieId) }

    suspend fun getMovieCredits(movieId: Int): Resource<CreditsResponse> =
        safeApiCall { apiService.getMovieCredits(movieId) }

    suspend fun getSimilarMovies(movieId: Int, page: Int = 1): Resource<MovieListResponse> =
        safeApiCall { apiService.getSimilarMovies(movieId, page) }

    suspend fun searchMovies(query: String, page: Int): Resource<MovieListResponse> =
        safeApiCall { apiService.searchMovies(query, page) }

    /**
     * Fetches movie credits for a specific person/actor.
     * 
     * Requirements: 1.2
     */
    suspend fun getPersonMovieCredits(personId: Int): Resource<PersonMovieCreditsResponse> =
        safeApiCall { apiService.getPersonMovieCredits(personId) }

    private suspend fun <T> safeApiCall(apiCall: suspend () -> T): Resource<T> =
        withContext(Dispatchers.IO) {
            try {
                Resource.Success(apiCall())
            } catch (e: HttpException) {
                val errorMessage = when (e.code()) {
                    401 -> "Authentication failed. Please check API key."
                    404 -> "Content not found."
                    429 -> "Too many requests. Please try again later."
                    in 500..599 -> "Server error. Please try again later."
                    else -> e.message() ?: "Unknown HTTP error"
                }
                Resource.Error(errorMessage)
            } catch (e: IOException) {
                Resource.Error("Network error. Please check your connection.")
            } catch (e: Exception) {
                Resource.Error(e.message ?: "An unexpected error occurred")
            }
        }
}
