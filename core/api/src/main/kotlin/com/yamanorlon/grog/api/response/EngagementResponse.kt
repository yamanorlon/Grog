package com.yamanorlon.grog.api.response

import io.ktor.openapi.JsonSchema
import kotlinx.serialization.Serializable
import com.yamanorlon.grog.domain.model.EngagementStatus

@Serializable
@JsonSchema.Description("Ответ API по Engagement")
data class EngagementResponse(
    @JsonSchema.Description("Engagement UUID")
    @JsonSchema.Format("uuid")
    val id: String,
    @JsonSchema.Description("Product UUID")
    @JsonSchema.Format("uuid")
    val productId: String,
    @JsonSchema.Description("Имя Engagement")
    val name: String,
    @JsonSchema.Description("Описание Engagement")
    val description: String? = null,
    @JsonSchema.Description("Цель Engagement")
    val target: String? = null,
    @JsonSchema.Description("Статус Engagement")
    val status: EngagementStatus,
    @JsonSchema.Description("Дата старта Engagement")
    @JsonSchema.Format("date-time")
    val startDate: String,
    @JsonSchema.Description("Дата закрытия Engagement")
    @JsonSchema.Format("date-time")
    val endDate: String? = null,
    @JsonSchema.Description("Дата создания Engagement")
    @JsonSchema.Format("date-time")
    val createdAt: String,
    @JsonSchema.Description("Дата последнего обновления")
    @JsonSchema.Format("date-time")
    val updatedAt: String,
    @JsonSchema.Description("Автор создания")
    val createdBy: String? = null,
    @JsonSchema.Description("Автор последнего обновления")
    val updatedBy: String? = null,
    @JsonSchema.Description("Версия для оптимистической блокировки")
    val version: Long,
)
