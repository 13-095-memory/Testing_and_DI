package com.memory.platform_specificfeatures.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.memory.platform_specificfeatures.data.model.NoteType
import com.memory.platform_specificfeatures.data.model.TodoItem
import com.memory.platform_specificfeatures.presentation.NotesViewModel
import com.memory.platform_specificfeatures.ui.components.formatNoteDate
import com.memory.platform_specificfeatures.ui.components.toComposeColor
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ReadNoteScreen(
    noteId: Long,
    onNavigateBack: () -> Unit
) {
    val viewModel: NotesViewModel = koinViewModel()

    // ── observe sebagai State → auto recompose saat toggle ───
    val note by viewModel.getNoteByIdFlow(noteId).collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (note == null) { onNavigateBack(); return }
    val currentNote = note!!

    val contentFontSize   = when (currentNote.fontSize) {
        "Small" -> 13.sp; "Large" -> 19.sp; else -> 16.sp
    }
    val contentFontWeight = if (currentNote.fontStyle == "Bold") FontWeight.Bold else FontWeight.Normal
    val contentFontItalic = if (currentNote.fontStyle == "Italic") FontStyle.Italic else FontStyle.Normal

    val pink      = MaterialTheme.colors.primary
    val surface   = MaterialTheme.colors.surface
    val textOn    = MaterialTheme.colors.onBackground
    val isDark    = !MaterialTheme.colors.isLight

    // ── background: dark mode pakai surface, light pakai warna note ──
    val screenBg  = if (isDark) MaterialTheme.colors.background
    else currentNote.color.toComposeColor().copy(alpha = 0.25f)

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text("Delete Note?", fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.onSurface)
            },
            text = {
                Text("\"${currentNote.title}\" will be permanently deleted.",
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f))
            },
            confirmButton = {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFE53935))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { viewModel.deleteNoteById(noteId); onNavigateBack() }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) { Text("Delete", color = Color.White, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(surface)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { showDeleteDialog = false }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) { Text("Cancel", color = MaterialTheme.colors.onSurface) }
            },
            backgroundColor = surface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(screenBg)   // ← fix dark mode
            .statusBarsPadding()
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier.size(42.dp).clip(CircleShape)
                    .background(surface.copy(alpha = 0.8f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onNavigateBack() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back",
                    tint = pink, modifier = Modifier.size(20.dp))
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(pink.copy(alpha = 0.15f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(currentNote.category, fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold, color = pink)
                }
                if (currentNote.noteType == NoteType.TODO) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(pink.copy(alpha = 0.85f))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("✓ To-do", fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold, color = Color.White)
                    }
                }
            }

            Box(
                modifier = Modifier.size(42.dp).clip(CircleShape)
                    .background(Color(0xFFFFEBEE))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { showDeleteDialog = true },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Delete",
                    tint = Color(0xFFE53935), modifier = Modifier.size(20.dp))
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            Text(formatNoteDate(currentNote.createdAt), fontSize = 12.sp,
                color = textOn.copy(alpha = 0.5f))

            Spacer(Modifier.height(12.dp))

            Text(currentNote.title, fontSize = 26.sp, fontWeight = FontWeight.Bold,
                color = textOn, lineHeight = 32.sp)

            Spacer(Modifier.height(16.dp))

            Box(modifier = Modifier.fillMaxWidth().height(1.dp)
                .background(pink.copy(alpha = 0.2f)))

            Spacer(Modifier.height(16.dp))

            when (currentNote.noteType) {
                NoteType.NOTE -> {
                    Text(
                        text       = currentNote.content,
                        fontSize   = contentFontSize,
                        fontWeight = contentFontWeight,
                        fontStyle  = contentFontItalic,
                        color      = textOn.copy(alpha = 0.85f),
                        lineHeight = contentFontSize * 1.6f
                    )
                }
                NoteType.TODO -> {
                    val progress = if (currentNote.todoItems.isEmpty()) 0f
                    else currentNote.todoItems.count { it.isDone }.toFloat() /
                            currentNote.todoItems.size

                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Progress", fontSize = 12.sp, color = textOn.copy(alpha = 0.5f))
                            Text(
                                "${currentNote.todoItems.count { it.isDone }}/${currentNote.todoItems.size}",
                                fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = pink
                            )
                        }
                        Spacer(Modifier.height(6.dp))
                        Box(
                            modifier = Modifier.fillMaxWidth().height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(pink.copy(alpha = 0.15f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(progress)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(pink)
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // ── todo items: sekarang recompose saat toggle ────────
                    currentNote.todoItems.forEach { item ->
                        TodoItemRow(
                            item     = item,
                            onToggle = { viewModel.toggleTodoItem(noteId, item.id) },
                            pink     = pink,
                            textOn   = textOn
                        )
                        Spacer(Modifier.height(10.dp))
                    }
                }
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
fun TodoItemRow(
    item: TodoItem,
    onToggle: () -> Unit,
    pink: Color,
    textOn: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (item.isDone) pink.copy(alpha = 0.08f)
                else MaterialTheme.colors.surface.copy(alpha = 0.6f)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onToggle() }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(if (item.isDone) pink else Color.Transparent)
                .then(
                    if (!item.isDone) Modifier.background(
                        MaterialTheme.colors.surface, CircleShape
                    ) else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            if (item.isDone) {
                Text("✓", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
            } else {
                Box(modifier = Modifier.size(22.dp).clip(CircleShape)) {
                    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                        drawCircle(
                            color  = pink.copy(alpha = 0.4f),
                            radius = size.minDimension / 2f,
                            style  = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5.dp.toPx())
                        )
                    }
                }
            }
        }

        Text(
            text           = item.text,
            fontSize       = 14.sp,
            color          = if (item.isDone) textOn.copy(alpha = 0.4f) else textOn.copy(alpha = 0.85f),
            fontWeight     = if (item.isDone) FontWeight.Normal else FontWeight.Medium,
            textDecoration = if (item.isDone)
                androidx.compose.ui.text.style.TextDecoration.LineThrough else null,
            modifier       = Modifier.weight(1f)
        )
    }
}

@Composable
fun InfoChip(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colors.surface.copy(alpha = 0.8f))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(text, fontSize = 11.sp, color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f))
    }
}