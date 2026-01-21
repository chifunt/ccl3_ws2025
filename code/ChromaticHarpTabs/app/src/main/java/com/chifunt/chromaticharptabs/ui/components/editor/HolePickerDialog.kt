package com.chifunt.chromaticharptabs.ui.components.editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chifunt.chromaticharptabs.R

@Composable
fun HolePickerDialog(
    onDismiss: () -> Unit,
    onHoleSelected: (Int) -> Unit
) {
    val spacingSmall = dimensionResource(R.dimen.spacing_small)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.hole_picker_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(spacingSmall)) {
                for (rowIndex in 0 until 3) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(spacingSmall),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        for (columnIndex in 0 until 4) {
                            val hole = rowIndex * 4 + columnIndex + 1
                            Button(
                                onClick = { onHoleSelected(hole) },
                                modifier = Modifier.size(56.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(
                                    text = hole.toString(),
                                    maxLines = 1,
                                    softWrap = false,
                                    fontSize = 24.sp
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.cancel_button))
            }
        }
    )
}
