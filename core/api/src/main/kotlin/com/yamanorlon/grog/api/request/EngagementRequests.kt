package com.yamanorlon.grog.api.request

import io.ktor.openapi.JsonSchema
import kotlinx.serialization.Serializable
import com.yamanorlon.grog.domain.model.EngagementStatus

@Serializable
@JsonSchema.Description("Данные для создание Engagement")
data class CreateEngagementRequest(
    @JsonSchema.Description("Product UUID")
    @JsonSchema.Format("uuid")
    val productId: String,
    @JsonSchema.Description("Имя Engagement")
    val name: String,
    @JsonSchema.Description("Описание Engagement")
    val description: String? = null,
    @JsonSchema.Description("Цель Engagement (ссылка на репо в GitLab (мб уберу в бущущем))")
    val target: String? = null,
    @JsonSchema.Description("Статус Engagement")
    val status: EngagementStatus = EngagementStatus.Planned,
    @JsonSchema.Description("Дата старта Engagement")
    @JsonSchema.Format("date-time")
    val startDate: String,
    @JsonSchema.Description("Дата закрытия Engagement (необязательная)")
    @JsonSchema.Format("date-time")
    val endDate: String? = null,
)

@Serializable
@JsonSchema.Description("Данные для обновления Engagement")
data class UpdateEngagementRequest(
    @JsonSchema.Description("Новый Product UUID")
    @JsonSchema.Format("uuid")
    val productId: String,
    @JsonSchema.Description("Новое имя Engagement")
    val name: String,
    @JsonSchema.Description("Новое описание")
    val description: String? = null,
    @JsonSchema.Description("Новая цель Engagement")
    val target: String? = null,
    @JsonSchema.Description("Новый статус Engagement")
    val status: EngagementStatus,
    @JsonSchema.Description("Новая дата старта")
    @JsonSchema.Format("date-time")
    val startDate: String,
    @JsonSchema.Description("Дата закрытия Engagement (необязательная)")
    @JsonSchema.Format("date-time")
    val endDate: String? = null,
    @JsonSchema.Description("Версия для оптимистической блокировки")
    val version: Long,
)
