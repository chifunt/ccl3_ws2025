package com.chifunt.chromaticharptabs.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.chifunt.chromaticharptabs.R
import com.chifunt.chromaticharptabs.ui.theme.DifficultyEasy
import com.chifunt.chromaticharptabs.ui.theme.DifficultyHard
import com.chifunt.chromaticharptabs.ui.theme.DifficultyMedium

@Composable
fun DifficultyFilterRow(
    selectedDifficulty: String,
    onDifficultySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacingSmall = dimensionResource(R.dimen.spacing_small)
    val filterHeight = dimensionResource(R.dimen.filter_chip_height)
    val easyLabel = stringResource(R.string.difficulty_easy)
    val mediumLabel = stringResource(R.string.difficulty_medium)
    val hardLabel = stringResource(R.string.difficulty_hard)

    Row(
        horizontalArrangement = Arrangement.spacedBy(spacingSmall),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        difficultyOptions().forEach { option ->
            val (selectedContainer, selectedLabel) = when (option) {
                easyLabel -> DifficultyEasy to MaterialTheme.colorScheme.onSecondaryContainer
                mediumLabel -> DifficultyMedium to MaterialTheme.colorScheme.onSecondaryContainer
                hardLabel -> DifficultyHard to MaterialTheme.colorScheme.onSecondaryContainer
                else -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
            }
            FilterChip(
                selected = selectedDifficulty == option,
                onClick = { onDifficultySelected(option) },
                label = {
                    Text(
                        text = option,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = selectedContainer,
                    selectedLabelColor = selectedLabel,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                shape = MaterialTheme.shapes.small,
                modifier = Modifier
                    .height(filterHeight)
                    .weight(1f)
            )
        }
    }
}
