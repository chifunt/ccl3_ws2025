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
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chifunt.chromaticharptabs.R
import com.chifunt.chromaticharptabs.data.TabNote
import com.chifunt.chromaticharptabs.ui.AppViewModelProvider
import com.chifunt.chromaticharptabs.ui.components.TabNotationInlineDisplay
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
    val steps = remember {
        listOf(
            OnboardingStep(
                titleRes = R.string.onboarding_title_welcome,
                bodyRes = R.string.onboarding_body_welcome,
                visual = {
                    val line = listOf(
                        TabNote(hole = 4, isBlow = true, isSlide = false),
                        TabNote(hole = 4, isBlow = false, isSlide = false),
                        TabNote(hole = 5, isBlow = true, isSlide = true),
                        TabNote(hole = 6, isBlow = true, isSlide = false)
                    )
                    TabNotationInlineDisplay(lines = listOf(line), centered = true)
                }
            ),
            OnboardingStep(
                titleRes = R.string.onboarding_title_notation,
                bodyRes = R.string.onboarding_body_notation,
                visual = {
                    val line = listOf(
                        TabNote(hole = 3, isBlow = true, isSlide = false),
                        TabNote(hole = 3, isBlow = false, isSlide = false),
                        TabNote(hole = 3, isBlow = true, isSlide = true)
                    )
                    TabNotationInlineDisplay(lines = listOf(line), centered = true)
                }
            ),
            OnboardingStep(
                titleRes = R.string.onboarding_title_edit,
                bodyRes = R.string.onboarding_body_edit,
                visual = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(spacingSmall),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedButton(onClick = {}, modifier = Modifier.weight(1f)) {
                            Text(text = "B")
                        }
                        OutlinedButton(onClick = {}, modifier = Modifier.weight(1f)) {
                            Text(text = "<")
                        }
                    }
                }
            )
        )
    }

    val pagerState = rememberPagerState(initialPage = 0, pageCount = { steps.size })
    val stepIndex = pagerState.currentPage

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(spacingMedium),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.onboarding_title),
                    fontSize = dimensionResource(R.dimen.headline).value.sp,
                    fontWeight = FontWeight.SemiBold
                )
                TextButton(onClick = {
                    settingsViewModel.setOnboardingCompleted(true)
                    onFinish()
                }) {
                    Text(text = stringResource(R.string.onboarding_skip))
                }
            }

            Spacer(Modifier.height(spacingMedium))

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { page ->
                val step = steps[page]
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = stringResource(step.titleRes),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(spacingSmall))
                    Text(text = stringResource(step.bodyRes))
                    Spacer(Modifier.height(spacingMedium))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        step.visual()
                    }
                }
            }
        }

        Column {
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

            if (stepIndex == steps.lastIndex) {
                Button(
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
