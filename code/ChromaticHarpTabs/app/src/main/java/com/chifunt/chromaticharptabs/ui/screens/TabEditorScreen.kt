package com.chifunt.chromaticharptabs.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.automirrored.outlined.Label
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.TextFields
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.VpnKey
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
import com.chifunt.chromaticharptabs.ui.AppViewModelProvider
import com.chifunt.chromaticharptabs.ui.viewmodels.TabEditorUiState
import com.chifunt.chromaticharptabs.ui.viewmodels.TabEditorViewModel
import com.chifunt.chromaticharptabs.ui.components.filters.FilterDropdownButton
import com.chifunt.chromaticharptabs.ui.components.LabeledTextField
import com.chifunt.chromaticharptabs.ui.components.notation.TabNotationEditor
import com.chifunt.chromaticharptabs.ui.components.difficultyOptions
import com.chifunt.chromaticharptabs.ui.components.keyOptions
import com.chifunt.chromaticharptabs.ui.components.TopBackBar

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TabEditorScreen(
    modifier: Modifier = Modifier,
    tabEditorViewModel: TabEditorViewModel = viewModel(factory = AppViewModelProvider.Factory),
    onCancel: () -> Unit,
    onSaved: (Int) -> Unit
) {
    val state by tabEditorViewModel.uiState.collectAsStateWithLifecycle()
    val spacingSmall = dimensionResource(R.dimen.spacing_small)
    val spacingMedium = dimensionResource(R.dimen.spacing_medium)
    val textFieldHeight = dimensionResource(R.dimen.text_field_height)
    val allLabel = stringResource(R.string.filter_all)
    val keyDefault = stringResource(R.string.key_c)
    val mediumLabel = stringResource(R.string.difficulty_medium)
    var holePickerTarget by remember { mutableStateOf<HolePickerTarget?>(null) }
    var lineToDelete by remember { mutableStateOf<Int?>(null) }
    var showDiscardDialog by remember { mutableStateOf(false) }
    val isDirty by tabEditorViewModel.isDirty.collectAsStateWithLifecycle()

    val handleBack = {
        if (isDirty) {
            showDiscardDialog = true
        } else {
            onCancel()
        }
    }

    LaunchedEffect(state.id, keyDefault, mediumLabel) {
        tabEditorViewModel.applyDefaults(keyDefault, mediumLabel)
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(spacingMedium)
            .verticalScroll(rememberScrollState())
    ) {
        EditorHeader(
            onBack = handleBack,
            title = stringResource(R.string.editor_title),
            spacingSmall = spacingSmall,
            spacingMedium = spacingMedium
        )

        DetailsCard(
            state = state,
            onTitleChange = tabEditorViewModel::updateTitle,
            onArtistChange = tabEditorViewModel::updateArtist,
            onKeyChange = tabEditorViewModel::updateKey,
            onDifficultyChange = tabEditorViewModel::updateDifficulty,
            onTagsInputChange = tabEditorViewModel::updateTagsInput,
            onRemoveTag = tabEditorViewModel::removeTag,
            spacingSmall = spacingSmall,
            spacingMedium = spacingMedium,
            textFieldHeight = textFieldHeight,
            keyDefault = keyDefault,
            mediumLabel = mediumLabel
        )

        Spacer(Modifier.height(spacingMedium))

        ContentCard(
            lines = state.lines,
            onAddNote = { lineIndex -> holePickerTarget = HolePickerTarget(lineIndex = lineIndex) },
            onAddLine = { holePickerTarget = HolePickerTarget(lineIndex = null) },
            onDeleteLine = { lineIndex -> lineToDelete = lineIndex },
            onDeleteNote = tabEditorViewModel::removeNote,
            onEditHole = { lineIndex, noteIndex ->
                holePickerTarget = HolePickerTarget(lineIndex = lineIndex, noteIndex = noteIndex)
            },
            onToggleBlow = tabEditorViewModel::toggleBlow,
            onToggleSlide = tabEditorViewModel::toggleSlide,
            onMoveNote = tabEditorViewModel::moveNote,
            spacingSmall = spacingSmall,
            spacingMedium = spacingMedium
        )

        state.errorMessageResId?.let { messageResId ->
            Spacer(Modifier.height(spacingSmall))
            Text(text = stringResource(messageResId), color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(spacingMedium))

        ActionRow(
            onSave = { tabEditorViewModel.saveTab(onSaved) },
            onCancel = onCancel,
            spacingSmall = spacingSmall
        )
    }

    holePickerTarget?.let { target ->
        HolePickerDialog(
            onDismiss = { holePickerTarget = null },
            onHoleSelected = { hole ->
                if (target.noteIndex != null && target.lineIndex != null) {
                    tabEditorViewModel.updateHole(target.lineIndex, target.noteIndex, hole)
                } else if (target.lineIndex == null) {
                    tabEditorViewModel.addLineWithNote(hole)
                } else {
                    tabEditorViewModel.addNote(target.lineIndex, hole)
                }
                holePickerTarget = null
            }
        )
    }

    lineToDelete?.let { lineIndex ->
        AlertDialog(
            onDismissRequest = { lineToDelete = null },
            title = { Text(text = stringResource(R.string.delete_line_title)) },
            text = { Text(text = stringResource(R.string.delete_line_message)) },
            confirmButton = {
                TextButton(onClick = {
                    tabEditorViewModel.removeLine(lineIndex)
                    lineToDelete = null
                }) {
                    Text(text = stringResource(R.string.delete_line_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { lineToDelete = null }) {
                    Text(text = stringResource(R.string.cancel_button))
                }
            }
        )
    }

    if (showDiscardDialog) {
        AlertDialog(
            onDismissRequest = { showDiscardDialog = false },
            title = { Text(text = stringResource(R.string.discard_changes_title)) },
            text = { Text(text = stringResource(R.string.discard_changes_message)) },
            confirmButton = {
                Button(onClick = {
                    showDiscardDialog = false
                    tabEditorViewModel.saveTab(onSaved)
                }) {
                    Text(text = stringResource(R.string.save_button))
                }
            },
            dismissButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(spacingSmall)) {
                    TextButton(onClick = { showDiscardDialog = false }) {
                        Text(text = stringResource(R.string.cancel_button))
                    }
                    TextButton(onClick = {
                        showDiscardDialog = false
                        onCancel()
                    }) {
                        Text(text = stringResource(R.string.discard_button))
                    }
                }
            }
        )
    }

    BackHandler(onBack = handleBack)
}

private data class HolePickerTarget(val lineIndex: Int?, val noteIndex: Int? = null)

@Composable
private fun EditorHeader(
    onBack: () -> Unit,
    title: String,
    spacingSmall: Dp,
    spacingMedium: Dp
) {
    TopBackBar(onBack = onBack)
    Spacer(Modifier.height(spacingSmall))
    Text(
        text = title,
        fontSize = dimensionResource(R.dimen.headline).value.sp,
        fontWeight = FontWeight.SemiBold
    )
    Spacer(Modifier.height(spacingMedium))
}

@Composable
private fun DetailsCard(
    state: TabEditorUiState,
    onTitleChange: (String) -> Unit,
    onArtistChange: (String) -> Unit,
    onKeyChange: (String) -> Unit,
    onDifficultyChange: (String) -> Unit,
    onTagsInputChange: (String) -> Unit,
    onRemoveTag: (String) -> Unit,
    spacingSmall: Dp,
    spacingMedium: Dp,
    textFieldHeight: Dp,
    keyDefault: String,
    mediumLabel: String
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
                    imageVector = Icons.Outlined.Description,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.width(spacingSmall))
                Text(
                    text = stringResource(R.string.editor_section_details),
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(spacingSmall))
            LabeledTextField(
                value = state.title,
                labelRes = R.string.title_label,
                onValueChange = onTitleChange,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.TextFields,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            )
            Spacer(Modifier.height(spacingSmall))

            LabeledTextField(
                value = state.artist,
                labelRes = R.string.artist_label,
                onValueChange = onArtistChange,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            )
            Spacer(Modifier.height(spacingSmall))

            FilterDropdownButton(
                label = stringResource(R.string.key_label),
                selected = state.key.ifBlank { keyDefault },
                options = keyOptions().drop(1),
                onSelected = onKeyChange,
                minHeight = textFieldHeight,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.VpnKey,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(spacingSmall))

            FilterDropdownButton(
                label = stringResource(R.string.difficulty_label),
                selected = state.difficulty.ifBlank { mediumLabel },
                options = difficultyOptions().drop(1),
                onSelected = onDifficultyChange,
                minHeight = textFieldHeight,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Tune,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(spacingSmall))

            LabeledTextField(
                value = state.tagsInput,
                labelRes = R.string.tags_label,
                onValueChange = onTagsInputChange,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.Label,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                }
            )
            if (state.tags.isNotEmpty()) {
                Spacer(Modifier.height(spacingSmall))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(spacingSmall),
                    verticalArrangement = Arrangement.spacedBy(spacingSmall),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    state.tags.forEach { tag ->
                        AssistChip(
                            onClick = {},
                            label = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(tag)
                                    Spacer(Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.Filled.Close,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier
                                            .size(14.dp)
                                            .clickable { onRemoveTag(tag) }
                                    )
                                }
                            },
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

@Composable
private fun ContentCard(
    lines: List<List<TabNote>>,
    onAddNote: (Int) -> Unit,
    onAddLine: () -> Unit,
    onDeleteLine: (Int) -> Unit,
    onDeleteNote: (Int, Int) -> Unit,
    onEditHole: (Int, Int) -> Unit,
    onToggleBlow: (Int, Int) -> Unit,
    onToggleSlide: (Int, Int) -> Unit,
    onMoveNote: (Int, Int, Int) -> Unit,
    spacingSmall: Dp,
    spacingMedium: Dp
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
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.width(spacingSmall))
                Text(
                    text = stringResource(R.string.editor_section_content),
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(spacingSmall))
            TabNotationEditor(
                lines = lines,
                onAddNote = onAddNote,
                onAddLine = onAddLine,
                onDeleteLine = onDeleteLine,
                onDeleteNote = onDeleteNote,
                onEditHole = onEditHole,
                onToggleBlow = onToggleBlow,
                onToggleSlide = onToggleSlide,
                onMoveNote = onMoveNote,
                lineSpacing = spacingMedium,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun ActionRow(
    onSave: () -> Unit,
    onCancel: () -> Unit,
    spacingSmall: Dp
) {
    Row(horizontalArrangement = Arrangement.spacedBy(spacingSmall), modifier = Modifier.fillMaxWidth()) {
        Button(onClick = onSave, modifier = Modifier.weight(1f)) {
            Icon(imageVector = Icons.Filled.Save, contentDescription = null)
            Spacer(Modifier.width(spacingSmall))
            Text(stringResource(R.string.save_button))
        }
        TextButton(onClick = onCancel, modifier = Modifier.weight(1f)) {
            Text(stringResource(R.string.cancel_button))
        }
    }
}

@Composable
private fun HolePickerDialog(
    onDismiss: () -> Unit,
    onHoleSelected: (Int) -> Unit
) {
    val spacingSmall = dimensionResource(R.dimen.spacing_small)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.hole_picker_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(spacingSmall)) {
                for (rowIndex in 0 until 3) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(spacingSmall),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        for (columnIndex in 0 until 4) {
                            val hole = rowIndex * 4 + columnIndex + 1
                            Button(
                                onClick = { onHoleSelected(hole) },
                                modifier = Modifier.size(56.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(
                                    text = hole.toString(),
                                    maxLines = 1,
                                    softWrap = false,
                                    fontSize = 24.sp
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.cancel_button))
            }
        }
    )
}
