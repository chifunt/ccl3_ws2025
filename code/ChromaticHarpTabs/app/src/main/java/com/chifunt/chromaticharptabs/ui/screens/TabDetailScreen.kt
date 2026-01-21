package com.chifunt.chromaticharptabs.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.automirrored.outlined.Label
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.VpnKey
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chifunt.chromaticharptabs.R
import com.chifunt.chromaticharptabs.data.TabNote
import com.chifunt.chromaticharptabs.data.TabNotationJson
import com.chifunt.chromaticharptabs.ui.AppViewModelProvider
import com.chifunt.chromaticharptabs.ui.components.DebouncedIconButton
import com.chifunt.chromaticharptabs.ui.components.FavoriteToggleButton
import com.chifunt.chromaticharptabs.ui.components.TagChip
import com.chifunt.chromaticharptabs.ui.components.cards.MetadataPill
import com.chifunt.chromaticharptabs.ui.components.TopBackBar
import com.chifunt.chromaticharptabs.ui.components.notation.TabNotationInlineDisplay
import com.chifunt.chromaticharptabs.data.parseTags
import com.chifunt.chromaticharptabs.ui.theme.RosePineDawnText
import com.chifunt.chromaticharptabs.ui.theme.RosePineText
import com.chifunt.chromaticharptabs.ui.viewmodels.TabDetailViewModel

@OptIn(ExperimentalLayoutApi::class)
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
    val spacingTight = 8.dp
    val showNotationInfo = remember { mutableStateOf(false) }
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val contentTextColor = if (isDark) RosePineText else RosePineDawnText

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(spacingMedium)
            .verticalScroll(rememberScrollState())
    ) {
        DetailTopBar(
            onBack = onBack,
            onShowNotationInfo = { showNotationInfo.value = true },
            onEdit = { onEdit(state.tab.id) },
            onDelete = { tabDetailViewModel.removeTab { onBack() } }
        )
        NotationInfoDialog(
            isVisible = showNotationInfo.value,
            spacingSmall = spacingSmall,
            onDismiss = { showNotationInfo.value = false }
        )
        Spacer(Modifier.height(spacingSmall))

        TitleRow(
            title = state.tab.title,
            artist = state.tab.artist,
            isFavorite = state.tab.isFavorite,
            spacingSmall = spacingSmall,
            onToggleFavorite = tabDetailViewModel::toggleFavorite
        )

        Spacer(Modifier.height(spacingMedium))

        MetadataSection(
            key = state.tab.key,
            difficulty = state.tab.difficulty,
            tags = state.tab.tags,
            spacingSmall = spacingSmall,
            spacingMedium = spacingMedium,
            spacingTight = spacingTight
        )

        Spacer(Modifier.height(spacingMedium))

        NotationSection(
            content = state.tab.content,
            spacingSmall = spacingSmall,
            spacingMedium = spacingMedium,
            contentTextColor = contentTextColor
        )

        Spacer(Modifier.height(spacingMedium))

        PracticeRow(
            onPractice = { onPractice(state.tab.id) },
            spacingSmall = spacingSmall
        )
    }
}

@Composable
private fun DetailTopBar(
    onBack: () -> Unit,
    onShowNotationInfo: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    TopBackBar(
        onBack = onBack,
        actions = {
            Row {
                DebouncedIconButton(onClick = onShowNotationInfo) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.HelpOutline,
                        contentDescription = stringResource(R.string.notation_info_button)
                    )
                }
                DebouncedIconButton(onClick = onEdit) {
                    Icon(Icons.Filled.Edit, contentDescription = stringResource(R.string.edit_button))
                }
                DebouncedIconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = stringResource(R.string.delete_button),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    )
}

@Composable
private fun NotationInfoDialog(
    isVisible: Boolean,
    spacingSmall: Dp,
    onDismiss: () -> Unit
) {
    if (!isVisible) {
        return
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = onDismiss) {
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

@Composable
private fun TitleRow(
    title: String,
    artist: String,
    isFavorite: Boolean,
    spacingSmall: Dp,
    onToggleFavorite: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.weight(1f).padding(start = spacingSmall)) {
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 20.sp)
            Text(artist.ifBlank { stringResource(R.string.unknown_artist) })
        }
        FavoriteToggleButton(
            isFavorite = isFavorite,
            onToggle = onToggleFavorite,
            contentDescriptionRes = R.string.favorite_toggle
        )
    }
}

@Composable
private fun PracticeRow(
    onPractice: () -> Unit,
    spacingSmall: Dp
) {
    Row(horizontalArrangement = Arrangement.spacedBy(spacingSmall), modifier = Modifier.fillMaxWidth()) {
        Button(onClick = onPractice, modifier = Modifier.weight(1f)) {
            Icon(Icons.Filled.PlayArrow, contentDescription = null)
            Spacer(Modifier.width(spacingSmall))
            Text(stringResource(R.string.practice_button))
        }
    }
}

@Composable
private fun MetadataSection(
    key: String,
    difficulty: String,
    tags: String,
    spacingSmall: Dp,
    spacingMedium: Dp,
    spacingTight: Dp
) {
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
            Spacer(Modifier.height(spacingMedium))
            Column(verticalArrangement = Arrangement.spacedBy(spacingTight)) {
                MetadataPill(
                    text = stringResource(
                        R.string.detail_metadata_key,
                        key.ifBlank { stringResource(R.string.unknown_value) }
                    ),
                    icon = Icons.Outlined.VpnKey,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    iconTint = MaterialTheme.colorScheme.primary
                )
                MetadataPill(
                    text = stringResource(R.string.detail_metadata_difficulty, difficulty),
                    icon = Icons.Outlined.Tune,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    iconTint = MaterialTheme.colorScheme.secondary
                )
                val tagList = parseTags(tags)
                if (tagList.isNotEmpty()) {
                    Spacer(Modifier.height(spacingTight))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.Label,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                        Spacer(Modifier.width(spacingSmall))
                        Text(
                            text = stringResource(R.string.detail_metadata_tags_label),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Spacer(Modifier.height(spacingTight))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(spacingSmall),
                        verticalArrangement = Arrangement.spacedBy(spacingSmall)
                    ) {
                        tagList.forEach { tag ->
                            TagChip(text = tag)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NotationSection(
    content: String,
    spacingSmall: Dp,
    spacingMedium: Dp,
    contentTextColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(Modifier.padding(spacingMedium)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.MusicNote,
                    contentDescription = null,
                    tint = contentTextColor
                )
                Spacer(Modifier.width(spacingSmall))
                Text(
                    text = stringResource(R.string.tab_content_label),
                    fontWeight = FontWeight.SemiBold,
                    color = contentTextColor
                )
            }
            Spacer(Modifier.height(spacingMedium))
            val notation = TabNotationJson.fromJson(content)
            if (notation != null && notation.lines.isNotEmpty()) {
                TabNotationInlineDisplay(
                    lines = notation.lines,
                    lineSpacing = spacingMedium,
                    glyphColor = contentTextColor
                )
            } else {
                Text(content, color = contentTextColor)
            }
        }
    }
}
