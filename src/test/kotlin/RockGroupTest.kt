package ru.aiAdventChallenge

import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class RockGroupTest {

    @Test
    fun testRockGroupSerialization() {
        val rockGroup = RockGroup(
            name = "The Beatles",
            foundedYear = 1960,
            country = "Великобритания",
            genre = "Рок-н-ролл, бит",
            famousAlbums = listOf("Abbey Road", "Sgt. Pepper's Lonely Hearts Club Band", "Revolver"),
            members = listOf("John Lennon", "Paul McCartney", "George Harrison", "Ringo Starr"),
            description = "Легендарная британская рок-группа, оказавшая огромное влияние на мировую музыку"
        )

        val json = Json {
            prettyPrint = true
            ignoreUnknownKeys = true
        }

        val jsonString = json.encodeToString(RockGroup.serializer(), rockGroup)
        println("Сериализация RockGroup в JSON:")
        println(jsonString)

        // Проверяем, что JSON содержит ожидаемые поля
        assertTrue(jsonString.contains("\"name\""))
        assertTrue(jsonString.contains("\"foundedYear\""))
        assertTrue(jsonString.contains("\"country\""))
        assertTrue(jsonString.contains("\"genre\""))
        assertTrue(jsonString.contains("\"famousAlbums\""))
        assertTrue(jsonString.contains("\"members\""))
        assertTrue(jsonString.contains("\"description\""))

        // Проверяем десериализацию
        val deserializedGroup = json.decodeFromString<RockGroup>(jsonString)
        assertEquals(rockGroup.name, deserializedGroup.name)
        assertEquals(rockGroup.foundedYear, deserializedGroup.foundedYear)
        assertEquals(rockGroup.country, deserializedGroup.country)
        assertEquals(rockGroup.genre, deserializedGroup.genre)
        assertEquals(rockGroup.famousAlbums, deserializedGroup.famousAlbums)
        assertEquals(rockGroup.members, deserializedGroup.members)
        assertEquals(rockGroup.description, deserializedGroup.description)
    }

    @Test
    fun testRockGroupsResponseSerialization() {
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

        val json = Json {
            prettyPrint = true
            ignoreUnknownKeys = true
        }

        val jsonString = json.encodeToString(RockGroupsResponse.serializer(), response)
        println("\nСериализация RockGroupsResponse в JSON:")
        println(jsonString)

        // Проверяем, что JSON содержит ожидаемые поля
        assertTrue(jsonString.contains("\"groups\""))
        assertTrue(jsonString.contains("\"totalCount\""))
        assertTrue(jsonString.contains("\"query\""))
        assertTrue(jsonString.contains("Queen"))
        assertTrue(jsonString.contains("Led Zeppelin"))

        // Проверяем десериализацию
        val deserializedResponse = json.decodeFromString<RockGroupsResponse>(jsonString)
        assertEquals(response.totalCount, deserializedResponse.totalCount)
        assertEquals(response.query, deserializedResponse.query)
        assertEquals(response.groups.size, deserializedResponse.groups.size)
        assertEquals(response.groups[0].name, deserializedResponse.groups[0].name)
        assertEquals(response.groups[1].name, deserializedResponse.groups[1].name)
    }
}