package com.chifunt.chromaticharptabs.ui.components.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.chifunt.chromaticharptabs.data.Tab
import com.chifunt.chromaticharptabs.ui.components.cards.TabCard

@Composable
fun TabList(
    tabs: List<Tab>,
    onOpen: (Int) -> Unit,
    onToggleFavorite: (Tab) -> Unit,
    spacingSmall: Dp,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    emptyMessage: String
) {
    if (tabs.isEmpty()) {
        LibraryEmptyState(message = emptyMessage)
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(spacingSmall),
            contentPadding = contentPadding
        ) {
            items(tabs) { tab ->
                TabCard(
                    tab = tab,
                    onOpen = { onOpen(tab.id) },
                    onToggleFavorite = { onToggleFavorite(tab) }
                )
            }
        }
    }
}
