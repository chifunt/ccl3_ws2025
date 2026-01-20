package com.chifunt.chromaticharptabs.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chifunt.chromaticharptabs.R
import com.chifunt.chromaticharptabs.data.Tab
import com.chifunt.chromaticharptabs.ui.AppViewModelProvider
import com.chifunt.chromaticharptabs.ui.viewmodels.TabListViewModel
import com.chifunt.chromaticharptabs.ui.components.AddTabButton
import com.chifunt.chromaticharptabs.ui.components.FavoriteSortRow
import com.chifunt.chromaticharptabs.ui.components.DebouncedIconButton
import com.chifunt.chromaticharptabs.ui.components.LibraryEmptyState
import com.chifunt.chromaticharptabs.ui.components.LibraryHeader
import com.chifunt.chromaticharptabs.ui.components.SearchField
import com.chifunt.chromaticharptabs.ui.components.TabCard
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(spacingMedium),
        horizontalAlignment = Alignment.CenterHorizontally
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
        Spacer(Modifier.height(spacingSmall))

        SearchField(
            value = state.searchQuery,
            onValueChange = tabListViewModel::updateSearchQuery,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(spacingMedium))

        FavoriteSortRow(
            difficultyFilter = state.difficulty ?: allLabel,
            onDifficultySelected = { option ->
                tabListViewModel.updateDifficulty(if (option == allLabel) null else option)
            },
            favoritesOnly = state.favoritesOnly,
            onToggleFavorites = tabListViewModel::toggleFavoritesOnly,
            sortOption = state.sortOption,
            onSortSelected = tabListViewModel::updateSortOption,
            keyFilter = state.keyFilter ?: allLabel,
            onKeySelected = { option ->
                tabListViewModel.updateKeyFilter(if (option == allLabel) null else option)
            }
        )

        Spacer(Modifier.height(spacingSmall))

        AddTabButton(onClick = onCreateNew, modifier = Modifier.fillMaxWidth())

        Spacer(Modifier.height(spacingMedium))

        if (state.tabs.isEmpty()) {
            LibraryEmptyState()
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(spacingSmall)) {
                items(state.tabs) { tab ->
                    TabCard(
                        tab = tab,
                        onOpen = { onTabClick(tab.id) },
                        onToggleFavorite = { tabListViewModel.toggleFavorite(tab) }
                    )
                }
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
                title = stringResource(R.string.sample_title_autumn_leaves),
                artist = stringResource(R.string.sample_artist_joseph_kosma),
                key = stringResource(R.string.key_g),
                difficulty = stringResource(R.string.difficulty_medium),
                tempo = 110,
                tags = stringResource(R.string.sample_tags_jazz_ballad),
                content = stringResource(R.string.sample_content_autumn_leaves),
                isFavorite = true,
                createdAt = 0L,
                updatedAt = 0L
            ),
            onOpen = {},
            onToggleFavorite = {}
        )
    }
}
