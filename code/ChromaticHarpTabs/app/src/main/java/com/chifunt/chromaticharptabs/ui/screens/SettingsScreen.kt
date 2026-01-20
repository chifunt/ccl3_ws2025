package com.chifunt.chromaticharptabs.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chifunt.chromaticharptabs.R
import com.chifunt.chromaticharptabs.ui.components.TopBackBar
import com.chifunt.chromaticharptabs.ui.AppViewModelProvider
import com.chifunt.chromaticharptabs.ui.components.FilterDropdownButton
import com.chifunt.chromaticharptabs.data.ThemeMode
import com.chifunt.chromaticharptabs.ui.viewmodels.SettingsViewModel

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    settingsViewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory),
    onBack: () -> Unit,
    onViewOnboarding: () -> Unit
) {
    val spacingMedium = dimensionResource(R.dimen.spacing_medium)
    val spacingSmall = dimensionResource(R.dimen.spacing_small)
    val filterHeight = dimensionResource(R.dimen.filter_chip_height)
    val themeMode = settingsViewModel.themeMode.collectAsStateWithLifecycle().value
    val themeOptions = listOf(
        ThemeMode.SYSTEM to stringResource(R.string.theme_system),
        ThemeMode.LIGHT to stringResource(R.string.theme_light),
        ThemeMode.DARK to stringResource(R.string.theme_dark)
    )
    val selectedThemeLabel = themeOptions.firstOrNull { it.first == themeMode }?.second
        ?: stringResource(R.string.theme_system)

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
        Spacer(Modifier.height(spacingMedium))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.settings_theme_detail),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            FilterDropdownButton(
                label = stringResource(R.string.settings_theme),
                selected = selectedThemeLabel,
                options = themeOptions.map { it.second },
                onSelected = { label ->
                    val mode = themeOptions.firstOrNull { it.second == label }?.first
                        ?: ThemeMode.SYSTEM
                    settingsViewModel.setThemeMode(mode)
                },
                modifier = Modifier.height(filterHeight)
            )
        }

        Spacer(Modifier.height(spacingMedium))

        TextButton(
            onClick = onViewOnboarding,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.settings_view_onboarding))
        }
    }
}
