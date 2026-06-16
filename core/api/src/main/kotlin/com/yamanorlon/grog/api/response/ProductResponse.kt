package com.yamanorlon.grog.api.response

import io.ktor.openapi.JsonSchema
import kotlinx.serialization.Serializable

@Serializable
@JsonSchema.Description("Ответ Api по Product")
data class ProductResponse(
    @JsonSchema.Description("Product UUID")
    @JsonSchema.Format("uuid")
    val id: String,
    @JsonSchema.Description("Имя Product")
    val name: String,
    @JsonSchema.Description("Описание Product")
    val description: String? = null,
    @JsonSchema.Description("Владелец Product")
    val owner: String? = null,
    @JsonSchema.Description("Теги")
    val tags: List<String> = emptyList(),
    @JsonSchema.Description("Дата создания")
    @JsonSchema.Format("date-time")
    val createdAt: String,
    @JsonSchema.Description("Дата последнего обновления")
    @JsonSchema.Format("date-time")
    val updatedAt: String,
    @JsonSchema.Description("Автор создания Product")
    val createdBy: String? = null,
    @JsonSchema.Description("Автор последнего обновления Product")
    val updatedBy: String? = null,
    @JsonSchema.Description("Версия для оптимистической блокировки")
    val version: Long,
)
