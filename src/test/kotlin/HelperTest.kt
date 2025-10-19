package ru.aiAdventChallenge

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import ru.aiAdventChallenge.models.RockGroup
import ru.aiAdventChallenge.models.RockGroupsResponse

class HelperTest {

    @Test
    fun testFormatRockGroupsResponse() {
        // Создаем тестовые данные
        val groups = listOf(
            RockGroup(
                name = "Queen",
                foundedYear = 1970,
                country = "Великобритания",
                genre = "Рок, глэм-рок",
                famousAlbums = listOf("A Night at the Opera", "Bohemian Rhapsody"),
                members = listOf("Freddie Mercury", "Brian May", "Roger Taylor", "John Deacon"),
                description = "Британская рок-группа, известная своими эпическими композициями"
            ),
            RockGroup(
                name = "Led Zeppelin",
                foundedYear = 1968,
                country = "Великобритания",
                genre = "Хард-рок, блюз-рок",
                famousAlbums = listOf("Led Zeppelin IV", "Physical Graffiti"),
                members = listOf("Robert Plant", "Jimmy Page", "John Paul Jones", "John Bonham"),
                description = "Одна из величайших хард-рок групп всех времен"
            )
        )

        val response = RockGroupsResponse(
            groups = groups,
            totalCount = 2,
            query = "знаменитые рок-группы"
        )

        // Вызываем функцию форматирования
        val formattedOutput = formatRockGroupsResponse(response)

        // Проверяем presence всех ключевых элементов
        assertTrue(formattedOutput.contains("🎸 Рок-группы по запросу: \"знаменитые рок-группы\""))
        assertTrue(formattedOutput.contains("Найдено групп: 2"))
        assertTrue(formattedOutput.contains("1. 🎵 Queen (1970)"))
        assertTrue(formattedOutput.contains("🌍 Страна: Великобритания"))
        assertTrue(formattedOutput.contains("🎼 Жанр: Рок, глэм-рок"))
        assertTrue(formattedOutput.contains("💿 Известные альбомы:"))
        assertTrue(formattedOutput.contains("• A Night at the Opera"))
        assertTrue(formattedOutput.contains("👥 Участники:"))
        assertTrue(formattedOutput.contains("• Freddie Mercury"))
        assertTrue(formattedOutput.contains("📝 Описание: Британская рок-группа, известная своими эпическими композициями"))
        assertTrue(formattedOutput.contains("2. 🎵 Led Zeppelin (1968)"))
        assertTrue(formattedOutput.contains("════════════════════════════════════════════════════════"))

        println("Тест форматирования вывода о рок-группах:")
        println(formattedOutput)
    }

    @Test
    fun testFormatEmptyRockGroupsResponse() {
        // Тест с пустым списком групп
        val emptyResponse = RockGroupsResponse(
            groups = emptyList(),
            totalCount = 0,
            query = "несуществующие группы"
        )

        val formattedOutput = formatRockGroupsResponse(emptyResponse)

        // Проверяем, что выводится сообщение об отсутствии групп
        assertTrue(formattedOutput.contains("🎸 Рок-группы по запросу: \"несуществующие группы\""))
        assertTrue(formattedOutput.contains("Найдено групп: 0"))
        assertTrue(formattedOutput.contains("К сожалению, информация о группах не найдена."))

        println("\nТест форматирования пустого ответа:")
        println(formattedOutput)
    }
}