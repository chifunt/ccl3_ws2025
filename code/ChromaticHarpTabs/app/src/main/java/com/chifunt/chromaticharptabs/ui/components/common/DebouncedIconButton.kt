package com.chifunt.chromaticharptabs.ui.components.common

import android.os.SystemClock
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.chifunt.chromaticharptabs.ui.haptics.rememberHapticClick

@Composable
fun DebouncedIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    debounceMs: Long = 600L,
    content: @Composable () -> Unit
) {
    var lastClickTime by remember { mutableLongStateOf(0L) }
    val hapticClick = rememberHapticClick(onClick)

    IconButton(
        onClick = {
            val now = SystemClock.elapsedRealtime()
            if (now - lastClickTime >= debounceMs) {
                lastClickTime = now
                hapticClick()
            }
        },
        modifier = modifier,
        enabled = enabled
    ) {
        content()
    }
}

@Composable
fun DebouncedFilledIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    debounceMs: Long = 600L,
    content: @Composable () -> Unit
) {
    var lastClickTime by remember { mutableLongStateOf(0L) }
    val hapticClick = rememberHapticClick(onClick)

    FilledIconButton(
        onClick = {
            val now = SystemClock.elapsedRealtime()
            if (now - lastClickTime >= debounceMs) {
                lastClickTime = now
                hapticClick()
            }
        },
        modifier = modifier,
        enabled = enabled
    ) {
        content()
    }
}
