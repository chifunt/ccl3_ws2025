package com.chifunt.chromaticharptabs.data

data class Tab(
    val id: Int = 0,
    val title: String,
    val artist: String,
    val key: String,
    val difficulty: String,
    val tags: String,
    val content: String,
    val isFavorite: Boolean,
    val createdAt: Long,
    val updatedAt: Long
)
