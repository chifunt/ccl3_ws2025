package com.chifunt.chromaticharptabs.ui.components.practice

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.Dp
import com.chifunt.chromaticharptabs.R
import com.chifunt.chromaticharptabs.ui.components.common.DebouncedFilledIconButton

@Composable
fun PracticeNavigationRow(
    spacingMedium: Dp,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    hasPrev: Boolean,
    hasNext: Boolean
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(spacingMedium),
        modifier = Modifier.fillMaxWidth()
    ) {
        DebouncedFilledIconButton(
            onClick = onPrev,
            enabled = hasPrev,
            debounceMs = 0L,
            modifier = Modifier
                .weight(1f)
                .height(dimensionResource(R.dimen.filter_chip_height))
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
        }
        DebouncedFilledIconButton(
            onClick = onNext,
            enabled = hasNext,
            debounceMs = 0L,
            modifier = Modifier
                .weight(1f)
                .height(dimensionResource(R.dimen.filter_chip_height))
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
        }
    }
}
