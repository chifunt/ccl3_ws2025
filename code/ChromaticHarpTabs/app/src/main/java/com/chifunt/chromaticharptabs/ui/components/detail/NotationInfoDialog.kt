package com.chifunt.chromaticharptabs.ui.components.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import com.chifunt.chromaticharptabs.R
import com.chifunt.chromaticharptabs.data.model.TabNote
import com.chifunt.chromaticharptabs.ui.components.notation.TabNotationInlineDisplay
import com.chifunt.chromaticharptabs.ui.components.common.HapticButton

@Composable
fun NotationInfoDialog(
    isVisible: Boolean,
    spacingSmall: Dp,
    onDismiss: () -> Unit
) {
    if (!isVisible) {
        return
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            HapticButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.close_button))
            }
        },
        title = { Text(text = stringResource(R.string.notation_info_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(spacingSmall)) {
                Text(text = stringResource(R.string.notation_info_blow))
                TabNotationInlineDisplay(
                    lines = listOf(listOf(TabNote(hole = 4, isBlow = true, isSlide = false)))
                )
                Spacer(Modifier.height(spacingSmall))
                Text(text = stringResource(R.string.notation_info_draw))
                TabNotationInlineDisplay(
                    lines = listOf(listOf(TabNote(hole = 4, isBlow = false, isSlide = false)))
                )
                Spacer(Modifier.height(spacingSmall))
                Text(text = stringResource(R.string.notation_info_slide))
                TabNotationInlineDisplay(
                    lines = listOf(listOf(TabNote(hole = 4, isBlow = true, isSlide = true)))
                )
                Spacer(Modifier.height(spacingSmall))
                Text(text = stringResource(R.string.notation_info_draw_slide))
                TabNotationInlineDisplay(
                    lines = listOf(listOf(TabNote(hole = 4, isBlow = false, isSlide = true)))
                )
            }
        }
    )
}
