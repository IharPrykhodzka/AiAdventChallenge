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

/**
 * Форматирует информацию о рок-группах для красивого вывода в терминал
 * @param response - ответ от AI с информацией о группах
 * @return отформатированная строка для вывода
 */
fun formatRockGroupsResponse(response: RockGroupsResponse): String {
    val result = StringBuilder()
    
    // Заголовок
    result.appendLine("🎸 Рок-группы по запросу: \"${response.query}\"")
    result.appendLine("Найдено групп: ${response.totalCount}")
    result.appendLine("═".repeat(60))
    
    if (response.groups.isEmpty()) {
        result.appendLine("К сожалению, информация о группах не найдена.")
        return result.toString()
    }
    
    // Выводим информацию о каждой группе
    response.groups.forEachIndexed { index, group ->
        result.appendLine()
        result.appendLine("${index + 1}. 🎵 ${group.name} (${group.foundedYear})")
        result.appendLine("   🌍 Страна: ${group.country}")
        result.appendLine("   🎼 Жанр: ${group.genre}")
        
        // Известные альбомы
        if (group.famousAlbums.isNotEmpty()) {
            result.appendLine("   💿 Известные альбомы:")
            group.famousAlbums.forEach { album ->
                result.appendLine("      • $album")
            }
        }
        
        // Участники группы
        if (group.members.isNotEmpty()) {
            result.appendLine("   👥 Участники:")
            group.members.forEach { member ->
                result.appendLine("      • $member")
            }
        }
        
        // Описание
        if (group.description.isNotBlank()) {
            result.appendLine("   📝 Описание: ${group.description}")
        }
        
        // Разделитель между группами
        if (index < response.groups.size - 1) {
            result.appendLine("─".repeat(40))
        }
    }
    
    result.appendLine()
    result.appendLine("═".repeat(60))
    
    return result.toString()
}