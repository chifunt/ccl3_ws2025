package com.chifunt.chromaticharptabs.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chifunt.chromaticharptabs.data.Tab
import com.chifunt.chromaticharptabs.data.TabRepository
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
        tempo = null,
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
    private val repository: TabRepository
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
            repository.setFavorite(tab.id, !tab.isFavorite, System.currentTimeMillis())
        }
    }
}
