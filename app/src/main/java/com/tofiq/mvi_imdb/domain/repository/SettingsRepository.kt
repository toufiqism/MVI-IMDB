package com.tofiq.mvi_imdb.domain.repository

import com.tofiq.mvi_imdb.domain.model.AppSettings
import com.tofiq.mvi_imdb.domain.model.ThemeMode
import com.tofiq.mvi_imdb.domain.model.ViewMode
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for app settings.
 */
interface SettingsRepository {
    val settings: Flow<AppSettings>
    suspend fun updateThemeMode(themeMode: ThemeMode)
    suspend fun updateCardCount(count: Int)
    suspend fun updateViewMode(viewMode: ViewMode)
}
