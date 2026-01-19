package com.chifunt.chromaticharptabs.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chifunt.chromaticharptabs.R
import com.chifunt.chromaticharptabs.ui.AppViewModelProvider
import com.chifunt.chromaticharptabs.ui.viewmodels.TabEditorViewModel
import com.chifunt.chromaticharptabs.ui.components.FilterDropdownButton
import com.chifunt.chromaticharptabs.ui.components.LabeledTextField
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
    val allLabel = stringResource(R.string.filter_all)
    val mediumLabel = stringResource(R.string.difficulty_medium)

    LaunchedEffect(state.id, state.difficulty) {
        if (state.id <= 0 && state.difficulty.isBlank()) {
            tabEditorViewModel.updateDifficulty(mediumLabel)
        }
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(spacingMedium)
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
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(spacingSmall))

        FilterDropdownButton(
            label = stringResource(R.string.difficulty_label),
            selected = state.difficulty.ifBlank { mediumLabel },
            options = difficultyOptions().drop(1),
            onSelected = tabEditorViewModel::updateDifficulty,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(spacingSmall))

        LabeledTextField(
            value = state.tempo,
            labelRes = R.string.tempo_label,
            onValueChange = tabEditorViewModel::updateTempo,
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

        LabeledTextField(
            value = state.content,
            labelRes = R.string.tab_content_label,
            onValueChange = tabEditorViewModel::updateContent,
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(R.dimen.editor_content_height)),
            singleLine = false,
            maxLines = 12
        )

        state.errorMessageResId?.let { messageResId ->
            Spacer(Modifier.height(spacingSmall))
            Text(text = stringResource(messageResId), color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(spacingMedium))

        Row(horizontalArrangement = Arrangement.spacedBy(spacingSmall), modifier = Modifier.fillMaxWidth()) {
            Button(onClick = { tabEditorViewModel.saveTab(onSaved) }, modifier = Modifier.weight(1f)) {
                Text(stringResource(R.string.save_button))
            }
            TextButton(onClick = onCancel, modifier = Modifier.weight(1f)) {
                Text(stringResource(R.string.cancel_button))
            }
        }
    }
}
