package com.chifunt.chromaticharptabs.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.chifunt.chromaticharptabs.data.model.TabNote
import com.chifunt.chromaticharptabs.data.notation.NoteFrequencyProvider

class VirtualHarmonicaViewModel(
    private val frequencyProvider: NoteFrequencyProvider
) : ViewModel() {

    fun frequencyFor(note: TabNote): Double? {
        return frequencyProvider.frequencyFor(note)
    }
}
