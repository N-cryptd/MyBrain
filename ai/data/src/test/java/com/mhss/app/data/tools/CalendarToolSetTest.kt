package com.mhss.app.data.tools

import com.mhss.app.domain.model.Calendar
import com.mhss.app.domain.model.CalendarEvent
import com.mhss.app.domain.model.CalendarEventFrequency
import com.mhss.app.domain.use_case.AddCalendarEventUseCase
import com.mhss.app.domain.use_case.GetAllCalendarsUseCase
import com.mhss.app.domain.use_case.GetEventsWithinRangeUseCase
import com.mhss.app.domain.use_case.SearchEventsByTitleWithinRangeUseCase
import com.mhss.app.preferences.PrefsConstants
import com.mhss.app.preferences.domain.model.PrefsKey
import com.mhss.app.preferences.domain.model.stringSetPreferencesKey
import com.mhss.app.preferences.domain.use_case.GetPreferenceUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CalendarToolSetTest {

    private lateinit var calendarToolSet: CalendarToolSet
    
    private val getEventsWithinRangeUseCase: GetEventsWithinRangeUseCase = mockk()
    private val searchEventsByTitleWithinRangeUseCase: SearchEventsByTitleWithinRangeUseCase = mockk()
    private val addCalendarEvent: AddCalendarEventUseCase = mockk()
    private val getAllCalendarsUseCase: GetAllCalendarsUseCase = mockk()
    private val getPreference: GetPreferenceUseCase = mockk()

    @Before
    fun setup() {
        calendarToolSet = CalendarToolSet(
            getEventsWithinRangeUseCase = getEventsWithinRangeUseCase,
            searchEventsByTitleWithinRangeUseCase = searchEventsByTitleWithinRangeUseCase,
            addCalendarEvent = addCalendarEvent,
            getAllCalendarsUseCase = getAllCalendarsUseCase,
            getPreference = getPreference
        )
    }

    @Test
    fun testGetEventsWithinRange() = runTest {
        val expectedEvents = listOf(
            CalendarEvent(
                id = 1,
                title = "Meeting",
                start = 1705272000000,
                end = 1705275600000,
                calendarId = 1
            ),
            CalendarEvent(
                id = 2,
                title = "Lunch",
                start = 1705279200000,
                end = 1705282800000,
                calendarId = 1
            )
        )
        
        coEvery { 
            getPreference(any<PrefsKey<Set<String>>>(), any()) 
        } returns flowOf(emptySet<String>())
        coEvery { getEventsWithinRangeUseCase(1705272000000, 1705293600000, emptyList()) } returns expectedEvents
        
        val result = calendarToolSet.getEventsWithinRange(
            startDateTime = "2024-01-15T09:00:00",
            endDateTime = "2024-01-15T15:00:00"
        )
        
        assertNotNull("Result should not be null", result)
        assertEquals("Should return 2 events", 2, result.events.size)
        assertEquals("First event title should match", "Meeting", result.events[0].title)
    }

    @Test
    fun testSearchEventsByNameWithinRange() = runTest {
        val expectedEvents = listOf(
            CalendarEvent(
                id = 1,
                title = "Team Meeting",
                start = 1705272000000,
                end = 1705275600000,
                calendarId = 1
            )
        )
        
        coEvery { 
            getPreference(any<PrefsKey<Set<String>>>(), any()) 
        } returns flowOf(emptySet<String>())
        coEvery { 
            searchEventsByTitleWithinRangeUseCase(
                startMillis = 1705272000000,
                endMillis = 1705358400000,
                titleQuery = "meeting",
                excludedCalendars = emptyList()
            )
        } returns expectedEvents
        
        val result = calendarToolSet.searchEventsByNameWithinRange(
            eventName = "meeting",
            startDateTime = "2024-01-15T09:00:00",
            endDateTime = "2024-01-16T09:00:00"
        )
        
        assertNotNull("Result should not be null", result)
        assertEquals("Should return 1 event", 1, result.events.size)
        assertEquals("Event title should contain query", "Team Meeting", result.events[0].title)
    }

    @Test
    fun testCreateEvent() = runTest {
        val eventId = 123L
        coEvery { addCalendarEvent(any()) } returns eventId
        
        val result = calendarToolSet.createEvent(
            title = "New Event",
            start = "2024-01-15T10:00:00",
            end = "2024-01-15T11:00:00",
            calendarId = 1,
            description = "Event description",
            location = "Office",
            allDay = false,
            recurring = false,
            frequency = CalendarEventFrequency.NEVER
        )
        
        assertNotNull("Result should not be null", result)
        assertEquals("Should return the created event ID", eventId, result.createdEventId)
        coVerify { addCalendarEvent(any()) }
    }

    @Test
    fun testCreateEventWithRecurrence() = runTest {
        val eventId = 456L
        coEvery { addCalendarEvent(any()) } returns eventId
        
        val result = calendarToolSet.createEvent(
            title = "Weekly Meeting",
            start = "2024-01-15T10:00:00",
            end = "2024-01-15T11:00:00",
            calendarId = 2,
            description = "Weekly team sync",
            location = "Conference Room",
            allDay = false,
            recurring = true,
            frequency = CalendarEventFrequency.WEEKLY
        )
        
        assertNotNull("Result should not be null", result)
        assertEquals("Should return the created event ID", eventId, result.createdEventId)
        coVerify { addCalendarEvent(any()) }
    }

    @Test
    fun testCreateEvents() = runTest {
        val eventInputs = listOf(
            CalendarEventInput(
                title = "Event 1",
                start = "2024-01-15T10:00:00",
                end = "2024-01-15T11:00:00",
                calendarId = 1
            ),
            CalendarEventInput(
                title = "Event 2",
                start = "2024-01-16T14:00:00",
                end = "2024-01-16T15:00:00",
                calendarId = 1
            )
        )
        
        coEvery { addCalendarEvent(any()) } returnsMany listOf(1L, 2L)
        
        val result = calendarToolSet.createEvents(eventInputs)
        
        assertNotNull("Result should not be null", result)
        assertEquals("Should return 2 event IDs", 2, result.createdEventIds.size)
        assertEquals("First ID should match", 1L, result.createdEventIds[0])
        coVerify(exactly = 2) { addCalendarEvent(any()) }
    }

    @Test
    fun testGetAllCalendars() = runTest {
        val expectedCalendars = mapOf(
            "Account 1" to listOf(
                Calendar(id = 1, name = "Personal", account = "Account 1", color = 0xFF0000.toInt()),
                Calendar(id = 2, name = "Work", account = "Account 1", color = 0x00FF00.toInt())
            ),
            "Account 2" to listOf(
                Calendar(id = 3, name = "Holidays", account = "Account 2", color = 0x0000FF.toInt())
            )
        )
        
        coEvery { 
            getPreference(stringSetPreferencesKey(PrefsConstants.EXCLUDED_CALENDARS_KEY), emptySet()) 
        } returns flowOf(emptySet<String>())
        coEvery { getAllCalendarsUseCase(emptyList()) } returns expectedCalendars
        
        val result = calendarToolSet.getAllCalendars()
        
        assertNotNull("Result should not be null", result)
        assertEquals("Should return 2 accounts", 2, result.calendars.size)
        assertTrue("Should have Account 1", result.calendars.containsKey("Account 1"))
        assertEquals("Account 1 should have 2 calendars", 2, result.calendars["Account 1"]?.size)
    }
}
