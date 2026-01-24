package com.chifunt.chromaticharptabs.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chifunt.chromaticharptabs.R
import com.chifunt.chromaticharptabs.data.model.TabNote
import com.chifunt.chromaticharptabs.data.model.Tab
import com.chifunt.chromaticharptabs.data.notation.HarmonicaNoteMap
import com.chifunt.chromaticharptabs.ui.AppViewModelProvider
import com.chifunt.chromaticharptabs.ui.audio.SineTonePlayer
import com.chifunt.chromaticharptabs.ui.components.cards.TabCard
import com.chifunt.chromaticharptabs.ui.components.notation.TabNotationEditor
import com.chifunt.chromaticharptabs.ui.components.notation.TabNotationInlineDisplay
import com.chifunt.chromaticharptabs.ui.components.common.HapticButton
import com.chifunt.chromaticharptabs.ui.components.common.HapticTextButton
import com.chifunt.chromaticharptabs.ui.viewmodels.SettingsViewModel

private data class OnboardingStep(
    val titleRes: Int,
    val bodyRes: Int,
    val visual: @Composable () -> Unit
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    modifier: Modifier = Modifier,
    settingsViewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory),
    onFinish: () -> Unit
) {
    val spacingMedium = dimensionResource(R.dimen.spacing_medium)
    val spacingSmall = dimensionResource(R.dimen.spacing_small)
    val tonePlayer = remember { SineTonePlayer() }
    DisposableEffect(Unit) {
        onDispose { tonePlayer.release() }
    }
    val steps = remember {
        listOf(
            OnboardingStep(
                titleRes = R.string.onboarding_title_app,
                bodyRes = R.string.onboarding_body_app,
                visual = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        ImageBlock(
                            painter = painterResource(R.drawable.ic_launcher_foreground),
                            size = 512.dp,
                            showBackground = false
                        )
                    }
                }
            ),
            OnboardingStep(
                titleRes = R.string.onboarding_title_library,
                bodyRes = R.string.onboarding_body_library,
                visual = {
                    TabCard(
                        tab = Tab(
                            id = 1,
                            title = "Fly Me to the Moon",
                            artist = "Frank Sinatra",
                            key = "C",
                            difficulty = "Medium",
                            tags = "jazz standard",
                            content = "",
                            isFavorite = true,
                            createdAt = 0L,
                            updatedAt = 0L
                        ),
                        onOpen = {},
                        onToggleFavorite = {}
                    )
                }
            ),
            OnboardingStep(
                titleRes = R.string.onboarding_title_create,
                bodyRes = R.string.onboarding_body_create,
                visual = {
                    TabNotationEditor(
                        lines = listOf(listOf(TabNote(hole = 7, isBlow = true, isSlide = false))),
                        onAddNote = {},
                        onAddLine = {},
                        onDeleteLine = {},
                        onDeleteNote = { _, _ -> },
                        onEditHole = { _, _ -> },
                        onToggleBlow = { _, _ -> },
                        onToggleSlide = { _, _ -> },
                        onMoveNote = { _, _, _ -> },
                        onPreviewNote = { _, _ -> },
                        onPreviewStop = {},
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            ),
            OnboardingStep(
                titleRes = R.string.onboarding_title_practice,
                bodyRes = R.string.onboarding_body_practice,
                visual = {
                    val line = listOf(
                        TabNote(hole = 8, isBlow = true, isSlide = false),
                        TabNote(hole = 8, isBlow = false, isSlide = false),
                        TabNote(hole = 7, isBlow = false, isSlide = false),
                        TabNote(hole = 7, isBlow = true, isSlide = false),
                        TabNote(hole = 6, isBlow = false, isSlide = false)
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Spacer(Modifier.height(spacingSmall))
                        TabNotationInlineDisplay(
                            lines = listOf(line),
                            centered = true,
                            pressHighlightColor = MaterialTheme.colorScheme.primary,
                            pressHighlightScale = true,
                            onNotePress = { note ->
                                HarmonicaNoteMap.frequencyFor(note)?.let { tonePlayer.start(it) }
                            },
                            onNoteRelease = { tonePlayer.stop() }
                        )
                    }
                }
            )
        )
    }

    val pagerState = rememberPagerState(initialPage = 0, pageCount = { steps.size })
    val stepIndex = pagerState.currentPage

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(spacingMedium)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopEnd),
            horizontalArrangement = Arrangement.End
        ) {
            HapticTextButton(
                onClick = {
                    settingsViewModel.setOnboardingCompleted(true)
                    onFinish()
                }
            ) {
                Text(text = stringResource(R.string.onboarding_skip))
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(bottom = 84.dp, top = 48.dp)
        ) { page ->
            val step = steps[page]
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(step.titleRes),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(spacingMedium))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    step.visual()
                }
                Spacer(Modifier.height(spacingSmall))
                Text(
                    text = stringResource(step.bodyRes),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                repeat(steps.size) { index ->
                    val color = if (index == stepIndex) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.outline
                    }
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 6.dp)
                            .size(10.dp)
                            .background(color, shape = MaterialTheme.shapes.small)
                    )
                }
            }

            Spacer(Modifier.height(spacingSmall))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                if (stepIndex == steps.lastIndex) {
                    HapticButton(
                        onClick = {
                            settingsViewModel.setOnboardingCompleted(true)
                            onFinish()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = stringResource(R.string.onboarding_finish))
                    }
                }
            }
        }
    }
}

@Composable
private fun ImageBlock(
    painter: Painter,
    size: Dp,
    showBackground: Boolean = true
) {
    Box(
        modifier = Modifier
            .size(size)
            .background(
                if (showBackground) MaterialTheme.colorScheme.surfaceVariant else Color.Transparent,
                shape = MaterialTheme.shapes.large
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painter,
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.size(size * 0.72f)
        )
    }
}
