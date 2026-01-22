package com.chifunt.chromaticharptabs.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chifunt.chromaticharptabs.R
import com.chifunt.chromaticharptabs.data.model.TabNote
import com.chifunt.chromaticharptabs.data.notation.HarmonicaNoteMap
import com.chifunt.chromaticharptabs.ui.audio.SineTonePlayer
import com.chifunt.chromaticharptabs.ui.components.common.DebouncedIconButton
import com.chifunt.chromaticharptabs.ui.components.notation.NoteGlyph
import com.chifunt.chromaticharptabs.ui.haptics.LocalHapticsEnabled
import com.chifunt.chromaticharptabs.ui.theme.RosePineDawnPine
import com.chifunt.chromaticharptabs.ui.theme.RosePinePine

private data class HarmonicaKeyId(
    val hole: Int,
    val isBlow: Boolean
)

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
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            DebouncedIconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back_button)
                )
            }
            Text(
                text = stringResource(R.string.virtual_harmonica),
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                modifier = Modifier.padding(end = spacingSmall)
            )
        }
        Spacer(Modifier.height(spacingMedium))

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
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
            val columnWidth = (maxWidth - spacingMedium) / 2
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
                    noteBounds = noteBounds,
                    modifier = Modifier.width(columnWidth)
                )
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

@Composable
private fun HarmonicaColumn(
    label: String,
    isBlow: Boolean,
    activeKey: HarmonicaKeyId?,
    activeColor: Color,
    cornerRadiusPx: Float,
    slideActive: Boolean,
    noteBounds: MutableMap<HarmonicaKeyId, Rect>,
    modifier: Modifier = Modifier
) {
    val spacingSmall = dimensionResource(R.dimen.spacing_small)
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(spacingSmall),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        (1..12).forEach { hole ->
            val pressed = activeKey?.hole == hole && activeKey.isBlow == isBlow
            HarmonicaKey(
                hole = hole,
                pressed = pressed,
                activeColor = activeColor,
                cornerRadiusPx = cornerRadiusPx,
                isSlide = slideActive,
                isBlow = isBlow,
                onBounds = { bounds ->
                    noteBounds[HarmonicaKeyId(hole, isBlow)] = bounds
                }
            )
        }
    }
}

@Composable
private fun HarmonicaKey(
    hole: Int,
    pressed: Boolean,
    activeColor: Color,
    cornerRadiusPx: Float,
    isSlide: Boolean,
    isBlow: Boolean,
    onBounds: (Rect) -> Unit
) {
    val border = dimensionResource(R.dimen.border_stroke_width)
    val size = 44.dp
    val scale = if (pressed) 1.08f else 1f
    val color = if (pressed) activeColor else MaterialTheme.colorScheme.onSurface
    val outlineColor = MaterialTheme.colorScheme.outline
    Box(
        modifier = Modifier
            .size(size)
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale
            )
            .onGloballyPositioned { coordinates ->
                onBounds(coordinates.boundsInRoot())
            }
            .background(
                color = if (pressed) color.copy(alpha = 0.15f) else Color.Transparent,
                shape = MaterialTheme.shapes.small
            )
            .drawBehind {
                drawRoundRect(
                    color = outlineColor,
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadiusPx, cornerRadiusPx),
                    style = Stroke(width = border.toPx())
                )
            },
        contentAlignment = Alignment.Center
    ) {
        NoteGlyph(
            hole = hole,
            isBlow = isBlow,
            isSlide = isSlide,
            color = color,
            noteSize = size,
            isCorrect = pressed,
            pressed = pressed
        )
    }
}

@Composable
private fun SlideHoldButton(
    active: Boolean,
    cornerRadiusPx: Float,
    onPress: () -> Unit,
    onRelease: () -> Unit
) {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val activeColor = if (isDark) RosePinePine else RosePineDawnPine
    val buttonColor = if (active) activeColor.copy(alpha = 0.2f) else Color.Transparent
    val border = dimensionResource(R.dimen.border_stroke_width)
    val outlineColor = MaterialTheme.colorScheme.outline
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(84.dp)
            .background(buttonColor, shape = MaterialTheme.shapes.small)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        onPress()
                        tryAwaitRelease()
                        onRelease()
                    }
                )
            }
            .drawBehind {
                drawRoundRect(
                    color = outlineColor,
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadiusPx, cornerRadiusPx),
                    style = Stroke(width = border.toPx())
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.slide_hold),
            fontWeight = FontWeight.SemiBold,
            color = if (active) activeColor else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
