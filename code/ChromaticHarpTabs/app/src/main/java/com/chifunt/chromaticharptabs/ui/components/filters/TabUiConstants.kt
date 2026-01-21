package com.chifunt.chromaticharptabs.ui.components.filters

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringArrayResource
import com.chifunt.chromaticharptabs.R

@Composable
internal fun difficultyOptions(): List<String> {
    return stringArrayResource(R.array.difficulty_options).toList()
}

@Composable
internal fun keyOptions(): List<String> {
    return stringArrayResource(R.array.key_options).toList()
}
