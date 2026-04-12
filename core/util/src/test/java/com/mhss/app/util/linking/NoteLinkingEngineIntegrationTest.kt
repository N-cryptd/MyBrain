package com.mhss.app.util.linking

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class NoteLinkingEngineIntegrationTest {

    private lateinit var engine: NoteLinkingEngine

    @Before
    fun setup() {
        engine = NoteLinkingEngine()
    }

    @Test
    fun `should handle circular links gracefully`() {
        val content = "Note A links to [[Note B]] and Note B links to [[Note A]]"

        val links = engine.detectLinks(content)

        assertEquals(2, links.size)
        assertTrue(links.any { it.text.equals("Note A", ignoreCase = true) })
        assertTrue(links.any { it.text.equals("Note B", ignoreCase = true) })
    }

    @Test
    fun `should handle duplicate links`() {
        val content = "Link to [[Note A]] twice: [[Note A]]"

        val links = engine.detectLinks(content)

        assertEquals(1, links.size, "Should deduplicate links")
        assertEquals("Note A", links[0].text)
    }

    @Test
    fun `should handle title with display text`() {
        val content = "See [[Note A|custom display text]]"

        val links = engine.detectLinks(content)

        assertEquals(1, links.size)
        assertEquals("Note A", links[0].text)
        assertEquals("custom display text", links[0].displayText)
        assertEquals(NoteLink.LinkType.TITLE_WITH_DISPLAY, links[0].type)
    }

    @Test
    fun `should handle ID links`() {
        val content = "Link to [[550e8400-e29b-41d4-a716-446655440000]]"

        val links = engine.detectLinks(content)

        assertEquals(1, links.size)
        assertEquals("550e8400-e29b-41d4-a716-446655440000", links[0].text)
        assertEquals(NoteLink.LinkType.ID, links[0].type)
    }

    @Test
    fun `should resolve link by title`() {
        val noteRefs = listOf(
            NoteRef("1", "Note A"),
            NoteRef("2", "Note B"),
            NoteRef("3", "Note C")
        )

        val resolved = engine.resolveLink("Note A", noteRefs)

        assertNotNull(resolved)
        assertEquals("1", resolved.id)
        assertEquals("Note A", resolved.title)
    }

    @Test
    fun `should resolve link by ID`() {
        val noteRefs = listOf(
            NoteRef("1", "Note A"),
            NoteRef("2", "Note B"),
            NoteRef("550e8400-e29b-41d4-a716-446655440000", "Note C")
        )

        val resolved = engine.resolveLink("550e8400-e29b-41d4-a716-446655440000", noteRefs)

        assertNotNull(resolved)
        assertEquals("550e8400-e29b-41d4-a716-446655440000", resolved.id)
        assertEquals("Note C", resolved.title)
    }

    @Test
    fun `should return null for unresolved link`() {
        val noteRefs = listOf(
            NoteRef("1", "Note A"),
            NoteRef("2", "Note B")
        )

        val resolved = engine.resolveLink("Non Existent Note", noteRefs)

        assertNull(resolved)
    }

    @Test
    fun `should format linked note IDs as JSON array`() {
        val noteIds = listOf("1", "2", "3")

        val json = engine.formatLinkedNoteIds(noteIds)

        assertEquals("[\"1\",\"2\",\"3\"]", json)
    }

    @Test
    fun `should extract linked note IDs from JSON`() {
        val json = "[\"1\",\"2\",\"3\"]"

        val noteIds = engine.extractLinkedNoteIds(json)

        assertEquals(3, noteIds.size)
        assertTrue(noteIds.contains("1"))
        assertTrue(noteIds.contains("2"))
        assertTrue(noteIds.contains("3"))
    }

    @Test
    fun `should handle empty JSON array`() {
        val json = "[]"

        val noteIds = engine.extractLinkedNoteIds(json)

        assertTrue(noteIds.isEmpty())
    }

    @Test
    fun `should handle invalid JSON`() {
        val json = "invalid json"

        val noteIds = engine.extractLinkedNoteIds(json)

        assertTrue(noteIds.isEmpty())
    }
}
