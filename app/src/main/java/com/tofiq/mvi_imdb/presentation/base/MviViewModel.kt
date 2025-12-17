package com.tofiq.mvi_imdb.presentation.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow

/**
 * Base ViewModel for MVI architecture pattern.
 * Handles processing of Intents and emitting new States.
 * 
 * Requirements: 8.1, 8.2 - Implements unidirectional data flow where:
 * - View emits Intents to ViewModel
 * - ViewModel processes Intents and emits new immutable States
 * - View renders UI based solely on current State
 * 
 * @param I The Intent type this ViewModel handles
 * @param S The State type this ViewModel emits
 */
abstract class MviViewModel<I : MviIntent, S : MviState> : ViewModel() {
    
    /**
     * The current UI state as a StateFlow.
     * Views should collect this to render the UI.
     */
    abstract val state: StateFlow<S>
    
    /**
     * Process an intent from the View.
     * This method should handle the intent and update the state accordingly.
     * 
     * @param intent The intent to process
     */
    abstract fun processIntent(intent: I)
}
