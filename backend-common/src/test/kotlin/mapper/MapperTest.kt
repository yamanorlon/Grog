package mapper

import models.*
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test

class MapperTest {

    @Test
    fun `test ProductMapper round-trip`() {
        val dto = ProductDto(
            id = 1L,
            name = "Web App",
            description = "Main site",
            created = LocalDateTime.now()
        )

        val entity = ProductMapper.toEntity(dto)
        assertEquals(dto.name, entity.name)
        assertEquals(dto.description, entity.description)

        val restoredDto = ProductMapper.toDto(entity)
        assertEquals(dto, restoredDto)
    }

    @Test
    fun `test EngagementMapper round-trip`() {
        val productEntity = ProductEntity(id = 1L, name = "Product A")

        val dto = EngagementDto(
            id = 2L,
            name = "Sprint 1",
            productId = 1L,
            active = true,
            targetStart = LocalDate.now(),
            targetEnd = LocalDate.now().plusDays(14)
        )

        val entity = EngagementMapper.toEntity(dto, productEntity)
        assertEquals(dto.name, entity.name)
        assertEquals(productEntity, entity.product)

        val restoredDto = EngagementMapper.toDto(entity)
        assertEquals(dto.id, restoredDto.id)
        assertEquals(dto.name, restoredDto.name)
        assertEquals(dto.productId, restoredDto.productId)
    }

    @Test
    fun `test FindingMapper round-trip`() {
        val productEntity = ProductEntity(id = 1L, name = "Prod")
        val engagementEntity = EngagementEntity(
            id = 2L,
            name = "Eng",
            product = productEntity
        )

        val dto = FindingDto(
            id = 3L,
            title = "XSS Vulnerability",
            severity = "Medium",
            description = "Reflected XSS in search",
            engagementId = 2L,
            filePath = "index.html",
            line = 10,
            cwe = 79,
            cve = null,
            isActive = true,
            verified = false,
            created = LocalDateTime.now()
        )

        val entity = FindingMapper.toEntity(dto, engagementEntity)
        assertEquals(dto.title, entity.title)
        assertEquals(dto.severity, entity.severity)
        assertEquals(engagementEntity, entity.engagement)

        val restoredDto = FindingMapper.toDto(entity)
        assertEquals(dto.id, restoredDto.id)
        assertEquals(dto.title, restoredDto.title)
        assertEquals(dto.engagementId, restoredDto.engagementId)
    }

    @Test
    fun `test UserMapper round-trip`(){
        val userEntity = UserEntity(
            id = 1L,
            username = "yamanorlon",
            email = "yamanorlon@gmail.com"
        )

        val dto = UserMapper.toDto(userEntity)

        assertEquals(dto.id, userEntity.id)
        assertEquals(dto.username, userEntity.username)
        assertEquals(dto.email, userEntity.email)

        val restoredEntity = UserMapper.toEntity(dto)

        assertEquals(dto.id, restoredEntity.id)
        assertEquals(dto.username, restoredEntity.username)
        assertEquals(dto.email, restoredEntity.email)
    }
}