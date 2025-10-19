package ru.aiAdventChallenge.models

/**
 * Класс для управления состоянием сессии создания рок-группы
 */
data class RockBandCreationSession(
    val isActive: Boolean = false,
    val currentStep: Int = 0,
    val totalSteps: Int = 0,
    val questions: List<String> = emptyList(),
    val answers: MutableList<String> = mutableListOf(),
    val isCompleted: Boolean = false
) {
    /**
     * Начинает новую сессию создания рок-группы
     */
    fun startNewSession(totalQuestions: Int): RockBandCreationSession {
        return copy(
            isActive = true,
            currentStep = 1,
            totalSteps = totalQuestions,
            questions = emptyList(),
            answers = mutableListOf(),
            isCompleted = false
        )
    }

    /**
     * Добавляет вопрос в список вопросов
     */
    fun addQuestion(question: String): RockBandCreationSession {
        return copy(
            questions = questions + question
        )
    }

    /**
     * Добавляет ответ на текущий вопрос и переходит к следующему шагу
     */
    fun addAnswer(answer: String): RockBandCreationSession {
        val newAnswers = answers.toMutableList()
        newAnswers.add(answer)

        val nextStep = currentStep + 1
        val isCompleted = nextStep > totalSteps

        return copy(
            currentStep = nextStep,
            answers = newAnswers,
            isCompleted = isCompleted
        )
    }

    /**
     * Завершает сессию
     */
    fun completeSession(): RockBandCreationSession {
        return copy(
            isActive = false,
            isCompleted = true
        )
    }

    /**
     * Сбрасывает сессию
     */
    fun resetSession(): RockBandCreationSession {
        return RockBandCreationSession()
    }

    /**
     * Возвращает текущий прогресс в формате "Вопрос X из Y"
     */
    fun getProgressText(): String {
        return if (isActive && !isCompleted) {
            "Вопрос $currentStep из $totalSteps"
        } else {
            ""
        }
    }

    /**
     * Проверяет, есть ли текущий вопрос без ответа
     */
    fun hasUnansweredQuestion(): Boolean {
        return isActive && !isCompleted && questions.isNotEmpty() && questions.size > answers.size
    }

    /**
     * Возвращает текущий вопрос
     */
    fun getCurrentQuestion(): String? {
        return if (hasUnansweredQuestion()) {
            questions[answers.size]
        } else {
            null
        }
    }
}