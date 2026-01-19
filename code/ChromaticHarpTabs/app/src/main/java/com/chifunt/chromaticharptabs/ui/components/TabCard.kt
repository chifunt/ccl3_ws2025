package com.chifunt.chromaticharptabs.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.chifunt.chromaticharptabs.R
import com.chifunt.chromaticharptabs.data.Tab

@Composable
fun TabCard(
    tab: Tab,
    onOpen: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onOpen,
        shape = MaterialTheme.shapes.medium
    ) {
        Column(Modifier.fillMaxWidth().padding(dimensionResource(R.dimen.spacing_medium))) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(tab.title, fontWeight = FontWeight.SemiBold)
                    Text(tab.artist.ifBlank { stringResource(R.string.unknown_artist) })
                }
                IconButton(onClick = onToggleFavorite) {
                    Icon(
                        imageVector = if (tab.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = stringResource(R.string.favorite_toggle)
                    )
                }
            }
            Spacer(Modifier.height(dimensionResource(R.dimen.spacing_small)))
            Text(stringResource(R.string.tab_metadata_line, tab.key, tab.difficulty))
            if (tab.tags.isNotBlank()) {
                Text(stringResource(R.string.tab_tags_line, tab.tags))
            }
        }
    }
}
