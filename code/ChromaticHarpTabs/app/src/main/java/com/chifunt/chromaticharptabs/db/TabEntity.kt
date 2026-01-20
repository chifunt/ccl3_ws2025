package com.chifunt.chromaticharptabs.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tabs")
data class TabEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val artist: String,
    @ColumnInfo("key_name")
    val key: String,
    val difficulty: String,
    val tags: String,
    val content: String,
    @ColumnInfo("is_favorite")
    val isFavorite: Boolean,
    @ColumnInfo("created_at")
    val createdAt: Long,
    @ColumnInfo("updated_at")
    val updatedAt: Long
)
