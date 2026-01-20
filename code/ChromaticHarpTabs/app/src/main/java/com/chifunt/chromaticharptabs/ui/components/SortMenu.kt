package com.chifunt.chromaticharptabs.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Sort
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.chifunt.chromaticharptabs.R
import com.chifunt.chromaticharptabs.ui.viewmodels.SortOption

@Composable
fun SortMenu(
    selected: SortOption,
    onSelected: (SortOption) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = SortOption.entries
    val labels = options.map { stringResource(it.labelRes) }
    FilterDropdownButton(
        label = stringResource(R.string.sort_label),
        selected = stringResource(selected.labelRes),
        options = labels,
        onSelected = { option ->
            val selectedIndex = labels.indexOf(option).coerceAtLeast(0)
            onSelected(options[selectedIndex])
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.Sort,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        modifier = modifier
    )
}
