package com.chifunt.chromaticharptabs.data.notation

import com.chifunt.chromaticharptabs.data.model.TabNote

interface NoteFrequencyProvider {
    fun frequencyFor(note: TabNote): Double?
}
