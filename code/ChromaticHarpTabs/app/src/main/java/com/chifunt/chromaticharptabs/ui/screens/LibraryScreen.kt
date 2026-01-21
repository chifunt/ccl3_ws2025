package com.chifunt.chromaticharptabs.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chifunt.chromaticharptabs.R
import com.chifunt.chromaticharptabs.data.Tab
import com.chifunt.chromaticharptabs.ui.AppViewModelProvider
import com.chifunt.chromaticharptabs.ui.viewmodels.SortOption
import com.chifunt.chromaticharptabs.ui.viewmodels.TabListViewModel
import com.chifunt.chromaticharptabs.ui.components.FavoriteSortRow
import com.chifunt.chromaticharptabs.ui.components.DebouncedIconButton
import com.chifunt.chromaticharptabs.ui.components.LibraryEmptyState
import com.chifunt.chromaticharptabs.ui.components.LibraryHeader
import com.chifunt.chromaticharptabs.ui.components.SearchField
import com.chifunt.chromaticharptabs.ui.components.cards.TabCard
import com.chifunt.chromaticharptabs.ui.theme.ChromaticHarpTabsTheme

@Composable
fun LibraryScreen(
    modifier: Modifier = Modifier,
    tabListViewModel: TabListViewModel = viewModel(factory = AppViewModelProvider.Factory),
    onTabClick: (Int) -> Unit,
    onCreateNew: () -> Unit,
    onSettings: () -> Unit
) {
    val state by tabListViewModel.uiState.collectAsStateWithLifecycle()
    val spacingSmall = dimensionResource(R.dimen.spacing_small)
    val spacingMedium = dimensionResource(R.dimen.spacing_medium)
    val allLabel = stringResource(R.string.filter_all)
    val hasFilters = state.searchQuery.isNotBlank() ||
        state.favoritesOnly ||
        state.keyFilter != null ||
        state.difficulty != null ||
        state.tagFilter.isNotEmpty()

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(spacingMedium)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LibraryHeaderRow(
                spacingSmall = spacingSmall,
                onSettings = onSettings
            )
            Spacer(Modifier.height(spacingSmall))

            SearchField(
                value = state.searchQuery,
                onValueChange = tabListViewModel::updateSearchQuery,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(spacingSmall))

            FiltersRow(
                allLabel = allLabel,
                difficulty = state.difficulty,
                onDifficultySelected = tabListViewModel::updateDifficulty,
                availableTags = state.availableTags,
                selectedTags = state.tagFilter,
                onToggleTag = tabListViewModel::toggleTagFilter,
                onClearTags = tabListViewModel::clearTagFilter,
                onClearAll = tabListViewModel::clearAllFilters,
                favoritesOnly = state.favoritesOnly,
                onToggleFavorites = tabListViewModel::toggleFavoritesOnly,
                sortOption = state.sortOption,
                onSortSelected = tabListViewModel::updateSortOption,
                keyFilter = state.keyFilter,
                onKeySelected = tabListViewModel::updateKeyFilter
            )

            Spacer(Modifier.height(spacingMedium))

            TabList(
                tabs = state.tabs,
                onOpen = onTabClick,
                onToggleFavorite = tabListViewModel::toggleFavorite,
                spacingSmall = spacingSmall,
                contentPadding = PaddingValues(bottom = 80.dp),
                emptyMessage = stringResource(
                    if (hasFilters) R.string.empty_search else R.string.empty_library
                )
            )
        }

        FloatingActionButton(
            onClick = onCreateNew,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(spacingSmall),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = stringResource(R.string.add_new_tab)
            )
        }
    }
}

@Composable
private fun LibraryHeaderRow(
    spacingSmall: Dp,
    onSettings: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LibraryHeader(modifier = Modifier.padding(start = spacingSmall))
        Spacer(Modifier.weight(1f))
        DebouncedIconButton(onClick = onSettings) {
            Icon(
                imageVector = Icons.Filled.Settings,
                contentDescription = stringResource(R.string.settings_button)
            )
        }
    }
}

@Composable
private fun FiltersRow(
    allLabel: String,
    difficulty: String?,
    onDifficultySelected: (String?) -> Unit,
    availableTags: List<String>,
    selectedTags: Set<String>,
    onToggleTag: (String) -> Unit,
    onClearTags: () -> Unit,
    onClearAll: () -> Unit,
    favoritesOnly: Boolean,
    onToggleFavorites: () -> Unit,
    sortOption: SortOption,
    onSortSelected: (SortOption) -> Unit,
    keyFilter: String?,
    onKeySelected: (String?) -> Unit
) {
    FavoriteSortRow(
        difficultyFilter = difficulty ?: allLabel,
        onDifficultySelected = { option ->
            onDifficultySelected(if (option == allLabel) null else option)
        },
        tagOptions = availableTags,
        selectedTags = selectedTags,
        onToggleTag = onToggleTag,
        onClearTags = onClearTags,
        onClearAll = onClearAll,
        favoritesOnly = favoritesOnly,
        onToggleFavorites = onToggleFavorites,
        sortOption = sortOption,
        onSortSelected = onSortSelected,
        keyFilter = keyFilter ?: allLabel,
        onKeySelected = { option ->
            onKeySelected(if (option == allLabel) null else option)
        }
    )
}

@Composable
private fun TabList(
    tabs: List<Tab>,
    onOpen: (Int) -> Unit,
    onToggleFavorite: (Tab) -> Unit,
    spacingSmall: Dp,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    emptyMessage: String
) {
    if (tabs.isEmpty()) {
        LibraryEmptyState(message = emptyMessage)
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(spacingSmall),
            contentPadding = contentPadding
        ) {
            items(tabs) { tab ->
                TabCard(
                    tab = tab,
                    onOpen = { onOpen(tab.id) },
                    onToggleFavorite = { onToggleFavorite(tab) }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TabCardPreview() {
    ChromaticHarpTabsTheme {
        TabCard(
            tab = Tab(
                id = 1,
                title = "Autumn Leaves",
                artist = "Joseph Kosma",
                key = "C",
                difficulty = "Medium",
                tags = "",
                content = "",
                isFavorite = true,
                createdAt = 0L,
                updatedAt = 0L
            ),
            onOpen = {},
            onToggleFavorite = {}
        )
    }
}
