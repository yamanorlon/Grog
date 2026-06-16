package com.yamanorlon.grog.unit.service

import com.yamanorlon.grog.api.request.UpdateProductRequest
import com.yamanorlon.grog.domain.exception.ConflictException
import com.yamanorlon.grog.domain.exception.NotFoundException
import com.yamanorlon.grog.domain.exception.ValidationException
import com.yamanorlon.grog.domain.model.Page
import com.yamanorlon.grog.domain.model.PageRequest
import com.yamanorlon.grog.domain.model.Product
import com.yamanorlon.grog.domain.model.ProductFilter
import com.yamanorlon.grog.domain.repository.ProductRepository
import com.yamanorlon.grog.service.ProductService
import com.yamanorlon.grog.factories.ProductTestFactory
import com.yamanorlon.grog.util.DateTimeUtils
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID

class ProductServiceTest {

    private val repository = mockk<ProductRepository>()
    private val service = ProductService(repository)

    @Test
    fun `create persists valid product`() {
        val request = ProductTestFactory.validCreateRequest()
        every { repository.create(any()) } answers { firstArg() }

        val created = service.create(request, "tester")

        assertEquals("Test Product", created.name)
        assertEquals("qa-team", created.owner)
        assertEquals("tester", created.createdBy)
        verify(exactly = 1) { repository.create(any()) }
    }

    @Test
    fun `create rejects blank name`() {
        assertThrows<ValidationException> {
            service.create(ProductTestFactory.blankNameCreateRequest(), "tester")
        }
        verify(exactly = 0) { repository.create(any()) }
    }

    @Test
    fun `create rejects name exceeding max length`() {
        assertThrows<ValidationException> {
            service.create(ProductTestFactory.longNameCreateRequest(), "tester")
        }
    }

    @Test
    fun `getById returns product when found`() {
        val id = UUID.randomUUID()
        val product = sampleProduct(id)
        every { repository.findById(id) } returns product

        val result = service.getById(id)

        assertEquals(id, result.id)
        verify { repository.findById(id) }
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

        val page = service.list(ProductFilter(), PageRequest(), null)

        assertEquals(0, page.totalElements)
        verify { repository.findAll(any(), any(), any()) }
    }

    @Test
    fun `update succeeds with matching version`() {
        val id = UUID.randomUUID()
        val existing = sampleProduct(id, version = 1)
        every { repository.findById(id) } returns existing
        every { repository.update(any()) } answers { firstArg() }

        val updated = service.update(id, ProductTestFactory.validUpdateRequest(version = 1), "tester")

        assertEquals("Updated Product", updated.name)
        verify { repository.update(any()) }
    }

    @Test
    fun `update throws when product missing`() {
        val id = UUID.randomUUID()
        every { repository.findById(id) } returns null

        assertThrows<NotFoundException> {
            service.update(id, UpdateProductRequest(name = "Updated", version = 0), "tester")
        }
    }

    @Test
    fun `update throws conflict on stale version`() {
        val id = UUID.randomUUID()
        every { repository.findById(id) } returns sampleProduct(id, version = 1)
        every { repository.update(any()) } returns null

        assertThrows<ConflictException> {
            service.update(id, UpdateProductRequest(name = "New", version = 1), "tester")
        }
    }

    @Test
    fun `delete succeeds when product exists`() {
        val id = UUID.randomUUID()
        every { repository.delete(id) } returns true

        service.delete(id)

        verify { repository.delete(id) }
    }

    @Test
    fun `delete throws when product missing`() {
        val id = UUID.randomUUID()
        every { repository.delete(id) } returns false

        assertThrows<NotFoundException> { service.delete(id) }
    }

    private fun sampleProduct(id: UUID, version: Long = 0): Product {
        val now = DateTimeUtils.now()
        return Product(
            id = id,
            name = "Old",
            description = null,
            owner = null,
            tags = emptyList(),
            createdAt = now,
            updatedAt = now,
            createdBy = "tester",
            updatedBy = "tester",
            version = version,
        )
    }
}
