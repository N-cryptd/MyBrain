package com.mhss.app.data.tools

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class UtilToolSetTest {

    private lateinit var utilToolSet: UtilToolSet

    @Before
    fun setup() {
        utilToolSet = UtilToolSet()
    }

    @Test
    fun testFormatDate() {
        val testMillis = 1705272000000L // 2024-01-15T09:00:00
        
        val result = utilToolSet.formatDate(testMillis)
        
        assertNotNull("Result should not be null", result)
        assertNotNull("Formatted date should not be null", result.formattedDate)
        assertTrue("Formatted date should not be empty", result.formattedDate.isNotEmpty())
    }

    @Test
    fun testFormatDateWithEpochZero() {
        val testMillis = 0L
        
        val result = utilToolSet.formatDate(testMillis)
        
        assertNotNull("Result should not be null", result)
        assertNotNull("Formatted date should not be null", result.formattedDate)
        assertTrue("Formatted date should not be empty", result.formattedDate.isNotEmpty())
    }

    @Test
    fun testFormatDateWithCurrentTime() {
        val currentTime = Clock.System.now().toEpochMilliseconds()
        
        val result = utilToolSet.formatDate(currentTime)
        
        assertNotNull("Result should not be null", result)
        assertNotNull("Formatted date should not be null", result.formattedDate)
        assertTrue("Formatted date should not be empty", result.formattedDate.isNotEmpty())
    }

    @Test
    fun testFormatDateWithFutureTime() {
        val futureMillis = 1735689600000L // 2025-01-01T00:00:00
        
        val result = utilToolSet.formatDate(futureMillis)
        
        assertNotNull("Result should not be null", result)
        assertNotNull("Formatted date should not be null", result.formattedDate)
        assertTrue("Formatted date should not be empty", result.formattedDate.isNotEmpty())
    }

    @Test
    fun testFormatDateWithPastTime() {
        val pastMillis = 1609459200000L // 2021-01-01T00:00:00
        
        val result = utilToolSet.formatDate(pastMillis)
        
        assertNotNull("Result should not be null", result)
        assertNotNull("Formatted date should not be null", result.formattedDate)
        assertTrue("Formatted date should not be empty", result.formattedDate.isNotEmpty())
    }

    @Test
    fun testFormatDateContainsDayName() {
        val testMillis = 1705272000000L // 2024-01-15 is a Monday
        
        val result = utilToolSet.formatDate(testMillis)
        
        assertNotNull("Result should not be null", result)
        val formattedDate = result.formattedDate
        
        val dayNames = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday",
            "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        assertTrue("Formatted date should contain a day name", dayNames.any { formattedDate.contains(it, ignoreCase = true) })
    }

    @Test
    fun testFormatDateContainsTime() {
        val testMillis = 1705272000000L
        
        val result = utilToolSet.formatDate(testMillis)
        
        assertNotNull("Result should not be null", result)
        val formattedDate = result.formattedDate
        
        assertTrue("Formatted date should contain time information", 
            formattedDate.contains(":") || formattedDate.contains("at") || formattedDate.contains("AM") || formattedDate.contains("PM"))
    }

    @Test
    fun testFormatDateDifferentTimes() {
        val times = listOf(
            1705272000000L, // Morning
            1705297200000L, // Afternoon
            1705322400000L, // Evening
            1705347600000L  // Late night
        )
        
        times.forEach { millis ->
            val result = utilToolSet.formatDate(millis)
            assertNotNull("Result for time $millis should not be null", result)
            assertNotNull("Formatted date for time $millis should not be null", result.formattedDate)
            assertTrue("Formatted date for time $millis should not be empty", result.formattedDate.isNotEmpty())
        }
    }

    @Test
    fun testFormatDateConsistentFormatting() {
        val testMillis = 1705272000000L
        
        val result1 = utilToolSet.formatDate(testMillis)
        val result2 = utilToolSet.formatDate(testMillis)
        
        assertNotNull("First result should not be null", result1)
        assertNotNull("Second result should not be null", result2)
        assertEquals("Formatting should be consistent for same time", result1.formattedDate, result2.formattedDate)
    }

    @Test
    fun testFormatDateReturnsString() {
        val testMillis = 1705272000000L
        
        val result = utilToolSet.formatDate(testMillis)
        
        assertTrue("Formatted date should be a String", result.formattedDate is String)
        assertTrue("Formatted date should have reasonable length", result.formattedDate.length >= 10)
    }
}
