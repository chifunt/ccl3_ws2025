package com.chifunt.chromaticharptabs.ui.components.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Label
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.VpnKey
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import com.chifunt.chromaticharptabs.R
import com.chifunt.chromaticharptabs.data.util.parseTags
import com.chifunt.chromaticharptabs.ui.components.common.TagChip
import com.chifunt.chromaticharptabs.ui.components.cards.MetadataPill

@Composable
fun MetadataSection(
    key: String,
    difficulty: String,
    tags: String,
    spacingSmall: Dp,
    spacingMedium: Dp,
    spacingTight: Dp
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(Modifier.padding(spacingMedium)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(Modifier.width(spacingSmall))
                Text(
                    text = stringResource(R.string.detail_metadata_title),
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(Modifier.height(spacingMedium))
            Column(verticalArrangement = Arrangement.spacedBy(spacingTight)) {
                MetadataPill(
                    text = stringResource(
                        R.string.detail_metadata_key,
                        key.ifBlank { stringResource(R.string.unknown_value) }
                    ),
                    icon = Icons.Outlined.VpnKey,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    iconTint = MaterialTheme.colorScheme.primary
                )
                MetadataPill(
                    text = stringResource(R.string.detail_metadata_difficulty, difficulty),
                    icon = Icons.Outlined.Tune,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    iconTint = MaterialTheme.colorScheme.secondary
                )
                val tagList = parseTags(tags)
                if (tagList.isNotEmpty()) {
                    Spacer(Modifier.height(spacingTight))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.Label,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                        Spacer(Modifier.width(spacingSmall))
                        Text(
                            text = stringResource(R.string.detail_metadata_tags_label),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Spacer(Modifier.height(spacingTight))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(spacingSmall),
                        verticalArrangement = Arrangement.spacedBy(spacingSmall)
                    ) {
                        tagList.forEach { tag ->
                            TagChip(text = tag)
                        }
                    }
                }
            }
        }
    }
}
