package com.chifunt.chromaticharptabs.ui.components.practice

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.MicOff
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import com.chifunt.chromaticharptabs.R
import com.chifunt.chromaticharptabs.ui.components.common.HapticIconButton
import com.chifunt.chromaticharptabs.ui.components.common.HapticSwitch
import com.chifunt.chromaticharptabs.ui.components.common.TopBackBar

@Composable
fun PracticeHeader(
    spacingSmall: Dp,
    spacingMedium: Dp,
    micEnabled: Boolean,
    micScale: Float,
    micHalo: Float,
    onMicToggle: (Boolean) -> Unit,
    onBack: () -> Unit,
    showSettings: Boolean,
    onShowSettingsChange: (Boolean) -> Unit,
    noteSize: Float,
    onNoteSizeChange: (Float) -> Unit,
    autoAdvanceLine: Boolean,
    onAutoAdvanceLineChange: (Boolean) -> Unit,
    advanceOnNoteStart: Boolean,
    onAdvanceOnNoteStartChange: (Boolean) -> Unit
) {
    TopBackBar(
        onBack = onBack,
        actions = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconToggleButton(
                    checked = micEnabled,
                    onCheckedChange = onMicToggle
                ) {
                    if (micEnabled) {
                        val micTint = MaterialTheme.colorScheme.primary
                        Icon(
                            imageVector = Icons.Outlined.Mic,
                            contentDescription = stringResource(R.string.practice_mic_on),
                            tint = micTint,
                            modifier = Modifier
                                .graphicsLayer(
                                    scaleX = micScale,
                                    scaleY = micScale
                                )
                                .drawBehind {
                                    val alpha = (1f - micHalo).coerceIn(0f, 1f) * 0.35f
                                    if (alpha > 0f) {
                                        drawCircle(
                                            color = micTint.copy(alpha = alpha),
                                            radius = size.minDimension / 2f * (1f + micHalo)
                                        )
                                    }
                                }
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.MicOff,
                            contentDescription = stringResource(R.string.practice_mic_off),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                HapticIconButton(onClick = { onShowSettingsChange(true) }) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = stringResource(R.string.practice_settings)
                    )
                }
                DropdownMenu(
                    expanded = showSettings,
                    onDismissRequest = { onShowSettingsChange(false) }
                ) {
                    Column(modifier = Modifier.padding(spacingMedium)) {
                        Text(
                            text = stringResource(R.string.practice_note_size),
                            fontWeight = FontWeight.Medium
                        )
                        Slider(
                            value = noteSize,
                            onValueChange = onNoteSizeChange,
                            valueRange = 24f..48f
                        )
                        Spacer(Modifier.height(spacingSmall))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = stringResource(R.string.practice_auto_advance))
                            Spacer(Modifier.width(spacingSmall))
                            HapticSwitch(
                                checked = autoAdvanceLine,
                                onCheckedChange = onAutoAdvanceLineChange
                            )
                        }
                        Spacer(Modifier.height(spacingSmall))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = stringResource(R.string.practice_advance_on_start))
                            Spacer(Modifier.width(spacingSmall))
                            HapticSwitch(
                                checked = advanceOnNoteStart,
                                onCheckedChange = onAdvanceOnNoteStartChange
                            )
                        }
                    }
                }
            }
        }
    )
}
