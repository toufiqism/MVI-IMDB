package com.tofiq.mvi_imdb.data.repository

import com.tofiq.mvi_imdb.data.local.SettingsDataStore
import com.tofiq.mvi_imdb.domain.model.AppSettings
import com.tofiq.mvi_imdb.domain.model.ThemeMode
import com.tofiq.mvi_imdb.domain.model.ViewMode
import com.tofiq.mvi_imdb.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of SettingsRepository using DataStore.
 */
@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) : SettingsRepository {

    override val settings: Flow<AppSettings> = settingsDataStore.settings

    override suspend fun updateThemeMode(themeMode: ThemeMode) {
        settingsDataStore.updateThemeMode(themeMode)
    }

    override suspend fun updateCardCount(count: Int) {
        settingsDataStore.updateCardCount(count)
    }

    override suspend fun updateViewMode(viewMode: ViewMode) {
        settingsDataStore.updateViewMode(viewMode)
    }
}
