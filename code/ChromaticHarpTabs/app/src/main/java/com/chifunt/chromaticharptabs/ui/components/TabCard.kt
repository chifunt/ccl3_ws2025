package com.chifunt.chromaticharptabs.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.Label
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.VpnKey
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.chifunt.chromaticharptabs.R
import com.chifunt.chromaticharptabs.data.Tab
import com.chifunt.chromaticharptabs.ui.components.DebouncedIconButton
import com.chifunt.chromaticharptabs.ui.theme.DifficultyEasy
import com.chifunt.chromaticharptabs.ui.theme.DifficultyHard
import com.chifunt.chromaticharptabs.ui.theme.DifficultyMedium

@Composable
fun TabCard(
    tab: Tab,
    onOpen: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacingSmall = dimensionResource(R.dimen.spacing_small)
    val easyLabel = stringResource(R.string.difficulty_easy)
    val mediumLabel = stringResource(R.string.difficulty_medium)
    val hardLabel = stringResource(R.string.difficulty_hard)
    val difficultyColor = when (tab.difficulty) {
        easyLabel -> DifficultyEasy
        mediumLabel -> DifficultyMedium
        hardLabel -> DifficultyHard
        else -> MaterialTheme.colorScheme.secondary
    }

    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onOpen,
        shape = MaterialTheme.shapes.medium
    ) {
        Column(Modifier.fillMaxWidth().padding(dimensionResource(R.dimen.spacing_medium))) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(tab.title, fontWeight = FontWeight.SemiBold)
                    Text(tab.artist.ifBlank { stringResource(R.string.unknown_artist) })
                }
                DebouncedIconButton(onClick = onToggleFavorite) {
                    Icon(
                        imageVector = if (tab.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = stringResource(R.string.favorite_toggle),
                        tint = if (tab.isFavorite) {
                            MaterialTheme.colorScheme.secondary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }
            Spacer(Modifier.height(spacingSmall))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Outlined.VpnKey,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(spacingSmall))
                Text(stringResource(R.string.detail_metadata_key, tab.key))
            }
            Spacer(Modifier.height(spacingSmall))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Outlined.Tune,
                    contentDescription = null,
                    tint = difficultyColor,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(spacingSmall))
                Text(
                    text = stringResource(R.string.detail_metadata_difficulty, tab.difficulty),
                    color = difficultyColor
                )
            }
            if (tab.tags.isNotBlank()) {
                Spacer(Modifier.height(spacingSmall))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Label,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(spacingSmall))
                    Text(
                        text = stringResource(R.string.tab_tags_line, tab.tags),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
