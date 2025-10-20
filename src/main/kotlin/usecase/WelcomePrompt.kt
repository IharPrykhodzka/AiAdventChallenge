package ru.aiAdventChallenge.usecase

import ru.aiAdventChallenge.ZAiClient
import ru.aiAdventChallenge.formatAgentResponse

class WelcomePrompt(private val aiClient: ZAiClient) {
    suspend fun execute() {
        try {
            println("Вступительный момент: Привет! Представься и расскажи, чем ты можешь помочь. Отвечай на русском языке.")
            println("Агент: печатает...")
            val welcomeResult = aiClient.sendMessage(
                message = "Привет! Представься и расскажи, чем ты можешь помочь. Отвечай на русском языке.",
                systemMessage = "You are a helpful assistant. Answer user questions concisely in Russian. " +
                        "Write beautifully, hyphenating to a new line and highlighting important parameters. " +
                        "And not the whole text in one line."
            )
            println("Агент: ${formatAgentResponse(welcomeResult, true)}")
            println()
            print("Вы: ")
        } catch (e: Exception) {
            println("Не удалось подключиться к API: ${e.message}")
            println("Проверьте интернет-соединение и API ключ")
            println()
        }
    }

    suspend fun standardMode(query: String, temperature: Double = 1.0): String {
        try {
            val response = aiClient.sendMessage(
                message = query,
                systemMessage = "You are a helpful assistant. Answer user questions concisely in Russian. " +
                        "Write beautifully, hyphenating to a new line and highlighting important parameters. " +
                        "And not the whole text in one line.",
                temperature = temperature
            )
            return response
        } catch (e: Exception) {
            println("StandardMode с температурой $temperature: ${e.message}")
            println("Не удалось подключиться к API: ${e.message}")
            println("Проверьте интернет-соединение и API ключ")
            println()
            return ""
        }
    }
}