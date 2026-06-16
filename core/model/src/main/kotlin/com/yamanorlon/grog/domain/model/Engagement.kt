package com.yamanorlon.grog.domain.model

import java.util.UUID
import java.time.Instant

data class Engagement(
    val id: UUID,
    val productId: UUID,
    val name: String,
    val description: String?,
    val target: String?,
    val status: EngagementStatus,
    val startDate: Instant,
    val endDate: Instant?,
    val createdAt: Instant,
    val updatedAt: Instant,
    val createdBy: String?,
    val updatedBy: String?,
    val version: Long,
)
