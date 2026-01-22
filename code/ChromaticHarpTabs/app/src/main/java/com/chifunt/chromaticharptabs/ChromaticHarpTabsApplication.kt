package com.chifunt.chromaticharptabs

import android.app.Application
import com.chifunt.chromaticharptabs.data.repository.SettingsRepository
import com.chifunt.chromaticharptabs.data.model.Tab
import com.chifunt.chromaticharptabs.data.repository.TabRepository
import com.chifunt.chromaticharptabs.db.TabDatabase
import org.json.JSONObject
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
            loadSampleTabs(now).forEach { tabRepository.addTab(it) }
        }
    }

    private fun loadSampleTabs(timestamp: Long): List<Tab> {
        val defaultKey = getString(R.string.key_c)
        val defaultDifficulty = getString(R.string.difficulty_medium)
        val sampleFiles = listOf(
            "samples/amazing_grace.json",
            "samples/danny_boy.json",
            "samples/autumn_leaves.json",
            "samples/fly_me_to_the_moon.json",
            "samples/moon_river.json",
            "samples/somewhere_over_the_rainbow.json",
            "samples/pink_panther.json"
        )
        return sampleFiles.mapNotNull { path ->
            runCatching {
                val json = assets.open(path).bufferedReader().use { it.readText() }
                val root = JSONObject(json)
                val content = root.getJSONObject("content").toString()
                Tab(
                    title = root.optString("title"),
                    artist = root.optString("artist"),
                    key = root.optString("key", defaultKey),
                    difficulty = root.optString("difficulty", defaultDifficulty),
                    tags = root.optString("tags"),
                    content = content,
                    isFavorite = root.optBoolean("favorite", false),
                    createdAt = timestamp,
                    updatedAt = timestamp
                )
            }.getOrNull()
        }
    }
}
