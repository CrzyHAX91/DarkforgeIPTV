package com.example.data.parser

import android.util.Log
import com.example.data.model.EpgProgram
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader
import java.util.UUID

object XmlTvParser {
    
    fun parse(xmlContent: String): List<EpgProgram> {
        val programs = mutableListOf<EpgProgram>()
        
        fun formatXmlTvTime(xmlTime: String): String {
            if (xmlTime.length >= 14) {
                // e.g., 20260620080000 +0000 -> 08:00
                return xmlTime.substring(8, 10) + ":" + xmlTime.substring(10, 12)
            }
            return xmlTime
        }
        
        try {
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val parser = factory.newPullParser()
            
            parser.setInput(StringReader(xmlContent))
            
            var eventType = parser.eventType
            
            var currentChannelId = ""
            var currentStart = ""
            var currentStop = ""
            var currentTitle = ""
            var currentDesc = ""
            
            var inProgramme = false
            var currentText = ""
            
            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        if (parser.name == "programme") {
                            inProgramme = true
                            currentTitle = ""
                            currentDesc = ""
                            currentChannelId = parser.getAttributeValue(null, "channel") ?: ""
                            val rawStart = parser.getAttributeValue(null, "start") ?: ""
                            val rawStop = parser.getAttributeValue(null, "stop") ?: ""
                            currentStart = formatXmlTvTime(rawStart)
                            currentStop = formatXmlTvTime(rawStop)
                        }
                    }
                    XmlPullParser.TEXT -> {
                        currentText = parser.text?.trim() ?: ""
                    }
                    XmlPullParser.END_TAG -> {
                        if (inProgramme) {
                            when (parser.name) {
                                "title" -> currentTitle = currentText
                                "desc" -> currentDesc = currentText
                                "programme" -> {
                                    programs.add(
                                        EpgProgram(
                                            id = UUID.randomUUID().toString(),
                                            channelId = currentChannelId,
                                            title = currentTitle,
                                            description = currentDesc,
                                            startTime = currentStart,
                                            endTime = currentStop
                                        )
                                    )
                                    inProgramme = false
                                }
                            }
                        }
                    }
                }
                eventType = parser.next()
            }
        } catch (e: Exception) {
            Log.e("XmlTvParser", "Error parsing XMLTV data", e)
        }
        
        return programs
    }
}
