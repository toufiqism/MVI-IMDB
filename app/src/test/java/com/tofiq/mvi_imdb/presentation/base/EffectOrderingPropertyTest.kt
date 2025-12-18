package com.tofiq.mvi_imdb.presentation.base

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.checkAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Test

/**
 * **Feature: mvi-effects, Property 3: Effect Ordering Preservation**
 * **Validates: Requirements 6.2, 6.3**
 * 
 * For any sequence of effects emitted by a ViewModel, collectors SHALL receive 
 * them in the same order they were emitted.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class EffectOrderingPropertyTest {

    /**
     * Test effect type for property testing
     */
    sealed interface TestEffect : MviEffect {
        data class Navigate(val id: Int) : TestEffect
    }

    /**
     * Test intent type for property testing
     */
    sealed interface TestIntent : MviIntent {
        data class EmitEffect(val id: Int) : TestIntent
    }

    /**
     * Test state type for property testing
     */
    data class TestState(val value: Int = 0) : MviState

    /**
     * Test ViewModel that exposes effect emission for testing
     */
    class TestViewModel : MviViewModel<TestIntent, TestState, TestEffect>() {
        private val _state = MutableStateFlow(TestState())
        override val state: StateFlow<TestState> = _state

        override fun processIntent(intent: TestIntent) {
            when (intent) {
                is TestIntent.EmitEffect -> sendEffect(TestEffect.Navigate(intent.id))
            }
        }

        /**
         * Expose emitEffect for testing purposes
         */
        suspend fun emitTestEffect(effect: TestEffect) {
            emitEffect(effect)
        }
    }

    @Test
    fun `Property 3 - effects are received in emission order`() = runTest {
        checkAll(100, Arb.list(Arb.int(1..1000), 1..20)) { ids ->
            val viewModel = TestViewModel()
            val receivedEffects = mutableListOf<TestEffect>()

            val job = launch {
                viewModel.effect.toList(receivedEffects)
            }

            // Emit effects for each id
            ids.forEach { id ->
                viewModel.emitTestEffect(TestEffect.Navigate(id))
            }
            advanceUntilIdle()

            // Verify order preserved
            val receivedIds = receivedEffects.filterIsInstance<TestEffect.Navigate>().map { it.id }
            receivedIds shouldBe ids

            job.cancel()
        }
    }

    @Test
    fun `Property 3 - effects emitted via processIntent preserve order`() = runTest {
        checkAll(100, Arb.list(Arb.int(1..1000), 1..20)) { ids ->
            val viewModel = TestViewModel()
            val receivedEffects = mutableListOf<TestEffect>()

            val job = launch {
                viewModel.effect.toList(receivedEffects)
            }

            // Emit effects via intents
            ids.forEach { id ->
                viewModel.processIntent(TestIntent.EmitEffect(id))
            }
            advanceUntilIdle()

            // Verify order preserved
            val receivedIds = receivedEffects.filterIsInstance<TestEffect.Navigate>().map { it.id }
            receivedIds shouldBe ids

            job.cancel()
        }
    }
}
