package com.chifunt.chromaticharptabs.ui.components.filters

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.chifunt.chromaticharptabs.R
import com.chifunt.chromaticharptabs.ui.components.difficultyOptions

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
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Tune,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary
            )
        },
        modifier = modifier
    )
}
