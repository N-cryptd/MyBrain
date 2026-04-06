package com.mhss.app.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp

@Composable
fun MarkdownWithLinks(
    content: String,
    onLinkClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val linkTag = "NOTE_LINK"
    val parts = remember(content) { parseContentWithLinks(content) }

    val annotatedString = buildAnnotatedString {
        parts.forEach { part ->
            when (part.type) {
                PartType.TEXT -> {
                    append(part.text)
                }
                PartType.LINK -> {
                    pushStringAnnotation(tag = linkTag, annotation = part.linkText)
                    withStyle(
                        style = SpanStyle(
                            color = Color(0xFF6366F1),
                            textDecoration = null
                        )
                    ) {
                        append(part.displayText ?: part.linkText)
                    }
                    pop()
                }
            }
        }
    }

    ClickableText(
        text = annotatedString,
        style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface),
        modifier = modifier,
        onClick = { offset ->
            annotatedString.getStringAnnotations(
                tag = linkTag,
                start = offset,
                end = offset
            ).firstOrNull()?.let { annotation ->
                onLinkClick(annotation.item)
            }
        }
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
            linkContent.substringAfter("|").trim()
        } else {
            null
        }
        val linkTitle = if (linkContent.contains("|")) {
            linkContent.substringBefore("|").trim()
        } else {
            linkContent
        }

        parts.add(ContentPart(
            type = PartType.LINK,
            linkText = linkTitle,
            displayText = displayText
        ))

        lastIndex = match.range.last + 1
    }

    if (lastIndex < content.length) {
        parts.add(ContentPart(PartType.TEXT, text = content.substring(lastIndex)))
    }

    return parts
}
