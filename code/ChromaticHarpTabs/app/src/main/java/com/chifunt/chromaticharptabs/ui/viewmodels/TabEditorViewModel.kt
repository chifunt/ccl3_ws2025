package com.chifunt.chromaticharptabs.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chifunt.chromaticharptabs.data.Tab
import com.chifunt.chromaticharptabs.data.TabNote
import com.chifunt.chromaticharptabs.data.TabNotation
import com.chifunt.chromaticharptabs.data.TabNotationJson
import com.chifunt.chromaticharptabs.data.TabRepository
import com.chifunt.chromaticharptabs.R
import com.chifunt.chromaticharptabs.ui.navigation.NAV_ARG_TAB_ID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val NEW_TAB_ID = -1

private fun blankEditorState(): TabEditorUiState {
    return TabEditorUiState(
        id = NEW_TAB_ID,
        title = "",
        artist = "",
        key = "",
        difficulty = "",
        tempo = "",
        tags = "",
        lines = emptyList(),
        isFavorite = false,
        createdAt = 0L,
        updatedAt = 0L,
        errorMessageResId = null
    )
}

data class TabEditorUiState(
    val id: Int,
    val title: String,
    val artist: String,
    val key: String,
    val difficulty: String,
    val tempo: String,
    val tags: String,
    val lines: List<List<TabNote>>,
    val isFavorite: Boolean,
    val createdAt: Long,
    val updatedAt: Long,
    val errorMessageResId: Int?
)

class TabEditorViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: TabRepository
) : ViewModel() {

    private val tabId: Int = savedStateHandle[NAV_ARG_TAB_ID] ?: NEW_TAB_ID
    private val _uiState = MutableStateFlow(blankEditorState())
    val uiState = _uiState.asStateFlow()

    init {
        if (tabId != NEW_TAB_ID) {
            viewModelScope.launch {
                val tab = repository.findTabById(tabId)
                _uiState.update {
                    val notation = TabNotationJson.fromJson(tab.content)
                    it.copy(
                        id = tab.id,
                        title = tab.title,
                        artist = tab.artist,
                        key = tab.key,
                        difficulty = tab.difficulty,
                        tempo = tab.tempo?.toString() ?: "",
                        tags = tab.tags,
                        lines = notation?.lines.orEmpty(),
                        isFavorite = tab.isFavorite,
                        createdAt = tab.createdAt,
                        updatedAt = tab.updatedAt
                    )
                }
            }
        }
    }

    fun updateTitle(value: String) {
        _uiState.update { it.copy(title = value, errorMessageResId = null) }
    }

    fun updateArtist(value: String) {
        _uiState.update { it.copy(artist = value, errorMessageResId = null) }
    }

    fun updateKey(value: String) {
        _uiState.update { it.copy(key = value, errorMessageResId = null) }
    }

    fun updateDifficulty(value: String) {
        _uiState.update { it.copy(difficulty = value, errorMessageResId = null) }
    }

    fun updateTempo(value: String) {
        _uiState.update { it.copy(tempo = value, errorMessageResId = null) }
    }

    fun updateTags(value: String) {
        _uiState.update { it.copy(tags = value, errorMessageResId = null) }
    }

    fun addNote(lineIndex: Int, hole: Int) {
        _uiState.update { state ->
            if (lineIndex !in state.lines.indices) {
                return@update state
            }
            val updatedLines = state.lines.toMutableList()
            val updatedLine = state.lines[lineIndex].toMutableList().apply {
                add(TabNote(hole = hole, isBlow = true, isSlide = false))
            }
            updatedLines[lineIndex] = updatedLine
            state.copy(lines = updatedLines, errorMessageResId = null)
        }
    }

    fun addLineWithNote(hole: Int) {
        _uiState.update { state ->
            val updatedLines = state.lines.toMutableList().apply {
                add(listOf(TabNote(hole = hole, isBlow = true, isSlide = false)))
            }
            state.copy(lines = updatedLines, errorMessageResId = null)
        }
    }

    fun updateHole(lineIndex: Int, noteIndex: Int, hole: Int) {
        _uiState.update { state ->
            if (lineIndex !in state.lines.indices) {
                return@update state
            }
            if (noteIndex !in state.lines[lineIndex].indices) {
                return@update state
            }
            val updatedLines = state.lines.toMutableList()
            val updatedLine = state.lines[lineIndex].toMutableList()
            val note = updatedLine[noteIndex]
            updatedLine[noteIndex] = note.copy(hole = hole)
            updatedLines[lineIndex] = updatedLine
            state.copy(lines = updatedLines, errorMessageResId = null)
        }
    }

    fun removeNote(lineIndex: Int, noteIndex: Int) {
        _uiState.update { state ->
            if (lineIndex !in state.lines.indices) {
                return@update state
            }
            if (noteIndex !in state.lines[lineIndex].indices) {
                return@update state
            }
            val updatedLines = state.lines.toMutableList()
            val updatedLine = state.lines[lineIndex].toMutableList().apply {
                removeAt(noteIndex)
            }
            updatedLines[lineIndex] = updatedLine
            state.copy(lines = updatedLines, errorMessageResId = null)
        }
    }

    fun removeLine(lineIndex: Int) {
        _uiState.update { state ->
            if (lineIndex !in state.lines.indices) {
                return@update state
            }
            val updatedLines = state.lines.toMutableList().apply {
                removeAt(lineIndex)
            }
            state.copy(lines = updatedLines, errorMessageResId = null)
        }
    }

    fun moveNote(lineIndex: Int, fromIndex: Int, toIndex: Int) {
        _uiState.update { state ->
            if (lineIndex !in state.lines.indices) {
                return@update state
            }
            if (fromIndex !in state.lines[lineIndex].indices) {
                return@update state
            }
            if (toIndex !in state.lines[lineIndex].indices) {
                return@update state
            }
            if (fromIndex == toIndex) {
                return@update state
            }
            val updatedLines = state.lines.toMutableList()
            val updatedLine = state.lines[lineIndex].toMutableList()
            val note = updatedLine.removeAt(fromIndex)
            updatedLine.add(toIndex, note)
            updatedLines[lineIndex] = updatedLine
            state.copy(lines = updatedLines, errorMessageResId = null)
        }
    }

    fun toggleBlow(lineIndex: Int, noteIndex: Int) {
        _uiState.update { state ->
            if (lineIndex !in state.lines.indices) {
                return@update state
            }
            if (noteIndex !in state.lines[lineIndex].indices) {
                return@update state
            }
            val updatedLines = state.lines.toMutableList()
            val updatedLine = state.lines[lineIndex].toMutableList()
            val note = updatedLine[noteIndex]
            updatedLine[noteIndex] = note.copy(isBlow = !note.isBlow)
            updatedLines[lineIndex] = updatedLine
            state.copy(lines = updatedLines, errorMessageResId = null)
        }
    }

    fun toggleSlide(lineIndex: Int, noteIndex: Int) {
        _uiState.update { state ->
            if (lineIndex !in state.lines.indices) {
                return@update state
            }
            if (noteIndex !in state.lines[lineIndex].indices) {
                return@update state
            }
            val updatedLines = state.lines.toMutableList()
            val updatedLine = state.lines[lineIndex].toMutableList()
            val note = updatedLine[noteIndex]
            updatedLine[noteIndex] = note.copy(isSlide = !note.isSlide)
            updatedLines[lineIndex] = updatedLine
            state.copy(lines = updatedLines, errorMessageResId = null)
        }
    }

    fun saveTab(onSaved: (Int) -> Unit) {
        viewModelScope.launch {
            val state = uiState.value
            val trimmedTitle = state.title.trim()

            val hasNotes = state.lines.any { it.isNotEmpty() }
            if (trimmedTitle.isBlank() || !hasNotes) {
                _uiState.update {
                    it.copy(errorMessageResId = R.string.error_required_fields)
                }
                return@launch
            }

            val now = System.currentTimeMillis()
            val tempoValue = state.tempo.toIntOrNull()

            val tab = Tab(
                id = if (state.id == NEW_TAB_ID) 0 else state.id,
                title = trimmedTitle,
                artist = state.artist.trim(),
                key = state.key.trim(),
                difficulty = state.difficulty,
                tempo = tempoValue,
                tags = state.tags.trim(),
                content = TabNotationJson.toJson(TabNotation(state.lines)),
                isFavorite = state.isFavorite,
                createdAt = if (state.id == NEW_TAB_ID) now else state.createdAt,
                updatedAt = now
            )

            val savedId = if (state.id == NEW_TAB_ID) {
                repository.addTab(tab).toInt()
            } else {
                repository.updateTab(tab)
                tab.id
            }

            onSaved(savedId)
        }
    }
}
