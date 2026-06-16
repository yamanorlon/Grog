package com.yamanorlon.grog.unit.service

import com.yamanorlon.grog.domain.exception.ConflictException
import com.yamanorlon.grog.domain.exception.NotFoundException
import com.yamanorlon.grog.domain.exception.ReferenceNotFoundException
import com.yamanorlon.grog.domain.exception.ValidationException
import com.yamanorlon.grog.domain.model.Engagement
import com.yamanorlon.grog.domain.model.EngagementStatus
import com.yamanorlon.grog.domain.model.Page
import com.yamanorlon.grog.domain.model.PageRequest
import com.yamanorlon.grog.domain.repository.EngagementRepository
import com.yamanorlon.grog.service.EngagementService
import com.yamanorlon.grog.factories.EngagementTestFactory
import com.yamanorlon.grog.util.DateTimeUtils
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID

class EngagementServiceTest {

    private val repository = mockk<EngagementRepository>()
    private val service = EngagementService(repository)

    @Test
    fun `create succeeds when product exists`() {
        val productId = UUID.randomUUID()
        every { repository.existsProduct(productId) } returns true
        every { repository.create(any()) } answers { firstArg() }

        val created = service.create(
            EngagementTestFactory.validCreateRequest(productId.toString()),
            "tester",
        )

        assertEquals("Q1 Pentest", created.name)
        assertEquals(productId, created.productId)
        verify { repository.create(any()) }
    }

    @Test
    fun `create fails when product does not exist`() {
        val productId = UUID.randomUUID()
        every { repository.existsProduct(productId) } returns false

        assertThrows<ReferenceNotFoundException> {
            service.create(EngagementTestFactory.validCreateRequest(productId.toString()), "tester")
        }
    }

    @Test
    fun `create validates date range`() {
        assertThrows<ValidationException> {
            service.create(
                EngagementTestFactory.invalidDateRangeRequest(UUID.randomUUID().toString()),
                "tester",
            )
        }
    }

    @Test
    fun `create rejects invalid product id format`() {
        assertThrows<ValidationException> {
            service.create(EngagementTestFactory.invalidProductIdRequest(), "tester")
        }
    }

    @Test
    fun `getById returns engagement when found`() {
        val id = UUID.randomUUID()
        val engagement = sampleEngagement(id)
        every { repository.findById(id) } returns engagement

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

        val page = service.list(com.yamanorlon.grog.domain.model.EngagementFilter(), PageRequest(), null)

        assertEquals(0, page.totalElements)
    }

    @Test
    fun `update succeeds with valid references`() {
        val id = UUID.randomUUID()
        val productId = UUID.randomUUID()
        val existing = sampleEngagement(id, productId, version = 0)
        every { repository.findById(id) } returns existing
        every { repository.existsProduct(productId) } returns true
        every { repository.update(any()) } answers { firstArg() }

        val updated = service.update(
            id,
            EngagementTestFactory.validUpdateRequest(productId.toString(), version = 0),
            "tester",
        )

        assertEquals("Updated Engagement", updated.name)
    }

    @Test
    fun `update throws when engagement missing`() {
        val id = UUID.randomUUID()
        every { repository.findById(id) } returns null

        assertThrows<NotFoundException> {
            service.update(id, EngagementTestFactory.validUpdateRequest(UUID.randomUUID().toString(), 0), "tester")
        }
    }

    @Test
    fun `update throws conflict on stale version`() {
        val id = UUID.randomUUID()
        val productId = UUID.randomUUID()
        every { repository.findById(id) } returns sampleEngagement(id, productId, version = 1)
        every { repository.existsProduct(productId) } returns true
        every { repository.update(any()) } returns null

        assertThrows<ConflictException> {
            service.update(id, EngagementTestFactory.validUpdateRequest(productId.toString(), version = 1), "tester")
        }
    }

    @Test
    fun `delete succeeds when engagement exists`() {
        val id = UUID.randomUUID()
        every { repository.delete(id) } returns true

        service.delete(id)

        verify { repository.delete(id) }
    }

    @Test
    fun `delete throws when engagement missing`() {
        val id = UUID.randomUUID()
        every { repository.delete(id) } returns false

        assertThrows<NotFoundException> { service.delete(id) }
    }

    private fun sampleEngagement(id: UUID, productId: UUID = UUID.randomUUID(), version: Long = 0): Engagement {
        val now = DateTimeUtils.now()
        return Engagement(
            id = id,
            productId = productId,
            name = "Engagement",
            description = null,
            target = null,
            status = EngagementStatus.Planned,
            startDate = now,
            endDate = null,
            createdAt = now,
            updatedAt = now,
            createdBy = "tester",
            updatedBy = "tester",
            version = version,
        )
    }
}
