package com.chifunt.chromaticharptabs.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.chifunt.chromaticharptabs.R
import com.chifunt.chromaticharptabs.data.model.TabNote
import com.chifunt.chromaticharptabs.data.notation.HarmonicaNoteMap
import com.chifunt.chromaticharptabs.ui.audio.SineTonePlayer
import com.chifunt.chromaticharptabs.ui.components.virtualharmonica.HarmonicaColumn
import com.chifunt.chromaticharptabs.ui.components.virtualharmonica.HarmonicaKeyId
import com.chifunt.chromaticharptabs.ui.components.virtualharmonica.HarmonicaRow
import com.chifunt.chromaticharptabs.ui.components.virtualharmonica.SlideHoldButton
import com.chifunt.chromaticharptabs.ui.components.virtualharmonica.VirtualHarmonicaHeader
import com.chifunt.chromaticharptabs.ui.haptics.LocalHapticsEnabled
import com.chifunt.chromaticharptabs.ui.theme.RosePineDawnPine
import com.chifunt.chromaticharptabs.ui.theme.RosePinePine

@Composable
fun VirtualHarmonicaScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacingSmall = dimensionResource(R.dimen.spacing_small)
    val spacingMedium = dimensionResource(R.dimen.spacing_medium)
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val activeColor = if (isDark) RosePinePine else RosePineDawnPine
    val tonePlayer = remember { SineTonePlayer() }
    val haptic = LocalHapticFeedback.current
    val hapticsEnabled = LocalHapticsEnabled.current
    var slideActive by remember { mutableStateOf(false) }
    var activeKey by remember { mutableStateOf<HarmonicaKeyId?>(null) }
    val noteBounds = remember { mutableStateMapOf<HarmonicaKeyId, Rect>() }
    var containerOffset by remember { mutableStateOf(Offset.Zero) }
    val cornerRadiusPx = with(LocalDensity.current) { 10.dp.toPx() }

    DisposableEffect(Unit) {
        onDispose { tonePlayer.release() }
    }

    LaunchedEffect(slideActive, activeKey) {
        val key = activeKey ?: return@LaunchedEffect
        val note = TabNote(hole = key.hole, isBlow = key.isBlow, isSlide = slideActive)
        HarmonicaNoteMap.frequencyFor(note)?.let { tonePlayer.start(it) }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(
                PaddingValues(
                    start = spacingMedium,
                    end = spacingMedium,
                    top = spacingSmall,
                    bottom = spacingMedium
                )
            )
    ) {
        VirtualHarmonicaHeader(
            spacingSmall = spacingSmall,
            onBack = onBack
        )
        Spacer(Modifier.height(spacingMedium))

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(bottom = spacingSmall)
                .onGloballyPositioned { coordinates ->
                    containerOffset = coordinates.positionInRoot()
                }
                .pointerInput(noteBounds, slideActive, hapticsEnabled, containerOffset) {
                    val pointerKeyMap = mutableMapOf<Long, HarmonicaKeyId>()
                    var activePointerId: Long? = null
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()
                            val pressed = event.changes.filter { it.pressed }

                            if (pressed.isEmpty()) {
                                if (activeKey != null) {
                                    activeKey = null
                                    tonePlayer.stop()
                                }
                                pointerKeyMap.clear()
                                activePointerId = null
                                continue
                            }

                            pressed.forEach { change ->
                                val pointerInRoot = change.position + containerOffset
                                val hit = noteBounds.entries.firstOrNull { it.value.contains(pointerInRoot) }?.key
                                if (hit != null) {
                                    pointerKeyMap[change.id.value] = hit
                                } else {
                                    pointerKeyMap.remove(change.id.value)
                                }
                            }

                            val pressedIds = pressed.map { it.id.value }.toSet()
                            pointerKeyMap.keys.retainAll(pressedIds)

                            val candidatePointerId = when {
                                activePointerId != null &&
                                    pressedIds.contains(activePointerId) &&
                                    pointerKeyMap.containsKey(activePointerId) -> activePointerId
                                else -> {
                                    val first = pressedIds.firstOrNull { pointerKeyMap.containsKey(it) }
                                    activePointerId = first
                                    first
                                }
                            }

                            val nextKey = candidatePointerId?.let { pointerKeyMap[it] }
                            if (nextKey != activeKey) {
                                activeKey = nextKey
                                if (nextKey == null) {
                                    tonePlayer.stop()
                                } else {
                                    if (hapticsEnabled) {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    }
                                    val note = TabNote(
                                        hole = nextKey.hole,
                                        isBlow = nextKey.isBlow,
                                        isSlide = slideActive
                                    )
                                    HarmonicaNoteMap.frequencyFor(note)?.let { tonePlayer.start(it) }
                                }
                            }
                        }
                    }
                }
        ) {
            val isLandscape = maxWidth > maxHeight
            val labelHeight = 22.dp
            if (isLandscape) {
                val rowHeight = (maxHeight - spacingMedium) / 2
                val keyWidth = (maxWidth - spacingSmall * 11) / 12
                val keyHeight = ((rowHeight - labelHeight - spacingSmall).coerceAtLeast(0.dp) / 1)
                    .coerceIn(28.dp, 44.dp)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(spacingMedium)
                ) {
                    HarmonicaRow(
                        label = stringResource(R.string.blow_label),
                        isBlow = true,
                        activeKey = activeKey,
                        activeColor = activeColor,
                        cornerRadiusPx = cornerRadiusPx,
                        slideActive = slideActive,
                        keyWidth = keyWidth,
                        keyHeight = keyHeight,
                        noteBounds = noteBounds,
                        modifier = Modifier.fillMaxWidth().height(rowHeight)
                    )
                    HarmonicaRow(
                        label = stringResource(R.string.draw_label),
                        isBlow = false,
                        activeKey = activeKey,
                        activeColor = activeColor,
                        cornerRadiusPx = cornerRadiusPx,
                        slideActive = slideActive,
                        keyWidth = keyWidth,
                        keyHeight = keyHeight,
                        noteBounds = noteBounds,
                        modifier = Modifier.fillMaxWidth().height(rowHeight)
                    )
                }
            } else {
                val columnWidth = (maxWidth - spacingMedium) / 2
                val keyHeight = ((maxHeight - labelHeight - spacingSmall * 12).coerceAtLeast(0.dp) / 12)
                    .coerceIn(28.dp, 44.dp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacingMedium)
                ) {
                    HarmonicaColumn(
                        label = stringResource(R.string.draw_label),
                        isBlow = false,
                        activeKey = activeKey,
                        activeColor = activeColor,
                        cornerRadiusPx = cornerRadiusPx,
                        slideActive = slideActive,
                        keyWidth = columnWidth,
                        keyHeight = keyHeight,
                        noteBounds = noteBounds,
                        modifier = Modifier.width(columnWidth)
                    )
                    HarmonicaColumn(
                        label = stringResource(R.string.blow_label),
                        isBlow = true,
                        activeKey = activeKey,
                        activeColor = activeColor,
                        cornerRadiusPx = cornerRadiusPx,
                        slideActive = slideActive,
                        keyWidth = columnWidth,
                        keyHeight = keyHeight,
                        noteBounds = noteBounds,
                        modifier = Modifier.width(columnWidth)
                    )
                }
            }
        }

        Spacer(Modifier.height(spacingMedium))

        SlideHoldButton(
            active = slideActive,
            cornerRadiusPx = cornerRadiusPx,
            onPress = {
                if (hapticsEnabled) {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                }
                slideActive = true
            },
            onRelease = { slideActive = false }
        )
    }
}
