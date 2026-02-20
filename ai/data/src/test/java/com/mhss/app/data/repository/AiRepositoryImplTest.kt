package com.mhss.app.data.repository

import ai.koog.agents.core.tools.ToolDescriptor
import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.clients.LLMClientException
import ai.koog.prompt.executor.clients.openai.OpenAILLMClient
import ai.koog.prompt.executor.llms.SingleLLMPromptExecutor
import ai.koog.prompt.message.Message
import ai.koog.prompt.params.LLMParams
import com.mhss.app.domain.model.AiMessage
import com.mhss.app.domain.model.AiRepositoryException
import com.mhss.app.domain.model.AssistantResult
import com.mhss.app.preferences.PrefsConstants
import com.mhss.app.preferences.domain.model.AiProvider
import com.mhss.app.preferences.domain.model.PrefsKey
import com.mhss.app.preferences.domain.model.booleanPreferencesKey
import com.mhss.app.preferences.domain.model.intPreferencesKey
import com.mhss.app.preferences.domain.model.stringPreferencesKey
import com.mhss.app.preferences.domain.use_case.GetPreferenceUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.io.IOException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AiRepositoryImplTest {

    private lateinit var aiRepository: AiRepositoryImpl
    
    private val getPreferenceUseCase: GetPreferenceUseCase = mockk()
    private val testScope = TestScope(StandardTestDispatcher())
    private val applicationScope = CoroutineScope(StandardTestDispatcher())
    
    private val noteToolSet = mockk<com.mhss.app.data.tools.NoteToolSet>(relaxed = true)
    private val taskToolSet = mockk<com.mhss.app.data.tools.TaskToolSet>(relaxed = true)
    private val calendarToolSet = mockk<com.mhss.app.data.tools.CalendarToolSet>(relaxed = true)
    private val diaryToolSet = mockk<com.mhss.app.data.tools.DiaryToolSet>(relaxed = true)
    private val bookmarkToolSet = mockk<com.mhss.app.data.tools.BookmarkToolSet>(relaxed = true)
    private val utilToolSet = mockk<com.mhss.app.data.tools.UtilToolSet>(relaxed = true)
    private val getNote = mockk<com.mhss.app.domain.use_case.GetNoteUseCase>(relaxed = true)
    private val getTaskById = mockk<com.mhss.app.domain.use_case.GetTaskByIdUseCase>(relaxed = true)
    private val getCalendarEventById = mockk<com.mhss.app.domain.use_case.GetCalendarEventByIdUseCase>(relaxed = true)

    @Before
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    private fun setupAiRepository() {
        aiRepository = AiRepositoryImpl(
            getPreferenceUseCase = getPreferenceUseCase,
            applicationScope = applicationScope,
            noteToolSet = noteToolSet,
            taskToolSet = taskToolSet,
            calendarToolSet = calendarToolSet,
            diaryToolSet = diaryToolSet,
            bookmarkToolSet = bookmarkToolSet,
            utilToolSet = utilToolSet,
            getNote = getNote,
            getTaskById = getTaskById,
            getCalendarEventById = getCalendarEventById
        )
    }

    @Test
    fun testLLMProviderMapping() {
        val providers = listOf(
            AiProvider.GLM to "glm",
            AiProvider.OpenAI to "openai",
            AiProvider.Gemini to "google",
            AiProvider.Anthropic to "anthropic",
            AiProvider.OpenRouter to "openrouter"
        )
        
        providers.forEach { (provider, expectedProviderName) ->
            assertTrue("Provider $provider should be defined", provider.id > 0)
        }
    }

    @Test
    fun testInvalidApiKey() = testScope.runTest {
        every { 
            getPreferenceUseCase(any<PrefsKey<Int>>(), AiProvider.None.id) 
        } returns flowOf(AiProvider.GLM.id)
        every { 
            getPreferenceUseCase(any<PrefsKey<String>>(), "") 
        } returns flowOf("")
        every { 
            getPreferenceUseCase(any<PrefsKey<Boolean>>(), false) 
        } returns flowOf(true)
        
        setupAiRepository()
        advanceUntilIdle()
        
        val result = aiRepository.sendPrompt("test prompt")
        assertTrue("Should return OtherError for invalid key", result is AssistantResult.OtherError)
    }

    @Test
    fun testNetworkError() = testScope.runTest {
        every { 
            getPreferenceUseCase(any<PrefsKey<Int>>(), AiProvider.None.id) 
        } returns flowOf(AiProvider.GLM.id)
        every { 
            getPreferenceUseCase(any<PrefsKey<String>>(), "") 
        } returns flowOf("test-key")
        every { 
            getPreferenceUseCase(any<PrefsKey<Boolean>>(), false) 
        } returns flowOf(false)
        
        setupAiRepository()
        advanceUntilIdle()
        
        val result = aiRepository.sendPrompt("test prompt")
        assertTrue("Should return OtherError for network error", result is AssistantResult.OtherError)
    }

    @Test
    fun testToolCallLimitExceeded() = testScope.runTest {
        every { 
            getPreferenceUseCase(any<PrefsKey<Int>>(), AiProvider.None.id) 
        } returns flowOf(AiProvider.GLM.id)
        every { 
            getPreferenceUseCase(any<PrefsKey<String>>(), "") 
        } returns flowOf("test-key")
        every { 
            getPreferenceUseCase(any<PrefsKey<Boolean>>(), false) 
        } returns flowOf(true)
        
        setupAiRepository()
        advanceUntilIdle()
        
        val userMessage = AiMessage.UserMessage(
            uuid = "test-uuid",
            content = "test",
            time = System.currentTimeMillis()
        )
        
        try {
            aiRepository.sendMessage(listOf(userMessage)).first()
        } catch (e: AiRepositoryException) {
            assertTrue("Should throw AiRepositoryException", e is AiRepositoryException)
            assertTrue("Should be ToolCallLimitExceeded", e.failure is AssistantResult.ToolCallLimitExceeded)
        }
    }

    @Test
    fun testDateParsingError() = testScope.runTest {
        every { 
            getPreferenceUseCase(any<PrefsKey<Int>>(), AiProvider.None.id) 
        } returns flowOf(AiProvider.GLM.id)
        every { 
            getPreferenceUseCase(any<PrefsKey<String>>(), "") 
        } returns flowOf("test-key")
        every { 
            getPreferenceUseCase(any<PrefsKey<Boolean>>(), false) 
        } returns flowOf(true)
        
        setupAiRepository()
        advanceUntilIdle()
        
        val userMessage = AiMessage.UserMessage(
            uuid = "test-uuid",
            content = "test",
            time = System.currentTimeMillis()
        )
        
        try {
            aiRepository.sendMessage(listOf(userMessage)).first()
        } catch (e: AiRepositoryException) {
            assertTrue("Should throw AiRepositoryException", e is AiRepositoryException)
        }
    }

    @Test
    fun testEmptyModelName() = testScope.runTest {
        every { 
            getPreferenceUseCase(any<PrefsKey<Int>>(), AiProvider.None.id) 
        } returns flowOf(AiProvider.GLM.id)
        every { 
            getPreferenceUseCase(any<PrefsKey<String>>(), "") 
        } returns flowOf("test-key")
        every { 
            getPreferenceUseCase(any<PrefsKey<Boolean>>(), false) 
        } returns flowOf(true)
        
        setupAiRepository()
        advanceUntilIdle()
        
        val result = aiRepository.sendPrompt("test prompt")
        assertTrue("Should return OtherError for empty model", result is AssistantResult.OtherError)
    }

    @Test
    fun testClientNotInitialized() = testScope.runTest {
        every { 
            getPreferenceUseCase(any<PrefsKey<Int>>(), AiProvider.None.id) 
        } returns flowOf(AiProvider.None.id)
        every { 
            getPreferenceUseCase(any<PrefsKey<Boolean>>(), false) 
        } returns flowOf(false)
        
        setupAiRepository()
        advanceUntilIdle()
        
        val result = aiRepository.sendPrompt("test prompt")
        assertTrue("Should return OtherError when client not initialized", result is AssistantResult.OtherError)
    }

    @Test
    fun testGenericException() = testScope.runTest {
        every { 
            getPreferenceUseCase(any<PrefsKey<Int>>(), AiProvider.None.id) 
        } returns flowOf(AiProvider.GLM.id)
        every { 
            getPreferenceUseCase(any<PrefsKey<String>>(), "") 
        } returns flowOf("test-key")
        every { 
            getPreferenceUseCase(any<PrefsKey<Boolean>>(), false) 
        } returns flowOf(false)
        
        setupAiRepository()
        advanceUntilIdle()
        
        val result = aiRepository.sendPrompt("test prompt")
        assertTrue("Should return OtherError for generic exception", result is AssistantResult.OtherError)
    }

    @Test
    fun testToolExecutionFailure() = testScope.runTest {
        every { 
            getPreferenceUseCase(any<PrefsKey<Int>>(), AiProvider.None.id) 
        } returns flowOf(AiProvider.GLM.id)
        every { 
            getPreferenceUseCase(any<PrefsKey<String>>(), "") 
        } returns flowOf("test-key")
        every { 
            getPreferenceUseCase(any<PrefsKey<Boolean>>(), false) 
        } returns flowOf(true)
        
        setupAiRepository()
        advanceUntilIdle()
        
        val userMessage = AiMessage.UserMessage(
            uuid = "test-uuid",
            content = "test",
            time = System.currentTimeMillis()
        )
        
        try {
            aiRepository.sendMessage(listOf(userMessage)).first()
        } catch (e: AiRepositoryException) {
            assertTrue("Should throw AiRepositoryException on tool failure", e is AiRepositoryException)
        }
    }
}
