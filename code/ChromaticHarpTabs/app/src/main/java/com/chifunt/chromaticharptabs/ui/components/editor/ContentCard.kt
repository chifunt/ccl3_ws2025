package com.chifunt.chromaticharptabs.ui.components.editor

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import com.chifunt.chromaticharptabs.R
import com.chifunt.chromaticharptabs.data.model.TabNote
import com.chifunt.chromaticharptabs.ui.components.notation.TabNotationEditor

@Composable
fun ContentCard(
    lines: List<List<TabNote>>,
    onAddNote: (Int) -> Unit,
    onAddLine: () -> Unit,
    onDeleteLine: (Int) -> Unit,
    onDeleteNote: (Int, Int) -> Unit,
    onEditHole: (Int, Int) -> Unit,
    onToggleBlow: (Int, Int) -> Unit,
    onToggleSlide: (Int, Int) -> Unit,
    onMoveNote: (Int, Int, Int) -> Unit,
    onPreviewNote: (Int, Int) -> Unit,
    onPreviewStop: () -> Unit,
    spacingSmall: Dp,
    spacingMedium: Dp
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(Modifier.padding(spacingMedium)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.MusicNote,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.width(spacingSmall))
                Text(
                    text = stringResource(R.string.editor_section_content),
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(spacingSmall))
            TabNotationEditor(
                lines = lines,
                onAddNote = onAddNote,
                onAddLine = onAddLine,
                onDeleteLine = onDeleteLine,
                onDeleteNote = onDeleteNote,
                onEditHole = onEditHole,
                onToggleBlow = onToggleBlow,
                onToggleSlide = onToggleSlide,
                onMoveNote = onMoveNote,
                onPreviewNote = onPreviewNote,
                onPreviewStop = onPreviewStop,
                lineSpacing = spacingMedium,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
