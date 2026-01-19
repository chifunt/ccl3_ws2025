package com.chifunt.chromaticharptabs.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.chifunt.chromaticharptabs.ChromaticHarpTabsApplication
import com.chifunt.chromaticharptabs.ui.viewmodel.PracticeViewModel
import com.chifunt.chromaticharptabs.ui.viewmodel.TabDetailViewModel
import com.chifunt.chromaticharptabs.ui.viewmodel.TabEditorViewModel
import com.chifunt.chromaticharptabs.ui.viewmodel.TabListViewModel

object AppViewModelProvider {

    val Factory = viewModelFactory {
        initializer {
            val app = this[APPLICATION_KEY] as ChromaticHarpTabsApplication
            TabListViewModel(app.tabRepository)
        }

        initializer {
            val app = this[APPLICATION_KEY] as ChromaticHarpTabsApplication
            TabDetailViewModel(this.createSavedStateHandle(), app.tabRepository)
        }

        initializer {
            val app = this[APPLICATION_KEY] as ChromaticHarpTabsApplication
            TabEditorViewModel(this.createSavedStateHandle(), app.tabRepository)
        }

        initializer {
            val app = this[APPLICATION_KEY] as ChromaticHarpTabsApplication
            PracticeViewModel(this.createSavedStateHandle(), app.tabRepository)
        }
    }
}
