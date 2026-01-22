package com.chifunt.chromaticharptabs.ui.components.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import com.chifunt.chromaticharptabs.R
import com.chifunt.chromaticharptabs.ui.components.common.HapticSwitch

@Composable
fun HapticsRow(
    enabled: Boolean,
    spacingSmall: Dp,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacingSmall)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = spacingSmall)
        ) {
            Text(
                text = stringResource(R.string.settings_haptics_detail),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )
        }
        HapticSwitch(
            checked = enabled,
            onCheckedChange = onToggle
        )
    }
}
