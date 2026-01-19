package com.chifunt.chromaticharptabs.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

@Composable
fun OnboardingScreen(
    modifier: Modifier = Modifier,
    settingsViewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory),
    onFinish: () -> Unit
) {
    val spacingMedium = dimensionResource(R.dimen.spacing_medium)
    val spacingSmall = dimensionResource(R.dimen.spacing_small)
    var stepIndex by remember { mutableStateOf(0) }

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

    val step = steps[stepIndex]

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(spacingMedium),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
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

            Row(
                horizontalArrangement = Arrangement.spacedBy(spacingSmall),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = { stepIndex = (stepIndex - 1).coerceAtLeast(0) },
                    enabled = stepIndex > 0,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = stringResource(R.string.onboarding_back))
                }
                Button(
                    onClick = {
                        if (stepIndex < steps.lastIndex) {
                            stepIndex += 1
                        } else {
                            settingsViewModel.setOnboardingCompleted(true)
                            onFinish()
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = stringResource(
                            if (stepIndex < steps.lastIndex) {
                                R.string.onboarding_next
                            } else {
                                R.string.onboarding_finish
                            }
                        )
                    )
                }
            }
        }
    }
}
