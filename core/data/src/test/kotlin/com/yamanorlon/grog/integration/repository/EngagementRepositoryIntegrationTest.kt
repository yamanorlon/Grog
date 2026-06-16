package com.yamanorlon.grog.integration.repository

import com.yamanorlon.grog.database.repository.EngagementRepositoryImpl
import com.yamanorlon.grog.database.repository.ProductRepositoryImpl
import com.yamanorlon.grog.domain.model.EngagementFilter
import com.yamanorlon.grog.domain.model.EngagementStatus
import com.yamanorlon.grog.domain.model.PageRequest
import com.yamanorlon.grog.factories.EngagementEntityFactory
import com.yamanorlon.grog.factories.ProductEntityFactory
import com.yamanorlon.grog.testsupport.AbstractIntegrationTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.UUID

class EngagementRepositoryIntegrationTest : AbstractIntegrationTest() {

    private val productRepository = ProductRepositoryImpl()
    private val repository = EngagementRepositoryImpl()

    @Test
    fun `insert and select engagement by id`() {
        val product = productRepository.create(ProductEntityFactory.valid())
        val created = repository.create(EngagementEntityFactory.valid(product.id))

        val found = repository.findById(created.id)

        assertNotNull(found)
        assertEquals(product.id, found!!.productId)
        assertEquals("Q1 Pentest", found.name)
    }

    @Test
    fun `update engagement increments version`() {
        val product = productRepository.create(ProductEntityFactory.valid())
        val created = repository.create(EngagementEntityFactory.valid(product.id))

        val updated = repository.update(created.copy(name = "Updated", status = EngagementStatus.InProgress))

        assertNotNull(updated)
        assertEquals("Updated", updated!!.name)
        assertEquals(1, updated.version)
    }

    @Test
    fun `delete engagement`() {
        val product = productRepository.create(ProductEntityFactory.valid())
        val created = repository.create(EngagementEntityFactory.valid(product.id))

        assertTrue(repository.delete(created.id))
        assertNull(repository.findById(created.id))
    }

    @Test
    fun `existsProduct returns true for existing product`() {
        val product = productRepository.create(ProductEntityFactory.valid())

        assertTrue(repository.existsProduct(product.id))
        assertFalse(repository.existsProduct(UUID.randomUUID()))
    }

    @Test
    fun `list engagements filtered by product`() {
        val product = productRepository.create(ProductEntityFactory.valid())
        repository.create(EngagementEntityFactory.valid(product.id).copy(name = "Scoped Engagement"))

        val page = repository.findAll(EngagementFilter(productId = product.id), PageRequest(), null)

        assertEquals(1, page.totalElements)
        assertEquals("Scoped Engagement", page.content.first().name)
    }

    @Test
    fun `insert fails when product foreign key missing`() {
        val orphan = EngagementEntityFactory.valid(productId = UUID.randomUUID())

        assertThrows(Exception::class.java) {
            repository.create(orphan)
        }
    }
}
