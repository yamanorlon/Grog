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
import com.yamanorlon.grog.service.EngagementService
import com.yamanorlon.grog.api.response.ErrorResponse
import com.yamanorlon.grog.api.mapper.EngagementMapper
import com.yamanorlon.grog.api.response.EngagementResponse
import com.yamanorlon.grog.api.response.EngagementPageResponse
import com.yamanorlon.grog.api.request.CreateEngagementRequest
import com.yamanorlon.grog.api.request.UpdateEngagementRequest

fun Route.engagementRoutes(engagementService: EngagementService) {
  route("/api/engagements") {

    post {
      val request = call.receive<CreateEngagementRequest>()
      call.respond(HttpStatusCode.Created,
        EngagementMapper.toResponse(
          engagementService.create(request, QueryParams.actor(call)
          )
        )
      )
    }.describe {
      operationId = "createEngagement";
      summary = "Create engagement";
      tag("Engagements")
      requestBody {
        required = true;
        schema = jsonSchema<CreateEngagementRequest>()
      }
      responses {
        HttpStatusCode.Created {
          schema = jsonSchema<EngagementResponse>()
        };
        HttpStatusCode.BadRequest {
          schema = jsonSchema<ErrorResponse>()
        }
      }
    }

    get {
      val page = engagementService.list(
        QueryParams.engagementFilter(call),
        QueryParams.pageRequest(call),
        QueryParams.sortSpec(call)
      )
      call.respond(EngagementMapper.toPageResponse(page))
    }.describe {
      operationId = "listEngagements";
      summary = "List engagements";
      tag("Engagements")
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
        query("productId") {
          required = false
        };
        query("status") {
          required = false
        };
        query("name") {
          required = false
        }
      }
      responses {
        HttpStatusCode.OK {
          schema = jsonSchema<EngagementPageResponse>()
        }
      }
    }

    get("/{id}") {
      call.respond(
        EngagementMapper.toResponse(
          engagementService.getById(
            QueryParams.uuidParam(call, "id")
          )
        )
      )
    }.describe {
      operationId = "getEngagementById";
      summary = "Get engagement by ID";
      tag("Engagements")
      parameters {
        path("id") {
          required = true
        }
      }
      responses {
        HttpStatusCode.OK {
          schema = jsonSchema<EngagementResponse>()
        };
        HttpStatusCode.NotFound {
          schema = jsonSchema<ErrorResponse>()
        }
      }
    }

    put("/{id}") {
      val request = call.receive<UpdateEngagementRequest>()
      call.respond(
        EngagementMapper.toResponse(
          engagementService.update(
            QueryParams.uuidParam(call, "id"),
            request,
            QueryParams.actor(call)
          )
        )
      )
    }.describe {
      operationId = "updateEngagement";
      summary = "Update engagement";
      tag("Engagements")
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
        schema = jsonSchema<UpdateEngagementRequest>()
      }
      responses {
        HttpStatusCode.OK {
          schema = jsonSchema<EngagementResponse>()
        };
        HttpStatusCode.Conflict {
          schema = jsonSchema<ErrorResponse>()
        }
      }
    }

    delete("/{id}") {
      engagementService.delete(QueryParams.uuidParam(call, "id"));
      call.respond(HttpStatusCode.NoContent)
    }.describe {
      operationId = "deleteEngagement";
      summary = "Delete engagement";
      tag("Engagements")
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
