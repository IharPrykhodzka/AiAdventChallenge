package ru.aiAdventChallenge.usecase

import ru.aiAdventChallenge.ZAiClient

/**
 * Класс для управления диалогом создания рок-группы
 */
class RockBandConversationManager(private val aiClient: ZAiClient) {
    
    /**
     * Начинает новый диалог создания рок-группы
     */
    suspend fun startConversation(userInput: String): ConversationResult {
        val systemMessage = """
            Пишите красиво, вежливо и официально с переносом на новую строку и выделяя важные параметры. 
            И не весь текст в одной строке.
            
            Ты - эксперт по музыкальным группам, особенно в жанре рок.
            Твоя роль помогать молодым талантам успешно собрать свою рок группу.
            Молодые люди зачастую сами не знают, что хотят.
            
            Тебе требуется задать несколько уточняющих и направляющих вопросов.
            Задать нужно 5 вопросов.
            
            Сначала огласи сколько вопросов собираешься задать.
            Затем задай первый вопрос.
            
            В конце каждого вопроса добавь в скобках [ВОПРОС_РАЗГОВОРА], чтобы система могла распознать вопросы.
            
            Пример ответа:
            "Я задам вам 5 вопросов, чтобы помочь создать идеальную рок-группу.
            
            Начнем с первого вопроса:
            
            Какой поджанр рока вам ближе всего? (хард-рок, панк-рок, альтернативный рок и т.д.) [ВОПРОС_РАЗГОВОРА]"
        """.trimIndent()
        
        val userMessage = "Проконсультируй молодых людей, как им создать рок группу. " +
                "Они будут тебя спрашивать и отвечать на твои вопросы: $userInput"
        
        try {
            val response = aiClient.sendMessage(userMessage, systemMessage)
            val questionCount = extractQuestionCount(response)
            
            return ConversationResult(
                response = response,
                questionCount = questionCount,
                isQuestion = containsQuestion(response)
            )
        } catch (e: Exception) {
            return ConversationResult(
                response = "Ошибка: ${e.message}",
                questionCount = 0,
                isQuestion = false,
                isError = true
            )
        }
    }
    
    /**
     * Обрабатывает ответ пользователя и генерирует следующий вопрос или итоговый результат
     */
    suspend fun processUserAnswer(
        userAnswer: String, 
        previousQuestions: List<String>, 
        previousAnswers: List<String>,
        totalQuestions: Int,
        currentStep: Int
    ): ConversationResult {
        val isLastQuestion = currentStep >= totalQuestions
        
        val systemMessage = if (isLastQuestion) {
            """
                Пишите красиво, вежливо и официально с переносом на новую строку и выделяя важные параметры. 
                И не весь текст в одной строке.
                
                Ты - эксперт по музыкальным группам, особенно в жанре рок.
                
                Пользователь ответил на все твои вопросы. Теперь тебе нужно:
                1. Проанализировать все полученные ответы
                2. На основе ответов составить подробный план действий для создания рок-группы
                3. Выдать рекомендации с пунктами которые нужно выполнить
                
                Вопросы и ответы:
                ${formatQuestionsAndAnswers(previousQuestions, previousAnswers)}
                
                В конце ответа добавь [РАЗГОВОР_ЗАВЕРШЕН], чтобы система поняла, что диалог завершен.
            """.trimIndent()
        } else {
            """
                Пишите красиво, вежливо и официально с переносом на новую строку и выделяя важные параметры. 
                И не весь текст в одной строке.
                
                Ты - эксперт по музыкальным группам, особенно в жанре рок.
                
                Пользователь ответил на твой вопрос. Задай следующий уточняющий вопрос.
                
                Текущий прогресс: Вопрос ${currentStep + 1} из $totalQuestions
                
                Предыдущие вопросы и ответы:
                ${formatQuestionsAndAnswers(previousQuestions, previousAnswers)}
                
                В конце каждого вопроса добавь в скобках [ВОПРОС_РАЗГОВОРА], чтобы система могла распознать вопросы.
            """.trimIndent()
        }
        
        try {
            val response = aiClient.sendMessage(userAnswer, systemMessage)
            
            return ConversationResult(
                response = response,
                questionCount = totalQuestions,
                isQuestion = containsQuestion(response),
                isCompleted = isLastQuestion
            )
        } catch (e: Exception) {
            return ConversationResult(
                response = "Ошибка: ${e.message}",
                questionCount = totalQuestions,
                isQuestion = false,
                isError = true
            )
        }
    }
    
    /**
     * Предлагает начать новый диалог
     */
    suspend fun suggestNewConversation(): String {
        val systemMessage = """
            Пишите красиво, вежливо и официально с переносом на новую строку.
            
            Ты - эксперт по музыкальным группах.
            
            Предложи пользователю помощь с созданием новой рок-группы.
            Будь краток и вежлив.
        """.trimIndent()
        
        try {
            return aiClient.sendMessage("Предложи помощь с созданием новой рок-группы", systemMessage)
        } catch (e: Exception) {
            return "Хотите создать еще одну рок-группу?"
        }
    }
    
    /**
     * Извлекает количество вопросов из ответа AI
     */
    private fun extractQuestionCount(response: String): Int {
        val regex = Regex("""(\d+)\s+(?:вопрос|вопроса|вопросов)""")
        return regex.find(response)?.groupValues?.get(1)?.toIntOrNull() ?: 5
    }
    
    /**
     * Проверяет, содержит ли ответ вопрос
     */
    private fun containsQuestion(response: String): Boolean {
        return response.contains("[ВОПРОС_РАЗГОВОРА]")
    }
    
    /**
     * Форматирует вопросы и ответы для отображения
     */
    private fun formatQuestionsAndAnswers(questions: List<String>, answers: List<String>): String {
        val result = StringBuilder()
        for (i in questions.indices) {
            result.append("Вопрос ${i + 1}: ${questions[i]}\n")
            if (i < answers.size) {
                result.append("Ответ: ${answers[i]}\n")
            }
            result.append("\n")
        }
        return result.toString()
    }
}

/**
 * Результат обработки сообщения в диалоге
 */
data class ConversationResult(
    val response: String,
    val questionCount: Int,
    val isQuestion: Boolean,
    val isCompleted: Boolean = false,
    val isError: Boolean = false
)