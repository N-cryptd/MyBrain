package com.mhss.app.util.linking

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class NoteLinkingEngineTest {

    private lateinit var engine: NoteLinkingEngine
    private val sampleNotes = listOf(
        NoteRef(id = "note-1", title = "Project Ideas"),
        NoteRef(id = "note-2", title = "Meeting Notes"),
        NoteRef(id = "123e4567-e89b-12d3-a456-426614174000", title = "Shopping List")
    )

    @Before
    fun setup() {
        engine = NoteLinkingEngine()
    }

    private fun link(text: String): String = "[[$text]]"

    @Test
    fun testDetectLinks_TitleOnly() {
        val content = "See ${link("Project Ideas")} for more details"
        val links = engine.detectLinks(content)

        assertEquals(1, links.size)
        assertEquals(NoteLink.LinkType.TITLE, links[0].type)
        assertEquals("Project Ideas", links[0].text)
    }

    @Test
    fun testDetectLinks_WithDisplayText() {
        val content = "Check ${link("Project Ideas|this project")} for details"
        val links = engine.detectLinks(content)

        assertEquals(1, links.size)
        assertEquals(NoteLink.LinkType.TITLE_WITH_DISPLAY, links[0].type)
        assertEquals("Project Ideas", links[0].text)
        assertEquals("this project", links[0].displayText)
    }

    @Test
    fun testDetectLinks_ByUUID() {
        val uuid = "123e4567-e89b-12d3-a456-426614174000"
        val content = "See ${link(uuid)} for details"
        val links = engine.detectLinks(content)

        assertEquals(1, links.size)
        assertEquals(NoteLink.LinkType.ID, links[0].type)
        assertEquals(uuid, links[0].text)
    }

    @Test
    fun testDetectLinks_MultipleLinks() {
        val content = "See ${link("Project Ideas")} and ${link("Meeting Notes")} for context"
        val links = engine.detectLinks(content)

        assertEquals(2, links.size)
        assertEquals(NoteLink.LinkType.TITLE, links[0].type)
        assertEquals(NoteLink.LinkType.TITLE, links[1].type)
    }

    @Test
    fun testDetectLinks_DuplicateLinks() {
        val content = "See ${link("Project Ideas")} and ${link("Project Ideas")} again"
        val links = engine.detectLinks(content)

        assertEquals(1, links.size)
    }

    @Test
    fun testResolveLink_ByTitleExactMatch() {
        val result = engine.resolveLink("Project Ideas", sampleNotes)

        assertNotNull(result)
        assertEquals("Project Ideas", result?.title)
    }

    @Test
    fun testResolveLink_ByTitleCaseInsensitive() {
        val result = engine.resolveLink("project ideas", sampleNotes)

        assertNotNull(result)
        assertEquals("Project Ideas", result?.title)
    }

    @Test
    fun testResolveLink_ByUUID() {
        val uuid = "123e4567-e89b-12d3-a456-426614174000"
        val result = engine.resolveLink(uuid, sampleNotes)

        assertNotNull(result)
        assertEquals(uuid, result?.id)
        assertEquals("Shopping List", result?.title)
    }

    @Test
    fun testResolveLink_NotFound() {
        val result = engine.resolveLink("Nonexistent Note", sampleNotes)

        assertNull(result)
    }

    @Test
    fun testResolveLink_WithDisplayText() {
        val result = engine.resolveLink("Project Ideas|Projects", sampleNotes)

        assertNotNull(result)
        assertEquals("Project Ideas", result?.title)
    }

    @Test
    fun testExtractLinkedNoteIds_EmptyString() {
        val result = engine.extractLinkedNoteIds("[]")
        assertTrue(result.isEmpty())
    }

    @Test
    fun testExtractLinkedNoteIds_SingleId() {
        val result = engine.extractLinkedNoteIds("[\"note-1\"]")

        assertEquals(1, result.size)
        assertEquals("note-1", result[0])
    }

    @Test
    fun testExtractLinkedNoteIds_MultipleIds() {
        val result = engine.extractLinkedNoteIds("[\"note-1\", \"note-2\"]")

        assertEquals(2, result.size)
        assertEquals("note-1", result[0])
        assertEquals("note-2", result[1])
    }

    @Test
    fun testExtractLinkedNoteIds_InvalidJson() {
        val result = engine.extractLinkedNoteIds("invalid json")
        assertTrue(result.isEmpty())
    }

    @Test
    fun testFormatLinkedNoteIds_EmptyList() {
        val result = engine.formatLinkedNoteIds(emptyList())
        assertEquals("[]", result)
    }

    @Test
    fun testFormatLinkedNoteIds_SingleId() {
        val result = engine.formatLinkedNoteIds(listOf("note-1"))
        assertEquals("[\"note-1\"]", result)
    }

    @Test
    fun testFormatLinkedNoteIds_MultipleIds() {
        val result = engine.formatLinkedNoteIds(listOf("note-1", "note-2"))
        assertEquals("[\"note-1\",\"note-2\"]", result)
    }
}
