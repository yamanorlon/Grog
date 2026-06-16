package com.yamanorlon.grog.api.response

import io.ktor.openapi.JsonSchema
import kotlinx.serialization.Serializable

@Serializable
@JsonSchema.Description("Пагинированый ответ с Products")
data class ProductPageResponse(
    val content: List<ProductResponse>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
)

@Serializable
@JsonSchema.Description("Пагинированый ответ с Engagements")
data class EngagementPageResponse(
    val content: List<EngagementResponse>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
)

@Serializable
@JsonSchema.Description("Пагинированый ответ с Findings")
data class FindingPageResponse(
    val content: List<FindingResponse>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
)
