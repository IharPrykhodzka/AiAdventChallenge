package ru.aiAdventChallenge

import java.io.FileInputStream
import java.io.IOException
import java.util.Properties

const val glmModelSenior = "glm-4.6"
const val glmModelMiddle = "glm-4.5"
const val glmModelJunior = "glm-4.5-air"

val llmZApi: String by lazy {
    loadApiKey()
}

private fun loadApiKey(): String {
    val properties = Properties()
    return try {
        // Ищем config.properties в корне проекта
        val configFile = "config.properties"
        properties.load(FileInputStream(configFile))
        val apiKey = properties.getProperty("llm.z.api.key")
        if (apiKey.isNullOrEmpty() || apiKey == "YOUR_API_KEY_HERE") {
            throw IllegalStateException("API ключ не найден или не настроен в config.properties")
        }
        apiKey
    } catch (e: IOException) {
        throw IllegalStateException("Не удалось прочитать файл config.properties: ${e.message}", e)
    } catch (e: Exception) {
        throw IllegalStateException("Ошибка при загрузке API ключа: ${e.message}", e)
    }
}