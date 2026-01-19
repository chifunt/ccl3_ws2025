package com.chifunt.chromaticharptabs.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chifunt.chromaticharptabs.R
import com.chifunt.chromaticharptabs.data.TabNotationJson
import com.chifunt.chromaticharptabs.ui.AppViewModelProvider
import com.chifunt.chromaticharptabs.ui.components.TopBackBar
import com.chifunt.chromaticharptabs.ui.components.TabNotationInlineDisplay
import com.chifunt.chromaticharptabs.ui.viewmodels.TabDetailViewModel

@Composable
fun TabDetailScreen(
    modifier: Modifier = Modifier,
    tabDetailViewModel: TabDetailViewModel = viewModel(factory = AppViewModelProvider.Factory),
    onBack: () -> Unit,
    onEdit: (Int) -> Unit,
    onPractice: (Int) -> Unit
) {
    val state by tabDetailViewModel.uiState.collectAsStateWithLifecycle()
    val spacingMedium = dimensionResource(R.dimen.spacing_medium)
    val spacingSmall = dimensionResource(R.dimen.spacing_small)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(spacingMedium)
            .verticalScroll(rememberScrollState())
    ) {
        TopBackBar(
            onBack = onBack,
            actions = {
                Row {
                    IconButton(onClick = { onEdit(state.tab.id) }) {
                        Icon(Icons.Filled.Edit, contentDescription = stringResource(R.string.edit_button))
                    }
                    IconButton(onClick = { tabDetailViewModel.removeTab { onBack() } }) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = stringResource(R.string.delete_button),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        )
        Spacer(Modifier.height(spacingSmall))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(state.tab.title, fontWeight = FontWeight.SemiBold, fontSize = 20.sp)
                Text(state.tab.artist.ifBlank { stringResource(R.string.unknown_artist) })
            }
            IconButton(onClick = { tabDetailViewModel.toggleFavorite() }) {
                Icon(
                    imageVector = if (state.tab.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = stringResource(R.string.favorite_toggle)
                )
            }
        }

        Spacer(Modifier.height(spacingMedium))

        OutlinedCard(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium) {
            Column(Modifier.padding(spacingMedium)) {
                Text(stringResource(R.string.detail_metadata_title))
                Spacer(Modifier.height(dimensionResource(R.dimen.spacing_small)))
                Text(
                    stringResource(
                        R.string.detail_metadata_key,
                        state.tab.key.ifBlank { stringResource(R.string.unknown_value) }
                    )
                )
                Text(stringResource(R.string.detail_metadata_difficulty, state.tab.difficulty))
                Text(
                    stringResource(
                        R.string.detail_metadata_tempo,
                        state.tab.tempo?.toString() ?: stringResource(R.string.tempo_unknown)
                    )
                )
                if (state.tab.tags.isNotBlank()) {
                    Text(stringResource(R.string.detail_metadata_tags, state.tab.tags))
                }
            }
        }

        Spacer(Modifier.height(spacingMedium))

        OutlinedCard(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium) {
            Column(Modifier.padding(spacingMedium)) {
                Text(stringResource(R.string.tab_content_label))
                Spacer(Modifier.height(dimensionResource(R.dimen.spacing_small)))
                val notation = TabNotationJson.fromJson(state.tab.content)
                if (notation != null && notation.lines.isNotEmpty()) {
                    TabNotationInlineDisplay(
                        lines = notation.lines,
                        lineSpacing = spacingMedium
                    )
                } else {
                    Text(state.tab.content)
                }
            }
        }

        Spacer(Modifier.height(spacingMedium))

        Row(horizontalArrangement = Arrangement.spacedBy(spacingSmall), modifier = Modifier.fillMaxWidth()) {
            Button(onClick = { onPractice(state.tab.id) }, modifier = Modifier.weight(1f)) {
                Icon(Icons.Filled.PlayArrow, contentDescription = null)
                Spacer(Modifier.width(dimensionResource(R.dimen.spacing_small)))
                Text(stringResource(R.string.practice_button))
            }
        }
    }
}
