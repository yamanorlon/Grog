package com.yamanorlon.grog.validation

import java.util.UUID
import io.konform.validation.Validation
import com.yamanorlon.grog.util.DateTimeUtils
import io.konform.validation.constraints.minimum
import io.konform.validation.constraints.maximum
import io.konform.validation.constraints.maxLength
import io.konform.validation.constraints.minLength
import com.yamanorlon.grog.api.request.CreateFindingRequest
import com.yamanorlon.grog.api.request.CreateProductRequest
import com.yamanorlon.grog.api.request.UpdateFindingRequest
import com.yamanorlon.grog.api.request.UpdateProductRequest
import com.yamanorlon.grog.api.request.CreateEngagementRequest
import com.yamanorlon.grog.api.request.UpdateEngagementRequest

object RequestValidators {
    private val uuidPattern = Regex("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")

    fun validateCreateProduct(request: CreateProductRequest): List<String> =
        createProductValidator.validate(request).errors.map { "${it.dataPath}: ${it.message}" }

    fun validateUpdateProduct(request: UpdateProductRequest): List<String> =
        updateProductValidator.validate(request).errors.map { "${it.dataPath}: ${it.message}" }

    fun validateCreateEngagement(request: CreateEngagementRequest): List<String> =
        createEngagementValidator.validate(request).errors.map { "${it.dataPath}: ${it.message}" }

    fun validateUpdateEngagement(request: UpdateEngagementRequest): List<String> =
        updateEngagementValidator.validate(request).errors.map { "${it.dataPath}: ${it.message}" }

    fun validateCreateFinding(request: CreateFindingRequest): List<String> =
        createFindingValidator.validate(request).errors.map { "${it.dataPath}: ${it.message}" }

    fun validateUpdateFinding(request: UpdateFindingRequest): List<String> =
        updateFindingValidator.validate(request).errors.map { "${it.dataPath}: ${it.message}" }

    fun parseUuid(value: String, field: String): UUID {
        if (!uuidPattern.matches(value)) {
            throw IllegalArgumentException("$field должно иметь валидный UUID")
        }
        return UUID.fromString(value)
    }

    private val createProductValidator = Validation<CreateProductRequest> {
        CreateProductRequest::name {
            constrain("не может быть пустым") {
                it.trim().isNotEmpty()
            }
            maxLength(255)
        }

        CreateProductRequest::description ifPresent {
            maxLength(5000)
        }

        CreateProductRequest::owner ifPresent {
            maxLength(255)
        }
    }

    private val updateProductValidator = Validation<UpdateProductRequest> {
        UpdateProductRequest::name {
            constrain("не может бытьп пустым") { it.trim().isNotEmpty() }
            maxLength(255)
        }

        UpdateProductRequest::description ifPresent {
            maxLength(5000)
        }

        UpdateProductRequest::owner ifPresent {
            maxLength(255)
        }

        UpdateProductRequest::version {
            constrain("must be non-negative") { it >= 0 }
        }
    }

    private val createEngagementValidator = Validation<CreateEngagementRequest> {
        CreateEngagementRequest::productId {
            minLength(36)
            maxLength(36)
            constrain("должен иметь валидный UUID") {
                uuidPattern.matches(it)
            }
        }

        CreateEngagementRequest::name {
            minLength(1)
            maxLength(255)
        }

        CreateEngagementRequest::description ifPresent {
            maxLength(5000)
        }

        CreateEngagementRequest::target ifPresent {
            maxLength(500)
        }

        CreateEngagementRequest::startDate {
            minLength(1)
            constrain("должен иметь стандарт ISO-8601") {
                isValidInstant(it)
            }
        }

        CreateEngagementRequest::endDate ifPresent {
            constrain("должен иметь стандарт ISO-8601") {
                isValidInstant(it)
            }
        }

        constrain("endData не может быть раньше startData") {
            val end = it.endDate ?: return@constrain true
            DateTimeUtils.parseInstant(end) >= DateTimeUtils.parseInstant(it.startDate)
        }
    }

    private val updateEngagementValidator = Validation<UpdateEngagementRequest> {
        UpdateEngagementRequest::productId {
            minLength(36)
            maxLength(36)
            constrain("должен иметь валидный UUID") {
                uuidPattern.matches(it)
            }
        }

        UpdateEngagementRequest::name {
            minLength(1)
            maxLength(255)
        }

        UpdateEngagementRequest::description ifPresent {
            maxLength(5000)
        }

        UpdateEngagementRequest::target ifPresent {
            maxLength(500)
        }

        UpdateEngagementRequest::startDate {
            minLength(1)
            constrain("должен иметь стандарт ISO-8601") {
                isValidInstant(it)
            }
        }

        UpdateEngagementRequest::endDate ifPresent {
            constrain("должен иметь стандарт ISO-8601") {
                isValidInstant(it)
            }
        }

        UpdateEngagementRequest::version {
            constrain("не может быть отрицательным") {
                it >= 0
            }
        }

        constrain("endData не может быть раньше startData") {
            val end = it.endDate ?: return@constrain true
            DateTimeUtils.parseInstant(end) >= DateTimeUtils.parseInstant(it.startDate)
        }
    }

    private val createFindingValidator = Validation<CreateFindingRequest> {
        CreateFindingRequest::engagementId {
            minLength(36)
            maxLength(36)
            constrain("должен иметь валидный UUID") {
                uuidPattern.matches(it)
            }
        }

        CreateFindingRequest::title {
            minLength(1)
            maxLength(500)
        }

        CreateFindingRequest::description {
            minLength(1)
            maxLength(10000)
        }

        CreateFindingRequest::cvssScore ifPresent {
            minimum(0.0)
            maximum(10.0)
        }

        CreateFindingRequest::cve ifPresent {
            maxLength(50)
        }

        CreateFindingRequest::cwe ifPresent {
            maxLength(50)
        }

        CreateFindingRequest::discoveredDate ifPresent {
            constrain("должен иметь стандарт ISO-8601") {
                isValidDate(it)
            }
        }
    }

    private val updateFindingValidator = Validation<UpdateFindingRequest> {
        UpdateFindingRequest::engagementId {
            minLength(36)
            maxLength(36)
            constrain("должен иметь валидный UUID") {
                uuidPattern.matches(it)
            }
        }

        UpdateFindingRequest::title {
            minLength(1)
            maxLength(500)
        }

        UpdateFindingRequest::description {
            minLength(1)
            maxLength(10000)
        }

        UpdateFindingRequest::cvssScore ifPresent {
            minimum(0.0)
            maximum(10.0)
        }

        UpdateFindingRequest::cve ifPresent {
            maxLength(50)
        }

        UpdateFindingRequest::cwe ifPresent {
            maxLength(50)
        }

        UpdateFindingRequest::discoveredDate ifPresent {
            constrain("должен иметь стандарт ISO-8601") {
                isValidDate(it)
            }
        }

        UpdateFindingRequest::version {
            constrain("endData не может быть раньше startData") {
                it >= 0
            }
        }
    }

    private fun isValidInstant(value: String): Boolean = runCatching {
        DateTimeUtils.parseInstant(value)
    }.isSuccess

    private fun isValidDate(value: String): Boolean = runCatching {
        DateTimeUtils.parseDate(value)
    }.isSuccess
}
