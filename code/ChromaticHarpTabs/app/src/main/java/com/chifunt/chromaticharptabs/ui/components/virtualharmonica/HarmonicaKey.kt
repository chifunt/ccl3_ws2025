package com.chifunt.chromaticharptabs.ui.components.virtualharmonica

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.Dp
import com.chifunt.chromaticharptabs.R
import com.chifunt.chromaticharptabs.ui.components.notation.NoteGlyph

@Composable
fun HarmonicaKey(
    hole: Int,
    pressed: Boolean,
    activeColor: Color,
    cornerRadiusPx: Float,
    isSlide: Boolean,
    isBlow: Boolean,
    keyWidth: Dp,
    keyHeight: Dp,
    onBounds: (Rect) -> Unit
) {
    val border = dimensionResource(R.dimen.border_stroke_width)
    val scale = if (pressed) 1.08f else 1f
    val color = if (pressed) activeColor else MaterialTheme.colorScheme.onSurface
    val outlineColor = MaterialTheme.colorScheme.outline
    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .width(keyWidth)
            .height(keyHeight)
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale
            )
            .onGloballyPositioned { coordinates ->
                onBounds(coordinates.boundsInRoot())
            }
            .background(
                color = if (pressed) color.copy(alpha = 0.15f) else Color.Transparent,
                shape = MaterialTheme.shapes.small
            )
            .drawBehind {
                drawRoundRect(
                    color = outlineColor,
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadiusPx, cornerRadiusPx),
                    style = Stroke(width = border.toPx())
                )
            },
        contentAlignment = Alignment.Center
    ) {
        NoteGlyph(
            hole = hole,
            isBlow = isBlow,
            isSlide = isSlide,
            color = color,
            noteSize = keyHeight,
            isCorrect = pressed,
            pressed = pressed
        )
    }
}
