package com.chifunt.chromaticharptabs.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chifunt.chromaticharptabs.data.model.TabNote
import com.chifunt.chromaticharptabs.data.model.TabNotationJson
import com.chifunt.chromaticharptabs.data.repository.TabRepository
import com.chifunt.chromaticharptabs.ui.navigation.NAV_ARG_TAB_ID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PracticeUiState(
    val title: String,
    val lines: List<List<TabNote>>,
    val currentIndex: Int
)

class PracticeViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: TabRepository
) : ViewModel() {

    private val tabId: Int = savedStateHandle[NAV_ARG_TAB_ID] ?: 0

    private val _uiState = MutableStateFlow(
        PracticeUiState(
            title = "",
            lines = listOf(emptyList()),
            currentIndex = 0
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
                    currentIndex = 0
                )
            }
        }
    }

    fun nextLine() {
        _uiState.update { state ->
            val newIndex = (state.currentIndex + 1).coerceAtMost(state.lines.lastIndex)
            state.copy(currentIndex = newIndex)
        }
    }

    fun previousLine() {
        _uiState.update { state ->
            val newIndex = (state.currentIndex - 1).coerceAtLeast(0)
            state.copy(currentIndex = newIndex)
        }
    }
}
