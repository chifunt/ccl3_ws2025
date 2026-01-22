package com.chifunt.chromaticharptabs.ui.components.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.chifunt.chromaticharptabs.R
import com.chifunt.chromaticharptabs.data.model.ThemeMode
import com.chifunt.chromaticharptabs.ui.components.filters.FilterDropdownButton

@Composable
fun ThemeRow(
    selectedThemeLabel: String,
    themeOptions: List<Pair<ThemeMode, String>>,
    filterHeight: Dp,
    spacingSmall: Dp,
    onThemeSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacingSmall)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = spacingSmall)
        ) {
            Text(
                text = stringResource(R.string.settings_theme_detail),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )
        }
        FilterDropdownButton(
            label = stringResource(R.string.settings_theme),
            selected = selectedThemeLabel,
            options = themeOptions.map { it.second },
            onSelected = onThemeSelected,
            modifier = Modifier
                .height(filterHeight)
                .width(170.dp)
        )
    }
}
