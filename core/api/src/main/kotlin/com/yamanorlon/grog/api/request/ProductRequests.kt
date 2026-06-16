package com.yamanorlon.grog.api.request

import io.ktor.openapi.JsonSchema
import kotlinx.serialization.Serializable

@Serializable
@JsonSchema.Description("Данные для создание Product")
data class CreateProductRequest(
    @JsonSchema.Description("Имя Product (должно быть уникальным)")
    val name: String,
    @JsonSchema.Description("Описание Product")
    val description: String? = null,
    @JsonSchema.Description("Кто владеет проектом (Product Owner) (можно сделать из GitLab Owner (после запила интеграции))")
    val owner: String? = null,
    @JsonSchema.Description("Теги")
    val tags: List<String> = emptyList(),
)

@Serializable
@JsonSchema.Description("Данные для обновление Product")
data class UpdateProductRequest(
    @JsonSchema.Description("Новое имя Product (должно быть уникальным)")
    val name: String,
    @JsonSchema.Description("Новое описание Product")
    val description: String? = null,
    @JsonSchema.Description("Обновленые Product Owner")
    val owner: String? = null,
    @JsonSchema.Description("Новые теги")
    val tags: List<String> = emptyList(),
    @JsonSchema.Description("Версия для оптимистической блокировки")
    val version: Long,
)
