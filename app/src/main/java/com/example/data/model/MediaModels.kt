package com.example.data.model

data class Video(
    val id: String,
    val title: String,
    val description: String,
    val videoUrl: String,
    val posterUrl: String? = null,
    val durationMin: Int = 0,
    val year: Int = 0,
    val tvgId: String? = null,
    val epgProgram: EpgProgram? = null,
    val hasLegalRights: Boolean = true,
    val allowDownload: Boolean = false,
    val requiresExternalPlayer: Boolean = false,
    val dateAddedMs: Long = System.currentTimeMillis()
)

data class Category(
    val id: String,
    val title: String,
    val videos: List<Video>
)

data class Collection(
    val id: String,
    val title: String,
    val description: String,
    val categories: List<Category>
)
