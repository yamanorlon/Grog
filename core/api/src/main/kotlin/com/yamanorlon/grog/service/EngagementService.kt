package com.yamanorlon.grog.service

import java.util.UUID
import org.slf4j.LoggerFactory
import com.yamanorlon.grog.domain.model.Page
import com.yamanorlon.grog.util.DateTimeUtils
import com.yamanorlon.grog.domain.model.SortSpec
import com.yamanorlon.grog.domain.model.Engagement
import com.yamanorlon.grog.domain.model.PageRequest
import com.yamanorlon.grog.validation.RequestValidators
import com.yamanorlon.grog.domain.model.EngagementFilter
import com.yamanorlon.grog.domain.exception.ConflictException
import com.yamanorlon.grog.domain.exception.NotFoundException
import com.yamanorlon.grog.api.request.CreateEngagementRequest
import com.yamanorlon.grog.api.request.UpdateEngagementRequest
import com.yamanorlon.grog.domain.exception.ValidationException
import com.yamanorlon.grog.domain.repository.EngagementRepository
import com.yamanorlon.grog.domain.exception.ReferenceNotFoundException

class EngagementService(
    private val engagementRepository: EngagementRepository,
) {
    private val logger = LoggerFactory.getLogger(EngagementService::class.java)

    fun create(request: CreateEngagementRequest, actor: String?): Engagement {
        val errors = RequestValidators.validateCreateEngagement(request)
        if (errors.isNotEmpty()) throw ValidationException(errors)

        val productId = RequestValidators.parseUuid(request.productId, "productId")
        if (!engagementRepository.existsProduct(productId)) {
            throw ReferenceNotFoundException("Product с ID=$productId не найден")
        }

        logger.info("Создание Engagement с именем={} productId={}", request.name, productId)
        val now = DateTimeUtils.now()
        val engagement = Engagement(
            id = UUID.randomUUID(),
            productId = productId,
            name = request.name.trim(),
            description = request.description?.trim(),
            target = request.target?.trim(),
            status = request.status,
            startDate = DateTimeUtils.parseInstant(request.startDate),
            endDate = request.endDate?.let(DateTimeUtils::parseInstant),
            createdAt = now,
            updatedAt = now,
            createdBy = actor,
            updatedBy = actor,
            version = 0,
        )
        return engagementRepository.create(engagement)
    }

    fun update(id: UUID, request: UpdateEngagementRequest, actor: String?): Engagement {
        val errors = RequestValidators.validateUpdateEngagement(request)
        if (errors.isNotEmpty()) throw ValidationException(errors)

        engagementRepository.findById(id)
            ?: throw NotFoundException("Engagement с ID=$id не найден")

        val productId = RequestValidators.parseUuid(request.productId, "productId")
        if (!engagementRepository.existsProduct(productId)) {
            throw ReferenceNotFoundException("Product с ID=$productId не найден")
        }

        val updated = Engagement(
            id = id,
            productId = productId,
            name = request.name.trim(),
            description = request.description?.trim(),
            target = request.target?.trim(),
            status = request.status,
            startDate = DateTimeUtils.parseInstant(request.startDate),
            endDate = request.endDate?.let(DateTimeUtils::parseInstant),
            createdAt = DateTimeUtils.now(),
            updatedAt = DateTimeUtils.now(),
            createdBy = null,
            updatedBy = actor,
            version = request.version,
        )

        val existing = engagementRepository.findById(id)!!
        val result = engagementRepository.update(
            updated.copy(
                createdAt = existing.createdAt,
                createdBy = existing.createdBy,
            ),
        ) ?: throw ConflictException("Engagement был изменен в другой транзакции.")

        logger.info("Engagement с ID успешно изменен={}", id)
        return result
    }

    fun getById(id: UUID): Engagement =
        engagementRepository.findById(id) ?: throw NotFoundException("Engagement с ID=$id не найден")

    fun list(filter: EngagementFilter, page: PageRequest, sort: SortSpec?): Page<Engagement> =
        engagementRepository.findAll(filter, page, sort)

    fun delete(id: UUID) {
        val deleted = engagementRepository.delete(id)
        if (!deleted) throw NotFoundException("Engagement с ID=$id не найден")
        logger.info("Удален Engagement с ID={}", id)
    }
}
