package com.chifunt.chromaticharptabs.ui.components.common

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource

@Composable
fun FavoriteToggleButton(
    isFavorite: Boolean,
    onToggle: () -> Unit,
    @StringRes contentDescriptionRes: Int? = null
) {
    val scale = remember { Animatable(1f) }
    val halo = remember { Animatable(1f) }
    var wasFavorite by remember { mutableStateOf(isFavorite) }
    LaunchedEffect(isFavorite) {
        val shouldAnimate = isFavorite && !wasFavorite
        wasFavorite = isFavorite
        if (shouldAnimate) {
            scale.snapTo(1f)
            halo.snapTo(0f)
            scale.animateTo(
                targetValue = 1.18f,
                animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing)
            )
            scale.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 160, easing = FastOutSlowInEasing)
            )
            halo.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing)
            )
        } else {
            scale.snapTo(1f)
            halo.snapTo(1f)
        }
    }

    DebouncedIconButton(onClick = onToggle) {
        val favoriteTint = MaterialTheme.colorScheme.secondary
        val inactiveTint = MaterialTheme.colorScheme.onSurfaceVariant
        Icon(
            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
            contentDescription = contentDescriptionRes?.let { stringResource(it) },
            tint = if (isFavorite) favoriteTint else inactiveTint,
            modifier = Modifier
                .graphicsLayer(
                    scaleX = scale.value,
                    scaleY = scale.value
                )
                .drawBehind {
                    if (isFavorite && halo.value < 1f) {
                        val alpha = (1f - halo.value).coerceIn(0f, 1f) * 0.35f
                        if (alpha > 0f) {
                            drawCircle(
                                color = favoriteTint.copy(alpha = alpha),
                                radius = size.minDimension / 2f * (1f + halo.value)
                            )
                        }
                    }
                }
        )
    }
}
