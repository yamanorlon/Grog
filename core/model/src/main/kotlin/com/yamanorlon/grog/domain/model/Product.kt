package com.yamanorlon.grog.domain.model

import java.util.UUID
import java.time.Instant

data class Product(
    val id: UUID,
    val name: String,
    val description: String?,
    val owner: String?,
    val tags: List<String>,
    val createdAt: Instant,
    val updatedAt: Instant,
    val createdBy: String?,
    val updatedBy: String?,
    val version: Long,
)
