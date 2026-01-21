package com.chifunt.chromaticharptabs.ui.components.notation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.chifunt.chromaticharptabs.R
import com.chifunt.chromaticharptabs.data.TabNote

internal val NoteTileSize = 120.dp
internal val NoteGlyphSize = 32.dp
private val DashedCornerRadius = 14.dp

@Composable
internal fun NoteTile(note: TabNote) {
    val spacingSmall = dimensionResource(R.dimen.spacing_small)
    val borderStroke = dimensionResource(R.dimen.border_stroke_width)

    OutlinedCard(
        modifier = Modifier.size(NoteTileSize),
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(borderStroke, MaterialTheme.colorScheme.outline)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacingSmall),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            NoteGlyph(
                hole = note.hole,
                isBlow = note.isBlow,
                isSlide = note.isSlide
            )
        }
    }
}

@Composable
internal fun NoteGlyph(
    hole: Int,
    isBlow: Boolean,
    isSlide: Boolean,
    modifier: Modifier = Modifier,
    color: Color? = null,
    pressed: Boolean = false
) {
    val borderStroke = dimensionResource(R.dimen.border_stroke_width)
    val outlineColor = color ?: MaterialTheme.colorScheme.onSurface
    val pressedColor = outlineColor.copy(alpha = 0.15f)

    Box(
        modifier = Modifier
            .size(NoteGlyphSize)
            .then(modifier)
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
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}
