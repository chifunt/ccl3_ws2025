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
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.automirrored.outlined.Label
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.VpnKey
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chifunt.chromaticharptabs.R
import com.chifunt.chromaticharptabs.data.TabNote
import com.chifunt.chromaticharptabs.data.TabNotationJson
import com.chifunt.chromaticharptabs.ui.AppViewModelProvider
import com.chifunt.chromaticharptabs.ui.components.DebouncedIconButton
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
    val spacingTight = 1.dp
    val showNotationInfo = remember { mutableStateOf(false) }

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
                    DebouncedIconButton(onClick = { showNotationInfo.value = true }) {
                        Icon(
                        imageVector = Icons.AutoMirrored.Outlined.HelpOutline,
                            contentDescription = stringResource(R.string.notation_info_button)
                        )
                    }
                    DebouncedIconButton(onClick = { onEdit(state.tab.id) }) {
                        Icon(Icons.Filled.Edit, contentDescription = stringResource(R.string.edit_button))
                    }
                    DebouncedIconButton(onClick = { tabDetailViewModel.removeTab { onBack() } }) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = stringResource(R.string.delete_button),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        )
        if (showNotationInfo.value) {
            AlertDialog(
                onDismissRequest = { showNotationInfo.value = false },
                confirmButton = {
                    Button(onClick = { showNotationInfo.value = false }) {
                        Text(text = stringResource(R.string.close_button))
                    }
                },
                title = { Text(text = stringResource(R.string.notation_info_title)) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(spacingSmall)) {
                        Text(text = stringResource(R.string.notation_info_blow))
                        TabNotationInlineDisplay(
                            lines = listOf(listOf(TabNote(hole = 4, isBlow = true, isSlide = false)))
                        )
                        Spacer(Modifier.height(spacingSmall))
                        Text(text = stringResource(R.string.notation_info_draw))
                        TabNotationInlineDisplay(
                            lines = listOf(listOf(TabNote(hole = 4, isBlow = false, isSlide = false)))
                        )
                        Spacer(Modifier.height(spacingSmall))
                        Text(text = stringResource(R.string.notation_info_slide))
                        TabNotationInlineDisplay(
                            lines = listOf(listOf(TabNote(hole = 4, isBlow = true, isSlide = true)))
                        )
                        Spacer(Modifier.height(spacingSmall))
                        Text(text = stringResource(R.string.notation_info_draw_slide))
                        TabNotationInlineDisplay(
                            lines = listOf(listOf(TabNote(hole = 4, isBlow = false, isSlide = true)))
                        )
                    }
                }
            )
        }
        Spacer(Modifier.height(spacingSmall))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.weight(1f).padding(start = spacingSmall)) {
                Text(state.tab.title, fontWeight = FontWeight.SemiBold, fontSize = 20.sp)
                Text(state.tab.artist.ifBlank { stringResource(R.string.unknown_artist) })
            }
            DebouncedIconButton(onClick = { tabDetailViewModel.toggleFavorite() }) {
                Icon(
                    imageVector = if (state.tab.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = stringResource(R.string.favorite_toggle)
                )
            }
        }

        Spacer(Modifier.height(spacingMedium))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(Modifier.padding(spacingMedium)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(Modifier.width(spacingSmall))
                    Text(
                        text = stringResource(R.string.detail_metadata_title),
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Spacer(Modifier.height(spacingSmall))
                Column(verticalArrangement = Arrangement.spacedBy(spacingTight)) {
                    AssistChip(
                        onClick = {},
                        label = {
                            Text(
                                text = stringResource(
                                    R.string.detail_metadata_key,
                                    state.tab.key.ifBlank { stringResource(R.string.unknown_value) }
                                )
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.VpnKey,
                                contentDescription = null
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            labelColor = MaterialTheme.colorScheme.onSurface,
                            leadingIconContentColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    AssistChip(
                        onClick = {},
                        label = { Text(stringResource(R.string.detail_metadata_difficulty, state.tab.difficulty)) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Tune,
                                contentDescription = null
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            labelColor = MaterialTheme.colorScheme.onSurface,
                            leadingIconContentColor = MaterialTheme.colorScheme.secondary
                        )
                    )
                    AssistChip(
                        onClick = {},
                        label = {
                            Text(
                                text = stringResource(
                                    R.string.detail_metadata_tempo,
                                    state.tab.tempo?.toString() ?: stringResource(R.string.tempo_unknown)
                                )
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Speed,
                                contentDescription = null
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            labelColor = MaterialTheme.colorScheme.onSurface,
                            leadingIconContentColor = MaterialTheme.colorScheme.tertiary
                        )
                    )
                    if (state.tab.tags.isNotBlank()) {
                        AssistChip(
                            onClick = {},
                            label = { Text(stringResource(R.string.detail_metadata_tags, state.tab.tags)) },
                            leadingIcon = {
                                Icon(
                                imageVector = Icons.AutoMirrored.Outlined.Label,
                                    contentDescription = null
                                )
                            },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                                labelColor = MaterialTheme.colorScheme.onSurface,
                                leadingIconContentColor = MaterialTheme.colorScheme.tertiary
                            )
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(spacingMedium))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            )
        ) {
            Column(Modifier.padding(spacingMedium)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.MusicNote,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    Spacer(Modifier.width(spacingSmall))
                    Text(
                        text = stringResource(R.string.tab_content_label),
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
                Spacer(Modifier.height(spacingMedium))
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
