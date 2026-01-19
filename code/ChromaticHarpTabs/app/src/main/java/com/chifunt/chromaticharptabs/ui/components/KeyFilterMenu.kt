package com.chifunt.chromaticharptabs.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.chifunt.chromaticharptabs.R

@Composable
fun KeyFilterMenu(
    selected: String,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    FilterDropdownButton(
        label = stringResource(R.string.key_filter_label),
        selected = selected,
        options = keyOptions(),
        onSelected = onSelected,
        modifier = modifier
    )
}
