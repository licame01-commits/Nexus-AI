package com.example.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.NexusAiService
import com.example.data.ChatMessageEntity
import com.example.data.ChatSessionEntity
import com.example.data.NexusDatabase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val TAG = "NexusViewModel"

sealed interface ChatStreamState {
    data object Idle : ChatStreamState
    data class Streaming(val partialResponse: String) : ChatStreamState
    data class Error(val message: String) : ChatStreamState
}

class NexusViewModel(application: Application) : AndroidViewModel(application) {

    private val db = NexusDatabase.getDatabase(application)
    private val chatDao = db.chatSessionDao()
    private val aiService = NexusAiService()

    // All saved sessions (Chats Recientes)
    val allSessions: StateFlow<List<ChatSessionEntity>> = chatDao.getAllSessions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active session ID
    private val _currentSessionId = MutableStateFlow<Long?>(null)
    val currentSessionId: StateFlow<Long?> = _currentSessionId.asStateFlow()

    // Active session details
    private val _currentSession = MutableStateFlow<ChatSessionEntity?>(null)
    val currentSession: StateFlow<ChatSessionEntity?> = _currentSession.asStateFlow()

    // Selected pedagogical level and subject
    private val _educationLevel = MutableStateFlow("Colegio / Secundaria")
    val educationLevel: StateFlow<String> = _educationLevel.asStateFlow()

    private val _selectedSubject = MutableStateFlow("Todas")
    val selectedSubject: StateFlow<String> = _selectedSubject.asStateFlow()

    // Search query for recent chats
    private val _recentChatsQuery = MutableStateFlow("")
    val recentChatsQuery: StateFlow<String> = _recentChatsQuery.asStateFlow()

