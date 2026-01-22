package com.chifunt.chromaticharptabs.ui.components.virtualharmonica

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import com.chifunt.chromaticharptabs.R

@Composable
fun HarmonicaColumn(
    label: String,
    isBlow: Boolean,
    activeKey: HarmonicaKeyId?,
    activeColor: Color,
    cornerRadiusPx: Float,
    slideActive: Boolean,
    keyWidth: Dp,
    keyHeight: Dp,
    noteBounds: MutableMap<HarmonicaKeyId, Rect>,
    modifier: Modifier = Modifier
) {
    val spacingSmall = dimensionResource(R.dimen.spacing_small)
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(spacingSmall),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        (1..12).forEach { hole ->
            val pressed = activeKey?.hole == hole && activeKey.isBlow == isBlow
            HarmonicaKey(
                hole = hole,
                pressed = pressed,
                activeColor = activeColor,
                cornerRadiusPx = cornerRadiusPx,
                isSlide = slideActive,
                isBlow = isBlow,
                keyWidth = keyWidth,
                keyHeight = keyHeight,
                onBounds = { bounds ->
                    noteBounds[HarmonicaKeyId(hole, isBlow)] = bounds
                }
            )
        }
    }
}
