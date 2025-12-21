package com.tofiq.mvi_imdb.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tofiq.mvi_imdb.data.local.entity.SearchEntity

@Dao
interface SearchDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearchResults(results: List<SearchEntity>)

    @Query("SELECT movieId FROM search_results WHERE `query` = :query AND page = :page")
    suspend fun getSearchResults(query: String, page: Int): List<Int>

    @Query("DELETE FROM search_results WHERE `query` = :query")
    suspend fun deleteSearchResults(query: String)

    @Query("DELETE FROM search_results WHERE timestamp < :cutoffTime")
    suspend fun deleteOldCache(cutoffTime: Long)
}
