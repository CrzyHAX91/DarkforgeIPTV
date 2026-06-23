package com.example.data.model

data class EpgProgram(
    val id: String,
    val channelId: String,
    val title: String,
    val description: String,
    val startTime: String,
    val endTime: String
)
