package com.tofiq.mvi_imdb.presentation.base

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

/**
 * Collects effects from a Flow and handles them.
 * Effects are consumed once and not replayed on recomposition.
 * 
 * This composable uses LaunchedEffect to collect effects in a lifecycle-aware manner.
 * The collection continues while the composable is in the composition and automatically
 * cancels when the composable leaves the composition.
 * 
 * Requirements: 3.1, 3.2, 3.3
 * - WHEN a Composable collects effects THEN the Composable SHALL use LaunchedEffect with the effect flow
 * - WHEN an effect is collected THEN the effect SHALL be processed and not replayed on recomposition
 * - THE effect collection SHALL continue while the Composable is in the composition
 * 
 * @param effect The Flow of effects to collect
 * @param onEffect Callback invoked for each effect received
 */
@Composable
fun <E : MviEffect> CollectEffect(
    effect: Flow<E>,
    onEffect: (E) -> Unit
) {
    LaunchedEffect(effect) {
        effect.collectLatest { onEffect(it) }
    }
}
