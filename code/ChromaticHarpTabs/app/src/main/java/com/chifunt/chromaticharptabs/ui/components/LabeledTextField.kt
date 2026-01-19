package com.chifunt.chromaticharptabs.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.chifunt.chromaticharptabs.R

@Composable
fun LabeledTextField(
    value: String,
    labelRes: Int,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    maxLines: Int = 1
) {
    val heightModifier = if (singleLine) {
        Modifier.height(dimensionResource(R.dimen.text_field_height))
    } else {
        Modifier
    }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(stringResource(labelRes)) },
        shape = MaterialTheme.shapes.small,
        singleLine = singleLine,
        maxLines = maxLines,
        modifier = modifier.then(heightModifier)
    )
}
