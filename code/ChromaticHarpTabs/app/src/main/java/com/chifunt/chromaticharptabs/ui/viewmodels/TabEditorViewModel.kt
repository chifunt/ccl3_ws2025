package com.chifunt.chromaticharptabs.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chifunt.chromaticharptabs.data.model.Tab
import com.chifunt.chromaticharptabs.data.model.TabNote
import com.chifunt.chromaticharptabs.data.model.TabNotation
import com.chifunt.chromaticharptabs.data.model.TabNotationJson
import com.chifunt.chromaticharptabs.data.notation.NoteFrequencyProvider
import com.chifunt.chromaticharptabs.data.repository.TabRepository
import com.chifunt.chromaticharptabs.R
import com.chifunt.chromaticharptabs.data.util.normalizeTagsInput
import com.chifunt.chromaticharptabs.data.util.parseTags
import com.chifunt.chromaticharptabs.ui.navigation.NAV_ARG_TAB_ID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
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
        tags = emptyList(),
        tagsInput = "",
        lines = emptyList(),
        isFavorite = false,
        createdAt = 0L,
        updatedAt = 0L,
        errorMessageResId = null
    )
}

private data class TabEditorSnapshot(
    val title: String,
    val artist: String,
    val key: String,
    val difficulty: String,
    val tags: List<String>,
    val tagsInput: String,
    val lines: List<List<TabNote>>
)

private fun TabEditorUiState.toSnapshot(): TabEditorSnapshot {
    return TabEditorSnapshot(
        title = title,
        artist = artist,
        key = key,
        difficulty = difficulty,
        tags = tags,
        tagsInput = tagsInput,
        lines = lines
    )
}

private data class ParsedTagsInput(
    val tagsToAdd: List<String>,
    val remainingInput: String
)

private fun parseTagsInput(raw: String): ParsedTagsInput {
    val normalized = normalizeTagsInput(raw.replace("\n", " ").replace("\r", " "))
    val endsWithDelimiter = raw.endsWith(" ") || raw.endsWith(",") || raw.endsWith("\n")
    val tokens = normalized.split(" ").filter { it.isNotBlank() }
    return if (tokens.isEmpty()) {
        ParsedTagsInput(tagsToAdd = emptyList(), remainingInput = if (endsWithDelimiter) "" else normalized)
    } else if (endsWithDelimiter) {
        ParsedTagsInput(tagsToAdd = tokens, remainingInput = "")
    } else {
        ParsedTagsInput(tagsToAdd = tokens.dropLast(1), remainingInput = tokens.last())
    }
}

data class TabEditorUiState(
    val id: Int,
    val title: String,
    val artist: String,
    val key: String,
    val difficulty: String,
    val tags: List<String>,
    val tagsInput: String,
    val lines: List<List<TabNote>>,
    val isFavorite: Boolean,
    val createdAt: Long,
    val updatedAt: Long,
    val errorMessageResId: Int?
)

class TabEditorViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: TabRepository,
    private val frequencyProvider: NoteFrequencyProvider
) : ViewModel() {

    private val tabId: Int = savedStateHandle[NAV_ARG_TAB_ID] ?: NEW_TAB_ID
    private val _uiState = MutableStateFlow(blankEditorState())
    val uiState = _uiState.asStateFlow()
    private val baseline = MutableStateFlow(blankEditorState().toSnapshot())

    val isDirty = combine(uiState, baseline) { state, base ->
        state.toSnapshot() != base
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

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
                        tags = parseTags(tab.tags),
                        tagsInput = "",
                        lines = notation?.lines.orEmpty(),
                        isFavorite = tab.isFavorite,
                        createdAt = tab.createdAt,
                        updatedAt = tab.updatedAt
                    )
                }
                baseline.value = _uiState.value.toSnapshot()
            }
        } else {
            baseline.value = _uiState.value.toSnapshot()
        }
    }

    fun updateTitle(value: String) {
        clearError { it.copy(title = value) }
    }

    fun updateArtist(value: String) {
        clearError { it.copy(artist = value) }
    }

    fun updateKey(value: String) {
        clearError { it.copy(key = value) }
    }

    fun updateDifficulty(value: String) {
        clearError { it.copy(difficulty = value) }
    }

    fun applyDefaults(defaultKey: String, defaultDifficulty: String) {
        var applied = false
        _uiState.update { state ->
            if (state.id != NEW_TAB_ID) {
                return@update state
            }
            val hasUserEdits = state.title.isNotBlank() ||
                state.artist.isNotBlank() ||
                state.tags.isNotEmpty() ||
                state.tagsInput.isNotBlank() ||
                state.lines.isNotEmpty()
            if (hasUserEdits) {
                return@update state
            }
            var nextState = state
            if (nextState.key.isBlank()) {
                nextState = nextState.copy(key = defaultKey)
            }
            if (nextState.difficulty.isBlank()) {
                nextState = nextState.copy(difficulty = defaultDifficulty)
            }
            applied = applied || nextState != state
            nextState
        }
        if (applied) {
            baseline.value = _uiState.value.toSnapshot()
        }
    }


    fun updateTagsInput(value: String) {
        val parsed = parseTagsInput(value)
        clearError { state ->
            val current = state.tags
            val combined = (current + parsed.tagsToAdd).distinct()
            return@clearError state.copy(
                tags = combined,
                tagsInput = parsed.remainingInput,
                errorMessageResId = null
            )
        }
    }

    fun commitTagsInput() {
        val input = uiState.value.tagsInput
        if (input.isBlank()) {
            return
        }
        updateTagsInput("$input ")
    }

    fun removeTag(tag: String) {
        clearError { state ->
            state.copy(tags = state.tags.filterNot { it == tag })
        }
    }

    fun addNote(lineIndex: Int, hole: Int) {
        updateLines { lines ->
            if (lineIndex !in lines.indices) {
                return@updateLines null
            }
            val updatedLines = lines.toMutableList()
            val updatedLine = lines[lineIndex].toMutableList().apply {
                add(TabNote(hole = hole, isBlow = true, isSlide = false))
            }
            updatedLines[lineIndex] = updatedLine
            updatedLines
        }
    }

    fun addLineWithNote(hole: Int) {
        updateLines { lines ->
            lines.toMutableList().apply {
                add(listOf(TabNote(hole = hole, isBlow = true, isSlide = false)))
            }
        }
    }

    fun updateHole(lineIndex: Int, noteIndex: Int, hole: Int) {
        updateLines { lines ->
            if (lineIndex !in lines.indices) {
                return@updateLines null
            }
            if (noteIndex !in lines[lineIndex].indices) {
                return@updateLines null
            }
            val updatedLines = lines.toMutableList()
            val updatedLine = lines[lineIndex].toMutableList()
            val note = updatedLine[noteIndex]
            updatedLine[noteIndex] = note.copy(hole = hole)
            updatedLines[lineIndex] = updatedLine
            updatedLines
        }
    }

    fun removeNote(lineIndex: Int, noteIndex: Int) {
        updateLines { lines ->
            if (lineIndex !in lines.indices) {
                return@updateLines null
            }
            if (noteIndex !in lines[lineIndex].indices) {
                return@updateLines null
            }
            val updatedLines = lines.toMutableList()
            val updatedLine = lines[lineIndex].toMutableList().apply {
                removeAt(noteIndex)
            }
            updatedLines[lineIndex] = updatedLine
            updatedLines
        }
    }

    fun removeLine(lineIndex: Int) {
        updateLines { lines ->
            if (lineIndex !in lines.indices) {
                return@updateLines null
            }
            lines.toMutableList().apply {
                removeAt(lineIndex)
            }
        }
    }

    fun moveNote(lineIndex: Int, fromIndex: Int, toIndex: Int) {
        updateLines { lines ->
            if (lineIndex !in lines.indices) {
                return@updateLines null
            }
            if (fromIndex !in lines[lineIndex].indices) {
                return@updateLines null
            }
            if (toIndex !in lines[lineIndex].indices) {
                return@updateLines null
            }
            if (fromIndex == toIndex) {
                return@updateLines null
            }
            val updatedLines = lines.toMutableList()
            val updatedLine = lines[lineIndex].toMutableList()
            val note = updatedLine.removeAt(fromIndex)
            updatedLine.add(toIndex, note)
            updatedLines[lineIndex] = updatedLine
            updatedLines
        }
    }

    fun toggleBlow(lineIndex: Int, noteIndex: Int) {
        updateLines { lines ->
            if (lineIndex !in lines.indices) {
                return@updateLines null
            }
            if (noteIndex !in lines[lineIndex].indices) {
                return@updateLines null
            }
            val updatedLines = lines.toMutableList()
            val updatedLine = lines[lineIndex].toMutableList()
            val note = updatedLine[noteIndex]
            updatedLine[noteIndex] = note.copy(isBlow = !note.isBlow)
            updatedLines[lineIndex] = updatedLine
            updatedLines
        }
    }

    fun toggleSlide(lineIndex: Int, noteIndex: Int) {
        updateLines { lines ->
            if (lineIndex !in lines.indices) {
                return@updateLines null
            }
            if (noteIndex !in lines[lineIndex].indices) {
                return@updateLines null
            }
            val updatedLines = lines.toMutableList()
            val updatedLine = lines[lineIndex].toMutableList()
            val note = updatedLine[noteIndex]
            updatedLine[noteIndex] = note.copy(isSlide = !note.isSlide)
            updatedLines[lineIndex] = updatedLine
            updatedLines
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
            val tagList = (state.tags + parseTags(state.tagsInput)).distinct()

            val tab = Tab(
                id = if (state.id == NEW_TAB_ID) 0 else state.id,
                title = trimmedTitle,
                artist = state.artist.trim(),
                key = state.key.trim(),
                difficulty = state.difficulty,
                tags = tagList.joinToString(" "),
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

    private inline fun clearError(
        crossinline transform: (TabEditorUiState) -> TabEditorUiState
    ) {
        _uiState.update { state ->
            transform(state).copy(errorMessageResId = null)
        }
    }

    private inline fun updateLines(
        crossinline transform: (List<List<TabNote>>) -> List<List<TabNote>>?
    ) {
        _uiState.update { state ->
            val updated = transform(state.lines) ?: return@update state
            state.copy(lines = updated, errorMessageResId = null)
        }
    }

    fun frequencyFor(note: TabNote): Double? {
        return frequencyProvider.frequencyFor(note)
    }
}
