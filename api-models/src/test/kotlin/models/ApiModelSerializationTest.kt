package models

import java.time.LocalDateTime
import org.junit.jupiter.api.Test
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule

class ApiModelSerializationTest {

    private val mapper = ObjectMapper().apply {
        registerModule(KotlinModule.Builder().build())
        registerModule(JavaTimeModule())
    }

    @Test
    fun `test ProductDto serialization and deserialization`() {
        val original = ProductDto(
            id = 1L,
            name = "Test Product",
            description = "Desc",
            created = LocalDateTime.of(2023, 1, 1, 12, 0)
        )

        val json = mapper.writeValueAsString(original)

        val restored = mapper.readValue(json, ProductDto::class.java)

        assertEquals(original, restored)
    }

    @Test
    fun `test EngagementDto serialization and deserialization`(){
        val original = EngagementDto(
            id = 1L,
            name = "Test engagement name",
            productId = 34,
            active = true
        )

        val json = mapper.writeValueAsString(original)

        val restored = mapper.readValue(json, EngagementDto::class.java)

        assertEquals(original, restored)
    }

    @Test
    fun `test FindingDto serialization with nulls`() {
        val original = FindingDto(
            id = null,
            title = "SQL Injection",
            severity = "High",
            description = null,
            engagementId = 10L,
            filePath = "/src/main.py",
            line = 42,
            cwe = 89,
            cve = null,
            isActive = true,
            verified = false,
            created = null
        )

        val json = mapper.writeValueAsString(original)
        val restored = mapper.readValue(json, FindingDto::class.java)

        assertEquals(original, restored)
    }

    @Test
    fun `test UserDto serialization and deserialization`(){
        val original = UserDto(
            id = 1L,
            username = "yamanorlon",
            email = "yamanorlon@gmail.com",
        )

        val json = mapper.writeValueAsString(original)
        val restored = mapper.readValue(json, UserDto::class.java)

        assertEquals(original, restored)
    }
}