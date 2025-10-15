package ru.aiAdventChallenge

/**
 * Форматирует ответ от агента для гармоничного вывода в консоли
 * @param response - исходный ответ от агента
 * @param singleLine - если true, заменяет все переносы строк на пробелы
 * @return отформатированный ответ
 */
fun formatAgentResponse(response: String, singleLine: Boolean = false): String {
    return if (singleLine) {
        // Заменяем только начальные переносы строк и лишние пробелы
        response.trimStart()
    } else {
        // Добавляем отступы для многострочных ответов
        response.split("\n").joinToString("\n") { line ->
            if (line.isNotBlank()) "    $line" else line
        }
    }
}