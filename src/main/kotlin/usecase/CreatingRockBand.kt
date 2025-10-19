package ru.aiAdventChallenge.usecase

import ru.aiAdventChallenge.ZAiClient
import ru.aiAdventChallenge.models.RockBandCreationSession

class CreatingRockBand (private val aiClient: ZAiClient) {
    
    private val conversationManager = RockBandConversationManager(aiClient)
    private var currentSession = RockBandCreationSession()
    
    /**
     * Основной метод для обработки пользовательского ввода
     */
    suspend fun execute(query: String): String {
        try {
            // Если сессия не активна, проверяем, хочет ли пользователь начать создание рок-группы
            if (!currentSession.isActive) {
                // Проверяем, содержит ли запрос слова о создании рок-группы
                if (isRockBandCreationRequest(query)) {
                    // Начинаем новую сессию
                    val result = conversationManager.startConversation(query)
                    
                    if (result.isError) {
                        return result.response
                    }
                    
                    // Создаем новую сессию с количеством вопросов
                    currentSession = currentSession.startNewSession(result.questionCount)
                    
                    // Добавляем первый вопрос в сессию
                    currentSession = currentSession.addQuestion(extractQuestionFromResponse(result.response))
                    
                    return formatResponseWithProgress(result.response, currentSession.getProgressText())
                } else {
                    // Если это не запрос на создание рок-группы, возвращаем пустой ответ
                    return ""
                }
            } else {
                // Сессия активна, обрабатываем ответ пользователя
                if (currentSession.hasUnansweredQuestion()) {
                    // Добавляем ответ пользователя
                    currentSession = currentSession.addAnswer(query)
                    
                    // Если это был последний ответ, генерируем итоговый результат
                    if (currentSession.isCompleted) {
                        val result = conversationManager.processUserAnswer(
                            query,
                            currentSession.questions,
                            currentSession.answers,
                            currentSession.totalSteps,
                            currentSession.currentStep - 1
                        )
                        
                        // Завершаем сессию
                        currentSession = currentSession.completeSession()
                        
                        // Добавляем предложение начать новый диалог
                        val finalResponse = result.response + "\n\n" + conversationManager.suggestNewConversation()
                        
                        return finalResponse
                    } else {
                        // Генерируем следующий вопрос
                        val result = conversationManager.processUserAnswer(
                            query,
                            currentSession.questions,
                            currentSession.answers,
                            currentSession.totalSteps,
                            currentSession.currentStep - 1
                        )
                        
                        if (result.isError) {
                            return result.response
                        }
                        
                        // Добавляем новый вопрос в сессию
                        currentSession = currentSession.addQuestion(extractQuestionFromResponse(result.response))
                        
                        return formatResponseWithProgress(result.response, currentSession.getProgressText())
                    }
                } else {
                    // Нет unanswered вопроса, что-то пошло не так
                    return "Ошибка в логике диалога. Пожалуйста, начните заново."
                }
            }
        } catch (e: Exception) {
            println("Не удалось подключиться к API: ${e.message}")
            println("Проверьте интернет-соединение и API ключ")
            println()
            return "Произошла ошибка при обработке запроса. Пожалуйста, попробуйте еще раз."
        }
    }
    
    /**
     * Проверяет, является ли запрос запросом на создание рок-группы
     */
    private fun isRockBandCreationRequest(query: String): Boolean {
        val lowerQuery = query.lowercase()
        return lowerQuery.contains("рок групп") ||
               lowerQuery.contains("создать рок") ||
               lowerQuery.contains("рок-групп") ||
               lowerQuery.contains("музыкальную групп") ||
               lowerQuery.contains("собрать групп")
    }
    
    /**
     * Извлекает вопрос из ответа AI
     */
    private fun extractQuestionFromResponse(response: String): String {
        // Удаляем метку вопроса из ответа
        return response.replace("[ВОПРОС_РАЗГОВОРА]", "").trim()
    }
    
    /**
     * Форматирует ответ с информацией о прогрессе
     */
    private fun formatResponseWithProgress(response: String, progress: String): String {
        // Удаляем метку вопроса из ответа
        val cleanResponse = response.replace("[ВОПРОС_РАЗГОВОРА]", "").trim()
        
        return if (progress.isNotEmpty()) {
            "$progress\n\n$cleanResponse"
        } else {
            cleanResponse
        }
    }
    
    /**
     * Сбрасывает текущую сессию
     */
    fun resetSession() {
        currentSession = currentSession.resetSession()
    }
    
    /**
     * Проверяет, активна ли сессия
     */
    fun isSessionActive(): Boolean {
        return currentSession.isActive
    }
}