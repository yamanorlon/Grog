package com.yamanorlon.grog.api

import com.yamanorlon.grog.api.response.EngagementResponse
import com.yamanorlon.grog.api.response.FindingPageResponse
import com.yamanorlon.grog.api.response.FindingResponse
import com.yamanorlon.grog.api.response.ProductResponse
import com.yamanorlon.grog.factories.EngagementTestFactory
import com.yamanorlon.grog.factories.FindingTestFactory
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

class FindingApiIntegrationTest : ApiIntegrationTest() {

    private suspend fun createEngagement(client: io.ktor.client.HttpClient): EngagementResponse {
        val product = client.post("/api/products") {
            contentType(ContentType.Application.Json)
            setBody(ProductTestFactory.validCreateRequest(name = "Finding Parent Product"))
        }.body<ProductResponse>()

        return client.post("/api/engagements") {
            contentType(ContentType.Application.Json)
            setBody(EngagementTestFactory.validCreateRequest(product.id, name = "Finding Parent Engagement"))
        }.body()
    }

    @Test
    fun `finding crud lifecycle`() = testApplication {
        configureTestApplication()
        val client = createJsonClient()
        val engagement = createEngagement(client)

        val createResponse = client.post("/api/findings") {
            contentType(ContentType.Application.Json)
            setBody(FindingTestFactory.validCreateRequest(engagement.id, title = "API Finding"))
        }
        assertEquals(HttpStatusCode.Created, createResponse.status)
        val created = createResponse.body<FindingResponse>()
        assertEquals("API Finding", created.title)

        val getResponse = client.get("/api/findings/${created.id}")
        assertEquals(HttpStatusCode.OK, getResponse.status)

        val listResponse = client.get("/api/findings?engagementId=${engagement.id}")
        assertEquals(HttpStatusCode.OK, listResponse.status)
        val page = listResponse.body<FindingPageResponse>()
        assertTrue(page.content.any { it.id == created.id })

        val updateResponse = client.put("/api/findings/${created.id}") {
            contentType(ContentType.Application.Json)
            setBody(FindingTestFactory.validUpdateRequest(engagement.id, created.version))
        }
        assertEquals(HttpStatusCode.OK, updateResponse.status)

        val deleteResponse = client.delete("/api/findings/${created.id}")
        assertEquals(HttpStatusCode.NoContent, deleteResponse.status)

        val missingResponse = client.get("/api/findings/${created.id}")
        assertEquals(HttpStatusCode.NotFound, missingResponse.status)
    }

    @Test
    fun `create finding with blank title returns 400`() = testApplication {
        configureTestApplication()
        val client = createJsonClient()
        val engagement = createEngagement(client)

        val response = client.post("/api/findings") {
            contentType(ContentType.Application.Json)
            setBody(FindingTestFactory.blankTitleRequest(engagement.id))
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
    }
}
