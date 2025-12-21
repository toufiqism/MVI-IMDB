package com.tofiq.mvi_imdb.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_results")
data class SearchEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val query: String,
    val page: Int,
    val movieId: Int,
    val timestamp: Long = System.currentTimeMillis()
)
