package com.chifunt.chromaticharptabs.ui.components.editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Label
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.TextFields
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Dp
import com.chifunt.chromaticharptabs.R
import com.chifunt.chromaticharptabs.ui.components.LabeledTextField
import com.chifunt.chromaticharptabs.ui.components.TagChip
import com.chifunt.chromaticharptabs.ui.components.difficultyOptions
import com.chifunt.chromaticharptabs.ui.components.filters.FilterDropdownButton
import com.chifunt.chromaticharptabs.ui.components.keyOptions
import com.chifunt.chromaticharptabs.ui.viewmodels.TabEditorUiState

@Composable
fun DetailsCard(
    state: TabEditorUiState,
    onTitleChange: (String) -> Unit,
    onArtistChange: (String) -> Unit,
    onKeyChange: (String) -> Unit,
    onDifficultyChange: (String) -> Unit,
    onTagsInputChange: (String) -> Unit,
    onCommitTag: () -> Unit,
    onRemoveTag: (String) -> Unit,
    spacingSmall: Dp,
    spacingMedium: Dp,
    textFieldHeight: Dp,
    keyDefault: String,
    mediumLabel: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(Modifier.padding(spacingMedium)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Description,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.width(spacingSmall))
                Text(
                    text = stringResource(R.string.editor_section_details),
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(spacingSmall))
            LabeledTextField(
                value = state.title,
                labelRes = R.string.title_label,
                onValueChange = onTitleChange,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.TextFields,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            )
            Spacer(Modifier.height(spacingSmall))

            LabeledTextField(
                value = state.artist,
                labelRes = R.string.artist_label,
                onValueChange = onArtistChange,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            )
            Spacer(Modifier.height(spacingSmall))

            FilterDropdownButton(
                label = stringResource(R.string.key_label),
                selected = state.key.ifBlank { keyDefault },
                options = keyOptions().drop(1),
                onSelected = onKeyChange,
                minHeight = textFieldHeight,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.VpnKey,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(spacingSmall))

            FilterDropdownButton(
                label = stringResource(R.string.difficulty_label),
                selected = state.difficulty.ifBlank { mediumLabel },
                options = difficultyOptions().drop(1),
                onSelected = onDifficultyChange,
                minHeight = textFieldHeight,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Tune,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(spacingSmall))

            LabeledTextField(
                value = state.tagsInput,
                labelRes = R.string.tags_label,
                onValueChange = onTagsInputChange,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { onCommitTag() }),
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.Label,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                }
            )
            if (state.tags.isNotEmpty()) {
                Spacer(Modifier.height(spacingSmall))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(spacingSmall),
                    verticalArrangement = Arrangement.spacedBy(spacingSmall),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    state.tags.forEach { tag ->
                        TagChip(
                            text = tag,
                            onRemove = { onRemoveTag(tag) }
                        )
                    }
                }
            }
        }
    }
}
