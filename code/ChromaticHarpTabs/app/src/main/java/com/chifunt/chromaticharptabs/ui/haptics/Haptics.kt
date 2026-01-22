package com.chifunt.chromaticharptabs.ui.haptics

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback

val LocalHapticsEnabled = staticCompositionLocalOf { true }

@Composable
fun rememberHapticClick(
    onClick: () -> Unit,
    type: HapticFeedbackType = HapticFeedbackType.TextHandleMove
): () -> Unit {
    val haptic = LocalHapticFeedback.current
    val enabled = LocalHapticsEnabled.current
    return {
        if (enabled) {
            haptic.performHapticFeedback(type)
        }
        onClick()
    }
}

@Composable
fun rememberHapticFeedback(
    type: HapticFeedbackType = HapticFeedbackType.TextHandleMove
): () -> Unit {
    val haptic = LocalHapticFeedback.current
    val enabled = LocalHapticsEnabled.current
    return {
        if (enabled) {
            haptic.performHapticFeedback(type)
        }
    }
}
