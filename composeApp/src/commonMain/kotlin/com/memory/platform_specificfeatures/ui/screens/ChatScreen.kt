package com.memory.platform_specificfeatures.ui.screens

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.memory.platform_specificfeatures.presentation.ChatMessage
import com.memory.platform_specificfeatures.presentation.ChatViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ChatScreen(onNavigateBack: () -> Unit) {
    val viewModel: ChatViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }

    val pink    = MaterialTheme.colors.primary
    val bg      = MaterialTheme.colors.background
    val surface = MaterialTheme.colors.surface
    val textOn  = MaterialTheme.colors.onBackground

    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { errMsg ->
            snackbarHostState.showSnackbar(errMsg)
            viewModel.clearError()
        }
    }

    Scaffold(
        backgroundColor = bg,
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    backgroundColor = Color(0xFFB00020),
                    contentColor = Color.White,
                    shape = RoundedCornerShape(12.dp)
                )
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
            // ── Top Bar ───────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(surface)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onNavigateBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Kembali",
                        tint = pink,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "RyNotes AI",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = textOn
                    )
                    Text(
                        "Asisten catatan pintarmu",
                        fontSize = 11.sp,
                        color = textOn.copy(alpha = 0.45f)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(surface)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { viewModel.clearChat() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Hapus percakapan",
                        tint = textOn.copy(alpha = 0.4f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Divider(color = textOn.copy(alpha = 0.07f), thickness = 1.dp)

            // ── Messages ──────────────────────────────────────
            Box(modifier = Modifier.weight(1f)) {
                if (uiState.messages.isEmpty() && !uiState.isLoading) {
                    EmptyChatPlaceholder(pink = pink, textOn = textOn, surface = surface)
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.messages) { message ->
                            ChatBubbleItem(
                                message = message,
                                pink = pink,
                                surface = surface,
                                textOn = textOn
                            )
                        }
                        if (uiState.isLoading) {
                            item {
                                AiTypingIndicator(
                                    pink = pink,
                                    surface = surface
                                )
                            }
                        }
                    }
                }
            }

            // ── Input Field ───────────────────────────────────
            Divider(color = textOn.copy(alpha = 0.07f), thickness = 1.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(surface)
                    .padding(horizontal = 12.dp, vertical = 10.dp)
                    .navigationBarsPadding()
                    .imePadding(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = {
                        Text(
                            "Tanya sesuatu...",
                            color = textOn.copy(alpha = 0.35f),
                            fontSize = 14.sp
                        )
                    },
                    shape = RoundedCornerShape(24.dp),
                    maxLines = 4,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if (inputText.isNotBlank()) {
                                viewModel.sendMessage(inputText.trim())
                                inputText = ""
                            }
                        }
                    ),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        backgroundColor = bg,
                        unfocusedBorderColor = textOn.copy(alpha = 0.12f),
                        focusedBorderColor = pink
                    )
                )

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            if (inputText.isNotBlank() && !uiState.isLoading) pink
                            else pink.copy(alpha = 0.3f)
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            enabled = inputText.isNotBlank() && !uiState.isLoading
                        ) {
                            viewModel.sendMessage(inputText.trim())
                            inputText = ""
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Kirim",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

// ── Chat Bubble ───────────────────────────────────────────────

@Composable
private fun ChatBubbleItem(
    message: ChatMessage,
    pink: Color,
    surface: Color,
    textOn: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!message.isUser) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(pink.copy(alpha = 0.12f))
                    .align(Alignment.Bottom),
                contentAlignment = Alignment.Center
            ) {
                Text("N", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = pink)
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp, topEnd = 16.dp,
                bottomStart = if (message.isUser) 16.dp else 4.dp,
                bottomEnd = if (message.isUser) 4.dp else 16.dp
            ),
            color = if (message.isUser) pink else surface,
            elevation = 0.dp,
            modifier = Modifier.widthIn(max = 260.dp)
        ) {
            Text(
                text = message.text,
                color = if (message.isUser) Color.White else textOn.copy(alpha = 0.85f),
                fontSize = 14.sp,
                lineHeight = 21.sp,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
            )
        }

        if (message.isUser) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(pink.copy(alpha = 0.15f))
                    .align(Alignment.Bottom),
                contentAlignment = Alignment.Center
            ) {
                Text("R", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = pink)
            }
        }
    }
}

// ── Typing Indicator ──────────────────────────────────────────

@Composable
private fun AiTypingIndicator(
    pink: Color,
    surface: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(pink.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Text("AI", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = pink)
        }

        Spacer(modifier = Modifier.width(8.dp))

        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp, topEnd = 16.dp,
                bottomEnd = 16.dp, bottomStart = 4.dp
            ),
            color = surface,
            elevation = 0.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(3) { index ->
                    val infiniteTransition = rememberInfiniteTransition(label = "dot_$index")
                    val alpha by infiniteTransition.animateFloat(
                        initialValue = 0.25f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(500),
                            repeatMode = RepeatMode.Reverse,
                            initialStartOffset = StartOffset(index * 150)
                        ),
                        label = "dot_alpha_$index"
                    )
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .alpha(alpha)
                            .clip(CircleShape)
                            .background(pink)
                    )
                }
            }
        }
    }
}

// ── Empty State ───────────────────────────────────────────────

@Composable
private fun EmptyChatPlaceholder(pink: Color, textOn: Color, surface: Color) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(pink.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "📝",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = pink
            )
        }
        Spacer(Modifier.height(6.dp))

        Text(
            "Asisten catatan pintarmu.\nTanya apa saja untuk memulai.",
            fontSize = 13.sp,
            color = textOn.copy(alpha = 0.45f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            lineHeight = 20.sp
        )

        Spacer(Modifier.height(28.dp))

        val suggestions = listOf(
            "Berikan ide catatan hari ini",
            "Bantu aku buat outline",
            "Tips agar lebih produktif"
        )

        suggestions.forEach { suggestion ->
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = surface,
                elevation = 0.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = suggestion,
                    fontSize = 13.sp,
                    color = textOn.copy(alpha = 0.6f),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }
        }
    }
}