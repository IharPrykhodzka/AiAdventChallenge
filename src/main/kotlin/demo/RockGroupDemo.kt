package ru.aiAdventChallenge.demo

import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import ru.aiAdventChallenge.RockGroupsResponse
import ru.aiAdventChallenge.ZAiClient
import ru.aiAdventChallenge.llmZApi
import ru.aiAdventChallenge.usecase.GetRockGroupsUseCase

/**
 * Демонстрационный класс для тестирования функциональности получения информации о рок-группах в JSON формате
 */
fun main() = runBlocking {
    println("=== Демонстрация работы с JSON-ответами о рок-группах ===")
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
    val getRockGroupsUseCase = GetRockGroupsUseCase(client)
    
    try {
        println("Запрос информации о рок-группах...")
        println()
        
        // Вызываем наш новый usecase
        val rockGroupsResponse = getRockGroupsUseCase.execute("знаменитые рок-группы 70-х")
        
        // Настройка JSON для красивого вывода
        val json = Json {
            prettyPrint = true
            ignoreUnknownKeys = true
        }
        
        // Выводим результат в формате JSON
        val jsonString = json.encodeToString(RockGroupsResponse.serializer(), rockGroupsResponse)
        println("Ответ от AI в формате JSON:")
        println(jsonString)
        println()
        
        // Анализируем результат
        println("Анализ результата:")
        println("- Количество групп: ${rockGroupsResponse.totalCount}")
        println("- Запрос: '${rockGroupsResponse.query}'")
        println()
        
        if (rockGroupsResponse.groups.isNotEmpty()) {
            println("Список групп:")
            rockGroupsResponse.groups.forEachIndexed { index, group ->
                println("${index + 1}. ${group.name} (${group.foundedYear}) - ${group.country}")
                println("   Жанр: ${group.genre}")
                println("   Известные альбомы: ${group.famousAlbums.joinToString(", ")}")
                println("   Участники: ${group.members.joinToString(", ")}")
                println("   Описание: ${group.description}")
                println()
            }
        } else {
            println("Группы не найдены или произошла ошибка при получении данных.")
        }
        
        println("Демонстрация завершена успешно!")
        
    } catch (e: Exception) {
        println("Ошибка при выполнении запроса: ${e.message}")
        e.printStackTrace()
    } finally {
        client.close()
    }
}