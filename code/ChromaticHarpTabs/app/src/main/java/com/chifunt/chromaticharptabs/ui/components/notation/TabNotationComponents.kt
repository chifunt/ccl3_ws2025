package com.chifunt.chromaticharptabs.ui.components.notation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.chifunt.chromaticharptabs.R
import com.chifunt.chromaticharptabs.data.TabNote
import com.chifunt.chromaticharptabs.ui.components.DebouncedIconButton
import com.chifunt.chromaticharptabs.ui.theme.ChromaticHarpTabsTheme
import kotlin.math.roundToInt

private val NoteTileSize = 120.dp
private val NoteGlyphSize = 32.dp
private val DashedCornerRadius = 14.dp

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TabNotationEditor(
    lines: List<List<TabNote>>,
    onAddNote: (lineIndex: Int) -> Unit,
    onAddLine: () -> Unit,
    onDeleteLine: (lineIndex: Int) -> Unit,
    onDeleteNote: (lineIndex: Int, noteIndex: Int) -> Unit,
    onEditHole: (lineIndex: Int, noteIndex: Int) -> Unit,
    onToggleBlow: (lineIndex: Int, noteIndex: Int) -> Unit,
    onToggleSlide: (lineIndex: Int, noteIndex: Int) -> Unit,
    onMoveNote: (lineIndex: Int, fromIndex: Int, toIndex: Int) -> Unit,
    lineSpacing: Dp = dimensionResource(R.dimen.spacing_small),
    modifier: Modifier = Modifier
) {
    val spacingSmall = dimensionResource(R.dimen.spacing_small)

    Column(modifier = modifier) {
        if (lines.isEmpty()) {
            AddLineButton(onClick = onAddLine)
            return@Column
        }

        lines.forEachIndexed { lineIndex, line ->
            LineSectionHeader(
                lineNumber = lineIndex + 1,
                onDeleteLine = { onDeleteLine(lineIndex) }
            )
            Spacer(Modifier.height(spacingSmall))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(spacingSmall),
                verticalAlignment = Alignment.Top
            ) {
                line.forEachIndexed { noteIndex, note ->
                    DraggableNoteTile(
                        note = note,
                        noteIndex = noteIndex,
                        lineSize = line.size,
                        onEditHole = { onEditHole(lineIndex, noteIndex) },
                        onToggleBlow = { onToggleBlow(lineIndex, noteIndex) },
                        onToggleSlide = { onToggleSlide(lineIndex, noteIndex) },
                        onDeleteNote = { onDeleteNote(lineIndex, noteIndex) },
                        onMoveNote = { fromIndex, toIndex ->
                            onMoveNote(lineIndex, fromIndex, toIndex)
                        }
                    )
                }
                AddNoteTile(onClick = { onAddNote(lineIndex) })
            }
            Spacer(Modifier.height(lineSpacing))
        }

        AddLineButton(onClick = onAddLine)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TabNotationDisplay(
    lines: List<List<TabNote>>,
    modifier: Modifier = Modifier,
    centered: Boolean = false,
    lineSpacing: Dp = dimensionResource(R.dimen.spacing_small)
) {
    val spacingSmall = dimensionResource(R.dimen.spacing_small)
    val horizontalArrangement = if (centered) {
        Arrangement.spacedBy(spacingSmall, Alignment.CenterHorizontally)
    } else {
        Arrangement.spacedBy(spacingSmall)
    }

    Column(modifier = modifier) {
        lines.forEach { line ->
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = horizontalArrangement,
                verticalArrangement = Arrangement.spacedBy(spacingSmall)
            ) {
                line.forEach { note ->
                    NoteTile(note = note)
                }
            }
            Spacer(Modifier.height(lineSpacing))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TabNotationInlineDisplay(
    lines: List<List<TabNote>>,
    modifier: Modifier = Modifier,
    lineSpacing: Dp = dimensionResource(R.dimen.spacing_small),
    centered: Boolean = false,
    glyphColor: androidx.compose.ui.graphics.Color? = null
) {
    val spacingSmall = dimensionResource(R.dimen.spacing_small)
    val horizontalArrangement = if (centered) {
        Arrangement.spacedBy(spacingSmall, Alignment.CenterHorizontally)
    } else {
        Arrangement.spacedBy(spacingSmall)
    }

    Column(modifier = modifier) {
        lines.forEach { line ->
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = horizontalArrangement,
                verticalArrangement = Arrangement.spacedBy(spacingSmall)
            ) {
                line.forEach { note ->
                    NoteGlyph(
                        hole = note.hole,
                        isBlow = note.isBlow,
                        isSlide = note.isSlide,
                        color = glyphColor
                    )
                }
            }
            Spacer(Modifier.height(lineSpacing))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TabNotationInlineDisplayPreview() {
    ChromaticHarpTabsTheme(darkTheme = true) {
        TabNotationInlineDisplay(
            lines = listOf(
                listOf(
                    TabNote(hole = 3, isBlow = true, isSlide = false),
                    TabNote(hole = 3, isBlow = false, isSlide = false),
                    TabNote(hole = 4, isBlow = true, isSlide = true)
                )
            ),
            centered = true
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TabNotationInlineDisplayLightPreview() {
    ChromaticHarpTabsTheme(darkTheme = false) {
        TabNotationInlineDisplay(
            lines = listOf(
                listOf(
                    TabNote(hole = 4, isBlow = true, isSlide = false),
                    TabNote(hole = 4, isBlow = false, isSlide = false),
                    TabNote(hole = 5, isBlow = true, isSlide = true)
                )
            ),
            centered = true
        )
    }
}

@Composable
private fun DraggableNoteTile(
    note: TabNote,
    noteIndex: Int,
    lineSize: Int,
    onEditHole: () -> Unit,
    onToggleBlow: () -> Unit,
    onToggleSlide: () -> Unit,
    onDeleteNote: () -> Unit,
    onMoveNote: (fromIndex: Int, toIndex: Int) -> Unit
) {
    val spacingSmall = dimensionResource(R.dimen.spacing_small)
    val borderStroke = dimensionResource(R.dimen.border_stroke_width)
    var dragOffset by remember { mutableStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }
    val itemWidthPx = with(androidx.compose.ui.platform.LocalDensity.current) {
        (NoteTileSize + spacingSmall).toPx()
    }

    OutlinedCard(
        modifier = Modifier
            .size(NoteTileSize)
            .offset { IntOffset(dragOffset.toInt(), 0) }
            .zIndex(if (isDragging) 1f else 0f)
            .pointerInput(noteIndex, lineSize) {
                detectDragGesturesAfterLongPress(
                    onDragStart = { isDragging = true },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        dragOffset += dragAmount.x
                    },
                    onDragEnd = {
                        val deltaIndex = (dragOffset / itemWidthPx).roundToInt()
                        val targetIndex = (noteIndex + deltaIndex).coerceIn(0, lineSize - 1)
                        if (lineSize > 1 && targetIndex != noteIndex) {
                            onMoveNote(noteIndex, targetIndex)
                        }
                        dragOffset = 0f
                        isDragging = false
                    },
                    onDragCancel = {
                        dragOffset = 0f
                        isDragging = false
                    }
                )
            },
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(borderStroke, MaterialTheme.colorScheme.outline)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(spacingSmall),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                DebouncedIconButton(
                    onClick = onDeleteNote,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = stringResource(R.string.delete_note),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
            NoteGlyph(
                hole = note.hole,
                isBlow = note.isBlow,
                isSlide = note.isSlide,
                modifier = Modifier.clickable(onClick = onEditHole)
            )
            Spacer(Modifier.height(spacingSmall))
            Row(horizontalArrangement = Arrangement.spacedBy(spacingSmall)) {
                val toggleModifier = Modifier.size(width = 48.dp, height = 32.dp)
                val blowContainer = if (note.isBlow) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                }
                val blowLabel = if (note.isBlow) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
                OutlinedButton(
                    onClick = onToggleBlow,
                    modifier = toggleModifier,
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = blowContainer,
                        contentColor = blowLabel
                    ),
                    border = BorderStroke(
                        borderStroke,
                        if (note.isBlow) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                    )
                ) {
                    Text(
                        text = if (note.isBlow) "B" else "D",
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                val slideContainer = if (note.isSlide) {
                    MaterialTheme.colorScheme.secondaryContainer
                } else {
                    Color.Transparent
                }
                val slideLabel = if (note.isSlide) {
                    MaterialTheme.colorScheme.onSecondaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
                OutlinedButton(
                    onClick = onToggleSlide,
                    modifier = toggleModifier,
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = slideContainer,
                        contentColor = slideLabel
                    ),
                    border = BorderStroke(
                        borderStroke,
                        if (note.isSlide) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outline
                    )
                ) {
                    Text(
                        text = "<",
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun NoteTile(note: TabNote) {
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
private fun NoteGlyph(
    hole: Int,
    isBlow: Boolean,
    isSlide: Boolean,
    modifier: Modifier = Modifier,
    color: androidx.compose.ui.graphics.Color? = null
) {
    val borderStroke = dimensionResource(R.dimen.border_stroke_width)
    val outlineColor = color ?: MaterialTheme.colorScheme.onSurface

    Box(
        modifier = Modifier
            .size(NoteGlyphSize)
            .then(modifier)
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
private fun AddNoteTile(onClick: () -> Unit) {
    DashedOutlineBox(
        modifier = Modifier.size(NoteTileSize),
        onClick = onClick
    ) {
        Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.add_note))
    }
}

@Composable
private fun LineSectionHeader(
    lineNumber: Int,
    onDeleteLine: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.line_label, lineNumber),
            fontWeight = FontWeight.SemiBold
        )
        DebouncedIconButton(onClick = onDeleteLine) {
            Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = stringResource(R.string.delete_line)
            )
        }
    }
}

@Composable
private fun AddLineButton(onClick: () -> Unit) {
    DashedOutlineBox(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        onClick = onClick
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Filled.Add, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(text = stringResource(R.string.add_new_line))
        }
    }
}

@Composable
private fun DashedOutlineBox(
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
