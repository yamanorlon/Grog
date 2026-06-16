package com.yamanorlon.grog.service

import java.util.UUID
import org.slf4j.LoggerFactory
import com.yamanorlon.grog.domain.model.Page
import com.yamanorlon.grog.util.DateTimeUtils
import com.yamanorlon.grog.domain.model.Finding
import com.yamanorlon.grog.domain.model.SortSpec
import com.yamanorlon.grog.domain.model.PageRequest
import com.yamanorlon.grog.domain.model.FindingFilter
import com.yamanorlon.grog.validation.RequestValidators
import com.yamanorlon.grog.api.request.CreateFindingRequest
import com.yamanorlon.grog.api.request.UpdateFindingRequest
import com.yamanorlon.grog.domain.exception.ConflictException
import com.yamanorlon.grog.domain.exception.NotFoundException
import com.yamanorlon.grog.domain.repository.FindingRepository
import com.yamanorlon.grog.domain.exception.ValidationException
import com.yamanorlon.grog.domain.exception.ReferenceNotFoundException

class FindingService(
    private val findingRepository: FindingRepository,
) {
    private val logger = LoggerFactory.getLogger(FindingService::class.java)

    fun create(request: CreateFindingRequest, actor: String?): Finding {
        val errors = RequestValidators.validateCreateFinding(request)
        if (errors.isNotEmpty()) throw ValidationException(errors)

        val engagementId = RequestValidators.parseUuid(request.engagementId, "engagementId")
        if (!findingRepository.existsEngagement(engagementId)) {
            throw ReferenceNotFoundException("Engagement с ID=$engagementId не найден")
        }

        logger.info("Создание сработки с именем={} engagementId={}", request.title, engagementId)
        val now = DateTimeUtils.now()
        val finding = Finding(
            id = UUID.randomUUID(),
            engagementId = engagementId,
            title = request.title.trim(),
            description = request.description.trim(),
            severity = request.severity,
            status = request.status,
            cvssScore = request.cvssScore,
            cve = request.cve?.trim(),
            cwe = request.cwe?.trim(),
            mitigation = request.mitigation?.trim(),
            impact = request.impact?.trim(),
            references = request.references.map { it.trim() }.filter { it.isNotEmpty() },
            discoveredDate = request.discoveredDate?.let(DateTimeUtils::parseDate),
            createdAt = now,
            updatedAt = now,
            createdBy = actor,
            updatedBy = actor,
            version = 0,
        )
        return findingRepository.create(finding)
    }

    fun update(id: UUID, request: UpdateFindingRequest, actor: String?): Finding {
        val errors = RequestValidators.validateUpdateFinding(request)
        if (errors.isNotEmpty()) throw ValidationException(errors)

        val existing = findingRepository.findById(id)
            ?: throw NotFoundException("Сработка с ID=$id не найдена")

        val engagementId = RequestValidators.parseUuid(request.engagementId, "engagementId")
        if (!findingRepository.existsEngagement(engagementId)) {
            throw ReferenceNotFoundException("Engagement с ID=$engagementId не найден")
        }

        val updated = existing.copy(
            engagementId = engagementId,
            title = request.title.trim(),
            description = request.description.trim(),
            severity = request.severity,
            status = request.status,
            cvssScore = request.cvssScore,
            cve = request.cve?.trim(),
            cwe = request.cwe?.trim(),
            mitigation = request.mitigation?.trim(),
            impact = request.impact?.trim(),
            references = request.references.map { it.trim() }.filter { it.isNotEmpty() },
            discoveredDate = request.discoveredDate?.let(DateTimeUtils::parseDate),
            updatedBy = actor,
            version = request.version,
        )

        val result = findingRepository.update(updated)
            ?: throw ConflictException("Сработка была изменена в другой транзакции.")

        logger.info("Успешно обновлена сработка с ID={}", id)
        return result
    }

    fun getById(id: UUID): Finding =
        findingRepository.findById(id) ?: throw NotFoundException("Сработка с ID=$id не найдена")

    fun list(filter: FindingFilter, page: PageRequest, sort: SortSpec?): Page<Finding> =
        findingRepository.findAll(filter, page, sort)

    fun delete(id: UUID) {
        val deleted = findingRepository.delete(id)
        if (!deleted) throw NotFoundException("Сработка с ID=$id не найдена")
        logger.info("Успешно удалена сработка с ID={}", id)
    }
}
