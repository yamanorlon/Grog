package com.yamanorlon.grog.integration.repository

import com.yamanorlon.grog.database.repository.ProductRepositoryImpl
import com.yamanorlon.grog.domain.model.PageRequest
import com.yamanorlon.grog.domain.model.ProductFilter
import com.yamanorlon.grog.factories.ProductEntityFactory
import com.yamanorlon.grog.testsupport.AbstractIntegrationTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.UUID

class ProductRepositoryIntegrationTest : AbstractIntegrationTest() {

    private val repository = ProductRepositoryImpl()

    @Test
    fun `insert and select product by id`() {
        val created = repository.create(ProductEntityFactory.valid(name = "Payments API"))

        val found = repository.findById(created.id)

        assertNotNull(found)
        assertEquals("Payments API", found!!.name)
        assertEquals(listOf("pci"), found.tags)
    }

    @Test
    fun `update product with optimistic locking`() {
        val created = repository.create(ProductEntityFactory.valid())
        val updated = repository.update(created.copy(name = "Renamed", version = created.version))

        assertNotNull(updated)
        assertEquals("Renamed", updated!!.name)
        assertEquals(1, updated.version)
    }

    @Test
    fun `update returns null on stale version`() {
        val created = repository.create(ProductEntityFactory.valid())

        val result = repository.update(created.copy(name = "Conflict", version = 99))

        assertNull(result)
    }

    @Test
    fun `delete product`() {
        val created = repository.create(ProductEntityFactory.valid())

        assertTrue(repository.delete(created.id))
        assertNull(repository.findById(created.id))
    }

    @Test
    fun `delete returns false for missing id`() {
        assertFalse(repository.delete(UUID.randomUUID()))
    }

    @Test
    fun `list products with tag filter`() {
        repository.create(ProductEntityFactory.valid(name = "Tagged Product").copy(tags = listOf("web")))

        val page = repository.findAll(ProductFilter(tag = "web"), PageRequest(), null)

        assertTrue(page.content.any { it.name == "Tagged Product" })
    }
}
