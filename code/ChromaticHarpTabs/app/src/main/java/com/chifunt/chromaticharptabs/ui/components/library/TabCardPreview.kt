package com.chifunt.chromaticharptabs.ui.components.library

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.chifunt.chromaticharptabs.data.Tab
import com.chifunt.chromaticharptabs.ui.components.cards.TabCard
import com.chifunt.chromaticharptabs.ui.theme.ChromaticHarpTabsTheme

@Composable
private fun TabCardPreview() {
    ChromaticHarpTabsTheme {
        TabCard(
            tab = Tab(
                id = 1,
                title = "Autumn Leaves",
                artist = "Joseph Kosma",
                key = "C",
                difficulty = "Medium",
                tags = "",
                content = "",
                isFavorite = true,
                createdAt = 0L,
                updatedAt = 0L
            ),
            onOpen = {},
            onToggleFavorite = {}
        )
    }
}
