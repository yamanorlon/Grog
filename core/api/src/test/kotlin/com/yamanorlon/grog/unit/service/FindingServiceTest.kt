package com.yamanorlon.grog.unit.service

import com.yamanorlon.grog.domain.exception.ConflictException
import com.yamanorlon.grog.domain.exception.NotFoundException
import com.yamanorlon.grog.domain.exception.ReferenceNotFoundException
import com.yamanorlon.grog.domain.exception.ValidationException
import com.yamanorlon.grog.domain.model.Finding
import com.yamanorlon.grog.domain.model.FindingStatus
import com.yamanorlon.grog.domain.model.Page
import com.yamanorlon.grog.domain.model.PageRequest
import com.yamanorlon.grog.domain.model.Severity
import com.yamanorlon.grog.domain.repository.FindingRepository
import com.yamanorlon.grog.service.FindingService
import com.yamanorlon.grog.factories.FindingTestFactory
import com.yamanorlon.grog.util.DateTimeUtils
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID

class FindingServiceTest {

    private val repository = mockk<FindingRepository>()
    private val service = FindingService(repository)

    @Test
    fun `create succeeds when engagement exists`() {
        val engagementId = UUID.randomUUID()
        every { repository.existsEngagement(engagementId) } returns true
        every { repository.create(any()) } answers { firstArg() }

        val created = service.create(
            FindingTestFactory.validCreateRequest(engagementId.toString()),
            "tester",
        )

        assertEquals("SQL Injection", created.title)
        assertEquals(Severity.High, created.severity)
        verify { repository.create(any()) }
    }

    @Test
    fun `create fails when engagement does not exist`() {
        val engagementId = UUID.randomUUID()
        every { repository.existsEngagement(engagementId) } returns false

        assertThrows<ReferenceNotFoundException> {
            service.create(FindingTestFactory.validCreateRequest(engagementId.toString()), "tester")
        }
    }

    @Test
    fun `create rejects blank title`() {
        assertThrows<ValidationException> {
            service.create(FindingTestFactory.blankTitleRequest(UUID.randomUUID().toString()), "tester")
        }
    }

    @Test
    fun `create rejects blank description`() {
        assertThrows<ValidationException> {
            service.create(FindingTestFactory.blankDescriptionRequest(UUID.randomUUID().toString()), "tester")
        }
    }

    @Test
    fun `create rejects invalid engagement id format`() {
        assertThrows<ValidationException> {
            service.create(FindingTestFactory.invalidEngagementIdRequest(), "tester")
        }
    }

    @Test
    fun `getById returns finding when found`() {
        val id = UUID.randomUUID()
        val finding = sampleFinding(id)
        every { repository.findById(id) } returns finding

        assertEquals(id, service.getById(id).id)
    }

    @Test
    fun `getById throws when not found`() {
        val id = UUID.randomUUID()
        every { repository.findById(id) } returns null

        assertThrows<NotFoundException> { service.getById(id) }
    }

    @Test
    fun `list delegates to repository`() {
        every { repository.findAll(any(), any(), any()) } returns Page(emptyList(), 0, 20, 0)

        val page = service.list(com.yamanorlon.grog.domain.model.FindingFilter(), PageRequest(), null)

        assertEquals(0, page.totalElements)
    }

    @Test
    fun `update succeeds with valid references`() {
        val id = UUID.randomUUID()
        val engagementId = UUID.randomUUID()
        val existing = sampleFinding(id, engagementId, version = 0)
        every { repository.findById(id) } returns existing
        every { repository.existsEngagement(engagementId) } returns true
        every { repository.update(any()) } answers { firstArg() }

        val updated = service.update(
            id,
            FindingTestFactory.validUpdateRequest(engagementId.toString(), version = 0),
            "tester",
        )

        assertEquals("Updated Finding", updated.title)
        assertEquals(Severity.Critical, updated.severity)
    }

    @Test
    fun `update throws when finding missing`() {
        val id = UUID.randomUUID()
        every { repository.findById(id) } returns null

        assertThrows<NotFoundException> {
            service.update(id, FindingTestFactory.validUpdateRequest(UUID.randomUUID().toString(), 0), "tester")
        }
    }

    @Test
    fun `update throws conflict on stale version`() {
        val id = UUID.randomUUID()
        val engagementId = UUID.randomUUID()
        every { repository.findById(id) } returns sampleFinding(id, engagementId, version = 1)
        every { repository.existsEngagement(engagementId) } returns true
        every { repository.update(any()) } returns null

        assertThrows<ConflictException> {
            service.update(id, FindingTestFactory.validUpdateRequest(engagementId.toString(), version = 1), "tester")
        }
    }

    @Test
    fun `delete succeeds when finding exists`() {
        val id = UUID.randomUUID()
        every { repository.delete(id) } returns true

        service.delete(id)

        verify { repository.delete(id) }
    }

    @Test
    fun `delete throws when finding missing`() {
        val id = UUID.randomUUID()
        every { repository.delete(id) } returns false

        assertThrows<NotFoundException> { service.delete(id) }
    }

    private fun sampleFinding(id: UUID, engagementId: UUID = UUID.randomUUID(), version: Long = 0): Finding {
        val now = DateTimeUtils.now()
        return Finding(
            id = id,
            engagementId = engagementId,
            title = "Finding",
            description = "Description",
            severity = Severity.Medium,
            status = FindingStatus.Open,
            cvssScore = null,
            cve = null,
            cwe = null,
            mitigation = null,
            impact = null,
            references = emptyList(),
            discoveredDate = null,
            createdAt = now,
            updatedAt = now,
            createdBy = "tester",
            updatedBy = "tester",
            version = version,
        )
    }
}
