package ru.aiAdventChallenge

import kotlinx.coroutines.runBlocking
import ru.aiAdventChallenge.usecase.CreatingRockBand
import ru.aiAdventChallenge.usecase.GetRockGroupsUseCase
import ru.aiAdventChallenge.usecase.WelcomePrompt
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
    val getRockGroupsUseCase = GetRockGroupsUseCase(client)
    val creatingRockBand = CreatingRockBand(client)

    try {
        // Сразу отправляем приветственное сообщение - это также проверит работоспособность API
        println("Подключение к Z.AI API...")
        val welcomePrompt = WelcomePrompt(client).apply { execute() }

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

                    // Проверяем, является ли ввод командой "подробнее"
                    if (userInput.lowercase() == "подробнее") {
                        // Запрашиваем дополнительную информацию о рок-группах через usecase
                        val rockGroupsResponse = getRockGroupsUseCase.execute("знаменитые рок-группы")

                        // Выводим результат в красивом отформатированном виде
                        println("Агент: Подробная информация о рок-группах:")
                        println(formatRockGroupsResponse(rockGroupsResponse))
                    } else if (userInput.lowercase() == "сброс" || userInput.lowercase() == "reset") {
                        // Команда для сброса сессии создания рок-группы
                        creatingRockBand.resetSession()
                        println("Агент: Сессия создания рок-группы сброшена. Можем начать заново.")
                    } else if (userInput.lowercase().contains("рок групп") &&
                        userInput.lowercase().contains("создать рок") &&
                        userInput.lowercase().contains("рок-групп") &&
                        !userInput.lowercase().contains("музыкальную групп") &&
                        userInput.lowercase().contains("собрать групп")
                    ) {
                        // Режим создания рок-группы
                        val response = creatingRockBand.execute(userInput)

                        if (response.isNotEmpty()) {
                            println("Агент: $response")

                            // Если сессия неактивна, но ответ был получен,
                            // значит это был не запрос на создание рок-группы
                            if (!creatingRockBand.isSessionActive() &&
                                !userInput.lowercase().contains("рок групп") &&
                                !userInput.lowercase().contains("создать рок") &&
                                !userInput.lowercase().contains("рок-групп") &&
                                !userInput.lowercase().contains("музыкальную групп") &&
                                !userInput.lowercase().contains("собрать групп")
                            ) {
                                // Это был обычный запрос, не связанный с созданием рок-группы
                                println("Агент: Я могу помочь вам создать рок-группу. Расскажите о своих желаниях!")
                            }
                        }
                    } else {
                        println("Тест на ответ с разной степеней температуры у модели:")
                        
                        println("Отправка запроса с температурой 0.0...")
                        val response0 = welcomePrompt.standardMode(query = userInput, temperature = 0.0)
                        if (response0.isNotEmpty()) {
                            println("${formatAgentResponse(response0, true)}")
                        } else {
                            println("Ошибка: не получен ответ с температурой 0.0")
                        }
                        println()
                        
                        println("Отправка запроса с температурой 0.7...")
                        val response07 = welcomePrompt.standardMode(query = userInput, temperature = 0.7)
                        if (response07.isNotEmpty()) {
                            println("${formatAgentResponse(response07, true)}")
                        } else {
                            println("Ошибка: не получен ответ с температурой 0.7")
                        }
                        println()
                        
                        println("Отправка запроса с температурой 1.0...")
                        val response1 = welcomePrompt.standardMode(query = userInput, temperature = 1.0)
                        if (response1.isNotEmpty()) {
                            println("${formatAgentResponse(response1, true)}")
                        } else {
                            println("Ошибка: не получен ответ с температурой 1.0")
                        }
                        println()

                        println("Отправка запроса с температурой 1.2...")
                        val response12 = welcomePrompt.standardMode(query = userInput, temperature = 1.2)
                        if (response12.isNotEmpty()) {
                            println("${formatAgentResponse(response12, true)}")
                        } else {
                            println("Ошибка: не получен ответ с температурой 1.2")
                        }
                        println()
                    }
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