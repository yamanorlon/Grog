package com.yamanorlon.grog.factories

import com.yamanorlon.grog.domain.model.Engagement
import com.yamanorlon.grog.domain.model.EngagementStatus
import com.yamanorlon.grog.domain.model.Finding
import com.yamanorlon.grog.domain.model.FindingStatus
import com.yamanorlon.grog.domain.model.Product
import com.yamanorlon.grog.domain.model.Severity
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

object ProductEntityFactory {
    fun valid(
        id: UUID = UUID.randomUUID(),
        name: String = "Payments API",
        now: Instant = Instant.parse("2026-01-15T10:00:00Z"),
    ) = Product(
        id = id,
        name = name,
        description = "Core service",
        owner = "security-team",
        tags = listOf("pci"),
        createdAt = now,
        updatedAt = now,
        createdBy = "tester",
        updatedBy = "tester",
        version = 0,
    )
}

object EngagementEntityFactory {
    fun valid(
        productId: UUID,
        id: UUID = UUID.randomUUID(),
        now: Instant = Instant.parse("2026-01-15T10:00:00Z"),
    ) = Engagement(
        id = id,
        productId = productId,
        name = "Q1 Pentest",
        description = "Annual assessment",
        target = "https://app.example.com",
        status = EngagementStatus.Planned,
        startDate = now,
        endDate = null,
        createdAt = now,
        updatedAt = now,
        createdBy = "tester",
        updatedBy = "tester",
        version = 0,
    )
}

object FindingEntityFactory {
    fun valid(
        engagementId: UUID,
        id: UUID = UUID.randomUUID(),
        now: Instant = Instant.parse("2026-01-15T10:00:00Z"),
    ) = Finding(
        id = id,
        engagementId = engagementId,
        title = "XSS in search",
        description = "Reflected XSS in query parameter",
        severity = Severity.High,
        status = FindingStatus.Open,
        cvssScore = 7.5,
        cve = "CVE-2026-0100",
        cwe = "CWE-79",
        mitigation = "Encode output",
        impact = "Session hijack",
        references = listOf("https://cwe.mitre.org/data/definitions/79.html"),
        discoveredDate = LocalDate.parse("2026-01-10"),
        createdAt = now,
        updatedAt = now,
        createdBy = "tester",
        updatedBy = "tester",
        version = 0,
    )
}
