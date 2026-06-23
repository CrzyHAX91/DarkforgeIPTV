package com.example.data.repository

import com.example.data.model.Category
import com.example.data.model.Collection
import com.example.data.model.Video
import com.example.data.parser.M3uParser
import com.example.data.parser.XmlTvParser
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface MediaRepository {
    fun getFeaturedCollection(): Flow<Collection>
    fun getCategories(): Flow<List<Category>>
    fun getVideoDetails(videoId: String): Flow<Video?>
    fun getRecommendations(): Flow<List<Category>>
}

class MockMediaRepository : MediaRepository {
    
    // Simulating a secure local M3U payload
    private val localM3uContent = """
        #EXTM3U
        #EXTINF:-1 tvg-id="action01" tvg-name="Action Movies" tvg-logo="https://example.com/logo1.png" group-title="Action VOD", 4K Action Stream
        https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4
        #EXTINF:-1 tvg-id="espn01" group-title="Live Sports", ESPN UHD
        https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4
        #EXTINF:-1 tvg-name="Sci-Fi" group-title="Sci-Fi Series", Tears of Steel (Sci-Fi Episode 1)
        https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4
        #EXTINF:-1 tvg-id="sky01" group-title="Live Sports", Sky Sports Main Event
        https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4
    """.trimIndent()
    
    private val localXmlTvContent = """
        <?xml version="1.0" encoding="UTF-8"?>
        <tv>
            <programme start="20260620080000 +0000" stop="20260620100000 +0000" channel="espn01">
                <title lang="en">SportsCenter Live</title>
                <desc lang="en">Live coverage of today's top sports events and analysis.</desc>
            </programme>
            <programme start="20260620090000 +0000" stop="20260620113000 +0000" channel="sky01">
                <title lang="en">Premier League: Arsenal vs Chelsea</title>
                <desc lang="en">Exclusive live coverage of the London Derby.</desc>
            </programme>
            <programme start="20260620060000 +0000" stop="20260620120000 +0000" channel="action01">
                <title lang="en">Die Hard Marathon</title>
                <desc lang="en">Yippee ki-yay! Non-stop action classics.</desc>
            </programme>
        </tv>
    """.trimIndent()

    private val parsedPrograms = XmlTvParser.parse(localXmlTvContent)
    private val parsedCategories = M3uParser.parse(localM3uContent)

    private val mockCategories = parsedCategories.map { category ->
        category.copy(videos = category.videos.map { video ->
            val program = parsedPrograms.find { it.channelId == video.tvgId }
            if (program != null) {
                video.copy(epgProgram = program, description = "Playing Now: ${program.title}\n${program.description}")
            } else {
                video
            }
        })
    }

    private val allVideos = mockCategories.flatMap { it.videos }

    private val mockCollection = Collection(
        id = "col1",
        title = "Featured Collections",
        description = "Your personal, securely parsed M3U playlist.",
        categories = mockCategories
    )

    override fun getFeaturedCollection(): Flow<Collection> = flow {
        delay(800) // Simulate network delay
        emit(mockCollection)
    }

    override fun getCategories(): Flow<List<Category>> = flow {
        delay(800)
        emit(mockCategories)
    }

    override fun getVideoDetails(videoId: String): Flow<Video?> = flow {
        delay(500)
        emit(allVideos.find { it.id == videoId })
    }

    override fun getRecommendations(): Flow<List<Category>> = flow {
        delay(600)
        emit(mockCategories)
    }
}
