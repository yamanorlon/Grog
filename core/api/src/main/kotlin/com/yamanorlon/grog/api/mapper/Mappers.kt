package com.yamanorlon.grog.api.mapper

import com.yamanorlon.grog.domain.model.Page
import com.yamanorlon.grog.util.DateTimeUtils
import com.yamanorlon.grog.domain.model.Finding
import com.yamanorlon.grog.domain.model.Product
import com.yamanorlon.grog.domain.model.Engagement
import com.yamanorlon.grog.api.response.FindingResponse
import com.yamanorlon.grog.api.response.ProductResponse
import com.yamanorlon.grog.api.response.EngagementResponse
import com.yamanorlon.grog.api.response.FindingPageResponse
import com.yamanorlon.grog.api.response.ProductPageResponse
import com.yamanorlon.grog.api.response.EngagementPageResponse


object ProductMapper {
    fun toResponse(product: Product) = ProductResponse(
        id = product.id.toString(),
        name = product.name,
        description = product.description,
        owner = product.owner,
        tags = product.tags,
        createdAt = DateTimeUtils.formatInstant(product.createdAt),
        updatedAt = DateTimeUtils.formatInstant(product.updatedAt),
        createdBy = product.createdBy,
        updatedBy = product.updatedBy,
        version = product.version,
    )

    fun toPageResponse(page: Page<Product>) = ProductPageResponse(
        content = page.content.map(::toResponse),
        page = page.page,
        size = page.size,
        totalElements = page.totalElements,
        totalPages = page.totalPages,
    )
}

object EngagementMapper {
    fun toResponse(engagement: Engagement) = EngagementResponse(
        id = engagement.id.toString(),
        productId = engagement.productId.toString(),
        name = engagement.name,
        description = engagement.description,
        target = engagement.target,
        status = engagement.status,
        startDate = DateTimeUtils.formatInstant(engagement.startDate),
        endDate = engagement.endDate?.let(DateTimeUtils::formatInstant),
        createdAt = DateTimeUtils.formatInstant(engagement.createdAt),
        updatedAt = DateTimeUtils.formatInstant(engagement.updatedAt),
        createdBy = engagement.createdBy,
        updatedBy = engagement.updatedBy,
        version = engagement.version,
    )

    fun toPageResponse(page: Page<Engagement>) = EngagementPageResponse(
        content = page.content.map(::toResponse),
        page = page.page,
        size = page.size,
        totalElements = page.totalElements,
        totalPages = page.totalPages,
    )
}

object FindingMapper {
    fun toResponse(finding: Finding) = FindingResponse(
        id = finding.id.toString(),
        engagementId = finding.engagementId.toString(),
        title = finding.title,
        description = finding.description,
        severity = finding.severity,
        status = finding.status,
        cvssScore = finding.cvssScore,
        cve = finding.cve,
        cwe = finding.cwe,
        mitigation = finding.mitigation,
        impact = finding.impact,
        references = finding.references,
        discoveredDate = finding.discoveredDate?.let(DateTimeUtils::formatDate),
        createdAt = DateTimeUtils.formatInstant(finding.createdAt),
        updatedAt = DateTimeUtils.formatInstant(finding.updatedAt),
        createdBy = finding.createdBy,
        updatedBy = finding.updatedBy,
        version = finding.version,
    )

    fun toPageResponse(page: Page<Finding>) = FindingPageResponse(
        content = page.content.map(::toResponse),
        page = page.page,
        size = page.size,
        totalElements = page.totalElements,
        totalPages = page.totalPages,
    )
}
