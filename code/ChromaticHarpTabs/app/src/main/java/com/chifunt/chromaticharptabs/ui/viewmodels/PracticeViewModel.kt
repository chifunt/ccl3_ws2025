package com.chifunt.chromaticharptabs.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chifunt.chromaticharptabs.data.model.TabNote
import com.chifunt.chromaticharptabs.data.model.TabNotationJson
import com.chifunt.chromaticharptabs.data.repository.TabRepository
import com.chifunt.chromaticharptabs.ui.navigation.NAV_ARG_TAB_ID
import com.chifunt.chromaticharptabs.data.notation.NoteFrequencyProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.ln

data class PracticeUiState(
    val title: String,
    val lines: List<List<TabNote>>,
    val currentIndex: Int,
    val currentNoteIndex: Int,
    val isTargetPlaying: Boolean,
    val isWrongNotePlaying: Boolean,
    val suppressNextLineHighlight: Boolean
)

class PracticeViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: TabRepository,
    private val frequencyProvider: NoteFrequencyProvider
) : ViewModel() {

    private val tabId: Int = savedStateHandle[NAV_ARG_TAB_ID] ?: 0
    private val toleranceCents = 50.0

    private val _uiState = MutableStateFlow(
        PracticeUiState(
            title = "",
            lines = listOf(emptyList()),
            currentIndex = 0,
            currentNoteIndex = 0,
            isTargetPlaying = false,
            isWrongNotePlaying = false,
            suppressNextLineHighlight = false
        )
    )
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val tab = repository.findTabById(tabId)
            val notation = TabNotationJson.fromJson(tab.content)
            val parsedLines = notation?.lines?.ifEmpty { listOf(emptyList()) } ?: listOf(emptyList())

            _uiState.update {
                it.copy(
                    title = tab.title,
                    lines = parsedLines,
                    currentIndex = 0,
                    currentNoteIndex = 0,
                    isTargetPlaying = false,
                    isWrongNotePlaying = false,
                    suppressNextLineHighlight = false
                )
            }
        }
    }

    fun nextLine() {
        _uiState.update { state ->
            val newIndex = (state.currentIndex + 1).coerceAtMost(state.lines.lastIndex)
            state.copy(
                currentIndex = newIndex,
                currentNoteIndex = 0,
                isTargetPlaying = false,
                isWrongNotePlaying = false,
                suppressNextLineHighlight = false
            )
        }
    }

    fun previousLine() {
        _uiState.update { state ->
            val newIndex = (state.currentIndex - 1).coerceAtLeast(0)
            state.copy(
                currentIndex = newIndex,
                currentNoteIndex = 0,
                isTargetPlaying = false,
                isWrongNotePlaying = false,
                suppressNextLineHighlight = false
            )
        }
    }

    fun onMicDisabled() {
        _uiState.update { state ->
            state.copy(
                currentNoteIndex = 0,
                isTargetPlaying = false,
                isWrongNotePlaying = false,
                suppressNextLineHighlight = false
            )
        }
    }

    fun onPitchUpdate(
        pitch: Float?,
        micEnabled: Boolean,
        autoAdvanceLine: Boolean,
        advanceOnNoteStart: Boolean,
        repeatLine: Boolean
    ) {
        _uiState.update { state ->
            if (!micEnabled) {
                return@update state
            }
            val currentLine = state.lines.getOrNull(state.currentIndex).orEmpty()
            if (currentLine.isEmpty()) {
                return@update state.copy(
                    currentNoteIndex = 0,
                    isTargetPlaying = false,
                    isWrongNotePlaying = false
                )
            }
            if (state.currentNoteIndex > currentLine.lastIndex) {
                return@update state
            }

            var currentNoteIndex = state.currentNoteIndex

            val pitchValue = pitch?.takeIf { it.isFinite() }
            if (state.suppressNextLineHighlight) {
                if (pitchValue != null) {
                    return@update state.copy(
                        isTargetPlaying = false,
                        isWrongNotePlaying = false
                    )
                }
            }

            val targetNote = currentLine.getOrNull(currentNoteIndex) ?: return@update state
            val targetFrequency = frequencyProvider.frequencyFor(targetNote) ?: return@update state
            val isCorrect = pitchValue != null &&
                abs(centsDifference(pitchValue.toDouble(), targetFrequency)) <= toleranceCents

            if (isCorrect) {
                if (
                    autoAdvanceLine &&
                    advanceOnNoteStart &&
                    currentNoteIndex == currentLine.lastIndex &&
                    state.currentIndex < state.lines.lastIndex
                ) {
                    val nextIndex = (state.currentIndex + 1).coerceAtMost(state.lines.lastIndex)
                    return@update state.copy(
                        currentIndex = nextIndex,
                        currentNoteIndex = 0,
                        isTargetPlaying = false,
                        isWrongNotePlaying = false,
                        suppressNextLineHighlight = true
                    )
                }
                return@update state.copy(
                    currentNoteIndex = currentNoteIndex,
                    isTargetPlaying = true,
                    isWrongNotePlaying = false,
                    suppressNextLineHighlight = false
                )
            } else {
                val isWrongNotePlaying = pitchValue != null
                if (state.isTargetPlaying) {
                    currentNoteIndex += 1
                    if (
                        autoAdvanceLine &&
                        currentNoteIndex > currentLine.lastIndex &&
                        state.currentIndex < state.lines.lastIndex
                    ) {
                        val nextIndex = (state.currentIndex + 1).coerceAtMost(state.lines.lastIndex)
                        return@update state.copy(
                            currentIndex = nextIndex,
                            currentNoteIndex = 0,
                            isTargetPlaying = false,
                            isWrongNotePlaying = false,
                            suppressNextLineHighlight = false
                        )
                    } else if (!autoAdvanceLine && repeatLine && currentNoteIndex > currentLine.lastIndex) {
                        currentNoteIndex = 0
                    }
                }
                return@update state.copy(
                    currentNoteIndex = currentNoteIndex,
                    isTargetPlaying = false,
                    isWrongNotePlaying = isWrongNotePlaying,
                    suppressNextLineHighlight = false
                )
            }
        }
    }

    private fun centsDifference(detectedFrequency: Double, targetFrequency: Double): Double {
        return 1200.0 * ln(detectedFrequency / targetFrequency) / ln(2.0)
    }

    fun frequencyFor(note: TabNote): Double? {
        return frequencyProvider.frequencyFor(note)
    }
}
