package ru.aiAdventChallenge

import kotlinx.coroutines.runBlocking
import ru.aiAdventChallenge.usecase.CreatingRockBand
import ru.aiAdventChallenge.models.RockBandCreationSession
import ru.aiAdventChallenge.usecase.RockBandConversationManager

/**
 * Тестовый класс для проверки функционала создания рок-группы
 */
fun main() = runBlocking {
    println("=== Тестирование функционала создания рок-группы ===")
    println()
    
    // Проверяем наличие API ключа
    val apiKey = try {
        llmZApi
    } catch (e: IllegalStateException) {
        println("Ошибка конфигурации: ${e.message}")
        println("Пожалуйста, создайте файл config.properties на основе config.properties.example")
        println("и добавьте в него ваш Z.AI API ключ.")
        return@runBlocking
    }
    
    val client = ZAiClient(apiKey)
    val creatingRockBand = CreatingRockBand(client)
    
    try {
        println("1. Тестирование начала диалога...")
        val initialResponse = creatingRockBand.execute("Хочу создать рок-группу")
        println("Ответ: $initialResponse")
        println("Статус сессии: ${creatingRockBand.isSessionActive()}")
        println()
        
        if (creatingRockBand.isSessionActive()) {
            println("2. Тестирование ответа на первый вопрос...")
            val secondResponse = creatingRockBand.execute("Мне нравится хард-рок и классический рок")
            println("Ответ: $secondResponse")
            println("Статус сессии: ${creatingRockBand.isSessionActive()}")
            println()
            
            println("3. Тестирование ответа на второй вопрос...")
            val thirdResponse = creatingRockBand.execute("У меня есть гитара, но нужен барабанщик и вокалист")
            println("Ответ: $thirdResponse")
            println("Статус сессии: ${creatingRockBand.isSessionActive()}")
            println()
        }
        
        println("4. Тестирование сброса сессии...")
        creatingRockBand.resetSession()
        println("Статус после сброса: ${creatingRockBand.isSessionActive()}")
        println()
        
        println("Тестирование завершено успешно!")
        
    } catch (e: Exception) {
        println("Ошибка при тестировании: ${e.message}")
        e.printStackTrace()
    } finally {
        client.close()
    }
}