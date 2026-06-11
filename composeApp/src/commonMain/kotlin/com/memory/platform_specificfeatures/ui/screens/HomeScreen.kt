package com.memory.platform_specificfeatures.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Canvas
import com.memory.platform_specificfeatures.presentation.NotesViewModel
import com.memory.platform_specificfeatures.presentation.SettingsViewModel
import com.memory.platform_specificfeatures.ui.components.NetworkStatusIndicator
import com.memory.platform_specificfeatures.ui.components.NoteCard
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import androidx.compose.material.icons.filled.AutoAwesome

@Composable
fun HomeScreen(
    onNavigateToCreate: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToRead: (Long) -> Unit,
    onNavigateToChat: () -> Unit = {}
) {
    val viewModel: NotesViewModel = koinViewModel()
    val settingsViewModel: SettingsViewModel = koinViewModel()

    val isConnected      by viewModel.isConnected.collectAsState()
    val searchQuery      by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val filteredNotes    by viewModel.filteredNotes.collectAsState()
    val showDate         by settingsViewModel.showDate.collectAsState()

    val bg       = MaterialTheme.colors.background
    val surface  = MaterialTheme.colors.surface
    val pink     = MaterialTheme.colors.primary
    val pinkMid  = MaterialTheme.colors.primaryVariant
    val textDark = MaterialTheme.colors.onBackground
    val textMid  = MaterialTheme.colors.onBackground.copy(alpha = 0.5f)

    val categories = listOf("All", "Personal", "Work", "Study", "Health")

    Scaffold(
        backgroundColor = bg,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreate,
                backgroundColor = pink,
                shape = CircleShape,
                modifier = Modifier.testTag("add_note_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(bg)
                .statusBarsPadding()
                .padding(padding)
        ) {
            NetworkStatusIndicator(isConnected = isConnected)

            HeroCalendarIllustration(
                primaryColor  = pink,
                surfaceColor  = surface,
                accentColor   = pinkMid
            )

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "RyNotes",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = textDark
                        )
                        Text(
                            text = "Make your day be more productive",
                            fontSize = 12.sp,
                            color = textMid
                        )
                    }

                    // ── Tombol AI Chat + Settings ─────────────
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Tombol AI Chat
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(pink.copy(alpha = 0.15f))
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) { onNavigateToChat() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.AutoAwesome,
                                contentDescription = "AI Chat",
                                tint = pink,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Tombol Settings
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(pink.copy(alpha = 0.15f))
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) { onNavigateToSettings() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = pink,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    placeholder = {
                        Text("Search notes...", color = textMid, fontSize = 13.sp)
                    },
                    modifier = Modifier.fillMaxWidth().testTag("search_text_field"),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = pink)
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        backgroundColor = surface,
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = pink
                    )
                )

                Spacer(Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    categories.forEach { category ->
                        val isSelected = selectedCategory == category
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) pink else surface)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) { viewModel.selectCategory(category) }
                                .padding(horizontal = 14.dp, vertical = 7.dp)
                                .testTag("category_chip_$category")
                        ) {
                            Text(
                                text = category,
                                color = if (isSelected) Color.White else textMid,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))

                if (filteredNotes.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize().testTag("empty_notes_view"),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🌸", fontSize = 40.sp)
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "No notes yet!",
                                color = textMid,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "Tap + to add your first note",
                                color = textMid.copy(alpha = 0.6f),
                                fontSize = 12.sp
                            )
                        }
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 100.dp)
                    ) {
                        items(filteredNotes) { note ->
                            Box(modifier = Modifier.testTag("note_card_${note.id}")) {
                                NoteCard(
                                    note = note,
                                    onClick = { onNavigateToRead(note.id) },
                                    showDate = showDate
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HeroCalendarIllustration(
    primaryColor: Color,
    surfaceColor: Color,
    accentColor: Color
) {
    val pinkLight = primaryColor.copy(alpha = 0.4f)
    val pinkPale  = primaryColor.copy(alpha = 0.15f)

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        val w = size.width
        val h = size.height
        val r = 20.dp.toPx()

        drawRoundRect(color = surfaceColor, size = Size(w, h), cornerRadius = CornerRadius(r))
        drawRoundRect(color = primaryColor, size = Size(w, 40.dp.toPx()), cornerRadius = CornerRadius(r))
        drawRect(color = primaryColor, topLeft = Offset(0f, 20.dp.toPx()), size = Size(w, 20.dp.toPx()))

        listOf(w * 0.3f, w * 0.7f).forEach { cx ->
            drawCircle(color = pinkPale, radius = 8.dp.toPx(), center = Offset(cx, 40.dp.toPx()))
            drawCircle(color = surfaceColor, radius = 5.dp.toPx(), center = Offset(cx, 40.dp.toPx()), style = Stroke(width = 2.dp.toPx()))
        }

        val gridTop = 52.dp.toPx()
        val cellW   = w / 7f
        val cellH   = (h - gridTop - 8.dp.toPx()) / 4f

        (1..28).forEachIndexed { index, day ->
            val col = index % 7
            val row = index / 7
            val cx  = cellW * col + cellW / 2f
            val cy  = gridTop + cellH * row + cellH / 2f
            when {
                day == 2 -> drawCircle(color = primaryColor, radius = 13.dp.toPx(), center = Offset(cx, cy))
                day < 2  -> drawCircle(color = pinkLight, radius = 3.dp.toPx(), center = Offset(cx, cy))
                else     -> drawCircle(color = pinkPale, radius = 3.dp.toPx(), center = Offset(cx, cy))
            }
        }

        listOf(Offset(28.dp.toPx(), h - 22.dp.toPx()), Offset(w - 28.dp.toPx(), h - 22.dp.toPx()))
            .forEach { center ->
                for (i in 0..4) {
                    val angle = (i * 72f) * (PI / 180f).toFloat()
                    drawCircle(color = pinkLight, radius = 5.dp.toPx(), center = Offset(center.x + cos(angle) * 9.dp.toPx(), center.y + sin(angle) * 9.dp.toPx()))
                }
                drawCircle(color = surfaceColor, radius = 4.dp.toPx(), center = center)
            }

        listOf(0.5f, 0.15f, 0.85f).forEach { xRatio ->
            drawCircle(color = primaryColor.copy(alpha = 0.25f), radius = 3.dp.toPx(), center = Offset(w * xRatio, h - 14.dp.toPx()))
        }
    }
}