package com.chifunt.chromaticharptabs.ui.components.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.VpnKey
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.chifunt.chromaticharptabs.R
import com.chifunt.chromaticharptabs.data.Tab
import com.chifunt.chromaticharptabs.ui.components.DebouncedIconButton
import com.chifunt.chromaticharptabs.ui.theme.DifficultyEasy
import com.chifunt.chromaticharptabs.ui.theme.DifficultyHard
import com.chifunt.chromaticharptabs.ui.theme.DifficultyMedium
import com.chifunt.chromaticharptabs.data.parseTags
import com.chifunt.chromaticharptabs.ui.theme.ChromaticHarpTabsTheme

@OptIn(ExperimentalLayoutApi::class)
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
            val tagList = parseTags(tab.tags)
            if (tagList.isNotEmpty()) {
                Spacer(Modifier.height(spacingSmall))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(spacingSmall),
                    verticalArrangement = Arrangement.spacedBy(spacingSmall),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    tagList.forEach { tag ->
                        AssistChip(
                            onClick = {},
                            label = { Text(tag) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                labelColor = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TabCardPreview() {
    ChromaticHarpTabsTheme(darkTheme = true) {
        TabCard(
            tab = Tab(
                id = 1,
                title = "Autumn Leaves",
                artist = "Joseph Kosma",
                key = "G",
                difficulty = "Medium",
                tags = "jazz ballad",
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

@Preview(showBackground = true)
@Composable
private fun TabCardNoTagsPreview() {
    ChromaticHarpTabsTheme(darkTheme = false) {
        TabCard(
            tab = Tab(
                id = 2,
                title = "Blue Bossa",
                artist = "Kenny Dorham",
                key = "C",
                difficulty = "Easy",
                tags = "",
                content = "",
                isFavorite = false,
                createdAt = 0L,
                updatedAt = 0L
            ),
            onOpen = {},
            onToggleFavorite = {}
        )
    }
}
