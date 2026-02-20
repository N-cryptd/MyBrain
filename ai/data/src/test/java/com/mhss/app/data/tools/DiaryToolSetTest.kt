package com.mhss.app.data.tools

import com.mhss.app.domain.model.DiaryEntry
import com.mhss.app.domain.model.Mood
import com.mhss.app.domain.use_case.AddDiaryEntryUseCase
import com.mhss.app.domain.use_case.GetDiaryEntryUseCase
import com.mhss.app.domain.use_case.SearchEntriesUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class DiaryToolSetTest {

    private lateinit var diaryToolSet: DiaryToolSet
    
    private val addDiaryEntry: AddDiaryEntryUseCase = mockk()
    private val searchEntries: SearchEntriesUseCase = mockk()
    private val getDiaryEntry: GetDiaryEntryUseCase = mockk()

    @Before
    fun setup() {
        diaryToolSet = DiaryToolSet(
            addDiaryEntry = addDiaryEntry,
            searchEntries = searchEntries,
            getDiaryEntry = getDiaryEntry
        )
    }

    @Test
    fun testCreateDiaryEntry() = runTest {
        coEvery { addDiaryEntry(any()) } returns Unit
        
        val result = diaryToolSet.createDiaryEntry(
            title = "My Day",
            content = "Today was a great day!",
            mood = Mood.GOOD
        )
        
        assertNotNull("Result should not be null", result)
        assertNotNull("Should return a diary entry ID", result.createdDiaryEntryId)
        coVerify { addDiaryEntry(any()) }
    }

    @Test
    fun testCreateDiaryEntryWithDifferentMoods() = runTest {
        val moods = listOf(Mood.AWESOME, Mood.GOOD, Mood.OKAY, Mood.BAD, Mood.TERRIBLE)
        
        moods.forEach { mood ->
            coEvery { addDiaryEntry(any()) } returns Unit
            
            val result = diaryToolSet.createDiaryEntry(
                title = "Entry with $mood",
                content = "Content",
                mood = mood
            )
            
            assertNotNull("Result for $mood should not be null", result)
            assertNotNull("Should return a diary entry ID for $mood", result.createdDiaryEntryId)
            coVerify { addDiaryEntry(any()) }
        }
    }

    @Test
    fun testSearchDiaryEntries() = runTest {
        val expectedEntries = listOf(
            DiaryEntry(
                title = "Happy Day",
                content = "Everything went well today",
                mood = Mood.GOOD,
                id = "entry-1"
            ),
            DiaryEntry(
                title = "Productive Day",
                content = "Accomplished a lot",
                mood = Mood.AWESOME,
                id = "entry-2"
            )
        )
        
        coEvery { searchEntries("happy") } returns expectedEntries
        
        val result = diaryToolSet.searchDiaryEntries("happy")
        
        assertNotNull("Result should not be null", result)
        assertEquals("Should return 2 entries", 2, result.entries.size)
        assertEquals("First entry title should match", "Happy Day", result.entries[0].title)
        assertEquals("First entry mood should match", Mood.GOOD, result.entries[0].mood)
        coVerify { searchEntries("happy") }
    }

    @Test
    fun testSearchDiaryEntriesWithEmptyQuery() = runTest {
        val expectedEntries = listOf(
            DiaryEntry(
                title = "First Entry",
                content = "Content 1",
                mood = Mood.OKAY,
                id = "entry-1"
            ),
            DiaryEntry(
                title = "Second Entry",
                content = "Content 2",
                mood = Mood.GOOD,
                id = "entry-2"
            ),
            DiaryEntry(
                title = "Third Entry",
                content = "Content 3",
                mood = Mood.BAD,
                id = "entry-3"
            )
        )
        
        coEvery { searchEntries("") } returns expectedEntries
        
        val result = diaryToolSet.searchDiaryEntries("")
        
        assertNotNull("Result should not be null", result)
        assertEquals("Should return 3 entries", 3, result.entries.size)
        coVerify { searchEntries("") }
    }

    @Test
    fun testGetDiaryEntry() = runTest {
        val expectedEntry = DiaryEntry(
            title = "Special Day",
            content = "Something special happened",
            mood = Mood.AWESOME,
            id = "entry-123"
        )
        
        coEvery { getDiaryEntry("entry-123") } returns expectedEntry
        
        val result = diaryToolSet.getDiaryEntry("entry-123")
        
        assertNotNull("Result should not be null", result)
        assertNotNull("Entry should not be null", result.entry)
        assertEquals("Entry title should match", "Special Day", result.entry?.title)
        assertEquals("Entry mood should match", Mood.AWESOME, result.entry?.mood)
        assertEquals("Entry ID should match", "entry-123", result.entry?.id)
        coVerify { getDiaryEntry("entry-123") }
    }

    @Test
    fun testGetDiaryEntryNotFound() = runTest {
        coEvery { getDiaryEntry("non-existent") } returns null
        
        val result = diaryToolSet.getDiaryEntry("non-existent")
        
        assertNotNull("Result should not be null", result)
        assertEquals("Entry should be null", null, result.entry)
        coVerify { getDiaryEntry("non-existent") }
    }
}
