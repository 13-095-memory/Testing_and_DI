package com.memory.platform_specificfeatures.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.memory.platform_specificfeatures.data.model.NoteColor
import com.memory.platform_specificfeatures.data.model.NoteType
import com.memory.platform_specificfeatures.data.model.TodoItem
import com.memory.platform_specificfeatures.presentation.NotesViewModel
import com.memory.platform_specificfeatures.ui.components.toComposeColor
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CreateNoteScreen(onNavigateBack: () -> Unit) {
    val viewModel: NotesViewModel = koinViewModel()

    var title             by remember { mutableStateOf("") }
    var content           by remember { mutableStateOf("") }
    var selectedCategory  by remember { mutableStateOf("Personal") }
    var selectedColor     by remember { mutableStateOf(NoteColor.PINK) }
    var selectedFontStyle by remember { mutableStateOf("Normal") }
    var selectedFontSize  by remember { mutableStateOf("Medium") }

    // ── Checklist mode ────────────────────────────────────────
    var isChecklistMode   by remember { mutableStateOf(false) }
    var todoItems         by remember { mutableStateOf(listOf<TodoItem>()) }
    var newItemText       by remember { mutableStateOf("") }
    var nextItemId        by remember { mutableStateOf(1L) }

    val categories = listOf("Personal", "Work", "Study", "Health", "Travel")
    val colors     = NoteColor.entries.toList()

    val contentFontSize   = when (selectedFontSize) {
        "Small" -> 12.sp; "Large" -> 18.sp; else -> 15.sp
    }
    val contentFontWeight = if (selectedFontStyle == "Bold") FontWeight.Bold else FontWeight.Normal
    val contentFontItalic = if (selectedFontStyle == "Italic") FontStyle.Italic else FontStyle.Normal

    val pink    = MaterialTheme.colors.primary
    val bg      = MaterialTheme.colors.background
    val surface = MaterialTheme.colors.surface
    val textOn  = MaterialTheme.colors.onBackground

    // validasi: note valid kalau title tidak kosong
    // checklist mode: minimal 1 item
    val isValid = title.isNotBlank() &&
            (if (isChecklistMode) todoItems.isNotEmpty() else true)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .statusBarsPadding()
    ) {
        // ── Top Bar ───────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(surface)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(pink.copy(alpha = 0.1f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onNavigateBack() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back",
                    tint = pink, modifier = Modifier.size(18.dp))
            }

            Text(
                if (isChecklistMode) "New Checklist ✅" else "New Note ✨",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = textOn
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isValid) pink else pink.copy(alpha = 0.3f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        if (isValid) {
                            if (isChecklistMode) {
                                viewModel.addTodoNote(
                                    title     = title,
                                    category  = selectedCategory,
                                    color     = selectedColor,
                                    todoItems = todoItems
                                )
                            } else {
                                viewModel.addNote(
                                    title     = title,
                                    content   = content,
                                    category  = selectedCategory,
                                    color     = selectedColor,
                                    fontStyle = selectedFontStyle,
                                    fontSize  = selectedFontSize
                                )
                            }
                            onNavigateBack()
                        }
                    }
                    .padding(horizontal = 20.dp, vertical = 9.dp)
            ) {
                Text("Save", color = Color.White,
                    fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(16.dp))

            // ── Title ─────────────────────────────────────────
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = {
                    Text(
                        if (isChecklistMode) "Checklist title..." else "Note title...",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = textOn.copy(alpha = 0.25f)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = textOn
                ),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor   = Color.Transparent
                )
            )

            // ── Checklist Toggle ──────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(surface)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        "Checklist mode",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = textOn
                    )
                    Text(
                        "Convert note to checkable items",
                        fontSize = 11.sp,
                        color = textOn.copy(alpha = 0.45f)
                    )
                }
                Switch(
                    checked = isChecklistMode,
                    onCheckedChange = {
                        isChecklistMode = it
                        // reset konten lainnya saat switch
                        if (it) content = "" else todoItems = listOf()
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor  = pink,
                        checkedTrackColor  = pink.copy(alpha = 0.4f),
                        uncheckedThumbColor = textOn.copy(alpha = 0.3f),
                        uncheckedTrackColor = textOn.copy(alpha = 0.15f)
                    )
                )
            }

            Spacer(Modifier.height(12.dp))

            // ── Content area: Note atau Checklist ─────────────
            AnimatedVisibility(visible = !isChecklistMode, enter = fadeIn(), exit = fadeOut()) {
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    placeholder = {
                        Text(
                            "Start writing your note here...",
                            fontSize = contentFontSize,
                            color = textOn.copy(alpha = 0.25f)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 140.dp),
                    textStyle = TextStyle(
                        fontSize   = contentFontSize,
                        fontWeight = contentFontWeight,
                        fontStyle  = contentFontItalic,
                        color      = textOn
                    ),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor   = Color.Transparent
                    )
                )
            }

            AnimatedVisibility(visible = isChecklistMode, enter = fadeIn(), exit = fadeOut()) {
                Column {
                    // existing todo items
                    todoItems.forEachIndexed { index, item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // bullet
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .border(1.5.dp, pink.copy(alpha = 0.4f), CircleShape)
                            )
                            Text(
                                text = item.text,
                                fontSize = 14.sp,
                                color = textOn.copy(alpha = 0.8f),
                                modifier = Modifier.weight(1f)
                            )
                            // hapus item
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(pink.copy(alpha = 0.1f))
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        todoItems = todoItems.toMutableList()
                                            .also { it.removeAt(index) }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Remove",
                                    tint = pink, modifier = Modifier.size(14.dp))
                            }
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    // input item baru
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = newItemText,
                            onValueChange = { newItemText = it },
                            placeholder = {
                                Text("Add item...", fontSize = 13.sp,
                                    color = textOn.copy(alpha = 0.3f))
                            },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            textStyle = TextStyle(fontSize = 14.sp, color = textOn),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = {
                                if (newItemText.isNotBlank()) {
                                    todoItems = todoItems + TodoItem(
                                        id   = nextItemId++,
                                        text = newItemText.trim()
                                    )
                                    newItemText = ""
                                }
                            }),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                backgroundColor      = surface,
                                unfocusedBorderColor = pink.copy(alpha = 0.2f),
                                focusedBorderColor   = pink
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(pink)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    if (newItemText.isNotBlank()) {
                                        todoItems = todoItems + TodoItem(
                                            id   = nextItemId++,
                                            text = newItemText.trim()
                                        )
                                        newItemText = ""
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add",
                                tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }

            Divider(
                color = pink.copy(alpha = 0.12f),
                modifier = Modifier.padding(top = 12.dp)
            )
            Spacer(Modifier.height(20.dp))

            // ── Section label ─────────────────────────────────
            @Composable
            fun SectionLabel(text: String) {
                Text(text, fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
                    color = pink.copy(alpha = 0.7f), letterSpacing = 1.sp)
                Spacer(Modifier.height(10.dp))
            }

            // ── Category ──────────────────────────────────────
            SectionLabel("CATEGORY")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { category ->
                    val isSelected = selectedCategory == category
                    val emoji = when (category) {
                        "Personal" -> "🌸"; "Work" -> "🎀"
                        "Study" -> "📖"; "Health" -> "🌿"; "Travel" -> "✈️"
                        else -> ""
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) pink else surface)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { selectedCategory = category }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text("$emoji $category", fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (isSelected) Color.White else textOn.copy(alpha = 0.55f))
                    }
                }
            }

            // ── Font options (hanya muncul kalau bukan checklist) ──
            AnimatedVisibility(visible = !isChecklistMode) {
                Column {
                    Spacer(Modifier.height(20.dp))
                    SectionLabel("FONT STYLE")
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        listOf(
                            Triple("Normal", "Aa", FontWeight.Normal to FontStyle.Normal),
                            Triple("Bold",   "Aa", FontWeight.Bold   to FontStyle.Normal),
                            Triple("Italic", "Aa", FontWeight.Normal  to FontStyle.Italic),
                        ).forEach { (label, display, style) ->
                            val isSelected = selectedFontStyle == label
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(if (isSelected) pink else surface)
                                    .border(1.dp, pink.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) { selectedFontStyle = label }
                                    .padding(horizontal = 18.dp, vertical = 10.dp)
                            ) {
                                Text(display, fontWeight = style.first, fontStyle = style.second,
                                    fontSize = 15.sp,
                                    color = if (isSelected) Color.White else textOn.copy(alpha = 0.55f))
                            }
                        }
                    }

                    Spacer(Modifier.height(20.dp))
                    SectionLabel("FONT SIZE")
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                        listOf(Triple("Small", "Aa", 12.sp),
                            Triple("Medium", "Aa", 15.sp),
                            Triple("Large",  "Aa", 19.sp)).forEach { (size, display, fSize) ->
                            val isSelected = selectedFontSize == size
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(if (isSelected) pink else surface)
                                    .border(1.dp, pink.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) { selectedFontSize = size }
                                    .padding(horizontal = 18.dp, vertical = 10.dp)
                            ) {
                                Text(display, fontSize = fSize, fontWeight = FontWeight.SemiBold,
                                    color = if (isSelected) Color.White else textOn.copy(alpha = 0.55f))
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── Note Color ────────────────────────────────────
            SectionLabel("NOTE COLOR")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(colors) { color ->
                    val isSelected = selectedColor == color
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(color.toComposeColor())
                            .border(
                                width = if (isSelected) 3.dp else 1.5.dp,
                                color = if (isSelected) pink else pink.copy(alpha = 0.2f),
                                shape = CircleShape
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { selectedColor = color }
                    ) {
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(pink)
                                    .align(Alignment.Center)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}