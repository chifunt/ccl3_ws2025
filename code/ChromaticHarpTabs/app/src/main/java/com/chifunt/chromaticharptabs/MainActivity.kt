package com.chifunt.chromaticharptabs

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chifunt.chromaticharptabs.ui.AppViewModelProvider
import com.chifunt.chromaticharptabs.ui.navigation.ChromaticHarpTabsApp
import com.chifunt.chromaticharptabs.ui.navigation.Routes
import com.chifunt.chromaticharptabs.ui.theme.ChromaticHarpTabsTheme
import com.chifunt.chromaticharptabs.ui.viewmodels.SettingsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settingsViewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory)
            val darkTheme = settingsViewModel.darkThemeEnabled.collectAsStateWithLifecycle().value
            val onboardingCompleted = settingsViewModel.onboardingCompleted.collectAsStateWithLifecycle().value

            ChromaticHarpTabsTheme(darkTheme = darkTheme) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val startRoute = if (onboardingCompleted) {
                        Routes.Library.route
                    } else {
                        Routes.Onboarding.route
                    }
                    ChromaticHarpTabsApp(
                        modifier = Modifier.padding(innerPadding),
                        startDestination = startRoute
                    )
                }
            }
        }
    }
}
