package com.chifunt.chromaticharptabs.ui.components.practice

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.chifunt.chromaticharptabs.data.model.TabNote
import com.chifunt.chromaticharptabs.ui.components.notation.NoteVisualState
import com.chifunt.chromaticharptabs.ui.components.notation.TabNotationInlineDisplay

@Composable
fun PracticeNotationArea(
    lines: List<List<TabNote>>,
    currentIndex: Int,
    spacingMedium: Dp,
    slideOffsetPx: Int,
    noteSize: Dp,
    micEnabled: Boolean,
    currentNoteIndex: Int,
    isTargetPlaying: Boolean,
    isWrongNotePlaying: Boolean,
    suppressNextLineHighlight: Boolean,
    goldColor: Color,
    pineColor: Color,
    subtleColor: Color,
    loveColor: Color,
    onNotePress: (TabNote) -> Unit,
    onNoteRelease: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(
            targetState = currentIndex,
            transitionSpec = {
                if (targetState > initialState) {
                    (slideInVertically { slideOffsetPx } + fadeIn(tween(120))) togetherWith
                        (slideOutVertically { -slideOffsetPx } + fadeOut(tween(120)))
                } else {
                    (slideInVertically { -slideOffsetPx } + fadeIn(tween(120))) togetherWith
                        (slideOutVertically { slideOffsetPx } + fadeOut(tween(120)))
                }
            },
            label = "practiceLineTransition"
        ) { index ->
            TabNotationInlineDisplay(
                lines = listOf(lines[index]),
                lineSpacing = spacingMedium,
                centered = true,
                noteSize = noteSize,
                noteColorProvider = if (micEnabled) { lineIndex, noteIndex, _ ->
                    if (lineIndex != 0) return@TabNotationInlineDisplay null
                    when {
                        noteIndex < currentNoteIndex -> subtleColor
                        suppressNextLineHighlight && noteIndex == currentNoteIndex -> subtleColor
                        noteIndex == currentNoteIndex && isTargetPlaying -> pineColor
                        noteIndex == currentNoteIndex && isWrongNotePlaying -> loveColor
                        noteIndex == currentNoteIndex -> goldColor
                        else -> null
                    }
                } else {
                    null
                },
                noteVisualProvider = if (micEnabled) { lineIndex, noteIndex, _ ->
                    if (lineIndex != 0) return@TabNotationInlineDisplay NoteVisualState()
                    NoteVisualState(
                        isCorrect = noteIndex == currentNoteIndex && isTargetPlaying,
                        isWrong = noteIndex == currentNoteIndex && isWrongNotePlaying
                    )
                } else {
                    null
                },
                pressHighlightColor = if (micEnabled) null else pineColor,
                pressHighlightScale = !micEnabled,
                hapticOnPress = true,
                onNotePress = { note ->
                    onNotePress(note)
                },
                onNoteRelease = { onNoteRelease() }
            )
        }
    }
}
