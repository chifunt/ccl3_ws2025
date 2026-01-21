package com.chifunt.chromaticharptabs.ui.components.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import com.chifunt.chromaticharptabs.R

@Composable
fun PracticeRow(
    onPractice: () -> Unit,
    spacingSmall: Dp
) {
    Row(horizontalArrangement = Arrangement.spacedBy(spacingSmall), modifier = Modifier.fillMaxWidth()) {
        Button(onClick = onPractice, modifier = Modifier.weight(1f)) {
            Icon(Icons.Filled.PlayArrow, contentDescription = null)
            Spacer(Modifier.width(spacingSmall))
            Text(stringResource(R.string.practice_button))
        }
    }
}
