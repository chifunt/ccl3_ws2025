package com.chifunt.chromaticharptabs.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign

@Composable
fun LibraryEmptyState(message: String, modifier: Modifier = Modifier) {
    Text(
        text = message,
        textAlign = TextAlign.Center,
        modifier = modifier
    )
}
