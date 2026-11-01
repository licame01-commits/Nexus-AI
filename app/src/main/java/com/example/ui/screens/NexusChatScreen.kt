package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.ChatMessageEntity
import com.example.data.ChatSessionEntity
import com.example.ui.ChatStreamState
import com.example.ui.theme.ChatGptBackground
import com.example.ui.theme.ChatGptChipBg
import com.example.ui.theme.ChatGptDeleteRed
import com.example.ui.theme.ChatGptInputContainer
import com.example.ui.theme.ChatGptSendButtonBg
import com.example.ui.theme.ChatGptSendButtonDisabled
import com.example.ui.theme.ChatGptSendButtonIcon
import com.example.ui.theme.ChatGptTextPrimary
import com.example.ui.theme.ChatGptTextSecondary
import com.example.ui.theme.ChatGptUserBubble

@Composable
fun NexusChatScreen(
    currentSession: ChatSessionEntity?,
    messages: List<ChatMessageEntity>,
    streamState: ChatStreamState,
    selectedSubject: String,
    educationLevel: String,
    onSendMessage: (String, Uri?) -> Unit,
    modifier: Modifier = Modifier
) {
    var inputText by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val listState = rememberLazyListState()

    // Android Zero-Permission Photo Picker
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        selectedImageUri = uri
    }

    val suggestions = remember(selectedSubject) {
        when (selectedSubject) {
            "Matemáticas" -> listOf(
                "Sube una foto de tu ejercicio de matemáticas y lo resuelvo paso a paso",
                "¿Cómo resolver 2x + 5 = 15?",
                "Explícame el teorema de Pitágoras con un ejemplo",
                "Ejercicios de fracciones y álgebra explicados"
            )
            "Robótica" -> listOf(
                "Sube foto de tu circuito o diagrama de robot y te ayudo a programarlo",
                "¿Cómo programar un robot seguidor de línea en Arduino?",
                "Diferencia entre servomotor y motor DC",
                "¿Cómo conectar un sensor ultrasónico HC-SR04?"
            )
            "Computación" -> listOf(
                "Sube captura de pantalla de tu código y encuentro el error",
                "Explícame qué es una función recursiva en Python",
                "¿Cómo funciona la búsqueda binaria?",
                "Diferencia entre frontend y backend"
            )
            "Lenguaje" -> listOf(
                "Sube foto de tu hoja de texto o redacción y la reviso",
                "Reglas de acentuación: agudas, graves y esdrújulas",
                "¿Cómo redactar una introducción y conclusión?",
                "¿Qué son las figuras retóricas y metáforas?"
            )
            "Historia" -> listOf(
                "Causas y consecuencias de la Segunda Guerra Mundial",
                "Impacto de la Revolución Industrial",
                "Línea de tiempo de la independencia latinoamericana",
                "¿Cómo vivían las civilizaciones antiguas?"
            )
            "Ciencias Naturales" -> listOf(
                "Sube foto del diagrama biológico o problema de física",
                "¿Cómo funciona la fotosíntesis paso a paso?",
                "Las 3 leyes de Newton explicadas con ejemplos",
                "Estructura del átomo: protones y electrones"
            )
            else -> listOf(
                "📸 Sube foto de tu tarea o cuaderno y la resuelvo paso a paso",
                "¿Cómo resolver un problema de matemáticas paso a paso?",
                "Explícame cómo funciona un circuito de robótica",
                "Consejos de redacción y comprensión lectora"
            )
        }
    }

    // Auto-scroll when messages update
    LaunchedEffect(messages.size, streamState) {
        val total = messages.size + if (streamState is ChatStreamState.Streaming) 1 else 0
        if (total > 0) {
            listState.animateScrollToItem(total - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ChatGptBackground)
            .testTag("educational_chat_screen")
    ) {
        // Messages List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Welcome screen when empty (ChatGPT style)
            if (messages.isEmpty() && streamState !is ChatStreamState.Streaming) {
                item {
                    ChatGptEmptyState(
                        educationLevel = educationLevel,
                        suggestions = suggestions,
                        onSelectSuggestion = { onSendMessage(it, null) }
                    )
                }
            }

            // Message Bubbles
            items(messages, key = { it.id }) { message ->
                ChatGptMessageRow(
                    sender = message.sender,
                    content = message.content,
                    imageUri = message.imageUri
                )
            }

            // Streaming active bubble
            if (streamState is ChatStreamState.Streaming) {
                item {
                    ChatGptMessageRow(
                        sender = "NEXUS",
                        content = streamState.partialResponse,
                        isStreaming = true
                    )
                }
            }

            // Error notice
            if (streamState is ChatStreamState.Error) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = ChatGptDeleteRed.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = streamState.message,
                            style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                            color = ChatGptDeleteRed,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
        }

        // Preview of attached image before sending
        AnimatedVisibility(
            visible = selectedImageUri != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            selectedImageUri?.let { uri ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .testTag("attached_image_preview_box")
                ) {
                    AsyncImage(
                        model = uri,
                        contentDescription = "Imagen de la tarea",
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                    IconButton(
                        onClick = { selectedImageUri = null },
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.TopEnd)
                            .background(Color.Black.copy(alpha = 0.7f), CircleShape)
                            .testTag("remove_attached_image_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Quitar foto",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }

        // ChatGPT Style Floating Input Bar with Photo Picker
        Surface(
            color = ChatGptBackground,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(26.dp))
                    .background(ChatGptInputContainer)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Photo Picker Attachment Button
                IconButton(
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    modifier = Modifier
                        .size(38.dp)
                        .testTag("attach_image_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.AddPhotoAlternate,
                        contentDescription = "Adjuntar foto de la tarea",
                        tint = if (selectedImageUri != null) ChatGptTextPrimary else ChatGptTextSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = {
                        Text(
                            text = if (selectedImageUri != null) "Añade una instrucción (opcional)..." else "Mensaje para Nexus...",
                            color = ChatGptTextSecondary,
                            fontSize = 15.sp
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input_field"),
                    maxLines = 5,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = ChatGptTextPrimary,
                        unfocusedTextColor = ChatGptTextPrimary,
                        cursorColor = ChatGptTextPrimary
                    )
                )

                Spacer(modifier = Modifier.width(4.dp))

                val isGenerating = streamState is ChatStreamState.Streaming
                val canSend = (inputText.isNotBlank() || selectedImageUri != null) && !isGenerating

                // Circular send button
                IconButton(
                    onClick = {
                        if (canSend) {
                            onSendMessage(inputText, selectedImageUri)
                            inputText = ""
                            selectedImageUri = null
                        }
                    },
                    enabled = canSend,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (canSend) ChatGptSendButtonBg else ChatGptSendButtonDisabled)
                        .testTag("send_chat_button")
                ) {
                    if (isGenerating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = ChatGptTextPrimary
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.ArrowUpward,
                            contentDescription = "Enviar",
                            tint = if (canSend) ChatGptSendButtonIcon else ChatGptTextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatGptEmptyState(
    educationLevel: String,
    suggestions: List<String>,
    onSelectSuggestion: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp, bottom = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Minimalist Icon
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(ChatGptInputContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = ChatGptTextPrimary,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "¿En qué te puedo ayudar hoy?",
            style = androidx.compose.material3.MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = ChatGptTextPrimary
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Escribe una duda o sube foto de tu tarea (Nivel: $educationLevel)",
            style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
            color = ChatGptTextSecondary
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Suggestion pills
        suggestions.take(4).forEach { suggestion ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { onSelectSuggestion(suggestion) }
                    .testTag("chatgpt_suggestion_card"),
                shape = RoundedCornerShape(16.dp),
                color = ChatGptChipBg
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = suggestion,
                        style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                        color = ChatGptTextPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = ChatGptTextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ChatGptMessageRow(
    sender: String,
    content: String,
    imageUri: String? = null,
    isStreaming: Boolean = false,
    modifier: Modifier = Modifier
) {
    val isUser = sender == "USER"

    Row(
        modifier = modifier
            .fillMaxWidth()
            .testTag(if (isUser) "user_message_row" else "assistant_message_row"),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(ChatGptInputContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "Nexus",
                    tint = ChatGptTextPrimary,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
        }

        if (isUser) {
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.widthIn(max = 300.dp)
            ) {
                // If an image was attached by the user, show it cleanly
                if (!imageUri.isNullOrBlank()) {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = "Foto adjunta",
                        modifier = Modifier
                            .widthIn(max = 240.dp)
                            .height(180.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .testTag("user_attached_image"),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = ChatGptUserBubble
                ) {
                    Text(
                        text = content,
                        style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                        color = ChatGptTextPrimary,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        lineHeight = 22.sp
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 2.dp)
            ) {
                Text(
                    text = content.ifBlank { if (isStreaming) "● Analizando..." else "" },
                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                    color = ChatGptTextPrimary,
                    lineHeight = 23.sp
                )

                if (isStreaming && content.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "● Escribiendo...",
                        style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                        color = ChatGptTextSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}
