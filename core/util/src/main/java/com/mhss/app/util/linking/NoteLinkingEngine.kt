package com.mhss.app.util.linking

import com.mhss.app.domain.model.Note
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive

@Serializable
data class NoteLink(
    val text: String,
    val type: LinkType,
    val displayText: String? = null
) {
    enum class LinkType {
        TITLE,
        ID,
        TITLE_WITH_DISPLAY
    }
}

class NoteLinkingEngine {
    
    private val linkPattern = Regex("""\[\[([^\]]+)\]\]""")
    private val displayTextPattern = Regex("""\|\s*([^|]+)\s*$""")
    private val uuidPattern = Regex("""^[0-9a-fA-F-]{36}$""")
    
    private val json = Json { ignoreUnknownKeys = true }
    
    fun detectLinks(content: String): List<NoteLink> {
        return linkPattern.findAll(content)
            .map { match ->
                val linkContent = match.groupValues[1].trim()
                parseLinkContent(linkContent)
            }
            .distinctBy { it.text.lowercase() }
    }
    
    fun resolveLink(
        linkText: String,
        allNotes: List<Note>
    ): Note? {
        return when {
            uuidPattern.matches(linkText) -> {
                allNotes.find { it.id.equals(linkText, ignoreCase = true) }
            }
            linkText.contains("|") -> {
                val title = linkText.substringBefore("|").trim()
                findNoteByTitle(title, allNotes)
            }
            else -> {
                findNoteByTitle(linkText, allNotes)
            }
        }
    }
    
    fun replaceLinksInContent(
        content: String,
        replacement: (NoteLink) -> String
    ): String {
        return linkPattern.replace(content) { match ->
            val linkContent = match.groupValues[1].trim()
            val link = parseLinkContent(linkContent)
            replacement(link)
        }
    }
    
    fun extractLinkedNoteIds(linkedNoteIdsJson: String): List<String> {
        return try {
            val jsonElement = json.parseToJsonElement(linkedNoteIdsJson)
            if (jsonElement is JsonArray) {
                jsonElement.mapNotNull { 
                    (it as? JsonPrimitive)?.content 
                }
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    fun formatLinkedNoteIds(noteIds: List<String>): String {
        return json.encodeToJsonElement(
            JsonArray(noteIds.map { jsonPrimitive(it) })
        ).toString()
    }
    
    private fun parseLinkContent(content: String): NoteLink {
        return if (content.contains("|")) {
            val parts = content.split("|", limit = 2)
            val title = parts[0].trim()
            val displayText = parts.getOrNull(1)?.trim()
            NoteLink(
                text = title,
                type = NoteLink.LinkType.TITLE_WITH_DISPLAY,
                displayText = displayText
            )
        } else if (uuidPattern.matches(content.trim())) {
            NoteLink(
                text = content.trim(),
                type = NoteLink.LinkType.ID
            )
        } else {
            NoteLink(
                text = content.trim(),
                type = NoteLink.LinkType.TITLE
            )
        }
    }
    
    private fun findNoteByTitle(title: String, notes: List<Note>): Note? {
        return notes.find { 
            it.title.equals(title, ignoreCase = true) 
        } ?: notes.find { 
            it.title.contains(title, ignoreCase = true) 
        }
    }
    
    private fun List<NoteLink>.distinctBy(selector: (NoteLink) -> String): List<NoteLink> {
        val seen = mutableSetOf<String>()
        return filter { 
            val key = selector(it).lowercase()
            seen.add(key)
        }
    }
}