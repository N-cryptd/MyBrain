package com.mhss.app.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.mhss.app.domain.model.Note
import com.mhss.app.ui.components.common.defaultMarkdownTypography
import com.mikepenz.markdown.coil2.Coil2ImageTransformerImpl
import com.mikepenz.markdown.m3.Markdown

@Composable
fun MarkdownWithLinks(
    content: String,
    onLinkClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val parts = remember(content) { parseContentWithLinks(content) }
    
    Text(
        text = buildAnnotatedString {
            parts.forEach { part ->
                when (part.type) {
                    PartType.TEXT -> {
                        append(part.text)
                    }
                    PartType.LINK -> {
                        withStyle(
                            style = SpanStyle(
                                color = Color(0xFF6366F1),
                                textDecoration = null
                            )
                        ) {
                            append(part.displayText ?: part.linkText)
                        }
                    }
                }
            }
        },
        modifier = modifier,
        style = MaterialTheme.typography.bodyMedium
    )
}

enum class PartType { TEXT, LINK }

data class ContentPart(
    val type: PartType,
    val text: String = "",
    val linkText: String = "",
    val displayText: String? = null
)

private fun parseContentWithLinks(content: String): List<ContentPart> {
    val parts = mutableListOf<ContentPart>()
    val linkPattern = Regex("""\[\[([^\]]+)\]\]""")
    
    var lastIndex = 0
    linkPattern.findAll(content).forEach { match ->
        val beforeText = content.substring(lastIndex, match.range.first)
        if (beforeText.isNotEmpty()) {
            parts.add(ContentPart(PartType.TEXT, text = beforeText))
        }
        
        val linkContent = match.groupValues[1].trim()
        val displayText = if (linkContent.contains("|")) {
            linkContent.substringBefore("|").trim()
        } else {
            null
        }
        
        parts.add(ContentPart(
            type = PartType.LINK,
            linkText = linkContent,
            displayText = displayText
        ))
        
        lastIndex = match.range.last + 1
    }
    
    if (lastIndex < content.length) {
        parts.add(ContentPart(PartType.TEXT, text = content.substring(lastIndex)))
    }
    
    return parts
}