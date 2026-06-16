@file:OptIn(io.ktor.utils.io.ExperimentalKtorApi::class)
package com.yamanorlon.grog.api.route

import io.ktor.openapi.jsonSchema

import io.ktor.server.routing.put
import io.ktor.server.routing.get
import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import com.yamanorlon.grog.util.QueryParams
import io.ktor.server.routing.openapi.describe
import com.yamanorlon.grog.service.ProductService
import com.yamanorlon.grog.api.mapper.ProductMapper
import com.yamanorlon.grog.api.response.ErrorResponse
import com.yamanorlon.grog.api.response.ProductResponse
import com.yamanorlon.grog.api.response.ProductPageResponse
import com.yamanorlon.grog.api.request.CreateProductRequest
import com.yamanorlon.grog.api.request.UpdateProductRequest

fun Route.productRoutes(productService: ProductService) {
  route("/api/products") {

    post {
      val request = call.receive<CreateProductRequest>()
      call.respond(
        HttpStatusCode.Created,
        ProductMapper.toResponse(
          productService.create(request, QueryParams.actor(call)
          )
        )
      )
    }.describe {
      operationId = "createProduct";
      summary = "Create product";
      description = "Creates a product";
      tag("Products")
      requestBody {
        required = true;
        schema = jsonSchema<CreateProductRequest>()
      }
      responses {
        HttpStatusCode.Created {
          schema = jsonSchema<ProductResponse>()
        };
        HttpStatusCode.BadRequest {
          schema = jsonSchema<ErrorResponse>()
        }
      }
    }

    get {
      val page = productService.list(QueryParams.productFilter(call), QueryParams.pageRequest(call), QueryParams.sortSpec(call))
      call.respond(ProductMapper.toPageResponse(page))
    }.describe {
      operationId = "listProducts";
      summary = "List products";
      tag("Products")
      parameters {
        query("page") {
          required = false
        };
        query("size") {
          required = false
        };
        query("sort") {
          required = false
        };
        query("name") {
          required = false
        };
        query("owner") {
          required = false
        };
        query("tag") {
          required = false
        }
      }
      responses {
        HttpStatusCode.OK {
          schema = jsonSchema<ProductPageResponse>()
        }
      }
    }

    get("/{id}") {
      call.respond(
        ProductMapper.toResponse(
          productService.getById(QueryParams.uuidParam(call, "id")
          )
        )
      )
    }.describe {
      operationId = "getProductById";
      summary = "Get product by ID";
      tag("Products")
      parameters {
        path("id") {
          required = true
        }
      }
      responses {
        HttpStatusCode.OK {
          schema = jsonSchema<ProductResponse>()
        };
        HttpStatusCode.NotFound {
          schema = jsonSchema<ErrorResponse>()
        }
      }
    }

    put("/{id}") {
      val request = call.receive<UpdateProductRequest>()
      call.respond(
        ProductMapper.toResponse(
          productService.update(QueryParams.uuidParam(call, "id"),
            request,
            QueryParams.actor(call)
          )
        )
      )
    }.describe {
      operationId = "updateProduct";
      summary = "Update product";
      tag("Products")
      parameters {
        path("id") {
          required = true
        };
        header("X-User-Id") {
          required = false
        }
      }
      requestBody {
        required = true;
        schema = jsonSchema<UpdateProductRequest>()
      }
      responses {
        HttpStatusCode.OK {
          schema = jsonSchema<ProductResponse>()
        };
        HttpStatusCode.Conflict {
          schema = jsonSchema<ErrorResponse>()
        }
      }
    }

    delete("/{id}") {
      productService.delete(QueryParams.uuidParam(call, "id"));
      call.respond(HttpStatusCode.NoContent)
    }.describe {
      operationId = "deleteProduct";
      summary = "Delete product";
      tag("Products")
      parameters {
        path("id") {
          required = true
        }
      }
      responses {
        HttpStatusCode.NoContent {}
      }
    }
  }
}
