package com.tofiq.mvi_imdb.presentation.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * Base ViewModel for MVI architecture pattern.
 * Handles processing of Intents, emitting new States, and emitting one-time Effects.
 * 
 * Requirements: 8.1, 8.2 - Implements unidirectional data flow where:
 * - View emits Intents to ViewModel
 * - ViewModel processes Intents and emits new immutable States
 * - View renders UI based solely on current State
 * 
 * Requirements: 2.1, 2.2, 2.3, 2.4, 2.5 - Effect support:
 * - ViewModel exposes an effect property as a Flow of the Effect type parameter
 * - ViewModel accepts three type parameters: Intent, State, and Effect
 * - ViewModel provides protected methods to emit effects from subclasses
 * - Effects are delivered to all active collectors exactly once
 * - Effects are buffered until a collector subscribes
 * 
 * @param I The Intent type this ViewModel handles
 * @param S The State type this ViewModel emits
 * @param E The Effect type this ViewModel emits for one-time events
 */
abstract class MviViewModel<I : MviIntent, S : MviState, E : MviEffect> : ViewModel() {
    
    /**
     * The current UI state as a StateFlow.
     * Views should collect this to render the UI.
     */
    abstract val state: StateFlow<S>
    
    /**
     * Channel for one-time effects. Uses BUFFERED capacity to ensure effects
     * are not lost if emitted before a collector subscribes.
     */
    private val _effect = Channel<E>(Channel.BUFFERED)
    
    /**
     * Flow of one-time effects that should be consumed exactly once by the View.
     * Effects are buffered and delivered in the order they were emitted.
     */
    val effect: Flow<E> = _effect.receiveAsFlow()
    
    /**
     * Process an intent from the View.
     * This method should handle the intent and update the state accordingly.
     * 
     * @param intent The intent to process
     */
    abstract fun processIntent(intent: I)
    
    /**
     * Emit a one-time effect to be consumed by the View.
     * This is a suspending function that will suspend if the buffer is full.
     * Effects are buffered if no collector is active.
     * 
     * @param effect The effect to emit
     */
    protected suspend fun emitEffect(effect: E) {
        _effect.send(effect)
    }
    
    /**
     * Non-suspending version for use in non-coroutine contexts.
     * Uses trySend which doesn't suspend and returns immediately.
     * If the buffer is full, the effect may be dropped.
     * 
     * @param effect The effect to emit
     */
    protected fun sendEffect(effect: E) {
        _effect.trySend(effect)
    }
}
