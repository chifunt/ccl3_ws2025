package com.chifunt.chromaticharptabs.ui.viewmodels

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chifunt.chromaticharptabs.R
import com.chifunt.chromaticharptabs.data.Tab
import com.chifunt.chromaticharptabs.data.TabRepository
import com.chifunt.chromaticharptabs.ui.components.parseTags
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class SortOption(@StringRes val labelRes: Int) {
    Title(R.string.sort_option_title),
    Artist(R.string.sort_option_artist),
    Newest(R.string.sort_option_newest),
    Oldest(R.string.sort_option_oldest)
}

data class TabListUiState(
    val tabs: List<Tab>,
    val searchQuery: String,
    val keyFilter: String?,
    val difficulty: String?,
    val sortOption: SortOption,
    val favoritesOnly: Boolean,
    val tagFilter: Set<String>,
    val availableTags: List<String>
)

private data class TabListFilters(
    val query: String,
    val keyFilter: String?,
    val difficulty: String?,
    val sortOption: SortOption,
    val favoritesOnly: Boolean,
    val tagFilter: Set<String>
)

@OptIn(ExperimentalCoroutinesApi::class)
class TabListViewModel(private val repository: TabRepository) : ViewModel() {

    private val searchQuery = MutableStateFlow("")
    private val keyFilter = MutableStateFlow<String?>(null)
    private val difficultyFilter = MutableStateFlow<String?>(null)
    private val tagFilter = MutableStateFlow<Set<String>>(emptySet())
    private val sortOption = MutableStateFlow(SortOption.Newest)
    private val favoritesOnly = MutableStateFlow(false)

    @Suppress("UNCHECKED_CAST")
    private val filters = combine(
        searchQuery,
        keyFilter,
        difficultyFilter,
        sortOption,
        favoritesOnly,
        tagFilter
    ) { values ->
        TabListFilters(
            query = values[0] as String,
            keyFilter = values[1] as String?,
            difficulty = values[2] as String?,
            sortOption = values[3] as SortOption,
            favoritesOnly = values[4] as Boolean,
            tagFilter = values[5] as Set<String>
        )
    }

    val uiState = filters
        .flatMapLatest { filter ->
            repository.getTabs(
                query = filter.query,
                keyFilter = filter.keyFilter,
                difficulty = filter.difficulty,
                favoritesOnly = filter.favoritesOnly,
                sortOption = filter.sortOption
            ).map { tabs ->
                val availableTags = tabs
                    .flatMap { parseTags(it.tags) }
                    .distinct()
                    .sorted()
                val filteredTabs = if (filter.tagFilter.isEmpty()) {
                    tabs
                } else {
                    tabs.filter { tab ->
                        parseTags(tab.tags).any { it in filter.tagFilter }
                    }
                }
                TabListUiState(
                    tabs = filteredTabs,
                    searchQuery = filter.query,
                    keyFilter = filter.keyFilter,
                    difficulty = filter.difficulty,
                    sortOption = filter.sortOption,
                    favoritesOnly = filter.favoritesOnly,
                    tagFilter = filter.tagFilter,
                    availableTags = availableTags
                )
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            TabListUiState(
                tabs = emptyList(),
                searchQuery = "",
                keyFilter = null,
                difficulty = null,
                sortOption = SortOption.Newest,
                favoritesOnly = false,
                tagFilter = emptySet(),
                availableTags = emptyList()
            )
        )

    fun updateSearchQuery(value: String) {
        searchQuery.value = value
    }

    fun updateKeyFilter(value: String?) {
        keyFilter.value = value
    }

    fun updateDifficulty(value: String?) {
        difficultyFilter.value = value
    }

    fun toggleTagFilter(tag: String) {
        tagFilter.update { current ->
            if (tag in current) current - tag else current + tag
        }
    }

    fun clearTagFilter() {
        tagFilter.value = emptySet()
    }

    fun updateSortOption(value: SortOption) {
        sortOption.value = value
    }

    fun toggleFavoritesOnly() {
        favoritesOnly.update { !it }
    }

    fun toggleFavorite(tab: Tab) {
        viewModelScope.launch {
            repository.setFavorite(tab.id, !tab.isFavorite, System.currentTimeMillis())
        }
    }
}
