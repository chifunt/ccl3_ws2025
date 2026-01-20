package com.chifunt.chromaticharptabs.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextOverflow
import com.chifunt.chromaticharptabs.R

@Composable
fun FilterDropdownButton(
    label: String,
    selected: String,
    options: List<String>,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    minHeight: Dp? = null,
    leadingIcon: (@Composable () -> Unit)? = null
) {
    var expanded by remember { mutableStateOf(false) }
    val borderWidth = dimensionResource(R.dimen.border_stroke_width)

    Box(modifier = modifier) {
        val buttonModifier = if (minHeight != null) {
            Modifier
                .fillMaxWidth()
                .heightIn(min = minHeight)
        } else {
            Modifier.fillMaxWidth()
        }
        OutlinedButton(
            onClick = { expanded = true },
            modifier = buttonModifier,
            shape = MaterialTheme.shapes.small,
            border = BorderStroke(borderWidth, MaterialTheme.colorScheme.outline),
            contentPadding = PaddingValues(
                horizontal = dimensionResource(R.dimen.spacing_medium)
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (leadingIcon != null) {
                    leadingIcon()
                    Spacer(Modifier.width(dimensionResource(R.dimen.spacing_small)))
                }
                Text(
                    text = label,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.width(dimensionResource(R.dimen.spacing_small)))
                Text(
                    text = selected,
                    color = MaterialTheme.colorScheme.secondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(dimensionResource(R.dimen.spacing_small)))
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
