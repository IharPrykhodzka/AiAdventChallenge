package ru.aiAdventChallenge.usecase

import kotlinx.serialization.json.Json
import ru.aiAdventChallenge.models.RockGroupsResponse
import ru.aiAdventChallenge.ZAiClient

/**
 * Use case для получения информации о рок-группах в формате JSON
 * Отвечает за бизнес-логику получения и обработки данных о музыкальных группах
 */
class GetRockGroupsUseCase(private val aiClient: ZAiClient) {
    
    private val json = Json { ignoreUnknownKeys = true }
    
    /**
     * Получает информацию о рок-группах на основе запроса
     * @param query - запрос для поиска информации о группах
     * @return RockGroupsResponse - структурированный ответ с информацией о группах
     */
    suspend fun execute(query: String = "знаменитые рок-группы"): RockGroupsResponse {
        val systemMessage = """
            Ты - эксперт по музыкальным группам, особенно в жанре рок. 
            Отвечай на вопросы пользователя о музыкальных группах в формате JSON.
            
            Структура ответа должна быть следующей:
            {
              "groups": [
                {
                  "name": "Название группы",
                  "foundedYear": Год основания,
                  "country": "Страна",
                  "genre": "Жанр",
                  "famousAlbums": ["Альбом1", "Альбом2", "Альбом3"],
                  "members": ["Участник1", "Участник2", "Участник3"],
                  "description": "Краткое описание группы"
                }
              ],
              "totalCount": Количество_групп,
              "query": "Запрос_пользователя"
            }
            
            Возвращай только JSON, без дополнительного текста.
        """.trimIndent()

        val userMessage = "Расскажи подробнее о рок-группах по запросу: $query"
        
        try {
            val response = aiClient.sendMessage(userMessage, systemMessage)
            
            // Пытаемся извлечь JSON из ответа
            val jsonStart = response.indexOf("{")
            val jsonEnd = response.lastIndexOf("}") + 1
            
            if (jsonStart != -1 && jsonEnd > jsonStart) {
                val jsonResponse = response.substring(jsonStart, jsonEnd)
                return json.decodeFromString<RockGroupsResponse>(jsonResponse)
            } else {
                // Если не удалось найти JSON, создаем ответ с ошибкой
                return createEmptyResponse(query)
            }
        } catch (e: Exception) {
            // В случае ошибки возвращаем пустой ответ
            return createEmptyResponse(query)
        }
    }
    
    /**
     * Создает пустой ответ в случае ошибки
     */
    private fun createEmptyResponse(query: String): RockGroupsResponse {
        return RockGroupsResponse(
            groups = emptyList(),
            totalCount = 0,
            query = query
        )
    }
}