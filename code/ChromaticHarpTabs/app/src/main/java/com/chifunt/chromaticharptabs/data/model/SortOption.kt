package com.chifunt.chromaticharptabs.data.model

import androidx.annotation.StringRes
import com.chifunt.chromaticharptabs.R

enum class SortOption(@StringRes val labelRes: Int) {
    Title(R.string.sort_option_title),
    Artist(R.string.sort_option_artist),
    Newest(R.string.sort_option_newest),
    Oldest(R.string.sort_option_oldest)
}
