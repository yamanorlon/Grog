package com.yamanorlon.grog.api.response

import io.ktor.openapi.JsonSchema
import kotlinx.serialization.Serializable

@Serializable
@JsonSchema.Description("Данные при ошибки API запроса")
data class ErrorResponse(
    @JsonSchema.Description("Временная метка")
    @JsonSchema.Format("date-time")
    val timestamp: String,
    @JsonSchema.Description("HTTP статус код")
    val status: Int,
    @JsonSchema.Description("HTTP статус фраза (Forbiden или др.)")
    val error: String,
    @JsonSchema.Description("Понятное сообщение при ошибке")
    val message: String,
    @JsonSchema.Description("Путь к ошибке")
    val path: String,
    @JsonSchema.Description("Дополнительные детали ошибки (опционально)")
    val details: List<String> = emptyList(),
)