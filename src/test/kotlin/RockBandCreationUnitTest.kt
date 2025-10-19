package ru.aiAdventChallenge

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import ru.aiAdventChallenge.usecase.CreatingRockBand
import ru.aiAdventChallenge.models.RockBandCreationSession
import ru.aiAdventChallenge.usecase.RockBandConversationManager
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Unit-тесты для функционала создания рок-группы
 */
class RockBandCreationUnitTest {

    @Test
    fun `test RockBandCreationSession initial state`() {
        val session = RockBandCreationSession()
        
        assertFalse(session.isActive)
        assertEquals(0, session.currentStep)
        assertEquals(0, session.totalSteps)
        assertTrue(session.questions.isEmpty())
        assertTrue(session.answers.isEmpty())
        assertFalse(session.isCompleted)
        assertEquals("", session.getProgressText())
    }

    @Test
    fun `test RockBandCreationSession start new session`() {
        val session = RockBandCreationSession()
        val newSession = session.startNewSession(5)
        
        assertTrue(newSession.isActive)
        assertEquals(1, newSession.currentStep)
        assertEquals(5, newSession.totalSteps)
        assertTrue(newSession.questions.isEmpty())
        assertTrue(newSession.answers.isEmpty())
        assertFalse(newSession.isCompleted)
        assertEquals("Вопрос 1 из 5", newSession.getProgressText())
    }

    @Test
    fun `test RockBandCreationSession add question`() {
        val session = RockBandCreationSession().startNewSession(3)
        val sessionWithQuestion = session.addQuestion("Какой жанр музыки вам нравится?")
        
        assertEquals(1, sessionWithQuestion.questions.size)
        assertEquals("Какой жанр музыки вам нравится?", sessionWithQuestion.questions[0])
        assertEquals("Вопрос 1 из 3", sessionWithQuestion.getProgressText())
    }

    @Test
    fun `test RockBandCreationSession add answer`() {
        val session = RockBandCreationSession().startNewSession(3)
        val sessionWithQuestion = session.addQuestion("Какой жанр музыки вам нравится?")
        val sessionWithAnswer = sessionWithQuestion.addAnswer("Мне нравится рок")
        
        assertEquals(1, sessionWithAnswer.answers.size)
        assertEquals("Мне нравится рок", sessionWithAnswer.answers[0])
        assertEquals(2, sessionWithAnswer.currentStep)
        assertEquals("Вопрос 2 из 3", sessionWithAnswer.getProgressText())
    }

    @Test
    fun `test RockBandCreationSession completion`() {
        val session = RockBandCreationSession().startNewSession(2)
        val sessionWithQuestions = session
            .addQuestion("Вопрос 1")
            .addQuestion("Вопрос 2")
        
        val sessionWithAnswers = sessionWithQuestions
            .addAnswer("Ответ 1")
            .addAnswer("Ответ 2")
        
        assertTrue(sessionWithAnswers.isCompleted)
        assertEquals(3, sessionWithAnswers.currentStep)
    }

    @Test
    fun `test RockBandCreationSession reset`() {
        val session = RockBandCreationSession().startNewSession(3)
        val resetSession = session.resetSession()
        
        assertFalse(resetSession.isActive)
        assertEquals(0, resetSession.currentStep)
        assertEquals(0, resetSession.totalSteps)
        assertTrue(resetSession.questions.isEmpty())
        assertTrue(resetSession.answers.isEmpty())
        assertFalse(resetSession.isCompleted)
    }

    @Test
    fun `test RockBandCreationSession get current question`() {
        val session = RockBandCreationSession().startNewSession(3)
        val sessionWithQuestion = session.addQuestion("Какой жанр музыки вам нравится?")
        
        assertEquals("Какой жанр музыки вам нравится?", sessionWithQuestion.getCurrentQuestion())
        
        val sessionWithAnswer = sessionWithQuestion.addAnswer("Рок")
        assertEquals(null, sessionWithAnswer.getCurrentQuestion())
        
        val sessionWithSecondQuestion = sessionWithAnswer.addQuestion("Какие инструменты у вас есть?")
        assertEquals("Какие инструменты у вас есть?", sessionWithSecondQuestion.getCurrentQuestion())
    }

    @Test
    fun `test CreatingRockBand session management`() {
        // Проверяем управление сессией без реального API-клиента
        // Этот тест проверяет базовую функциональность управления состоянием
        
        // Поскольку мы не можем легко мокать ZAiClient, мы проверим только базовую логику
        // через создание сессии вручную
        
        val session = RockBandCreationSession()
        assertFalse(session.isActive)
        
        val activeSession = session.startNewSession(5)
        assertTrue(activeSession.isActive)
        assertEquals(1, activeSession.currentStep)
        assertEquals(5, activeSession.totalSteps)
        
        val completedSession = activeSession
            .addQuestion("Вопрос 1")
            .addAnswer("Ответ 1")
            .addQuestion("Вопрос 2")
            .addAnswer("Ответ 2")
            .addQuestion("Вопрос 3")
            .addAnswer("Ответ 3")
            .addQuestion("Вопрос 4")
            .addAnswer("Ответ 4")
            .addQuestion("Вопрос 5")
            .addAnswer("Ответ 5")
        
        assertTrue(completedSession.isCompleted)
        assertEquals(6, completedSession.currentStep)
        
        val resetSession = completedSession.resetSession()
        assertFalse(resetSession.isActive)
        assertEquals(0, resetSession.currentStep)
    }
}