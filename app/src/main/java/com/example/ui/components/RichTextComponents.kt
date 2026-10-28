package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Reusable Rich Text Formatting Toolbar
 * Enables users to quickly insert Bold, Italic, Headings, Bullet Lists,
 * Numbered Lists, Bible Quotes, and Spiritual symbols into any text area.
 */
@Composable
fun RichTextFormattingBar(
    text: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 6.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Format :",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Bold **B**
            FormatButton(label = "B", isBold = true) {
                onTextChange(text + " **texte en gras**")
            }

            // Italic *I*
            FormatButton(label = "I", isItalic = true) {
                onTextChange(text + " *texte en italique*")
            }

            // Heading ###
            FormatButton(label = "H") {
                val prefix = if (text.isEmpty() || text.endsWith("\n")) "" else "\n"
                onTextChange("$text$prefix### Titre de section\n")
            }

            // Bullet List •
            FormatButton(label = "• Puce") {
                val prefix = if (text.isEmpty() || text.endsWith("\n")) "" else "\n"
                onTextChange("$text$prefix• Point clé : ")
            }

            // Numbered List 1.
            FormatButton(label = "1. Liste") {
                val prefix = if (text.isEmpty() || text.endsWith("\n")) "" else "\n"
                onTextChange("$text${prefix}1. Première étape : ")
            }

            // Scripture quote >
            FormatButton(label = "❝ Verset") {
                val prefix = if (text.isEmpty() || text.endsWith("\n")) "" else "\n"
                onTextChange("$text$prefix> « Verset ou citation » (Réf)\n")
            }

            // Cross symbol ✝
            FormatButton(label = "✝ Foi") {
                onTextChange("$text ✝ ")
            }

            // Breakthrough symbol ✨
            FormatButton(label = "✨ Percée") {
                onTextChange("$text ✨ ")
            }
        }
    }
}

@Composable
private fun FormatButton(
    label: String,
    isBold: Boolean = false,
    isItalic: Boolean = false,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(6.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isBold) FontWeight.ExtraBold else FontWeight.Medium,
            fontStyle = if (isItalic) FontStyle.Italic else FontStyle.Normal,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

/**
 * FormattedText: Parses simple markdown structures:
 * - Bold: **word**
 * - Italic: *word*
 * - Headings: ### Title
 * - Quotes: > text
 * - Bullets: • text
 */
@Composable
fun FormattedContentText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurface,
    lineHeightSp: Int = 19
) {
    val lines = text.lines()
    Column(modifier = modifier) {
        lines.forEach { line ->
            when {
                line.trimStart().startsWith("###") -> {
                    val heading = line.replaceFirst("###", "").trim()
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = heading,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                }
                line.trimStart().startsWith(">") -> {
                    val quote = line.replaceFirst(">", "").trim()
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(width = 3.dp, height = 24.dp)
                                    .background(MaterialTheme.colorScheme.primary)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = quote,
                                fontSize = 12.sp,
                                fontStyle = FontStyle.Italic,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
                line.trimStart().startsWith("•") -> {
                    val bulletContent = line.replaceFirst("•", "").trim()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "•",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(end = 6.dp)
                        )
                        Text(
                            text = renderMarkdownInline(bulletContent),
                            fontSize = 13.sp,
                            lineHeight = lineHeightSp.sp,
                            color = color
                        )
                    }
                }
                else -> {
                    Text(
                        text = renderMarkdownInline(line),
                        fontSize = 13.sp,
                        lineHeight = lineHeightSp.sp,
                        color = color
                    )
                }
            }
        }
    }
}

/**
 * Parses inline **bold** and *italic* into an AnnotatedString
 */
private fun renderMarkdownInline(text: String): AnnotatedString {
    return buildAnnotatedString {
        var cursor = 0
        while (cursor < text.length) {
            val boldStart = text.indexOf("**", cursor)
            if (boldStart != -1) {
                val boldEnd = text.indexOf("**", boldStart + 2)
                if (boldEnd != -1) {
                    append(text.substring(cursor, boldStart))
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(text.substring(boldStart + 2, boldEnd))
                    }
                    cursor = boldEnd + 2
                    continue
                }
            }

            val italicStart = text.indexOf("*", cursor)
            if (italicStart != -1 && (italicStart == text.length - 1 || text[italicStart + 1] != '*')) {
                val italicEnd = text.indexOf("*", italicStart + 1)
                if (italicEnd != -1) {
                    append(text.substring(cursor, italicStart))
                    withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                        append(text.substring(italicStart + 1, italicEnd))
                    }
                    cursor = italicEnd + 1
                    continue
                }
            }

            // Append remainder
            append(text.substring(cursor))
            break
        }
    }
}
