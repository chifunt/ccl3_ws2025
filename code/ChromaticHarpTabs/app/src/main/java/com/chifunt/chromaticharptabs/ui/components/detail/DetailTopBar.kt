package com.chifunt.chromaticharptabs.ui.components.detail

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.chifunt.chromaticharptabs.R
import com.chifunt.chromaticharptabs.ui.components.common.DebouncedIconButton
import com.chifunt.chromaticharptabs.ui.components.common.TopBackBar

@Composable
fun DetailTopBar(
    onBack: () -> Unit,
    onShowNotationInfo: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    TopBackBar(
        onBack = onBack,
        actions = {
            Row {
                DebouncedIconButton(onClick = onShowNotationInfo) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.HelpOutline,
                        contentDescription = stringResource(R.string.notation_info_button)
                    )
                }
                DebouncedIconButton(onClick = onEdit) {
                    Icon(Icons.Filled.Edit, contentDescription = stringResource(R.string.edit_button))
                }
                DebouncedIconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = stringResource(R.string.delete_button),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    )
}
