package com.chifunt.chromaticharptabs.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chifunt.chromaticharptabs.data.model.Tab
import com.chifunt.chromaticharptabs.data.model.TabNote
import com.chifunt.chromaticharptabs.data.notation.NoteFrequencyProvider
import com.chifunt.chromaticharptabs.data.repository.TabRepository
import com.chifunt.chromaticharptabs.ui.navigation.NAV_ARG_TAB_ID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private fun emptyTab(): Tab {
    val now = System.currentTimeMillis()
    return Tab(
        id = 0,
        title = "",
        artist = "",
        key = "",
        difficulty = "",
        tags = "",
        content = "",
        isFavorite = false,
        createdAt = now,
        updatedAt = now
    )
}

data class TabDetailUiState(
    val tab: Tab
)

class TabDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: TabRepository,
    private val frequencyProvider: NoteFrequencyProvider
) : ViewModel() {

    private val tabId: Int = savedStateHandle[NAV_ARG_TAB_ID] ?: 0

    private val _uiState = MutableStateFlow(TabDetailUiState(emptyTab()))
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val tab = repository.findTabById(tabId)
            _uiState.update { it.copy(tab = tab) }
        }
    }

    fun removeTab(onRemoved: () -> Unit) {
        viewModelScope.launch {
            repository.removeTab(uiState.value.tab)
            onRemoved()
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            val tab = uiState.value.tab
            val now = System.currentTimeMillis()
            val updatedTab = tab.copy(isFavorite = !tab.isFavorite, updatedAt = now)
            _uiState.update { it.copy(tab = updatedTab) }
            repository.setFavorite(tab.id, updatedTab.isFavorite, now)
        }
    }

    fun frequencyFor(note: TabNote): Double? {
        return frequencyProvider.frequencyFor(note)
    }
}
