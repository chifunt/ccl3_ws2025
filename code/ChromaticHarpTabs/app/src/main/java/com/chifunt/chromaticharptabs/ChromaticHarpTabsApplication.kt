package com.chifunt.chromaticharptabs

import android.app.Application
import com.chifunt.chromaticharptabs.data.SettingsRepository
import com.chifunt.chromaticharptabs.data.Tab
import com.chifunt.chromaticharptabs.data.TabRepository
import com.chifunt.chromaticharptabs.db.TabDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class ChromaticHarpTabsApplication : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val tabRepository by lazy {
        val dao = TabDatabase.getDatabase(this).tabDao()
        TabRepository(dao)
    }
    val settingsRepository by lazy { SettingsRepository(this) }

    override fun onCreate() {
        super.onCreate()
        seedSampleTabsIfNeeded()
    }

    private fun seedSampleTabsIfNeeded() {
        applicationScope.launch {
            if (tabRepository.countTabs() > 0) {
                return@launch
            }
            val now = System.currentTimeMillis()
            val easy = getString(R.string.difficulty_easy)
            val medium = getString(R.string.difficulty_medium)
            val hard = getString(R.string.difficulty_hard)
            val keyC = getString(R.string.key_c)
            val keyD = getString(R.string.key_d)
            val keyE = getString(R.string.key_e)
            val keyF = getString(R.string.key_f)
            val keyG = getString(R.string.key_g)
            val samples = listOf(
                Tab(
                    title = getString(R.string.sample_title_autumn_leaves),
                    artist = getString(R.string.sample_artist_joseph_kosma),
                    key = keyG,
                    difficulty = medium,
                    tempo = 110,
                    tags = getString(R.string.sample_tags_jazz_ballad),
                    content = getString(R.string.sample_content_autumn_leaves),
                    isFavorite = true,
                    createdAt = now,
                    updatedAt = now
                ),
                Tab(
                    title = getString(R.string.sample_title_blue_bossa),
                    artist = getString(R.string.sample_artist_kenny_dorham),
                    key = keyC,
                    difficulty = medium,
                    tempo = 128,
                    tags = getString(R.string.sample_tags_latin_jazz),
                    content = getString(R.string.sample_content_blue_bossa),
                    isFavorite = false,
                    createdAt = now,
                    updatedAt = now
                ),
                Tab(
                    title = getString(R.string.sample_title_amazing_grace),
                    artist = getString(R.string.sample_artist_traditional),
                    key = keyD,
                    difficulty = easy,
                    tempo = 80,
                    tags = getString(R.string.sample_tags_hymn_slow),
                    content = getString(R.string.sample_content_amazing_grace),
                    isFavorite = false,
                    createdAt = now,
                    updatedAt = now
                ),
                Tab(
                    title = getString(R.string.sample_title_all_blues),
                    artist = getString(R.string.sample_artist_miles_davis),
                    key = keyG,
                    difficulty = hard,
                    tempo = 120,
                    tags = getString(R.string.sample_tags_jazz_modal),
                    content = getString(R.string.sample_content_all_blues),
                    isFavorite = true,
                    createdAt = now,
                    updatedAt = now
                ),
                Tab(
                    title = getString(R.string.sample_title_misty),
                    artist = getString(R.string.sample_artist_erroll_garner),
                    key = keyE,
                    difficulty = hard,
                    tempo = 72,
                    tags = getString(R.string.sample_tags_ballad_jazz),
                    content = getString(R.string.sample_content_misty),
                    isFavorite = false,
                    createdAt = now,
                    updatedAt = now
                ),
                Tab(
                    title = getString(R.string.sample_title_songbird),
                    artist = getString(R.string.sample_artist_christine_mcvie),
                    key = keyF,
                    difficulty = medium,
                    tempo = 96,
                    tags = getString(R.string.sample_tags_pop_mellow),
                    content = getString(R.string.sample_content_songbird),
                    isFavorite = false,
                    createdAt = now,
                    updatedAt = now
                )
            )

            samples.forEach { tabRepository.addTab(it) }
        }
    }
}
