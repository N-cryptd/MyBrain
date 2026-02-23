package com.mhss.app.domain

const val MAX_CONSECUTIVE_TOOL_CALLS = 15

val baseChatSystemMessage = """
    You are a personal AI assistant.
    You help users with their questions and requests and provide detailed explanations if needed.
""".trimIndent()

val toolsSystemMessage = """
    You can make multiple tool calls after each other to fulfill the user's request.
    no need to ask the user to proceed after each tool.
    it's highly encouraged to make multiple tool calls in one response when possible.
    after you no need to make tool calls any more, give the user a short summary of what you did.
""".trimIndent()


val String.summarizeNotePrompt: String
    get() = """
        Summarize this note in bullet points.
        IMPORTANT: DO NOT include any reasoning, analysis, thinking process, or phrases like "Here's a summary" or "Let me think".
        Respond ONLY with the bullet point summary itself.
        Use Markdown for formatting.
        Respond using the same language as the original note language.
        Note content:
        $this
        
        Summary:
    """.trimIndent()

val String.autoFormatNotePrompt: String
    get() = """
        Format this note in a more readable way.
        Include headings, bullet points, and other formatting.
        IMPORTANT: DO NOT include any reasoning, analysis, thinking process, or phrases like "Here's a formatted note" or "Let me format this".
        Respond ONLY with the formatted note itself.
        Use Markdown for formatting.
        Respond using the same language as the original note language.
        Note content:
        $this
        
        Formatted note:
    """.trimIndent()

val String.correctSpellingNotePrompt: String
    get() = """
        Correct spelling and grammar errors in this note.
        IMPORTANT: DO NOT include any reasoning, analysis, thinking process, or phrases like "Here's a corrected note" or "Let me check this".
        Respond ONLY with the corrected note itself.
        Respond using the same language as the original note language.
        Note content:
        $this
        
        Corrected note:
    """.trimIndent()

val String.autoFormatNotePrompt: String
    get() = """
        Format this note in a more readable way.
        Include headings, bullet points, and other formatting.
        IMPORTANT: DO NOT include any reasoning, analysis, thinking process, or phrases like "Here's the formatted note" or "Let me format this".
        Respond ONLY with the formatted note itself.
        Use Markdown for formatting.
        Respond using the same language as the original note language.
        Note content:
        $this
        
        Formatted note:
    """.trimIndent()

val String.correctSpellingNotePrompt: String
    get() = """
        Correct spelling and grammar errors in this note.
        IMPORTANT: DO NOT include any reasoning, analysis, thinking process, or phrases like "Here's the corrected note" or "Let me check this".
        Respond ONLY with the corrected note itself.
        Respond using the same language as the original note language.
        Note content:
        $this
        
        Corrected note:
    """.trimIndent()

val String.autoFormatNotePrompt: String
    get() = """
        Format this note in a more readable way.
        Include headings, bullet points, and other formatting.
        Respond with the formatted note only and don't say anything else.
        Use Markdown for formatting.
        Respond using the same language as the original note language.
        Note content:
        $this
        Formatted note:
    """.trimIndent()

val String.correctSpellingNotePrompt: String
    get() = """
        Correct the spelling and grammar errors in this note.
        Respond with the corrected note only and don't say anything else.
        Respond using the same language as the original note language.
        Note content:
        $this
        Corrected note:
    """.trimIndent()