package com.chifunt.chromaticharptabs.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chifunt.chromaticharptabs.R
import com.chifunt.chromaticharptabs.ui.AppViewModelProvider
import com.chifunt.chromaticharptabs.ui.viewmodels.TabEditorViewModel
import com.chifunt.chromaticharptabs.ui.components.FilterDropdownButton
import com.chifunt.chromaticharptabs.ui.components.LabeledTextField
import com.chifunt.chromaticharptabs.ui.components.TabNotationEditor
import com.chifunt.chromaticharptabs.ui.components.difficultyOptions
import com.chifunt.chromaticharptabs.ui.components.keyOptions
import com.chifunt.chromaticharptabs.ui.components.TopBackBar

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
    val mediumLabel = stringResource(R.string.difficulty_medium)
    var holePickerTarget by remember { mutableStateOf<HolePickerTarget?>(null) }
    var lineToDelete by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(state.id, state.difficulty) {
        if (state.id <= 0 && state.difficulty.isBlank()) {
            tabEditorViewModel.updateDifficulty(mediumLabel)
        }
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(spacingMedium)
            .verticalScroll(rememberScrollState())
    ) {
        TopBackBar(onBack = onCancel)
        Spacer(Modifier.height(spacingSmall))

        Text(
            text = stringResource(R.string.editor_title),
            fontSize = dimensionResource(R.dimen.headline).value.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.height(spacingMedium))

        LabeledTextField(
            value = state.title,
            labelRes = R.string.title_label,
            onValueChange = tabEditorViewModel::updateTitle,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(spacingSmall))

        LabeledTextField(
            value = state.artist,
            labelRes = R.string.artist_label,
            onValueChange = tabEditorViewModel::updateArtist,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(spacingSmall))

        FilterDropdownButton(
            label = stringResource(R.string.key_label),
            selected = state.key.ifBlank { allLabel },
            options = keyOptions(),
            onSelected = { option ->
                tabEditorViewModel.updateKey(if (option == allLabel) "" else option)
            },
            minHeight = textFieldHeight,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(spacingSmall))

        FilterDropdownButton(
            label = stringResource(R.string.difficulty_label),
            selected = state.difficulty.ifBlank { mediumLabel },
            options = difficultyOptions().drop(1),
            onSelected = tabEditorViewModel::updateDifficulty,
            minHeight = textFieldHeight,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(spacingSmall))

        LabeledTextField(
            value = state.tempo,
            labelRes = R.string.tempo_label,
            onValueChange = { value ->
                tabEditorViewModel.updateTempo(value.filter { it.isDigit() })
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(spacingSmall))

        LabeledTextField(
            value = state.tags,
            labelRes = R.string.tags_label,
            onValueChange = tabEditorViewModel::updateTags,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(spacingSmall))

        Text(
            text = stringResource(R.string.tab_content_label),
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(spacingSmall))
        TabNotationEditor(
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
            lineSpacing = spacingMedium,
            modifier = Modifier.fillMaxWidth()
        )

        state.errorMessageResId?.let { messageResId ->
            Spacer(Modifier.height(spacingSmall))
            Text(text = stringResource(messageResId), color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(spacingMedium))

        Row(horizontalArrangement = Arrangement.spacedBy(spacingSmall), modifier = Modifier.fillMaxWidth()) {
            Button(onClick = { tabEditorViewModel.saveTab(onSaved) }, modifier = Modifier.weight(1f)) {
                Icon(imageVector = Icons.Filled.Save, contentDescription = null)
                Spacer(Modifier.width(spacingSmall))
                Text(stringResource(R.string.save_button))
            }
            TextButton(onClick = onCancel, modifier = Modifier.weight(1f)) {
                Text(stringResource(R.string.cancel_button))
            }
        }
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
}

private data class HolePickerTarget(val lineIndex: Int?, val noteIndex: Int? = null)

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
