package com.chifunt.chromaticharptabs.data.notation

import com.chifunt.chromaticharptabs.data.model.TabNote

data class HarmonicaNoteKey(
    val hole: Int,
    val isBlow: Boolean,
    val isSlide: Boolean
)

object HarmonicaNoteMap : NoteFrequencyProvider {
    private val frequencies = mapOf(
        HarmonicaNoteKey(hole = 1, isBlow = true, isSlide = false) to 261.63,
        HarmonicaNoteKey(hole = 1, isBlow = false, isSlide = false) to 293.66,
        HarmonicaNoteKey(hole = 1, isBlow = true, isSlide = true) to 277.18,
        HarmonicaNoteKey(hole = 1, isBlow = false, isSlide = true) to 311.13,
        HarmonicaNoteKey(hole = 2, isBlow = true, isSlide = false) to 329.63,
        HarmonicaNoteKey(hole = 2, isBlow = false, isSlide = false) to 349.23,
        HarmonicaNoteKey(hole = 2, isBlow = true, isSlide = true) to 349.23,
        HarmonicaNoteKey(hole = 2, isBlow = false, isSlide = true) to 369.99,
        HarmonicaNoteKey(hole = 3, isBlow = true, isSlide = false) to 392.00,
        HarmonicaNoteKey(hole = 3, isBlow = false, isSlide = false) to 440.00,
        HarmonicaNoteKey(hole = 3, isBlow = true, isSlide = true) to 415.30,
        HarmonicaNoteKey(hole = 3, isBlow = false, isSlide = true) to 466.16,
        HarmonicaNoteKey(hole = 4, isBlow = true, isSlide = false) to 523.25,
        HarmonicaNoteKey(hole = 4, isBlow = false, isSlide = false) to 493.88,
        HarmonicaNoteKey(hole = 4, isBlow = true, isSlide = true) to 554.37,
        HarmonicaNoteKey(hole = 4, isBlow = false, isSlide = true) to 523.25,
        HarmonicaNoteKey(hole = 5, isBlow = true, isSlide = false) to 523.25,
        HarmonicaNoteKey(hole = 5, isBlow = false, isSlide = false) to 587.33,
        HarmonicaNoteKey(hole = 5, isBlow = true, isSlide = true) to 554.37,
        HarmonicaNoteKey(hole = 5, isBlow = false, isSlide = true) to 622.25,
        HarmonicaNoteKey(hole = 6, isBlow = true, isSlide = false) to 659.25,
        HarmonicaNoteKey(hole = 6, isBlow = false, isSlide = false) to 698.46,
        HarmonicaNoteKey(hole = 6, isBlow = true, isSlide = true) to 698.46,
        HarmonicaNoteKey(hole = 6, isBlow = false, isSlide = true) to 739.99,
        HarmonicaNoteKey(hole = 7, isBlow = true, isSlide = false) to 783.99,
        HarmonicaNoteKey(hole = 7, isBlow = false, isSlide = false) to 880.00,
        HarmonicaNoteKey(hole = 7, isBlow = true, isSlide = true) to 830.61,
        HarmonicaNoteKey(hole = 7, isBlow = false, isSlide = true) to 932.33,
        HarmonicaNoteKey(hole = 8, isBlow = true, isSlide = false) to 1046.50,
        HarmonicaNoteKey(hole = 8, isBlow = false, isSlide = false) to 987.77,
        HarmonicaNoteKey(hole = 8, isBlow = true, isSlide = true) to 1108.73,
        HarmonicaNoteKey(hole = 8, isBlow = false, isSlide = true) to 1046.50,
        HarmonicaNoteKey(hole = 9, isBlow = true, isSlide = false) to 1046.50,
        HarmonicaNoteKey(hole = 9, isBlow = false, isSlide = false) to 1174.66,
        HarmonicaNoteKey(hole = 9, isBlow = true, isSlide = true) to 1108.73,
        HarmonicaNoteKey(hole = 9, isBlow = false, isSlide = true) to 1244.51,
        HarmonicaNoteKey(hole = 10, isBlow = true, isSlide = false) to 1318.51,
        HarmonicaNoteKey(hole = 10, isBlow = false, isSlide = false) to 1396.91,
        HarmonicaNoteKey(hole = 10, isBlow = true, isSlide = true) to 1396.91,
        HarmonicaNoteKey(hole = 10, isBlow = false, isSlide = true) to 1479.98,
        HarmonicaNoteKey(hole = 11, isBlow = true, isSlide = false) to 1567.98,
        HarmonicaNoteKey(hole = 11, isBlow = false, isSlide = false) to 1760.00,
        HarmonicaNoteKey(hole = 11, isBlow = true, isSlide = true) to 1661.22,
        HarmonicaNoteKey(hole = 11, isBlow = false, isSlide = true) to 1864.66,
        HarmonicaNoteKey(hole = 12, isBlow = true, isSlide = false) to 2093.00,
        HarmonicaNoteKey(hole = 12, isBlow = false, isSlide = false) to 1975.53,
        HarmonicaNoteKey(hole = 12, isBlow = true, isSlide = true) to 2217.46,
        HarmonicaNoteKey(hole = 12, isBlow = false, isSlide = true) to 2093.00
    )

    override fun frequencyFor(note: TabNote): Double? {
        return frequencies[HarmonicaNoteKey(note.hole, note.isBlow, note.isSlide)]
    }
}
