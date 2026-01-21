package com.chifunt.chromaticharptabs.ui.components.common

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

@Composable
fun FavoriteToggleButton(
    isFavorite: Boolean,
    onToggle: () -> Unit,
    @StringRes contentDescriptionRes: Int? = null
) {
    DebouncedIconButton(onClick = onToggle) {
        Icon(
            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
            contentDescription = contentDescriptionRes?.let { stringResource(it) },
            tint = if (isFavorite) {
                MaterialTheme.colorScheme.secondary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    }
}
