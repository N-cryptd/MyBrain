package com.mhss.app.preferences.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AiProviderTest {

    @Test
    fun testGLMProviderConfiguration() {
        val glmProvider = AiProvider.GLM
        
        assertEquals("GLM provider should have id = 7", 7, glmProvider.id)
        assertNotNull("GLM provider should have a key preference", glmProvider.keyPref)
        assertNotNull("GLM provider should have a model preference", glmProvider.modelPref)
        assertNotNull("GLM provider should have a custom URL preference", glmProvider.customUrlPref)
        assertNotNull("GLM provider should have a custom URL enabled preference", glmProvider.customUrlEnabledPref)
        assertTrue("GLM provider should support custom URL", glmProvider.supportsCustomUrl)
        assertFalse("GLM provider should not require custom URL", glmProvider.requiresCustomUrl)
        assertFalse("GLM provider should not be a local provider", glmProvider.isLocalProvider)
    }

    @Test
    fun testGLMPrefKeys() {
        val glmProvider = AiProvider.GLM
        
        assertEquals("GLM key pref should be glm_key", "glm_key", glmProvider.keyPref)
        assertEquals("GLM model pref should be glm_model", "glm_model", glmProvider.modelPref)
        assertEquals("GLM URL pref should be glm_url", "glm_url", glmProvider.customUrlPref)
        assertEquals("GLM use URL pref should be glm_use_url", "glm_use_url", glmProvider.customUrlEnabledPref)
    }

    @Test
    fun testGLMDefaults() {
        val glmProvider = AiProvider.GLM
        
        assertEquals("GLM default model should be glm-4.7", "glm-4.7", glmProvider.defaultModel)
        assertEquals("GLM default base URL should be correct", "https://api.z.ai/api/coding/paas/v4", glmProvider.defaultBaseUrl)
    }

    @Test
    fun testGLMProviderPrefsKeys() {
        val glmProvider = AiProvider.GLM
        
        assertNotNull("GLM provider should have string keyPrefsKey", glmProvider.keyPrefsKey)
        assertNotNull("GLM provider should have string modelPrefsKey", glmProvider.modelPrefsKey)
        assertNotNull("GLM provider should have string customUrlPrefsKey", glmProvider.customUrlPrefsKey)
        assertNotNull("GLM provider should have boolean customUrlEnabledPrefsKey", glmProvider.customUrlEnabledPrefsKey)
    }

    @Test
    fun testGLMProviderEnumEntry() {
        val providers = AiProvider.entries
        assertTrue("GLM should be in AiProvider entries", providers.contains(AiProvider.GLM))
    }

    @Test
    fun testIntToAiProviderGLM() {
        val provider = 7.toAiProvider()
        assertEquals("ID 7 should map to GLM provider", AiProvider.GLM, provider)
    }

    @Test
    fun testGLMKeyInfoUrls() {
        val glmProvider = AiProvider.GLM
        
        assertEquals("GLM key info URL should be correct", "https://z.ai/apikeys", glmProvider.keyInfoUrl)
        assertEquals("GLM models info URL should be correct", "https://docs.z.ai/devpack/overview", glmProvider.modelsInfoUrl)
    }
}
