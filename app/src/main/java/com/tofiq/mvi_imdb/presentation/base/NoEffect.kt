package com.tofiq.mvi_imdb.presentation.base

/**
 * Placeholder Effect type for ViewModels that don't yet emit effects.
 * This allows gradual migration to the Effect-enabled MviViewModel.
 * 
 * ViewModels can use this as their Effect type parameter until they
 * are migrated to use screen-specific effects.
 */
sealed interface NoEffect : MviEffect
