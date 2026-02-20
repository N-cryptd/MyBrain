package com.mhss.app.data.tools

import com.mhss.app.domain.model.Bookmark
import com.mhss.app.domain.use_case.AddBookmarkUseCase
import com.mhss.app.domain.use_case.SearchBookmarksUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class BookmarkToolSetTest {

    private lateinit var bookmarkToolSet: BookmarkToolSet
    
    private val addBookmark: AddBookmarkUseCase = mockk()
    private val searchBookmarksUseCase: SearchBookmarksUseCase = mockk()

    @Before
    fun setup() {
        bookmarkToolSet = BookmarkToolSet(
            addBookmark = addBookmark,
            searchBookmarksUseCase = searchBookmarksUseCase
        )
    }

    @Test
    fun testCreateBookmark() = runTest {
        val bookmarkId = 123L
        coEvery { addBookmark(any()) } returns bookmarkId
        
        val result = bookmarkToolSet.createBookmark(
            url = "https://example.com",
            title = "Example Site",
            description = "An example website"
        )
        
        assertNotNull("Result should not be null", result)
        assertEquals("Should return the bookmark ID", bookmarkId, result.createdBookmarkId)
        coVerify { addBookmark(any()) }
    }

    @Test
    fun testCreateBookmarkMinimal() = runTest {
        val bookmarkId = 456L
        coEvery { addBookmark(any()) } returns bookmarkId
        
        val result = bookmarkToolSet.createBookmark(
            url = "https://example.com",
            title = "",
            description = ""
        )
        
        assertNotNull("Result should not be null", result)
        assertEquals("Should return the bookmark ID", bookmarkId, result.createdBookmarkId)
        coVerify { addBookmark(any()) }
    }

    @Test
    fun testCreateBookmarkWithTitleOnly() = runTest {
        val bookmarkId = 789L
        coEvery { addBookmark(any()) } returns bookmarkId
        
        val result = bookmarkToolSet.createBookmark(
            url = "https://github.com",
            title = "GitHub",
            description = ""
        )
        
        assertNotNull("Result should not be null", result)
        assertEquals("Should return the bookmark ID", bookmarkId, result.createdBookmarkId)
        coVerify { addBookmark(any()) }
    }

    @Test
    fun testSearchBookmarks() = runTest {
        val expectedBookmarks = listOf(
            Bookmark(
                url = "https://example.com",
                title = "Example Site",
                description = "An example website",
                id = "bookmark-1"
            ),
            Bookmark(
                url = "https://github.com",
                title = "GitHub",
                description = "Code hosting platform",
                id = "bookmark-2"
            )
        )
        
        coEvery { searchBookmarksUseCase("github") } returns expectedBookmarks
        
        val result = bookmarkToolSet.searchBookmarks("github")
        
        assertNotNull("Result should not be null", result)
        assertEquals("Should return 2 bookmarks", 2, result.bookmarks.size)
        assertEquals("First bookmark title should match", "Example Site", result.bookmarks[0].title)
        assertEquals("Second bookmark title should match", "GitHub", result.bookmarks[1].title)
        coVerify { searchBookmarksUseCase("github") }
    }

    @Test
    fun testSearchBookmarksByTitle() = runTest {
        val expectedBookmarks = listOf(
            Bookmark(
                url = "https://github.com",
                title = "GitHub",
                description = "Code hosting platform",
                id = "bookmark-1"
            )
        )
        
        coEvery { searchBookmarksUseCase("GitHub") } returns expectedBookmarks
        
        val result = bookmarkToolSet.searchBookmarks("GitHub")
        
        assertNotNull("Result should not be null", result)
        assertEquals("Should return 1 bookmark", 1, result.bookmarks.size)
        assertEquals("Bookmark title should match", "GitHub", result.bookmarks[0].title)
        coVerify { searchBookmarksUseCase("GitHub") }
    }

    @Test
    fun testSearchBookmarksByUrl() = runTest {
        val expectedBookmarks = listOf(
            Bookmark(
                url = "https://example.com/test",
                title = "Test Page",
                description = "A test page",
                id = "bookmark-1"
            )
        )
        
        coEvery { searchBookmarksUseCase("example.com") } returns expectedBookmarks
        
        val result = bookmarkToolSet.searchBookmarks("example.com")
        
        assertNotNull("Result should not be null", result)
        assertEquals("Should return 1 bookmark", 1, result.bookmarks.size)
        assertTrue("Bookmark URL should contain query", result.bookmarks[0].url.contains("example.com"))
        coVerify { searchBookmarksUseCase("example.com") }
    }

    @Test
    fun testSearchBookmarksByDescription() = runTest {
        val expectedBookmarks = listOf(
            Bookmark(
                url = "https://kotlinlang.org",
                title = "Kotlin",
                description = "Programming language",
                id = "bookmark-1"
            ),
            Bookmark(
                url = "https://developer.android.com",
                title = "Android Developers",
                description = "Android development resources",
                id = "bookmark-2"
            )
        )
        
        coEvery { searchBookmarksUseCase("development") } returns expectedBookmarks
        
        val result = bookmarkToolSet.searchBookmarks("development")
        
        assertNotNull("Result should not be null", result)
        assertEquals("Should return 2 bookmarks", 2, result.bookmarks.size)
        coVerify { searchBookmarksUseCase("development") }
    }

    @Test
    fun testSearchBookmarksWithEmptyQuery() = runTest {
        val expectedBookmarks = listOf(
            Bookmark(
                url = "https://example.com",
                title = "Example",
                description = "Description",
                id = "bookmark-1"
            ),
            Bookmark(
                url = "https://test.com",
                title = "Test",
                description = "Test Description",
                id = "bookmark-2"
            ),
            Bookmark(
                url = "https://demo.com",
                title = "Demo",
                description = "Demo Description",
                id = "bookmark-3"
            )
        )
        
        coEvery { searchBookmarksUseCase("") } returns expectedBookmarks
        
        val result = bookmarkToolSet.searchBookmarks("")
        
        assertNotNull("Result should not be null", result)
        assertEquals("Should return 3 bookmarks", 3, result.bookmarks.size)
        coVerify { searchBookmarksUseCase("") }
    }
}
