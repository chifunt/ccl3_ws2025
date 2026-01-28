package com.chifunt.chromaticharptabs.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.chifunt.chromaticharptabs.ChromaticHarpTabsApplication
import com.chifunt.chromaticharptabs.data.notation.HarmonicaNoteMap
import com.chifunt.chromaticharptabs.ui.viewmodels.OnboardingViewModel
import com.chifunt.chromaticharptabs.ui.viewmodels.PracticeViewModel
import com.chifunt.chromaticharptabs.ui.viewmodels.SettingsViewModel
import com.chifunt.chromaticharptabs.ui.viewmodels.TabDetailViewModel
import com.chifunt.chromaticharptabs.ui.viewmodels.TabEditorViewModel
import com.chifunt.chromaticharptabs.ui.viewmodels.TabListViewModel
import com.chifunt.chromaticharptabs.ui.viewmodels.VirtualHarmonicaViewModel

object AppViewModelProvider {

    val Factory = viewModelFactory {
        initializer {
            val app = this[APPLICATION_KEY] as ChromaticHarpTabsApplication
            TabListViewModel(app.tabRepository)
        }

        initializer {
            val app = this[APPLICATION_KEY] as ChromaticHarpTabsApplication
            TabDetailViewModel(this.createSavedStateHandle(), app.tabRepository, HarmonicaNoteMap)
        }

        initializer {
            val app = this[APPLICATION_KEY] as ChromaticHarpTabsApplication
            TabEditorViewModel(this.createSavedStateHandle(), app.tabRepository, HarmonicaNoteMap)
        }

        initializer {
            val app = this[APPLICATION_KEY] as ChromaticHarpTabsApplication
            PracticeViewModel(this.createSavedStateHandle(), app.tabRepository, HarmonicaNoteMap)
        }

        initializer {
            VirtualHarmonicaViewModel(HarmonicaNoteMap)
        }

        initializer {
            val app = this[APPLICATION_KEY] as ChromaticHarpTabsApplication
            SettingsViewModel(app.settingsRepository)
        }

        initializer {
            OnboardingViewModel(HarmonicaNoteMap)
        }
    }
}
