package com.chifunt.chromaticharptabs.ui.components.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.TextButton
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.material3.MaterialTheme
import com.chifunt.chromaticharptabs.ui.haptics.rememberHapticClick
import com.chifunt.chromaticharptabs.ui.haptics.rememberHapticFeedback

@Composable
fun HapticButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    shape: Shape = MaterialTheme.shapes.small,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    content: @Composable RowScope.() -> Unit
) {
    Button(
        onClick = rememberHapticClick(onClick = onClick),
        modifier = modifier,
        enabled = enabled,
        colors = colors,
        shape = shape,
        contentPadding = contentPadding,
        content = content
    )
}

@Composable
fun HapticOutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.outlinedButtonColors(),
    shape: Shape = MaterialTheme.shapes.small,
    border: BorderStroke? = null,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    content: @Composable RowScope.() -> Unit
) {
    OutlinedButton(
        onClick = rememberHapticClick(onClick = onClick),
        modifier = modifier,
        enabled = enabled,
        colors = colors,
        shape = shape,
        border = border,
        contentPadding = contentPadding,
        content = content
    )
}

@Composable
fun HapticTextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.textButtonColors(),
    shape: Shape = MaterialTheme.shapes.small,
    contentPadding: PaddingValues = ButtonDefaults.TextButtonContentPadding,
    content: @Composable RowScope.() -> Unit
) {
    TextButton(
        onClick = rememberHapticClick(onClick = onClick),
        modifier = modifier,
        enabled = enabled,
        colors = colors,
        shape = shape,
        contentPadding = contentPadding,
        content = content
    )
}

@Composable
fun HapticIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    IconButton(
        onClick = rememberHapticClick(onClick = onClick),
        modifier = modifier,
        enabled = enabled,
        content = content
    )
}

@Composable
fun HapticFloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = contentColorFor(containerColor),
    content: @Composable () -> Unit
) {
    FloatingActionButton(
        onClick = rememberHapticClick(onClick = onClick),
        modifier = modifier,
        containerColor = containerColor,
        contentColor = contentColor,
        content = content
    )
}

@Composable
fun HapticSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: SwitchColors = SwitchDefaults.colors()
) {
    val haptic = rememberHapticFeedback()
    Switch(
        checked = checked,
        onCheckedChange = { checkedValue ->
            haptic()
            onCheckedChange(checkedValue)
        },
        modifier = modifier,
        enabled = enabled,
        colors = colors
    )
}
