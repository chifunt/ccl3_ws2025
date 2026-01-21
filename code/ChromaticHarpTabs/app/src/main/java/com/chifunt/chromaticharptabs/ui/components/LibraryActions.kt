package com.chifunt.chromaticharptabs.ui.components

import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import com.chifunt.chromaticharptabs.R

@Composable
fun AddTabButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val filterHeight = dimensionResource(R.dimen.filter_chip_height)
    Button(
        onClick = onClick,
        modifier = modifier.height(filterHeight),
        shape = MaterialTheme.shapes.small
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = null
        )
        Spacer(Modifier.width(dimensionResource(R.dimen.spacing_small)))
        Text(stringResource(R.string.add_new_tab))
    }
}
