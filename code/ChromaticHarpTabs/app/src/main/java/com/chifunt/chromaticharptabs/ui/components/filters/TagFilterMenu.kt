package com.chifunt.chromaticharptabs.ui.components.filters

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Label
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.chifunt.chromaticharptabs.R
import com.chifunt.chromaticharptabs.ui.haptics.rememberHapticFeedback
import com.chifunt.chromaticharptabs.ui.theme.ChromaticHarpTabsTheme

@Composable
fun TagFilterMenu(
    options: List<String>,
    selected: Set<String>,
    onToggleTag: (String) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val borderWidth = dimensionResource(R.dimen.border_stroke_width)
    val haptic = rememberHapticFeedback()
    val label = stringResource(R.string.tags_label)
    val selectionText = if (selected.isEmpty()) {
        stringResource(R.string.filter_all)
    } else {
        stringResource(R.string.tag_filter_selected, selected.size)
    }

    OutlinedButton(
        onClick = {
            haptic()
            expanded = true
        },
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(borderWidth, MaterialTheme.colorScheme.outline)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.Label,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.tertiary
            )
            Spacer(Modifier.width(dimensionResource(R.dimen.spacing_small)))
            Text(text = label)
            Spacer(Modifier.width(dimensionResource(R.dimen.spacing_small)))
            Text(
                text = selectionText,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(dimensionResource(R.dimen.spacing_small)))
            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = null
            )
        }
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        if (selected.isNotEmpty()) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.tag_filter_clear)) },
                onClick = {
                    haptic()
                    onClear()
                    expanded = false
                }
            )
        }
        if (options.isEmpty()) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.tag_filter_empty)) },
                onClick = {
                    haptic()
                    expanded = false
                }
            )
        } else {
            options.forEach { tag ->
                DropdownMenuItem(
                    text = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(
                                dimensionResource(R.dimen.spacing_small)
                            ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = selected.contains(tag),
                                onCheckedChange = null
                            )
                            Text(text = tag)
                        }
                    },
                    onClick = {
                        haptic()
                        onToggleTag(tag)
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TagFilterMenuPreview() {
    ChromaticHarpTabsTheme(darkTheme = true) {
        TagFilterMenu(
            options = listOf("jazz", "ballad", "latin"),
            selected = setOf("jazz", "latin"),
            onToggleTag = {},
            onClear = {},
            modifier = Modifier.width(180.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TagFilterMenuLightPreview() {
    ChromaticHarpTabsTheme(darkTheme = false) {
        TagFilterMenu(
            options = listOf("jazz", "ballad", "latin"),
            selected = emptySet(),
            onToggleTag = {},
            onClear = {},
            modifier = Modifier.width(180.dp)
        )
    }
}
