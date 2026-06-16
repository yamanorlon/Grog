package com.yamanorlon.grog.api

import com.yamanorlon.grog.testcontainers.ApiIntegrationTest
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ApiNegativeIntegrationTest : ApiIntegrationTest() {

    @Test
    fun `malformed json returns 400`() = testApplication {
        configureTestApplication()
        val client = createJsonClient()

        val response = client.post("/api/products") {
            contentType(ContentType.Application.Json)
            setBody("{invalid-json")
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun `empty payload returns 400`() = testApplication {
        configureTestApplication()
        val client = createJsonClient()

        val response = client.post("/api/products") {
            contentType(ContentType.Application.Json)
            setBody("{}")
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun `invalid product id format returns 400`() = testApplication {
        configureTestApplication()
        val client = createJsonClient()

        val response = client.get("/api/products/not-a-uuid")

        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun `invalid engagement id format returns 400`() = testApplication {
        configureTestApplication()
        val client = createJsonClient()

        val response = client.get("/api/engagements/12345")

        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun `invalid finding id format returns 400`() = testApplication {
        configureTestApplication()
        val client = createJsonClient()

        val response = client.get("/api/findings/abcdef")

        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun `get missing product returns 404`() = testApplication {
        configureTestApplication()
        val client = createJsonClient()

        val response = client.get("/api/products/00000000-0000-0000-0000-000000000099")

        assertEquals(HttpStatusCode.NotFound, response.status)
    }
}
