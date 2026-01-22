package com.chifunt.chromaticharptabs.ui.components.settings

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.chifunt.chromaticharptabs.R
import com.chifunt.chromaticharptabs.ui.components.common.HapticOutlinedButton

@Composable
fun ViewOnboardingButton(
    onClick: () -> Unit
) {
    HapticOutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = stringResource(R.string.settings_view_onboarding))
    }
}
