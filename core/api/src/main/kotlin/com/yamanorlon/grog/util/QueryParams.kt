package com.yamanorlon.grog.util

import java.util.UUID
import com.yamanorlon.grog.domain.model.SortSpec
import com.yamanorlon.grog.domain.model.Severity
import io.ktor.server.application.ApplicationCall
import com.yamanorlon.grog.domain.model.PageRequest
import com.yamanorlon.grog.domain.model.FindingFilter
import com.yamanorlon.grog.domain.model.FindingStatus
import com.yamanorlon.grog.domain.model.ProductFilter
import com.yamanorlon.grog.domain.model.SortDirection
import com.yamanorlon.grog.validation.RequestValidators
import com.yamanorlon.grog.domain.model.EngagementFilter
import com.yamanorlon.grog.domain.model.EngagementStatus

object QueryParams {

    fun pageRequest(call: ApplicationCall): PageRequest {
        val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 0
        val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 20
        return PageRequest(page = page, size = size)
    }

    fun sortSpec(call: ApplicationCall): SortSpec? {
        val sort = call.request.queryParameters["sort"] ?: return null
        val descending = sort.startsWith("-")
        val field = if (descending) sort.drop(1) else sort
        if (field.isBlank()) return null
        return SortSpec(
            field,
            if (descending){
                SortDirection.DESC
            }
            else {
                SortDirection.ASC
            }
        )
    }

    fun productFilter(call: ApplicationCall) = ProductFilter(
        name = call.request.queryParameters["name"],
        owner = call.request.queryParameters["owner"],
        tag = call.request.queryParameters["tag"],
    )

    fun engagementFilter(call: ApplicationCall): EngagementFilter {
        val productId = call.request.queryParameters["productId"]?.let {
            RequestValidators.parseUuid(it, "productId")
        }
        val status = call.request.queryParameters["status"]?.let { EngagementStatus.valueOf(it) }
        return EngagementFilter(
            productId = productId,
            status = status,
            name = call.request.queryParameters["name"],
        )
    }

    fun findingFilter(call: ApplicationCall): FindingFilter {
        val engagementId = call.request.queryParameters["engagementId"]?.let {
            RequestValidators.parseUuid(it, "engagementId")
        }
        val severity = call.request.queryParameters["severity"]?.let { Severity.valueOf(it) }
        val status = call.request.queryParameters["status"]?.let { FindingStatus.valueOf(it) }
        return FindingFilter(
            engagementId = engagementId,
            severity = severity,
            status = status,
            title = call.request.queryParameters["title"],
        )
    }

    fun actor(call: ApplicationCall): String? =
        call.request.headers["X-User-Id"]?.takeIf { it.isNotBlank() }

    fun uuidParam(call: ApplicationCall, name: String): UUID =
        RequestValidators.parseUuid(call.parameters[name] ?: throw IllegalArgumentException("$name is required"), name)

}
