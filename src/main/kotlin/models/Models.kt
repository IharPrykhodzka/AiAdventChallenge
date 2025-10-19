package ru.aiAdventChallenge.models

import kotlinx.serialization.Serializable

@Serializable
data class ZAiMessage(
    val role: String,
    val content: String
)

@Serializable
data class ZAiRequest(
    val model: String,
    val messages: List<ZAiMessage>,
    val temperature: Int = 1,
    val max_tokens: Int = 65536,
    val stream: Boolean = false
)

@Serializable
data class ZAiToolCall(
    val function: ZAiFunction,
    val id: String,
    val type: String
)

@Serializable
data class ZAiFunction(
    val name: String,
    val arguments: String = "{}"
)

@Serializable
data class ZAiMessageResponse(
    val role: String,
    val content: String,
    val reasoning_content: String? = null,
    val tool_calls: List<ZAiToolCall> = emptyList()
)

@Serializable
data class ZAiChoice(
    val index: Int,
    val message: ZAiMessageResponse,
    val finish_reason: String
)

@Serializable
data class ZAiPromptTokensDetails(
    val cached_tokens: Int
)

@Serializable
data class ZAiUsage(
    val prompt_tokens: Int,
    val completion_tokens: Int,
    val prompt_tokens_details: ZAiPromptTokensDetails? = null,
    val total_tokens: Int
)

@Serializable
data class ZAiWebSearch(
    val title: String,
    val content: String,
    val link: String,
    val media: String? = null,
    val icon: String? = null,
    val refer: String? = null,
    val publish_date: String? = null
)

@Serializable
data class ZAiResponse(
    val id: String,
    val request_id: String,
    val created: Int,
    val model: String,
    val choices: List<ZAiChoice>,
    val usage: ZAiUsage,
    val web_search: List<ZAiWebSearch> = emptyList()
)

@Serializable
data class ZAiError(
    val code: String,
    val message: String? = null
)

@Serializable
data class ZAiErrorResponse(
    val error: ZAiError
)

// Модели для ответа о музыкальных группах в формате JSON
@Serializable
data class RockGroup(
    val name: String,
    val foundedYear: Int,
    val country: String,
    val genre: String,
    val famousAlbums: List<String>,
    val members: List<String>,
    val description: String
)

@Serializable
data class RockGroupsResponse(
    val groups: List<RockGroup>,
    val totalCount: Int,
    val query: String
)