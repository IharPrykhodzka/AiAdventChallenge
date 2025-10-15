package ru.aiAdventChallenge

import kotlinx.coroutines.runBlocking
import java.util.Scanner

fun main() = runBlocking {
    val scanner = Scanner(System.`in`)

    println("=== Z.AI Agent Terminal ===")
    println("Добро пожаловать в терминал для общения с Z.AI агентом!")
    println("Введите 'exit', 'quit' или 'выход' для завершения работы.")
    println()

    // Используем API ключ из Constants.kt
    val apiKey = try {
        llmZApi
    } catch (e: IllegalStateException) {
        println("Ошибка конфигурации: ${e.message}")
        println("Пожалуйста, создайте файл config.properties на основе config.properties.example")
        println("и добавьте в него ваш Z.AI API ключ.")
        return@runBlocking
    }
    val client = ZAiClient(apiKey)

    try {
        // Сразу отправляем приветственное сообщение - это также проверит работоспособность API
        println("Подключение к Z.AI API...")
        try {
            println("Вступительный момент: Привет! Представься и расскажи, чем ты можешь помочь. Отвечай на русском языке.")
            println("Агент: печатает...")
            val welcomeResult = client.sendMessage(
                message = "Привет! Представься и расскажи, чем ты можешь помочь. Отвечай на русском языке.",
                systemMessage = "You are a helpful assistant. Answer user questions concisely in Russian."
            )
            println("Агент: ${formatAgentResponse(welcomeResult, true)}")
            println()
            print("Вы: ")
        } catch (e: Exception) {
            println("Не удалось подключиться к API: ${e.message}")
            println("Проверьте интернет-соединение и API ключ")
            println()
        }

        // Интерактивный режим
        while (true) {
            try {
                if (!scanner.hasNextLine()) {
                    println("Ввод недоступен. Завершение работы.")
                    break
                }
                
                val userInput = scanner.nextLine()

                if (userInput.lowercase() in listOf("exit", "quit", "выход")) {
                    println("До свидания!")
                    break
                }

                if (userInput.isBlank()) {
                    continue
                }

                try {
                    println("Агент: печатает...")
                    val result = client.sendMessage(
                        message = userInput,
                        systemMessage = "You are a helpful assistant. Answer user questions concisely in Russian."
                    )
                    println("Агент: ${formatAgentResponse(result, true)}")
                    println()
                    print("Вы: ")
                } catch (e: Exception) {
                    println("Ошибка: ${e.message}")
                    println("Попробуйте еще раз или введите 'exit' для выхода.")
                    println()
                }
            } catch (e: Exception) {
                println("Ошибка чтения ввода: ${e.message}")
                break
            }
        }
    } finally {
        try {
            scanner.close()
        } catch (e: Exception) {
            // Игнорируем ошибки при закрытии scanner
        }
        client.close()
    }
}