package com.chifunt.chromaticharptabs.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chifunt.chromaticharptabs.data.Tab
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
        content = "",
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
    val content: String,
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
                    it.copy(
                        id = tab.id,
                        title = tab.title,
                        artist = tab.artist,
                        key = tab.key,
                        difficulty = tab.difficulty,
                        tempo = tab.tempo?.toString() ?: "",
                        tags = tab.tags,
                        content = tab.content,
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

    fun updateContent(value: String) {
        _uiState.update { it.copy(content = value, errorMessageResId = null) }
    }

    fun saveTab(onSaved: (Int) -> Unit) {
        viewModelScope.launch {
            val state = uiState.value
            val trimmedTitle = state.title.trim()
            val trimmedContent = state.content.trim()

            if (trimmedTitle.isBlank() || trimmedContent.isBlank()) {
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
                content = trimmedContent,
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
