package com.yamanorlon.grog.api

import com.yamanorlon.grog.api.response.EngagementPageResponse
import com.yamanorlon.grog.api.response.EngagementResponse
import com.yamanorlon.grog.api.response.ProductResponse
import com.yamanorlon.grog.factories.EngagementTestFactory
import com.yamanorlon.grog.factories.ProductTestFactory
import com.yamanorlon.grog.testcontainers.ApiIntegrationTest
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class EngagementApiIntegrationTest : ApiIntegrationTest() {

    private suspend fun createProduct(client: io.ktor.client.HttpClient): ProductResponse {
        val response = client.post("/api/products") {
            contentType(ContentType.Application.Json)
            setBody(ProductTestFactory.validCreateRequest(name = "Engagement Parent"))
        }
        assertEquals(HttpStatusCode.Created, response.status)
        return response.body()
    }

    @Test
    fun `engagement crud lifecycle`() = testApplication {
        configureTestApplication()
        val client = createJsonClient()
        val product = createProduct(client)

        val createResponse = client.post("/api/engagements") {
            contentType(ContentType.Application.Json)
            setBody(EngagementTestFactory.validCreateRequest(product.id, name = "API Engagement"))
        }
        assertEquals(HttpStatusCode.Created, createResponse.status)
        val created = createResponse.body<EngagementResponse>()
        assertEquals("API Engagement", created.name)

        val getResponse = client.get("/api/engagements/${created.id}")
        assertEquals(HttpStatusCode.OK, getResponse.status)

        val listResponse = client.get("/api/engagements?productId=${product.id}")
        assertEquals(HttpStatusCode.OK, listResponse.status)
        val page = listResponse.body<EngagementPageResponse>()
        assertTrue(page.content.any { it.id == created.id })

        val updateResponse = client.put("/api/engagements/${created.id}") {
            contentType(ContentType.Application.Json)
            setBody(EngagementTestFactory.validUpdateRequest(product.id, created.version))
        }
        assertEquals(HttpStatusCode.OK, updateResponse.status)

        val deleteResponse = client.delete("/api/engagements/${created.id}")
        assertEquals(HttpStatusCode.NoContent, deleteResponse.status)

        val missingResponse = client.get("/api/engagements/${created.id}")
        assertEquals(HttpStatusCode.NotFound, missingResponse.status)
    }

    @Test
    fun `create engagement with missing product returns 400`() = testApplication {
        configureTestApplication()
        val client = createJsonClient()

        val response = client.post("/api/engagements") {
            contentType(ContentType.Application.Json)
            setBody(
                EngagementTestFactory.validCreateRequest(
                    productId = "00000000-0000-0000-0000-000000000099",
                    name = "Orphan Engagement",
                ),
            )
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
    }
}
