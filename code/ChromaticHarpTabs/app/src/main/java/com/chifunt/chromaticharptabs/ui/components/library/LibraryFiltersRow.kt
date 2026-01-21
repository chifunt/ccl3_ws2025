package com.chifunt.chromaticharptabs.ui.components.library

import androidx.compose.runtime.Composable
import com.chifunt.chromaticharptabs.ui.viewmodels.SortOption

@Composable
fun LibraryFiltersRow(
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
