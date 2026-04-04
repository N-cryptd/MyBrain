package com.mhss.app.util.linking

import com.mhss.app.domain.model.Note
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class NoteLinkingEngineTest {

    private lateinit var engine: NoteLinkingEngine
    private val sampleNotes = listOf(
        Note(
            id = "note-1",
            title = "Project Ideas",
            content = "Some project ideas",
            createdDate = 0L,
            updatedDate = 0L,
            pinned = false,
            linkedNoteIds = "",
            backlinkCount = 0
        ),
        Note(
            id = "note-2",
            title = "Meeting Notes",
            content = "Meeting notes from yesterday",
            createdDate = 0L,
            updatedDate = 0L,
            pinned = false,
            linkedNoteIds = "",
            backlinkCount = 0
        ),
        Note(
            id = "123e4567-e89b-12d3-a456-426614174000",
            title = "Shopping List",
            content = "Grocery items",
            createdDate = 0L,
            updatedDate = 0L,
            pinned = false,
            linkedNoteIds = "",
            backlinkCount = 0
        )
    )

    @Before
    fun setup() {
        engine = NoteLinkingEngine()
    }

    @Test
    fun testDetectLinks_TitleOnly() {
        val content = "See [[Project Ideas]] for more details"
        val links = engine.detectLinks(content)
        
        assertEquals("Should detect one link", 1, links.size)
        assertEquals("Link type should be TITLE", NoteLink.LinkType.TITLE, links[0].type)
        assertEquals("Link text should match", "Project Ideas", links[0].text)
    }

    @Test
    fun testDetectLinks_WithDisplayText() {
        val content = "Check [[Project Ideas|this project]] for details"
        val links = engine.detectLinks(content)
        
        assertEquals("Should detect one link", 1, links.size)
        assertEquals("Link type should be TITLE_WITH_DISPLAY", NoteLink.LinkType.TITLE_WITH_DISPLAY, links[0].type)
        assertEquals("Link text should match", "Project Ideas", links[0].text)
        assertEquals("Display text should match", "this project", links[0].displayText)
    }

    @Test
    fun testDetectLinks_ByUUID() {
        val uuid = "123e4567-e89b-12d3-a456-426614174000"
        val content = "See [[$uuid]] for details"
        val links = engine.detectLinks(content)
        
        assertEquals("Should detect one link", 1, links.size)
        assertEquals("Link type should be ID", NoteLink.LinkType.ID, links[0].type)
        assertEquals("Link text should match", uuid, links[0].text)
    }

    @Test
    fun testDetectLinks_MultipleLinks() {
        val content = "See [[Project Ideas]] and [[Meeting Notes]] for context"
        val links = engine.detectLinks(content)
        
        assertEquals("Should detect two links", 2, links.size)
        assertEquals("First link type should be TITLE", NoteLink.LinkType.TITLE, links[0].type)
        assertEquals("Second link type should be TITLE", NoteLink.LinkType.TITLE, links[1].type)
    }

    @Test
    fun testDetectLinks_DuplicateLinks() {
        val content = "See [[Project Ideas]] and [[Project Ideas]] again"
        val links = engine.detectLinks(content)
        
        assertEquals("Should deduplicate links", 1, links.size)
    }

    @Test
    fun testResolveLink_ByTitleExactMatch() {
        val result = engine.resolveLink("Project Ideas", sampleNotes)
        
        assertNotNull("Should find note by title", result)
        assertEquals("Should match exact title", "Project Ideas", result?.title)
    }

    @Test
    fun testResolveLink_ByTitleCaseInsensitive() {
        val result = engine.resolveLink("project ideas", sampleNotes)
        
        assertNotNull("Should find note by title (case insensitive)", result)
        assertEquals("Should match title", "Project Ideas", result?.title)
    }

    @Test
    fun testResolveLink_ByUUID() {
        val uuid = "123e4567-e89b-12d3-a456-426614174000"
        val result = engine.resolveLink(uuid, sampleNotes)
        
        assertNotNull("Should find note by UUID", result)
        assertEquals("Should match ID", uuid, result?.id)
        assertEquals("Should match title", "Shopping List", result?.title)
    }

    @Test
    fun testResolveLink_NotFound() {
        val result = engine.resolveLink("Nonexistent Note", sampleNotes)
        
        assertNull("Should return null for nonexistent note", result)
    }

    @Test
    fun testResolveLink_WithDisplayText() {
        val result = engine.resolveLink("Project Ideas|Projects", sampleNotes)
        
        assertNotNull("Should find note by title (with display text)", result)
        assertEquals("Should match title", "Project Ideas", result?.title)
    }

    @Test
    fun testExtractLinkedNoteIds_EmptyString() {
        val result = engine.extractLinkedNoteIds("[]")
        
        assertTrue("Should return empty list", result.isEmpty())
    }

    @Test
    fun testExtractLinkedNoteIds_SingleId() {
        val result = engine.extractLinkedNoteIds("[\"note-1\"]")
        
        assertEquals("Should extract one ID", 1, result.size)
        assertEquals("ID should match", "note-1", result[0])
    }

    @Test
    fun testExtractLinkedNoteIds_MultipleIds() {
        val result = engine.extractLinkedNoteIds("[\"note-1\", \"note-2\"]")
        
        assertEquals("Should extract two IDs", 2, result.size)
        assertEquals("First ID should match", "note-1", result[0])
        assertEquals("Second ID should match", "note-2", result[1])
    }

    @Test
    fun testExtractLinkedNoteIds_InvalidJson() {
        val result = engine.extractLinkedNoteIds("invalid json")
        
        assertTrue("Should return empty list for invalid JSON", result.isEmpty())
    }

    @Test
    fun testFormatLinkedNoteIds_EmptyList() {
        val result = engine.formatLinkedNoteIds(emptyList())
        
        assertEquals("Should format empty list", "[]", result)
    }

    @Test
    fun testFormatLinkedNoteIds_SingleId() {
        val result = engine.formatLinkedNoteIds(listOf("note-1"))
        
        assertEquals("Should format single ID", "[\"note-1\"]", result)
    }

    @Test
    fun testFormatLinkedNoteIds_MultipleIds() {
        val result = engine.formatLinkedNoteIds(listOf("note-1", "note-2"))
        
        assertEquals("Should format multiple IDs", "[\"note-1\", \"note-2\"]", result)
    }
}