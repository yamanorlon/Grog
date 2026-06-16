package com.yamanorlon.grog.factories

import com.yamanorlon.grog.api.request.CreateEngagementRequest
import com.yamanorlon.grog.api.request.CreateFindingRequest
import com.yamanorlon.grog.api.request.CreateProductRequest
import com.yamanorlon.grog.api.request.UpdateEngagementRequest
import com.yamanorlon.grog.api.request.UpdateFindingRequest
import com.yamanorlon.grog.api.request.UpdateProductRequest
import com.yamanorlon.grog.domain.model.EngagementStatus
import com.yamanorlon.grog.domain.model.FindingStatus
import com.yamanorlon.grog.domain.model.Severity
import java.util.UUID

object ProductTestFactory {
    fun validCreateRequest(
        name: String = "Test Product",
        description: String? = "Test description",
        owner: String? = "qa-team",
        tags: List<String> = listOf("test"),
    ) = CreateProductRequest(name = name, description = description, owner = owner, tags = tags)

    fun blankNameCreateRequest() = CreateProductRequest(name = "   ")

    fun longNameCreateRequest() = CreateProductRequest(name = "x".repeat(300))

    fun validUpdateRequest(version: Long, name: String = "Updated Product") =
        UpdateProductRequest(name = name, description = "Updated", owner = "qa-team", tags = listOf("updated"), version = version)
}

object EngagementTestFactory {
    fun validCreateRequest(
        productId: String,
        name: String = "Q1 Pentest",
        startDate: String = "2026-01-15T10:00:00Z",
        status: EngagementStatus = EngagementStatus.Planned,
    ) = CreateEngagementRequest(
        productId = productId,
        name = name,
        description = "Security assessment",
        target = "https://app.example.com",
        status = status,
        startDate = startDate,
    )

    fun invalidDateRangeRequest(productId: String) = CreateEngagementRequest(
        productId = productId,
        name = "Bad dates",
        startDate = "2026-06-01T00:00:00Z",
        endDate = "2026-01-01T00:00:00Z",
    )

    fun invalidProductIdRequest() = CreateEngagementRequest(
        productId = "not-a-uuid",
        name = "Invalid",
        startDate = "2026-01-15T10:00:00Z",
    )

    fun validUpdateRequest(productId: String, version: Long) = UpdateEngagementRequest(
        productId = productId,
        name = "Updated Engagement",
        description = "Updated",
        target = "https://updated.example.com",
        status = EngagementStatus.InProgress,
        startDate = "2026-01-15T10:00:00Z",
        version = version,
    )
}

object FindingTestFactory {
    fun validCreateRequest(
        engagementId: String,
        title: String = "SQL Injection",
        severity: Severity = Severity.High,
    ) = CreateFindingRequest(
        engagementId = engagementId,
        title = title,
        description = "Unsanitized input in login form",
        severity = severity,
        status = FindingStatus.Open,
        cvssScore = 8.1,
        cve = "CVE-2026-0001",
        cwe = "CWE-89",
        mitigation = "Use parameterized queries",
        impact = "Data breach",
        references = listOf("https://owasp.org"),
        discoveredDate = "2026-01-10",
    )

    fun blankTitleRequest(engagementId: String) = CreateFindingRequest(
        engagementId = engagementId,
        title = "",
        description = "Description",
        severity = Severity.Low,
    )

    fun blankDescriptionRequest(engagementId: String) = CreateFindingRequest(
        engagementId = engagementId,
        title = "Valid title",
        description = "",
        severity = Severity.Low,
    )

    fun invalidEngagementIdRequest() = CreateFindingRequest(
        engagementId = UUID.randomUUID().toString().replace('-', 'x'),
        title = "Finding",
        description = "Description",
        severity = Severity.Medium,
    )

    fun validUpdateRequest(engagementId: String, version: Long) = UpdateFindingRequest(
        engagementId = engagementId,
        title = "Updated Finding",
        description = "Updated description",
        severity = Severity.Critical,
        status = FindingStatus.Open,
        cvssScore = 9.0,
        version = version,
    )
}
