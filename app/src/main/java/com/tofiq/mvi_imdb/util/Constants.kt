package com.tofiq.mvi_imdb.util

object Constants {
    const val TMDB_BASE_URL = "https://api.themoviedb.org/3/"
    const val TMDB_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/"
    const val TMDB_POSTER_SIZE = "w500"
    const val TMDB_BACKDROP_SIZE = "w780"
    const val TMDB_PROFILE_SIZE = "w185"
    
    const val TMDB_API_KEY = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJlZmExODZiZGJiZjljM2NhNTk0M2NiZWQ1YWVhYjkwZSIsIm5iZiI6MTY1MzA2MTc3My45MSwic3ViIjoiNjI4N2I4OGQyMDlmMTgxMmM2MmI0MGZkIiwic2NvcGVzIjpbImFwaV9yZWFkIl0sInZlcnNpb24iOjF9.M9MLp59i43yJxoqrME5-zBXX3Rn5m7DFLw3nRZZO8kQ"
    
    const val DATABASE_NAME = "movie_database"
    
    const val SEARCH_DEBOUNCE_MS = 300L
    const val MIN_SEARCH_LENGTH = 2
    
    fun getPosterUrl(path: String?): String? = 
        path?.let { "$TMDB_IMAGE_BASE_URL$TMDB_POSTER_SIZE$it" }
    
    fun getBackdropUrl(path: String?): String? = 
        path?.let { "$TMDB_IMAGE_BASE_URL$TMDB_BACKDROP_SIZE$it" }
    
    fun getProfileUrl(path: String?): String? = 
        path?.let { "$TMDB_IMAGE_BASE_URL$TMDB_PROFILE_SIZE$it" }
}
