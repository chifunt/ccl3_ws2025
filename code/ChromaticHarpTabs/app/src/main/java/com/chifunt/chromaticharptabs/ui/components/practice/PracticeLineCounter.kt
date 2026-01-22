package com.chifunt.chromaticharptabs.ui.components.practice

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.chifunt.chromaticharptabs.R

@Composable
fun PracticeLineCounter(
    currentIndex: Int,
    total: Int,
    lineColor: Color,
    lineScale: Float,
    modifier: Modifier = Modifier
) {
    Text(
        text = stringResource(R.string.practice_line_counter, currentIndex + 1, total),
        fontWeight = FontWeight.Medium,
        color = lineColor,
        modifier = modifier.graphicsLayer(
            scaleX = lineScale,
            scaleY = lineScale
        )
    )
}
