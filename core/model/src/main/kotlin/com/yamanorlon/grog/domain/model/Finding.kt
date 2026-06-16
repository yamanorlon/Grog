package com.yamanorlon.grog.domain.model

import java.util.UUID
import java.time.Instant
import java.time.LocalDate

data class Finding(
    val id: UUID,
    val engagementId: UUID,
    val title: String,
    val description: String,
    val severity: Severity,
    val status: FindingStatus,
    val cvssScore: Double?,
    val cve: String?,
    val cwe: String?,
    val mitigation: String?,
    val impact: String?,
    val references: List<String>,
    val discoveredDate: LocalDate?,
    val createdAt: Instant,
    val updatedAt: Instant,
    val createdBy: String?,
    val updatedBy: String?,
    val version: Long,
)
