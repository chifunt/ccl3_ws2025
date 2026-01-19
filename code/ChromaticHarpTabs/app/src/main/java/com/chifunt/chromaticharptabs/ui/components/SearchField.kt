package com.chifunt.chromaticharptabs.ui.components

import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.chifunt.chromaticharptabs.R

@Composable
fun SearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(stringResource(R.string.search_label)) },
        placeholder = { Text(text = stringResource(R.string.search_label)) },
        leadingIcon = { Icon(imageVector = Icons.Filled.Search, contentDescription = null) },
        textStyle = MaterialTheme.typography.bodyLarge,
        singleLine = true,
        shape = MaterialTheme.shapes.small,
        modifier = modifier.defaultMinSize(minHeight = dimensionResource(R.dimen.text_field_height))
    )
}
