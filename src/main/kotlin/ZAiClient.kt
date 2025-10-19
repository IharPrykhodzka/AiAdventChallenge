package ru.aiAdventChallenge

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import ru.aiAdventChallenge.models.ZAiErrorResponse
import ru.aiAdventChallenge.models.ZAiMessage
import ru.aiAdventChallenge.models.ZAiRequest
import ru.aiAdventChallenge.models.ZAiResponse

class ZAiClient(private val apiKey: String) {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                prettyPrint = true
            })
        }

        install(HttpTimeout) {
            connectTimeoutMillis = 120_000
            requestTimeoutMillis = 300_000
            socketTimeoutMillis = 300_000
        }
    }

    private val json = Json { ignoreUnknownKeys = true }

    suspend fun sendMessage(message: String, systemMessage: String? = null): String {
        try {
            val messages = mutableListOf<ZAiMessage>()

            // Добавляем системное сообщение, если оно есть
            systemMessage?.let {
                messages.add(ZAiMessage(role = "system", content = it))
            }

            // Добавляем сообщение пользователя
            messages.add(ZAiMessage(role = "user", content = message))

            val request = ZAiRequest(
                model = glmModelMiddle,
                messages = messages
            )

            // Для отладки выведем запрос
            val requestString = json.encodeToString(ZAiRequest.serializer(), request)

            val httpResponse = client.post("https://api.z.ai/api/coding/paas/v4/chat/completions") {
                header("Content-Type", "application/json")
                header("Accept", "application/json")
                header("Accept-Language", "en-US,en")
                header("Authorization", apiKey)
                header("User-Agent", "AiAdventChallenge/1.0")
                setBody(request)
            }

            val responseString = httpResponse.body<String>()

            try {
                val response = json.decodeFromString<ZAiResponse>(responseString)
                val firstChoice = response.choices.firstOrNull()
                if (firstChoice != null) {
                    val assistantMessage = firstChoice.message
                    if (assistantMessage.role == "assistant") {
                        return assistantMessage.content
                    } else {
                        return "No assistant message found in response"
                    }
                } else {
                    return "No choices in response"
                }
            } catch (e: Exception) {
                try {
                    // Если не получилось, попробуем распарсить как ответ с ошибкой
                    val errorResponse = json.decodeFromString<ZAiErrorResponse>(responseString)
                    return "API Error: ${errorResponse.error.code} - ${errorResponse.error.message ?: "Unknown error"}"
                } catch (e2: Exception) {
                    return "Error parsing response: ${e.message}. Raw response: $responseString"
                }
            }
        } catch (e: Exception) {
//            println("DEBUG: Исключение при отправке запроса: ${e.javaClass.simpleName}: ${e.message}")
            e.printStackTrace()
            return "Ошибка подключения к API: ${e.message}"
        }
    }

    fun close() {
        client.close()
    }
}