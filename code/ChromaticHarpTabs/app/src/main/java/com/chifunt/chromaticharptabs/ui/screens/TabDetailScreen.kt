package com.chifunt.chromaticharptabs.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalDensity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chifunt.chromaticharptabs.R
import com.chifunt.chromaticharptabs.data.notation.HarmonicaNoteMap
import com.chifunt.chromaticharptabs.ui.AppViewModelProvider
import com.chifunt.chromaticharptabs.ui.audio.SineTonePlayer
import com.chifunt.chromaticharptabs.ui.components.detail.DeleteConfirmDialog
import com.chifunt.chromaticharptabs.ui.components.detail.DetailTopBar
import com.chifunt.chromaticharptabs.ui.components.detail.MetadataSection
import com.chifunt.chromaticharptabs.ui.components.detail.NotationInfoDialog
import com.chifunt.chromaticharptabs.ui.components.detail.NotationSection
import com.chifunt.chromaticharptabs.ui.components.detail.TitleRow
import com.chifunt.chromaticharptabs.ui.components.common.HapticFloatingActionButton
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
    val showDeleteConfirm = remember { mutableStateOf(false) }
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val contentTextColor = if (isDark) RosePineText else RosePineDawnText
    val tonePlayer = remember { SineTonePlayer() }
    val scrollState = rememberScrollState()
    val density = LocalDensity.current
    var topBarHeightPx by remember { mutableIntStateOf(0) }
    val topBarHeight = with(density) { topBarHeightPx.toDp() }
    val isScrolled = scrollState.value > with(density) { spacingSmall.toPx() }
    val topBarColor = if (isScrolled) MaterialTheme.colorScheme.surface else Color.Transparent

    DisposableEffect(Unit) {
        onDispose { tonePlayer.release() }
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        NotationInfoDialog(
            isVisible = showNotationInfo.value,
            spacingSmall = spacingSmall,
            onDismiss = { showNotationInfo.value = false }
        )
        DeleteConfirmDialog(
            isVisible = showDeleteConfirm.value,
            onDismiss = { showDeleteConfirm.value = false },
            onConfirm = {
                showDeleteConfirm.value = false
                tabDetailViewModel.removeTab { onBack() }
            }
        )

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
                .padding(top = topBarHeight)
                .verticalScroll(scrollState)
        ) {
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
                contentTextColor = contentTextColor,
                onNotePress = { note ->
                    HarmonicaNoteMap.frequencyFor(note)?.let { tonePlayer.start(it) }
                },
                onNoteRelease = { tonePlayer.stop() }
            )

            Spacer(Modifier.height(88.dp))

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
            DetailTopBar(
                onBack = onBack,
                onShowNotationInfo = { showNotationInfo.value = true },
                onEdit = { onEdit(state.tab.id) },
                onDelete = { showDeleteConfirm.value = true }
            )
        }

        HapticFloatingActionButton(
            onClick = { onPractice(state.tab.id) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(spacingMedium + spacingSmall),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = stringResource(R.string.practice_button)
            )
        }
    }
}
