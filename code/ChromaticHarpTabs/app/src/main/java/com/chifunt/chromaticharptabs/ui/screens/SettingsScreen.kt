package com.chifunt.chromaticharptabs.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chifunt.chromaticharptabs.R
import com.chifunt.chromaticharptabs.ui.AppViewModelProvider
import com.chifunt.chromaticharptabs.data.model.ThemeMode
import com.chifunt.chromaticharptabs.ui.components.settings.SettingsHeader
import com.chifunt.chromaticharptabs.ui.components.settings.ThemeRow
import com.chifunt.chromaticharptabs.ui.components.settings.ViewOnboardingButton
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
            .verticalScroll(rememberScrollState())
    ) {
        SettingsHeader(
            onBack = onBack,
            spacingSmall = spacingSmall,
            spacingMedium = spacingMedium
        )

        ThemeRow(
            selectedThemeLabel = selectedThemeLabel,
            themeOptions = themeOptions,
            filterHeight = filterHeight,
            spacingSmall = spacingSmall,
            onThemeSelected = { label ->
                val mode = themeOptions.firstOrNull { it.second == label }?.first
                    ?: ThemeMode.SYSTEM
                settingsViewModel.setThemeMode(mode)
            }
        )

        Spacer(Modifier.height(spacingMedium))

        ViewOnboardingButton(onClick = onViewOnboarding)
    }
}
