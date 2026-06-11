package com.memory.platform_specificfeatures.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.memory.platform_specificfeatures.data.model.Note
import com.memory.platform_specificfeatures.data.model.NoteColor
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

// Semua varian pink monokromatik — tidak ada warna lain
private val cardPalette = listOf(
    Color(0xFFFFF0F5), // rose white
    Color(0xFFFFDDEE), // blush pink
    Color(0xFFFFECF3), // soft pink
    Color(0xFFFAD4E8), // dusty rose
    Color(0xFFFFE8F2), // petal
    Color(0xFFF8CDDF), // mauve light
)

@Composable
fun NoteCard(
    note: Note,
    onClick: () -> Unit,
    showDate: Boolean = false,
    modifier: Modifier = Modifier
) {
    val cardBg        = note.color.toComposeColor()
    val textPrimary   = Color(0xFF5C1A3A)
    val textSecondary = Color(0xFFAA6080)
    val categoryBg    = Color(0x33C2185B)

    val interactionSource = remember { MutableInteractionSource() }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(interactionSource = interactionSource, indication = null) { onClick() },
        shape = RoundedCornerShape(20.dp),
        elevation = 0.dp,
        backgroundColor = cardBg
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            val emoji = categoryEmoji(note.category)
            if (emoji.isNotEmpty()) {
                Text(text = emoji, fontSize = 16.sp)
                Spacer(Modifier.height(4.dp))
            }
            Text(
                text = note.title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = textPrimary
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = note.content,
                fontSize = 11.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                color = textSecondary
            )
            if (note.category.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .background(color = categoryBg, shape = RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = note.category,
                        fontSize = 10.sp,
                        color = textPrimary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            if (showDate) {
                Spacer(Modifier.height(6.dp))
                Text(
                    text = formatNoteDate(note.createdAt),
                    fontSize = 10.sp,
                    color = textSecondary
                )
            }
        }
    }
}

private fun categoryEmoji(category: String): String = when (category.lowercase()) {
    "personal" -> "🌸"
    "work"     -> "🎀"
    "study"    -> "📖"
    "health"   -> "🌿"
    "travel"   -> "✈️"
    else       -> ""
}

fun formatNoteDate(timestamp: Long): String {
    return try {
        val instant   = Instant.fromEpochMilliseconds(timestamp)
        val localDate = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        val month     = localDate.month.name.take(3)
            .lowercase().replaceFirstChar { it.uppercase() }
        "$month ${localDate.dayOfMonth}, ${localDate.year}"
    } catch (e: Exception) { "" }
}

// Semua NoteColor → varian pink monokromatik
fun NoteColor.toComposeColor(): Color = when (this) {
    NoteColor.WHITE  -> Color(0xFFFFF0F5)
    NoteColor.YELLOW -> Color(0xFFFFECF3)
    NoteColor.GREEN  -> Color(0xFFFFDDEE)
    NoteColor.BLUE   -> Color(0xFFFAD4E8)
    NoteColor.PINK   -> Color(0xFFF8CDDF)
    NoteColor.PURPLE -> Color(0xFFFFE8F2)
}