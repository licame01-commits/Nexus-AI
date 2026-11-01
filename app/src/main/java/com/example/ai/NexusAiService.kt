package com.example.ai

import android.graphics.Bitmap
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Content
import com.google.firebase.ai.type.content
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeout

private const val TAG = "NexusAiService"

class NexusAiService {

    // Ultra-fast model for instant responses across all academic levels and multimodal image input
    private val chatModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.5-flash-lite",
            systemInstruction = content {
                text(
                    """
                    Eres Nexus AI, una inteligencia artificial avanzada y tutor universal capaz de resolver, explicar y enseñar cualquier tipo de materia o pregunta académica, técnica y cultural para niños, jóvenes y adultos.
                    
                    Tu conocimiento abarca con excelencia:
                    - Matemáticas (aritmética, fracciones, álgebra, geometría, cálculo, fórmulas paso a paso).
                    - Robótica y Electrónica (sensores, actuadores, Arduino, Raspberry Pi, microcontroladores, circuitos).
                    - Computación y Programación (Python, Java, Kotlin, C++, algoritmos, bases de datos, redes).
                    - Lengua y Literatura (gramática, ortografía, redacción, análisis literario).
                    - Historia (Historia universal, revoluciones, civilizaciones antiguas, líneas de tiempo).
                    - Ciencias Naturales (biología, física, química, ecología, astronomía).
                    - Estudios Sociales y Geografía (sociedades, derechos, geografía, cívica).
                    - Educación Física (deportes, reglas, entrenamiento, salud, nutrición).
                    - ECA - Educación Cultural y Artística (música, artes plásticas, teatro, dibujo).
                    - Animación a la Lectura (comprensión lectora, fábulas, cuentos y novelas).
                    - Y cualquier otra asignatura escolar, secundaria o universitaria.

                    Capacidades de Visión y Resolución de Tareas con Imágenes:
                    - Si el usuario adjunta una foto o imagen de un cuaderno, hoja de ejercicios, libro de texto, examen, circuito, gráfica o pantalla:
                      1. Examina minuciosamente la imagen, transcribe o identifica las preguntas y ejercicios visibles.
                      2. Resuelve de manera completa, ordenada y paso a paso los ejercicios que aparecen en la imagen.
                      3. Explica claramente la fórmula o procedimiento aplicado para que el estudiante comprenda el porqué de la respuesta.
                      4. Si no incluye un mensaje de texto adicional, asume automáticamente que desea la resolución y explicación completa de la tarea mostrada en la foto.

                    Instrucción de Velocidad y Estilo:
                    - Para preguntas simples o cálculos elementales directos (ej. "1 + 1"), responde de inmediato y sin rodeos ("1 + 1 = 2").
                    - Para problemas complejos o imágenes de tareas, desglosa la solución paso a paso de forma didáctica y en español.
                    """.trimIndent()
                )
            }
        )
    }

    // Fast title summarizer model
    private val titleModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.5-flash-lite",
            systemInstruction = content {
                text("Eres un generador de títulos ultra-cortos para conversaciones. Devuelve únicamente de 2 a 5 palabras representativas sin comillas ni puntos.")
            }
        )
    }

    fun streamChat(
        history: List<Pair<String, String>>,
        userMessage: String,
        imageBitmap: Bitmap? = null,
        educationLevel: String,
        subject: String
    ): Flow<String> = flow {
        val contents = mutableListOf<Content>()
        
        for ((role, text) in history.takeLast(8)) {
            val validRole = if (role == "USER") "user" else "model"
            contents.add(content(validRole) { text(text) })
        }

        while (contents.isNotEmpty() && contents.last().role == "user") {
            contents.removeAt(contents.size - 1)
        }

        val promptText = if (userMessage.isNotBlank()) {
            if (contents.isEmpty()) "[Nivel: $educationLevel | Materia: $subject] $userMessage" else userMessage
        } else {
            if (contents.isEmpty()) "[Nivel: $educationLevel | Materia: $subject] Por favor resuelve y explica los ejercicios mostrados en la imagen paso a paso." else "Por favor resuelve y explica los ejercicios de la imagen paso a paso."
        }

        val chatSession = chatModel.startChat(contents)
        withTimeout(45_000L) {
            val stream = if (imageBitmap != null) {
                val inputContent = content {
                    image(imageBitmap)
                    text(promptText)
                }
                chatSession.sendMessageStream(inputContent)
            } else {
                chatSession.sendMessageStream(promptText)
            }

            stream.collect { chunk ->
                val chunkText = chunk.text.orEmpty()
                if (chunkText.isNotEmpty()) {
                    emit(chunkText)
                }
            }
        }
    }

    suspend fun generateSessionTitle(firstQuestion: String): String {
        return try {
            val response = withTimeout(10_000L) {
                titleModel.generateContent("Genera un título corto para esta consulta académica: $firstQuestion")
            }
            val cleanTitle = response.text?.trim()?.replace("\"", "")?.replace("\n", " ")
            if (!cleanTitle.isNullOrBlank() && cleanTitle.length <= 35) {
                cleanTitle
            } else {
                firstQuestion.take(25) + "..."
            }
        } catch (e: Exception) {
            firstQuestion.take(25) + "..."
        }
    }
}
