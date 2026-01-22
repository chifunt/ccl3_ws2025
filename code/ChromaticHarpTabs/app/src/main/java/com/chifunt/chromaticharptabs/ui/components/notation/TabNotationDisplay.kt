package com.chifunt.chromaticharptabs.ui.components.notation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Alignment
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.chifunt.chromaticharptabs.R
import com.chifunt.chromaticharptabs.data.model.TabNote
import com.chifunt.chromaticharptabs.ui.theme.ChromaticHarpTabsTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TabNotationInlineDisplay(
    lines: List<List<TabNote>>,
    modifier: Modifier = Modifier,
    lineSpacing: Dp = dimensionResource(R.dimen.spacing_small),
    centered: Boolean = false,
    glyphColor: androidx.compose.ui.graphics.Color? = null,
    noteSize: Dp? = null,
    noteColorProvider: ((lineIndex: Int, noteIndex: Int, note: TabNote) -> androidx.compose.ui.graphics.Color?)? = null,
    onNotePress: ((TabNote) -> Unit)? = null,
    onNoteRelease: ((TabNote) -> Unit)? = null
) {
    val spacingSmall = dimensionResource(R.dimen.spacing_small)
    val horizontalArrangement = if (centered) {
        Arrangement.spacedBy(spacingSmall, Alignment.CenterHorizontally)
    } else {
        Arrangement.spacedBy(spacingSmall)
    }
    val noteBounds = remember { mutableStateMapOf<Pair<Int, Int>, NoteHit>() }
    val activeKey = remember { mutableStateOf<Pair<Int, Int>?>(null) }
    val rootCoordinates = remember { mutableStateOf<LayoutCoordinates?>(null) }

    Column(
        modifier = modifier
            .onGloballyPositioned { rootCoordinates.value = it }
            .then(
                if (onNotePress != null) {
                    Modifier.pointerInput(noteBounds, onNotePress) {
                        awaitPointerEventScope {
                            while (true) {
                                val event = awaitPointerEvent()
                                val change = event.changes.firstOrNull() ?: continue
                                val coords = rootCoordinates.value
                                val position = coords?.localToRoot(change.position) ?: change.position
                                val hit = noteBounds.values.firstOrNull { it.bounds.contains(position) }
                                if (!change.pressed) {
                                    activeKey.value?.let { key ->
                                        noteBounds[key]?.note?.let { onNoteRelease?.invoke(it) }
                                    }
                                    activeKey.value = null
                                    continue
                                }
                                val nextKey = hit?.key
                                if (nextKey != activeKey.value) {
                                    activeKey.value?.let { key ->
                                        noteBounds[key]?.note?.let { onNoteRelease?.invoke(it) }
                                    }
                                    activeKey.value = nextKey
                                    hit?.note?.let { onNotePress(it) }
                                }
                            }
                        }
                    }
                } else {
                    Modifier
                }
            )
    ) {
        lines.forEachIndexed { lineIndex, line ->
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = horizontalArrangement,
                verticalArrangement = Arrangement.spacedBy(spacingSmall)
            ) {
                line.forEachIndexed { noteIndex, note ->
                    val key = lineIndex to noteIndex
                    DisposableEffect(key) {
                        onDispose { noteBounds.remove(key) }
                    }
                    val noteColor = noteColorProvider?.invoke(lineIndex, noteIndex, note) ?: glyphColor
                    val noteModifier = Modifier.onGloballyPositioned { coords ->
                        noteBounds[key] = NoteHit(
                            key = key,
                            note = note,
                            bounds = coords.boundsInRoot()
                        )
                    }
                    NoteGlyph(
                        hole = note.hole,
                        isBlow = note.isBlow,
                        isSlide = note.isSlide,
                        color = noteColor,
                        noteSize = noteSize,
                        modifier = noteModifier,
                        pressed = activeKey.value == key
                    )
                }
            }
            Spacer(Modifier.height(lineSpacing))
        }
    }
}

private data class NoteHit(
    val key: Pair<Int, Int>,
    val note: TabNote,
    val bounds: Rect
)

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
