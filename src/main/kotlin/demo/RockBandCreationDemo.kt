package ru.aiAdventChallenge.demo

import kotlinx.coroutines.runBlocking
import ru.aiAdventChallenge.ZAiClient
import ru.aiAdventChallenge.llmZApi
import ru.aiAdventChallenge.usecase.CreatingRockBand

/**
 * Демонстрационный класс для тестирования функционала создания рок-группы
 */
fun main() = runBlocking {
    println("=== Демонстрация создания рок-группы ===")
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
        println("Пользователь: Хочу создать рок-группу")
        println("Агент: печатает...")
        
        // Начинаем диалог
        val initialResponse = creatingRockBand.execute("Хочу создать рок-группу")
        println("Агент: $initialResponse")
        println("Статус сессии: ${creatingRockBand.isSessionActive()}")
        println()
        
        if (creatingRockBand.isSessionActive()) {
            println("Пользователь: Мне нравится хард-рок и классический рок")
            println("Агент: печатает...")
            
            // Отвечаем на первый вопрос
            val secondResponse = creatingRockBand.execute("Мне нравится хард-рок и классический рок")
            println("Агент: $secondResponse")
            println("Статус сессии: ${creatingRockBand.isSessionActive()}")
            println()
            
            println("Пользователь: У меня есть гитара, но нужен барабанщик и вокалист")
            println("Агент: печатает...")
            
            // Отвечаем на второй вопрос
            val thirdResponse = creatingRockBand.execute("У меня есть гитара, но нужен барабанщик и вокалист")
            println("Агент: $thirdResponse")
            println("Статус сессии: ${creatingRockBand.isSessionActive()}")
            println()
            
            println("Пользователь: Я готов репетировать 2 раза в неделю")
            println("Агент: печатает...")
            
            // Отвечаем на третий вопрос
            val fourthResponse = creatingRockBand.execute("Я готов репетировать 2 раза в неделю")
            println("Агент: $fourthResponse")
            println("Статус сессии: ${creatingRockBand.isSessionActive()}")
            println()
        }
        
        println("=== Демонстрация сброса сессии ===")
        creatingRockBand.resetSession()
        println("Статус после сброса: ${creatingRockBand.isSessionActive()}")
        println()
        
        println("=== Демонстрация завершена ===")
        
    } catch (e: Exception) {
        println("Ошибка при демонстрации: ${e.message}")
        e.printStackTrace()
    } finally {
        client.close()
    }
}