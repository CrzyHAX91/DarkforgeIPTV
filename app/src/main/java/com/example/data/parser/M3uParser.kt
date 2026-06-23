package com.example.data.parser

import com.example.data.model.Category
import com.example.data.model.Video
import java.util.UUID

object M3uParser {
    fun parse(m3uContent: String): List<Category> {
        val lines = m3uContent.lines()
        if (lines.isEmpty() || !lines[0].startsWith("#EXTM3U")) {
            return emptyList()
        }

        val categoryMap = mutableMapOf<String, MutableList<Video>>()
        var currentTitle = ""
        var currentGroup = "Uncategorized"
        var currentLogo = ""
        var currentTvgId = ""
        var isSecure = false

        for (i in 1 until lines.size) {
            val line = lines[i].trim()
            if (line.isEmpty()) continue

            if (line.startsWith("#EXTINF:")) {
                // Extract tvg-id
                val idMatch = Regex("tvg-id=\"([^\"]+)\"").find(line)
                if (idMatch != null) {
                    currentTvgId = idMatch.groupValues[1]
                } else {
                    currentTvgId = ""
                }

                // Extract group-title
                val groupMatch = Regex("group-title=\"([^\"]+)\"").find(line)
                if (groupMatch != null) {
                    currentGroup = groupMatch.groupValues[1]
                } else {
                    currentGroup = "Uncategorized"
                }

                // Extract tvg-logo
                val logoMatch = Regex("tvg-logo=\"([^\"]+)\"").find(line)
                if (logoMatch != null) {
                    currentLogo = logoMatch.groupValues[1]
                } else {
                    currentLogo = ""
                }

                // Extract title
                val titleSplit = line.split(",")
                if (titleSplit.size > 1) {
                    currentTitle = titleSplit.last().trim()
                } else {
                    currentTitle = "Unknown Stream"
                }
            } else if (line.startsWith("http://") || line.startsWith("https://")) {
                val videoUrl = line
                isSecure = videoUrl.startsWith("https://")
                
                // Only allow valid stream formats
                val isValidStream = videoUrl.endsWith(".m3u8") || videoUrl.endsWith(".ts") || videoUrl.endsWith(".mp4") || videoUrl.endsWith(".mkv")
                if(isValidStream || videoUrl.contains("?")) { // allow api streams
                    val video = Video(
                        id = UUID.randomUUID().toString(),
                        title = currentTitle,
                        description = "Streaming from $currentGroup" + if (isSecure) " (Secure)" else "",
                        posterUrl = currentLogo.ifEmpty { null },
                        videoUrl = videoUrl,
                        tvgId = currentTvgId.ifEmpty { null },
                        year = 2024,
                        durationMin = 0
                    )
                    
                    categoryMap.getOrPut(currentGroup) { mutableListOf() }.add(video)
                }
                
                // Reset for next
                currentTitle = ""
                currentGroup = "Uncategorized"
                currentLogo = ""
                currentTvgId = ""
            }
        }

        return categoryMap.map { (group, videos) ->
            Category(id = UUID.randomUUID().toString(), title = group, videos = videos)
        }
    }
}