    // Filtered recent sessions
    val filteredSessions: StateFlow<List<ChatSessionEntity>> = combine(
        allSessions,
        _recentChatsQuery
    ) { sessions, query ->
        if (query.isBlank()) {
            sessions
        } else {
            sessions.filter {
                it.title.contains(query, ignoreCase = true) ||
                    it.subject.contains(query, ignoreCase = true) ||
                    it.previewSnippet.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Current messages for active session
    @OptIn(ExperimentalCoroutinesApi::class)
    val currentMessages: StateFlow<List<ChatMessageEntity>> = _currentSessionId
        .flatMapLatest { sessionId ->
            if (sessionId != null) {
                chatDao.getMessagesForSession(sessionId)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _streamState = MutableStateFlow<ChatStreamState>(ChatStreamState.Idle)
    val streamState: StateFlow<ChatStreamState> = _streamState.asStateFlow()

    init {
        seedInitialSessions()
    }

    private fun seedInitialSessions() {
        viewModelScope.launch {
            val count = chatDao.getSessionCount()
            if (count == 0) {
                // Seed 1: Robótica y Arduino
                val session1Id = chatDao.insertSession(
                    ChatSessionEntity(
                        title = "Robótica con Arduino: Sensores Ultrasónicos",
                        subject = "Robótica",
                        educationLevel = "Colegio / Secundaria",
                        previewSnippet = "¿Cómo conectar un sensor HC-SR04 y programar un robot esquivador?",
                        updatedAt = System.currentTimeMillis() - 3600_000
                    )
                )
                chatDao.insertMessage(
                    ChatMessageEntity(
                        sessionId = session1Id,
                        sender = "USER",
                        content = "¿Cómo funciona un sensor ultrasónico HC-SR04 en Arduino y cómo programar la distancia para que el robot esquive obstáculos?",
                        timestamp = System.currentTimeMillis() - 3590_000
                    )
                )
                chatDao.insertMessage(
                    ChatMessageEntity(
                        sessionId = session1Id,
                        sender = "NEXUS",
                        content = "¡Excelente proyecto de robótica! El sensor **HC-SR04** funciona como un ecolocalizador: emite un pulso de ultrasonido (Trigger) y mide el tiempo que tarda en rebotar y volver (Echo).\n\n### 1. Conexiones Básicas:\n- **VCC:** 5V\n- **GND:** Tierra (GND)\n- **Trig:** Pin digital 9\n- **Echo:** Pin digital 10\n\n### 2. Fórmula de Distancia:\nDistancia (cm) = (Tiempo en µs × 0.0343) / 2\n\n### 3. Lógica para Esquivar:\nSi la distancia leída es menor a 20 cm, detén los servomotores y haz girar una rueda para cambiar de dirección.",
                        timestamp = System.currentTimeMillis() - 3580_000
                    )
                )

                // Seed 2: Matemáticas
                val session2Id = chatDao.insertSession(
                    ChatSessionEntity(
                        title = "Resolución de Ecuaciones Cuadráticas",
                        subject = "Matemáticas",
                        educationLevel = "Colegio / Secundaria",
                        previewSnippet = "Explicación paso a paso de la fórmula general con ejemplos prácticos.",
                        updatedAt = System.currentTimeMillis() - 7200_000
                    )
                )
                chatDao.insertMessage(
                    ChatMessageEntity(
                        sessionId = session2Id,
                        sender = "USER",
                        content = "¿Cómo resolver la ecuación 2x² + 5x - 3 = 0 usando la fórmula general?",
                        timestamp = System.currentTimeMillis() - 7190_000
                    )
                )
                chatDao.insertMessage(
                    ChatMessageEntity(
                        sessionId = session2Id,
                        sender = "NEXUS",
                        content = "Vamos a resolverla paso a paso aplicando la fórmula cuadrática general:\n\nx = (-b ± √(b² - 4ac)) / (2a)\n\n1. **Identificamos los coeficientes:**\n   - a = 2\n   - b = 5\n   - c = -3\n\n2. **Calculamos el discriminante:**\n   b² - 4ac = 5² - 4(2)(-3) = 25 + 24 = 49\n   Como √49 = 7, tendremos dos soluciones reales.\n\n3. **Hallamos las raíces:**\n   - x₁ = (-5 + 7) / 4 = 2/4 = 0.5\n   - x₂ = (-5 - 7) / 4 = -12/4 = -3\n\n**Solución:** Las soluciones son **x = 0.5** y **x = -3**.",
                        timestamp = System.currentTimeMillis() - 7180_000
                    )
                )

                // Seed 3: Animación a la lectura y ECA
                val session3Id = chatDao.insertSession(
                    ChatSessionEntity(
                        title = "El Principito: Análisis de Metáforas",
                        subject = "Animación a la Lectura",
                        educationLevel = "Escuela / Niños",
                        previewSnippet = "¿Qué significa la frase 'Lo esencial es invisible a los ojos'?",
                        updatedAt = System.currentTimeMillis() - 10800_000
                    )
                )
                chatDao.insertMessage(
                    ChatMessageEntity(
                        sessionId = session3Id,
                        sender = "USER",
                        content = "¿Qué enseña el zorro a El Principito y por qué es tan especial?",
                        timestamp = System.currentTimeMillis() - 10790_000
                    )
                )
                chatDao.insertMessage(
                    ChatMessageEntity(
                        sessionId = session3Id,
                        sender = "NEXUS",
                        content = "🌟 ¡Una de las partes más hermosas de la literatura universal!\n\nEl zorro le enseña a El Principito el verdadero significado de **'domesticar'**, que significa crear lazos de amistad y afecto.\n\n### Tres lecciones mágicas del zorro:\n1. **Crear lazos:** Cuando conoces a alguien y te preocupas por él, se vuelve único en el mundo para ti.\n2. **El tiempo que le dedicas:** Tu rosa es importante por el tiempo que has pasado cuidándola.\n3. **El secreto:** *'Solo con el corazón se puede ver bien; lo esencial es invisible para los ojos'*. Las cosas más valiosas, como el amor, la bondad y la amistad, no se pueden tocar, pero se sienten profundamente.",
                        timestamp = System.currentTimeMillis() - 10780_000
                    )
                )

                // Set latest session as active by default
                _currentSessionId.value = session1Id
                _currentSession.value = chatDao.getLatestSession()
            } else {
                val latest = chatDao.getLatestSession()
                _currentSessionId.value = latest?.id
                _currentSession.value = latest
            }
        }
    }

    fun setEducationLevel(level: String) {
        _educationLevel.value = level
    }

    fun setSelectedSubject(subject: String) {
        _selectedSubject.value = subject
    }

    fun setRecentChatsQuery(query: String) {
        _recentChatsQuery.value = query
    }

    fun selectSession(sessionId: Long) {
        viewModelScope.launch {
            _currentSessionId.value = sessionId
            val sessions = allSessions.value
            _currentSession.value = sessions.find { it.id == sessionId }
        }
    }

    fun startNewChat() {
        viewModelScope.launch {
            val newSession = ChatSessionEntity(
                title = "Nueva Consulta Académica",
                subject = if (_selectedSubject.value != "Todas") _selectedSubject.value else "General",
                educationLevel = _educationLevel.value,
                previewSnippet = "Consulta lista para iniciar.",
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            val newId = chatDao.insertSession(newSession)
            _currentSessionId.value = newId
            _currentSession.value = newSession.copy(id = newId)
        }
    }

    fun deleteSession(sessionId: Long) {
        viewModelScope.launch {
            chatDao.deleteSessionById(sessionId)
            if (_currentSessionId.value == sessionId) {
                val remaining = chatDao.getLatestSession()
                _currentSessionId.value = remaining?.id
                _currentSession.value = remaining
            }
        }
    }

    fun clearAllSessions() {
        viewModelScope.launch {
            chatDao.clearAllSessions()
            startNewChat()
        }
    }

    fun sendUserMessage(messageText: String, imageUri: android.net.Uri? = null) {
        val trimmed = messageText.trim()
        if (trimmed.isBlank() && imageUri == null) return

        // 1. Fetch previous history strictly BEFORE saving new message to avoid turn collisions
        val history = currentMessages.value
            .filter { it.content != trimmed }
            .map { it.sender to it.content }

        val displayPrompt = trimmed.ifBlank { "Resolución de tarea adjunta en imagen" }

        viewModelScope.launch {
            var activeSessionId = _currentSessionId.value

            // If no active session, create one
            if (activeSessionId == null) {
                val createdSession = ChatSessionEntity(
                    title = displayPrompt.take(28) + if (displayPrompt.length > 28) "..." else "",
                    subject = if (_selectedSubject.value != "Todas") _selectedSubject.value else "General",
                    educationLevel = _educationLevel.value,
                    previewSnippet = displayPrompt.take(50),
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
                activeSessionId = chatDao.insertSession(createdSession)
                _currentSessionId.value = activeSessionId
                _currentSession.value = createdSession.copy(id = activeSessionId)
            }

            // Save user message to database immediately
            val userMsg = ChatMessageEntity(
                sessionId = activeSessionId,
                sender = "USER",
                content = displayPrompt,
                imageUri = imageUri?.toString(),
                timestamp = System.currentTimeMillis()
            )
            chatDao.insertMessage(userMsg)

            // Update session snippet & timestamp
            val session = _currentSession.value
            if (session != null) {
                chatDao.updateSession(
                    session.copy(
                        previewSnippet = displayPrompt.take(60),
                        updatedAt = System.currentTimeMillis()
                    )
                )
            }

            // Decode scaled bitmap safely in background
            val bitmap = imageUri?.let {
                com.example.util.ImageUtils.uriToScaledBitmap(getApplication(), it)
            }

            // 2. Stream AI Response with ultra-fast latency
            _streamState.value = ChatStreamState.Streaming("")
            val accumulated = StringBuilder()

            try {
                aiService.streamChat(
                    history = history,
                    userMessage = trimmed,
                    imageBitmap = bitmap,
                    educationLevel = _educationLevel.value,
                    subject = if (_selectedSubject.value != "Todas") _selectedSubject.value else session?.subject ?: "General"
                ).collect { chunk ->
                    accumulated.append(chunk)
                    _streamState.value = ChatStreamState.Streaming(accumulated.toString())
                }

                val aiContent = accumulated.toString()

                // 3. Save AI Response in Database
                val aiMsg = ChatMessageEntity(
                    sessionId = activeSessionId,
                    sender = "NEXUS",
                    content = aiContent,
                    timestamp = System.currentTimeMillis()
                )
                chatDao.insertMessage(aiMsg)
                _streamState.value = ChatStreamState.Idle

                // 4. Generate title in background without blocking chat
                if (session?.title == "Nueva Consulta Académica" || history.isEmpty()) {
                    launch {
                        val smartTitle = aiService.generateSessionTitle(displayPrompt)
                        chatDao.updateSession(
                            session?.copy(
                                title = smartTitle,
                                previewSnippet = displayPrompt.take(60),
                                updatedAt = System.currentTimeMillis()
                            ) ?: ChatSessionEntity(
                                id = activeSessionId,
                                title = smartTitle,
                                previewSnippet = displayPrompt.take(60)
                            )
                        )
                        _currentSession.value = _currentSession.value?.copy(title = smartTitle)
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Chat failed", e)
                _streamState.value = ChatStreamState.Error(
                    e.localizedMessage ?: "Nexus no pudo procesar la respuesta."
                )
            }
        }
    }
}
