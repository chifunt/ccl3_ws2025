package com.chifunt.chromaticharptabs.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.chifunt.chromaticharptabs.R
import com.chifunt.chromaticharptabs.ui.components.TopBackBar

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    val spacingMedium = dimensionResource(R.dimen.spacing_medium)
    val spacingSmall = dimensionResource(R.dimen.spacing_small)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(spacingMedium)
    ) {
        TopBackBar(onBack = onBack)
        Spacer(Modifier.height(spacingSmall))
        Text(
            text = stringResource(R.string.settings_title),
            fontSize = dimensionResource(R.dimen.headline).value.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(spacingSmall))
        Text(text = stringResource(R.string.settings_description))
    }
}
