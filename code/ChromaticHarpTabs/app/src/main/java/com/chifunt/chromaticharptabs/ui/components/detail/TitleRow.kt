package com.chifunt.chromaticharptabs.ui.components.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.chifunt.chromaticharptabs.R
import com.chifunt.chromaticharptabs.ui.components.FavoriteToggleButton

@Composable
fun TitleRow(
    title: String,
    artist: String,
    isFavorite: Boolean,
    spacingSmall: Dp,
    onToggleFavorite: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.weight(1f).padding(start = spacingSmall)) {
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 20.sp)
            Text(artist.ifBlank { stringResource(R.string.unknown_artist) })
        }
        FavoriteToggleButton(
            isFavorite = isFavorite,
            onToggle = onToggleFavorite,
            contentDescriptionRes = R.string.favorite_toggle
        )
    }
}
