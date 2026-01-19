package com.chifunt.chromaticharptabs.data

import com.chifunt.chromaticharptabs.db.TabDao
import com.chifunt.chromaticharptabs.db.TabEntity
import com.chifunt.chromaticharptabs.ui.viewmodel.SortOption
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TabRepository(private val tabDao: TabDao) {

    fun getTabs(
        query: String,
        keyFilter: String?,
        difficulty: String?,
        favoritesOnly: Boolean,
        sortOption: SortOption
    ): Flow<List<Tab>> {
        val favoritesFlag = if (favoritesOnly) 1 else 0
        val entities = when (sortOption) {
            SortOption.Title -> tabDao.getTabsSortedByTitle(query, keyFilter, difficulty, favoritesFlag)
            SortOption.Artist -> tabDao.getTabsSortedByArtist(query, keyFilter, difficulty, favoritesFlag)
            SortOption.Newest -> tabDao.getTabsSortedByNewest(query, keyFilter, difficulty, favoritesFlag)
            SortOption.Oldest -> tabDao.getTabsSortedByOldest(query, keyFilter, difficulty, favoritesFlag)
        }

        return entities.map { entityList ->
            entityList.map { entity ->
                Tab(
                    id = entity.id,
                    title = entity.title,
                    artist = entity.artist,
                    key = entity.key,
                    difficulty = entity.difficulty,
                    tempo = entity.tempo,
                    tags = entity.tags,
                    content = entity.content,
                    isFavorite = entity.isFavorite,
                    createdAt = entity.createdAt,
                    updatedAt = entity.updatedAt
                )
            }
        }
    }

    suspend fun findTabById(tabId: Int): Tab {
        val entity = tabDao.findTabById(tabId)
        return Tab(
            id = entity.id,
            title = entity.title,
            artist = entity.artist,
            key = entity.key,
            difficulty = entity.difficulty,
            tempo = entity.tempo,
            tags = entity.tags,
            content = entity.content,
            isFavorite = entity.isFavorite,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

    suspend fun addTab(tab: Tab): Long {
        return tabDao.insertTab(tab.toEntity())
    }

    suspend fun updateTab(tab: Tab) {
        tabDao.updateTab(tab.toEntity())
    }

    suspend fun removeTab(tab: Tab) {
        tabDao.deleteTab(tab.toEntity())
    }

    suspend fun setFavorite(tabId: Int, isFavorite: Boolean, updatedAt: Long) {
        tabDao.setFavorite(tabId, isFavorite, updatedAt)
    }

    suspend fun countTabs(): Int {
        return tabDao.countTabs()
    }

    private fun Tab.toEntity(): TabEntity {
        return TabEntity(
            id = id,
            title = title,
            artist = artist,
            key = key,
            difficulty = difficulty,
            tempo = tempo,
            tags = tags,
            content = content,
            isFavorite = isFavorite,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}
