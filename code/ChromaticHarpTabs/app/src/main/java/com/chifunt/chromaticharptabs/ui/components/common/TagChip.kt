package com.chifunt.chromaticharptabs.ui.components.common

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.chifunt.chromaticharptabs.ui.components.cards.MetadataPill

@Composable
fun TagChip(
    text: String,
    modifier: Modifier = Modifier,
    onRemove: (() -> Unit)? = null
) {
    MetadataPill(
        text = text,
        icon = null,
        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
        iconTint = MaterialTheme.colorScheme.tertiary,
        modifier = modifier,
        trailingContent = if (onRemove != null) {
            {
                Spacer(Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .size(14.dp)
                        .clickable(onClick = onRemove)
                )
            }
        } else {
            null
        }
    )
}
