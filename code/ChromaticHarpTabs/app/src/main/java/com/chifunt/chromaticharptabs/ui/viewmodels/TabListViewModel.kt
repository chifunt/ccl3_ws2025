package com.chifunt.chromaticharptabs.ui.viewmodels

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chifunt.chromaticharptabs.R
import com.chifunt.chromaticharptabs.data.Tab
import com.chifunt.chromaticharptabs.data.TabRepository
import com.chifunt.chromaticharptabs.data.parseTags
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
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

    private val baseFilters = combine(
        searchQuery,
        keyFilter,
        difficultyFilter
    ) { query, key, difficulty ->
        Triple(query, key, difficulty)
    }

    private val filters = combine(
        baseFilters,
        sortOption,
        favoritesOnly,
        tagFilter
    ) { base, sort, favorites, tags ->
        TabListFilters(
            query = base.first,
            keyFilter = base.second,
            difficulty = base.third,
            sortOption = sort,
            favoritesOnly = favorites,
            tagFilter = tags
        )
    }

    private val tabs = filters.flatMapLatest { filter ->
        repository.getTabs(
            query = filter.query,
            keyFilter = filter.keyFilter,
            difficulty = filter.difficulty,
            favoritesOnly = filter.favoritesOnly,
            sortOption = filter.sortOption
        )
    }

    val uiState = combine(filters, tabs) { filter, tabs ->
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
        updateFilter(searchQuery, value)
    }

    fun updateKeyFilter(value: String?) {
        updateFilter(keyFilter, value)
    }

    fun updateDifficulty(value: String?) {
        updateFilter(difficultyFilter, value)
    }

    fun toggleTagFilter(tag: String) {
        tagFilter.update { current ->
            if (tag in current) current - tag else current + tag
        }
    }

    fun clearTagFilter() {
        updateFilter(tagFilter, emptySet())
    }

    fun updateSortOption(value: SortOption) {
        updateFilter(sortOption, value)
    }

    fun toggleFavoritesOnly() {
        favoritesOnly.update { !it }
    }

    fun toggleFavorite(tab: Tab) {
        viewModelScope.launch {
            repository.setFavorite(tab.id, !tab.isFavorite, System.currentTimeMillis())
        }
    }

    private fun <T> updateFilter(filter: MutableStateFlow<T>, value: T) {
        filter.value = value
    }
}
