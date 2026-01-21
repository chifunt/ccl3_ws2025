package com.chifunt.chromaticharptabs.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chifunt.chromaticharptabs.R
import com.chifunt.chromaticharptabs.data.HarmonicaNoteMap
import com.chifunt.chromaticharptabs.ui.AppViewModelProvider
import com.chifunt.chromaticharptabs.ui.audio.SineTonePlayer
import com.chifunt.chromaticharptabs.ui.components.DebouncedFilledIconButton
import com.chifunt.chromaticharptabs.ui.components.notation.TabNotationInlineDisplay
import com.chifunt.chromaticharptabs.ui.viewmodels.PracticeViewModel
import com.chifunt.chromaticharptabs.ui.components.TopBackBar

@Composable
fun PracticeScreen(
    modifier: Modifier = Modifier,
    practiceViewModel: PracticeViewModel = viewModel(factory = AppViewModelProvider.Factory),
    onBack: () -> Unit
) {
    val state by practiceViewModel.uiState.collectAsStateWithLifecycle()
    val spacingMedium = dimensionResource(R.dimen.spacing_medium)
    val tonePlayer = remember { SineTonePlayer() }

    DisposableEffect(Unit) {
        onDispose { tonePlayer.release() }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(spacingMedium),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopBackBar(onBack = onBack)

        Scaffold { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = state.title, fontWeight = FontWeight.SemiBold, fontSize = 18.sp)

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    TabNotationInlineDisplay(
                        lines = listOf(state.lines[state.currentIndex]),
                        lineSpacing = spacingMedium,
                        centered = true,
                        onNotePress = { note ->
                            HarmonicaNoteMap.frequencyFor(note)?.let { tonePlayer.start(it) }
                        },
                        onNoteRelease = { tonePlayer.stop() }
                    )
                }

                Text(
                    text = stringResource(
                        R.string.practice_line_counter,
                        state.currentIndex + 1,
                        state.lines.size
                    ),
                    fontWeight = FontWeight.Medium
                )

                Spacer(Modifier.height(spacingMedium))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(spacingMedium),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    DebouncedFilledIconButton(
                        onClick = { practiceViewModel.previousLine() },
                        enabled = state.currentIndex > 0,
                        debounceMs = 0L,
                        modifier = Modifier
                            .weight(1f)
                            .height(dimensionResource(R.dimen.filter_chip_height))
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                    DebouncedFilledIconButton(
                        onClick = { practiceViewModel.nextLine() },
                        enabled = state.currentIndex < state.lines.lastIndex,
                        debounceMs = 0L,
                        modifier = Modifier
                            .weight(1f)
                            .height(dimensionResource(R.dimen.filter_chip_height))
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                    }
                }
            }
        }
    }
}
