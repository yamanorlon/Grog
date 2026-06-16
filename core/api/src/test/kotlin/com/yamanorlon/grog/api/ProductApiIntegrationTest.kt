package com.yamanorlon.grog.api

import com.yamanorlon.grog.api.response.ProductPageResponse
import com.yamanorlon.grog.api.response.ProductResponse
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

class ProductApiIntegrationTest : ApiIntegrationTest() {

    @Test
    fun `product crud lifecycle`() = testApplication {
        configureTestApplication()
        val client = createJsonClient()

        val createResponse = client.post("/api/products") {
            contentType(ContentType.Application.Json)
            setBody(ProductTestFactory.validCreateRequest(name = "API Product"))
        }
        assertEquals(HttpStatusCode.Created, createResponse.status)
        val created = createResponse.body<ProductResponse>()
        assertEquals("API Product", created.name)

        val getResponse = client.get("/api/products/${created.id}")
        assertEquals(HttpStatusCode.OK, getResponse.status)

        val listResponse = client.get("/api/products?page=0&size=10")
        assertEquals(HttpStatusCode.OK, listResponse.status)
        val page = listResponse.body<ProductPageResponse>()
        assertTrue(page.content.any { it.id == created.id })

        val updateResponse = client.put("/api/products/${created.id}") {
            contentType(ContentType.Application.Json)
            setBody(ProductTestFactory.validUpdateRequest(version = created.version, name = "Updated Product"))
        }
        assertEquals(HttpStatusCode.OK, updateResponse.status)

        val deleteResponse = client.delete("/api/products/${created.id}")
        assertEquals(HttpStatusCode.NoContent, deleteResponse.status)

        val missingResponse = client.get("/api/products/${created.id}")
        assertEquals(HttpStatusCode.NotFound, missingResponse.status)
    }

    @Test
    fun `create product with blank name returns 400`() = testApplication {
        configureTestApplication()
        val client = createJsonClient()

        val response = client.post("/api/products") {
            contentType(ContentType.Application.Json)
            setBody(ProductTestFactory.blankNameCreateRequest())
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
    }
}
