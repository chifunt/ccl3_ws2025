package com.chifunt.chromaticharptabs.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.chifunt.chromaticharptabs.R

@Composable
fun DifficultyFilterMenu(
    selected: String,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    FilterDropdownButton(
        label = stringResource(R.string.difficulty_label),
        selected = selected,
        options = difficultyOptions(),
        onSelected = onSelected,
        modifier = modifier
    )
}
