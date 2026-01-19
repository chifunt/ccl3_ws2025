package com.chifunt.chromaticharptabs.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TabDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTab(tabEntity: TabEntity): Long

    @Update
    suspend fun updateTab(tabEntity: TabEntity)

    @Delete
    suspend fun deleteTab(tabEntity: TabEntity)

    @Query("SELECT * FROM tabs WHERE id = :id")
    suspend fun findTabById(id: Int): TabEntity

    @Query("UPDATE tabs SET is_favorite = :isFavorite, updated_at = :updatedAt WHERE id = :id")
    suspend fun setFavorite(id: Int, isFavorite: Boolean, updatedAt: Long)

    @Query("SELECT COUNT(*) FROM tabs")
    suspend fun countTabs(): Int

    @Query(
        """
        SELECT * FROM tabs
        WHERE (:query = '' OR title LIKE '%' || :query || '%' OR artist LIKE '%' || :query || '%')
        AND (:keyFilter IS NULL OR key_name = :keyFilter)
        AND (:difficulty IS NULL OR difficulty = :difficulty)
        AND (:favoritesOnly = 0 OR is_favorite = 1)
        ORDER BY title COLLATE NOCASE ASC
        """
    )
    fun getTabsSortedByTitle(
        query: String,
        keyFilter: String?,
        difficulty: String?,
        favoritesOnly: Int
    ): Flow<List<TabEntity>>

    @Query(
        """
        SELECT * FROM tabs
        WHERE (:query = '' OR title LIKE '%' || :query || '%' OR artist LIKE '%' || :query || '%')
        AND (:keyFilter IS NULL OR key_name = :keyFilter)
        AND (:difficulty IS NULL OR difficulty = :difficulty)
        AND (:favoritesOnly = 0 OR is_favorite = 1)
        ORDER BY artist COLLATE NOCASE ASC
        """
    )
    fun getTabsSortedByArtist(
        query: String,
        keyFilter: String?,
        difficulty: String?,
        favoritesOnly: Int
    ): Flow<List<TabEntity>>

    @Query(
        """
        SELECT * FROM tabs
        WHERE (:query = '' OR title LIKE '%' || :query || '%' OR artist LIKE '%' || :query || '%')
        AND (:keyFilter IS NULL OR key_name = :keyFilter)
        AND (:difficulty IS NULL OR difficulty = :difficulty)
        AND (:favoritesOnly = 0 OR is_favorite = 1)
        ORDER BY created_at DESC
        """
    )
    fun getTabsSortedByNewest(
        query: String,
        keyFilter: String?,
        difficulty: String?,
        favoritesOnly: Int
    ): Flow<List<TabEntity>>

    @Query(
        """
        SELECT * FROM tabs
        WHERE (:query = '' OR title LIKE '%' || :query || '%' OR artist LIKE '%' || :query || '%')
        AND (:keyFilter IS NULL OR key_name = :keyFilter)
        AND (:difficulty IS NULL OR difficulty = :difficulty)
        AND (:favoritesOnly = 0 OR is_favorite = 1)
        ORDER BY created_at ASC
        """
    )
    fun getTabsSortedByOldest(
        query: String,
        keyFilter: String?,
        difficulty: String?,
        favoritesOnly: Int
    ): Flow<List<TabEntity>>
}
