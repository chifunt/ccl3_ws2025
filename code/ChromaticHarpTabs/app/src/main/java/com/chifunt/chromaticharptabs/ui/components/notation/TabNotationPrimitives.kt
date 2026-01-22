package com.chifunt.chromaticharptabs.ui.components.notation

import androidx.compose.foundation.clickable
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.chifunt.chromaticharptabs.R
import com.chifunt.chromaticharptabs.ui.haptics.rememberHapticClick

internal val NoteTileSize = 120.dp
internal val NoteGlyphSize = 32.dp
private val DashedCornerRadius = 14.dp

@Composable
fun NoteGlyph(
    hole: Int,
    isBlow: Boolean,
    isSlide: Boolean,
    modifier: Modifier = Modifier,
    color: Color? = null,
    noteSize: Dp? = null,
    isCorrect: Boolean = false,
    isWrong: Boolean = false,
    pressed: Boolean = false
) {
    val borderStroke = dimensionResource(R.dimen.border_stroke_width)
    val outlineColor = color ?: MaterialTheme.colorScheme.onSurface
    val pressedColor = outlineColor.copy(alpha = 0.15f)
    val glyphSize = noteSize ?: NoteGlyphSize
    val baseTextSize = with(androidx.compose.ui.platform.LocalDensity.current) {
        (glyphSize * 0.5f).toSp()
    }
    val correctScale by animateFloatAsState(
        targetValue = if (isCorrect) 1.12f else 1f,
        animationSpec = tween(durationMillis = 120),
        label = "noteCorrectScale"
    )
    val correctAlpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 120),
        label = "noteCorrectAlpha"
    )
    val targetScale = correctScale
    val scaledTextSize = androidx.compose.ui.unit.TextUnit(
        value = baseTextSize.value * targetScale,
        type = baseTextSize.type
    )

    Box(
        modifier = Modifier
            .size(glyphSize)
            .then(modifier)
            .graphicsLayer(
                scaleX = targetScale,
                scaleY = targetScale,
                alpha = if (isCorrect) correctAlpha else 1f
            )
            .background(
                color = if (pressed) pressedColor else Color.Transparent,
                shape = MaterialTheme.shapes.small
            )
            .drawBehind {
                val strokeWidth = borderStroke.toPx()
                if (!isBlow) {
                    drawCircle(
                        color = outlineColor,
                        radius = size.minDimension / 2 - strokeWidth,
                        style = Stroke(width = strokeWidth * 2)
                    )
                }
                if (isSlide) {
                    val inset = size.width * 0.15f
                    val y = strokeWidth * -3.5f
                    drawLine(
                        color = outlineColor,
                        start = androidx.compose.ui.geometry.Offset(inset, y),
                        end = androidx.compose.ui.geometry.Offset(size.width - inset, y),
                        strokeWidth = strokeWidth * 2f
                    )
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = hole.toString(),
            fontWeight = FontWeight.SemiBold,
            fontSize = scaledTextSize,
            color = outlineColor
        )
    }
}

@Composable
internal fun DashedOutlineBox(
    modifier: Modifier,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    val borderStroke = dimensionResource(R.dimen.border_stroke_width)
    val outlineColor = MaterialTheme.colorScheme.outline
    val cornerRadiusPx = with(androidx.compose.ui.platform.LocalDensity.current) {
        DashedCornerRadius.toPx()
    }

    Box(
        modifier = modifier
            .drawBehind {
                val strokeWidth = borderStroke.toPx()
                val dash = floatArrayOf(strokeWidth * 4f, strokeWidth * 3f)
                drawRoundRect(
                    color = outlineColor,
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadiusPx, cornerRadiusPx),
                    style = Stroke(
                        width = strokeWidth,
                        pathEffect = PathEffect.dashPathEffect(dash, 0f)
                    )
                )
            }
            .clickable(onClick = rememberHapticClick(onClick)),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}
