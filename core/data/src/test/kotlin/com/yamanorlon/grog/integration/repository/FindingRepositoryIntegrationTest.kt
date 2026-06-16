package com.yamanorlon.grog.integration.repository

import com.yamanorlon.grog.database.repository.EngagementRepositoryImpl
import com.yamanorlon.grog.database.repository.FindingRepositoryImpl
import com.yamanorlon.grog.database.repository.ProductRepositoryImpl
import com.yamanorlon.grog.domain.model.FindingFilter
import com.yamanorlon.grog.domain.model.FindingStatus
import com.yamanorlon.grog.domain.model.PageRequest
import com.yamanorlon.grog.domain.model.Severity
import com.yamanorlon.grog.factories.EngagementEntityFactory
import com.yamanorlon.grog.factories.FindingEntityFactory
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

class FindingRepositoryIntegrationTest : AbstractIntegrationTest() {

    private val productRepository = ProductRepositoryImpl()
    private val engagementRepository = EngagementRepositoryImpl()
    private val repository = FindingRepositoryImpl()

    private fun createEngagement() =
        engagementRepository.create(
            EngagementEntityFactory.valid(productRepository.create(ProductEntityFactory.valid()).id),
        )

    @Test
    fun `insert and select finding by id`() {
        val engagement = createEngagement()
        val created = repository.create(FindingEntityFactory.valid(engagement.id))

        val found = repository.findById(created.id)

        assertNotNull(found)
        assertEquals(engagement.id, found!!.engagementId)
        assertEquals(Severity.High, found.severity)
    }

    @Test
    fun `update finding increments version`() {
        val engagement = createEngagement()
        val created = repository.create(FindingEntityFactory.valid(engagement.id))

        val updated = repository.update(created.copy(title = "Updated XSS", severity = Severity.Critical))

        assertNotNull(updated)
        assertEquals("Updated XSS", updated!!.title)
        assertEquals(1, updated.version)
    }

    @Test
    fun `delete finding`() {
        val engagement = createEngagement()
        val created = repository.create(FindingEntityFactory.valid(engagement.id))

        assertTrue(repository.delete(created.id))
        assertNull(repository.findById(created.id))
    }

    @Test
    fun `existsEngagement returns expected result`() {
        val engagement = createEngagement()

        assertTrue(repository.existsEngagement(engagement.id))
        assertFalse(repository.existsEngagement(UUID.randomUUID()))
    }

    @Test
    fun `list findings filtered by severity`() {
        val engagement = createEngagement()
        repository.create(FindingEntityFactory.valid(engagement.id).copy(title = "Critical issue", severity = Severity.Critical))

        val page = repository.findAll(FindingFilter(severity = Severity.Critical), PageRequest(), null)

        assertTrue(page.content.any { it.title == "Critical issue" })
    }

    @Test
    fun `insert fails when engagement foreign key missing`() {
        val orphan = FindingEntityFactory.valid(engagementId = UUID.randomUUID())

        assertThrows(Exception::class.java) {
            repository.create(orphan)
        }
    }
}
