package com.chifunt.chromaticharptabs.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.chifunt.chromaticharptabs.R
import com.chifunt.chromaticharptabs.ui.viewmodels.SortOption

@Composable
fun FavoriteSortRow(
    difficultyFilter: String,
    onDifficultySelected: (String) -> Unit,
    favoritesOnly: Boolean,
    onToggleFavorites: () -> Unit,
    sortOption: SortOption,
    onSortSelected: (SortOption) -> Unit,
    keyFilter: String,
    onKeySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val filterHeight = dimensionResource(R.dimen.filter_chip_height)
    val borderWidth = dimensionResource(R.dimen.border_stroke_width)
    val spacingSmall = dimensionResource(R.dimen.spacing_small)
    val scrollState = rememberScrollState()

    Row(
        horizontalArrangement = Arrangement.spacedBy(spacingSmall),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
    ) {
        OutlinedButton(
            onClick = onToggleFavorites,
            modifier = Modifier
                .height(filterHeight)
                .width(56.dp),
            shape = MaterialTheme.shapes.small,
            contentPadding = PaddingValues(0.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = if (favoritesOnly) {
                    MaterialTheme.colorScheme.secondaryContainer
                } else {
                    Color.Transparent
                },
                contentColor = if (favoritesOnly) {
                    MaterialTheme.colorScheme.onSecondaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            ),
            border = BorderStroke(
                width = borderWidth,
                color = if (favoritesOnly) {
                    MaterialTheme.colorScheme.secondary
                } else {
                    MaterialTheme.colorScheme.outline
                }
            )
        ) {
            Icon(
                imageVector = if (favoritesOnly) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                contentDescription = stringResource(R.string.favorites_only)
            )
        }

        SortMenu(
            selected = sortOption,
            onSelected = onSortSelected,
            modifier = Modifier
                .height(filterHeight)
                .width(180.dp)
        )

        KeyFilterMenu(
            selected = keyFilter,
            onSelected = onKeySelected,
            modifier = Modifier
                .height(filterHeight)
                .width(160.dp)
        )

        DifficultyFilterMenu(
            selected = difficultyFilter,
            onSelected = onDifficultySelected,
            modifier = Modifier
                .height(filterHeight)
                .width(160.dp)
        )
    }
}
