package com.chifunt.chromaticharptabs.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chifunt.chromaticharptabs.R
import com.chifunt.chromaticharptabs.ui.AppViewModelProvider
import com.chifunt.chromaticharptabs.ui.viewmodels.TabListViewModel
import com.chifunt.chromaticharptabs.ui.components.common.HapticFloatingActionButton
import com.chifunt.chromaticharptabs.ui.components.library.SearchField
import com.chifunt.chromaticharptabs.ui.components.library.LibraryFiltersRow
import com.chifunt.chromaticharptabs.ui.components.library.LibraryHeaderRow
import com.chifunt.chromaticharptabs.ui.components.library.TabList

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

            LibraryFiltersRow(
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

        HapticFloatingActionButton(
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
