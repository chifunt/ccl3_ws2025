package com.chifunt.chromaticharptabs.ui.components.settings

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.chifunt.chromaticharptabs.R
import com.chifunt.chromaticharptabs.ui.components.TopBackBar

@Composable
fun SettingsHeader(
    onBack: () -> Unit,
    spacingSmall: Dp,
    spacingMedium: Dp
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
    Spacer(Modifier.height(spacingMedium))
}
