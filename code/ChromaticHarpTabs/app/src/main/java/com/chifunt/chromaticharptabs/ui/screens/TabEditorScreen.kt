package com.chifunt.chromaticharptabs.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chifunt.chromaticharptabs.R
import com.chifunt.chromaticharptabs.ui.AppViewModelProvider
import com.chifunt.chromaticharptabs.ui.audio.SineTonePlayer
import com.chifunt.chromaticharptabs.ui.components.common.DebouncedIconButton
import com.chifunt.chromaticharptabs.ui.viewmodels.TabEditorViewModel
import com.chifunt.chromaticharptabs.ui.components.editor.ContentCard
import com.chifunt.chromaticharptabs.ui.components.editor.DetailsCard
import com.chifunt.chromaticharptabs.ui.components.editor.HolePickerDialog
import com.chifunt.chromaticharptabs.ui.components.common.TopBackBar
import com.chifunt.chromaticharptabs.ui.components.common.HapticButton
import com.chifunt.chromaticharptabs.ui.components.common.HapticTextButton

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
    val keyDefault = stringResource(R.string.key_c)
    val mediumLabel = stringResource(R.string.difficulty_medium)
    var holePickerTarget by remember { mutableStateOf<HolePickerTarget?>(null) }
    var lineToDelete by remember { mutableStateOf<Int?>(null) }
    var showDiscardDialog by remember { mutableStateOf(false) }
    val isDirty by tabEditorViewModel.isDirty.collectAsStateWithLifecycle()
    val tonePlayer = remember { SineTonePlayer() }
    val scrollState = rememberScrollState()
    val density = LocalDensity.current
    var topBarHeightPx by remember { mutableIntStateOf(0) }
    val topBarHeight = with(density) { topBarHeightPx.toDp() }
    val isScrolled = scrollState.value > with(density) { spacingSmall.toPx() }
    val topBarColor = if (isScrolled) MaterialTheme.colorScheme.surface else Color.Transparent
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    DisposableEffect(Unit) {
        onDispose { tonePlayer.release() }
    }

    LaunchedEffect(state.errorMessageResId) {
        state.errorMessageResId?.let { resId ->
            snackbarHostState.showSnackbar(context.getString(resId))
        }
    }

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
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    PaddingValues(
                        start = spacingMedium,
                        end = spacingMedium,
                        bottom = spacingMedium
                    )
                )
                .padding(top = topBarHeight + spacingSmall)
                .imePadding()
                .verticalScroll(scrollState)
        ) {
            Text(
                text = stringResource(R.string.editor_title),
                fontSize = dimensionResource(R.dimen.headline).value.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(spacingMedium))

            DetailsCard(
                state = state,
                onTitleChange = tabEditorViewModel::updateTitle,
                onArtistChange = tabEditorViewModel::updateArtist,
                onKeyChange = tabEditorViewModel::updateKey,
                onDifficultyChange = tabEditorViewModel::updateDifficulty,
                onTagsInputChange = tabEditorViewModel::updateTagsInput,
                onCommitTag = tabEditorViewModel::commitTagsInput,
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
                onPreviewNote = { lineIndex, noteIndex ->
                    val note = state.lines.getOrNull(lineIndex)?.getOrNull(noteIndex)
                    note?.let { tabEditorViewModel.frequencyFor(it) }?.let { tonePlayer.start(it) }
                },
                onPreviewStop = { tonePlayer.stop() },
                spacingSmall = spacingSmall,
                spacingMedium = spacingMedium
            )

        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    topBarHeightPx = coordinates.size.height
                }
                .background(topBarColor)
                .padding(horizontal = spacingMedium, vertical = spacingSmall)
                .align(Alignment.TopCenter)
        ) {
            TopBackBar(
                onBack = handleBack,
                actions = {
                    DebouncedIconButton(onClick = { tabEditorViewModel.saveTab(onSaved) }) {
                        Icon(
                            imageVector = Icons.Filled.Save,
                            contentDescription = stringResource(R.string.save_button),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .imePadding()
                .padding(horizontal = spacingMedium, vertical = spacingSmall)
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
                HapticTextButton(onClick = {
                    tabEditorViewModel.removeLine(lineIndex)
                    lineToDelete = null
                }) {
                    Text(text = stringResource(R.string.delete_line_confirm))
                }
            },
            dismissButton = {
                HapticTextButton(onClick = { lineToDelete = null }) {
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
                HapticButton(onClick = {
                    showDiscardDialog = false
                    tabEditorViewModel.saveTab(onSaved)
                }) {
                    Text(text = stringResource(R.string.save_button))
                }
            },
            dismissButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(spacingSmall)) {
                    HapticTextButton(onClick = { showDiscardDialog = false }) {
                        Text(text = stringResource(R.string.cancel_button))
                    }
                    HapticTextButton(onClick = {
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
