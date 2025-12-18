package com.tofiq.mvi_imdb.presentation.base

/**
 * Base marker interface for all MVI Effects.
 * 
 * Effects represent one-time events that should be consumed exactly once by the View.
 * Unlike [MviState] which persists and represents the current UI state that survives
 * configuration changes, Effects are fire-and-forget events that should not be replayed.
 * 
 * Examples of Effects:
 * - Navigation events (navigate to another screen)
 * - Showing transient messages (snackbar, toast)
 * - Triggering analytics events
 * - Error notifications that should not persist
 * 
 * Key differences from State:
 * - State: Persistent, survives configuration changes, represents current UI
 * - Effect: Transient, consumed once, represents one-time actions
 * 
 * Requirements: 1.1, 1.3 - THE MVI_Architecture SHALL provide a base MviEffect marker interface
 * for all Effect types, located in the presentation/base package alongside MviIntent and MviState
 */
interface MviEffect
