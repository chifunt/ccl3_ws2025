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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.chifunt.chromaticharptabs.R
import com.chifunt.chromaticharptabs.data.TabNote
import com.chifunt.chromaticharptabs.ui.theme.ChromaticHarpTabsTheme

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
