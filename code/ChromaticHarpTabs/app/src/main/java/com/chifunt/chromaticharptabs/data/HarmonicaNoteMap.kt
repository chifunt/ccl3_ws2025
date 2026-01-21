package com.chifunt.chromaticharptabs.data

data class HarmonicaNoteKey(
    val hole: Int,
    val isBlow: Boolean,
    val isSlide: Boolean
)

object HarmonicaNoteMap {
    private val frequencies = mapOf(
        HarmonicaNoteKey(1, true, false) to 261.63,
        HarmonicaNoteKey(1, false, false) to 293.66,
        HarmonicaNoteKey(1, true, true) to 277.18,
        HarmonicaNoteKey(1, false, true) to 311.13,
        HarmonicaNoteKey(2, true, false) to 329.63,
        HarmonicaNoteKey(2, false, false) to 349.23,
        HarmonicaNoteKey(2, true, true) to 349.23,
        HarmonicaNoteKey(2, false, true) to 369.99,
        HarmonicaNoteKey(3, true, false) to 392.00,
        HarmonicaNoteKey(3, false, false) to 440.00,
        HarmonicaNoteKey(3, true, true) to 415.30,
        HarmonicaNoteKey(3, false, true) to 466.16,
        HarmonicaNoteKey(4, true, false) to 523.25,
        HarmonicaNoteKey(4, false, false) to 493.88,
        HarmonicaNoteKey(4, true, true) to 554.37,
        HarmonicaNoteKey(4, false, true) to 523.25,
        HarmonicaNoteKey(5, true, false) to 659.25,
        HarmonicaNoteKey(5, false, false) to 587.33,
        HarmonicaNoteKey(5, true, true) to 698.46,
        HarmonicaNoteKey(5, false, true) to 622.25,
        HarmonicaNoteKey(6, true, false) to 783.99,
        HarmonicaNoteKey(6, false, false) to 698.46,
        HarmonicaNoteKey(6, true, true) to 830.61,
        HarmonicaNoteKey(6, false, true) to 739.99,
        HarmonicaNoteKey(7, true, false) to 1046.50,
        HarmonicaNoteKey(7, false, false) to 880.00,
        HarmonicaNoteKey(7, true, true) to 1108.73,
        HarmonicaNoteKey(7, false, true) to 932.33,
        HarmonicaNoteKey(8, true, false) to 1318.51,
        HarmonicaNoteKey(8, false, false) to 987.77,
        HarmonicaNoteKey(8, true, true) to 1396.91,
        HarmonicaNoteKey(8, false, true) to 1046.50,
        HarmonicaNoteKey(9, true, false) to 1567.98,
        HarmonicaNoteKey(9, false, false) to 1174.66,
        HarmonicaNoteKey(9, true, true) to 1661.22,
        HarmonicaNoteKey(9, false, true) to 1244.51,
        HarmonicaNoteKey(10, true, false) to 2093.00,
        HarmonicaNoteKey(10, false, false) to 1396.91,
        HarmonicaNoteKey(10, true, true) to 2217.46,
        HarmonicaNoteKey(10, false, true) to 1479.98,
        HarmonicaNoteKey(11, true, false) to 2637.02,
        HarmonicaNoteKey(11, false, false) to 1760.00,
        HarmonicaNoteKey(11, true, true) to 2793.83,
        HarmonicaNoteKey(11, false, true) to 1864.66,
        HarmonicaNoteKey(12, true, false) to 3135.96,
        HarmonicaNoteKey(12, false, false) to 2093.00,
        HarmonicaNoteKey(12, true, true) to 3322.44,
        HarmonicaNoteKey(12, false, true) to 2217.46
    )

    fun frequencyFor(note: TabNote): Double? {
        return frequencies[HarmonicaNoteKey(note.hole, note.isBlow, note.isSlide)]
    }
}
