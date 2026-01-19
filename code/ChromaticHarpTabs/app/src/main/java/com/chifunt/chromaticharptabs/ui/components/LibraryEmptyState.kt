package com.chifunt.chromaticharptabs.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.chifunt.chromaticharptabs.R

@Composable
fun LibraryEmptyState(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(R.string.empty_library),
        textAlign = TextAlign.Center,
        modifier = modifier
    )
}
