package com.chifunt.chromaticharptabs.ui.components.detail

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import com.chifunt.chromaticharptabs.R
import com.chifunt.chromaticharptabs.data.model.TabNotationJson
import com.chifunt.chromaticharptabs.data.model.TabNote
import com.chifunt.chromaticharptabs.ui.components.notation.TabNotationInlineDisplay
import com.chifunt.chromaticharptabs.ui.theme.RosePineDawnPine
import com.chifunt.chromaticharptabs.ui.theme.RosePinePine

@Composable
fun NotationSection(
    content: String,
    spacingSmall: Dp,
    spacingMedium: Dp,
    contentTextColor: Color,
    onNotePress: ((TabNote) -> Unit)? = null,
    onNoteRelease: ((TabNote) -> Unit)? = null
) {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val activeColor = if (isDark) RosePinePine else RosePineDawnPine
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
                    tint = contentTextColor
                )
                Spacer(Modifier.width(spacingSmall))
                Text(
                    text = stringResource(R.string.tab_content_label),
                    fontWeight = FontWeight.SemiBold,
                    color = contentTextColor
                )
            }
            Spacer(Modifier.height(spacingMedium))
            val notation = TabNotationJson.fromJson(content)
            if (notation != null && notation.lines.isNotEmpty()) {
                TabNotationInlineDisplay(
                    lines = notation.lines,
                    lineSpacing = spacingMedium,
                    glyphColor = contentTextColor,
                    pressHighlightColor = activeColor,
                    pressHighlightScale = true,
                    hapticOnPress = onNotePress != null,
                    onNotePress = onNotePress,
                    onNoteRelease = onNoteRelease
                )
            } else {
                Text(content, color = contentTextColor)
            }
        }
    }
}
