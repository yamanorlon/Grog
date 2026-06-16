@file:OptIn(io.ktor.utils.io.ExperimentalKtorApi::class)
package com.yamanorlon.grog.api.route

import io.ktor.openapi.jsonSchema
import io.ktor.server.routing.get
import io.ktor.server.routing.put
import io.ktor.server.routing.post
import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.route
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import com.yamanorlon.grog.util.QueryParams
import io.ktor.server.routing.openapi.describe
import com.yamanorlon.grog.service.FindingService
import com.yamanorlon.grog.api.mapper.FindingMapper
import com.yamanorlon.grog.api.response.ErrorResponse
import com.yamanorlon.grog.api.response.FindingResponse
import com.yamanorlon.grog.api.response.FindingPageResponse
import com.yamanorlon.grog.api.request.CreateFindingRequest
import com.yamanorlon.grog.api.request.UpdateFindingRequest

fun Route.findingRoutes(findingService: FindingService) {
  route("/api/findings") {

    post {
      val request = call.receive<CreateFindingRequest>()
      call.respond(HttpStatusCode.Created,
        FindingMapper.toResponse(
          findingService.create(request, QueryParams.actor(call)
          )
        )
      )
    }.describe {
      operationId = "createFinding";
      summary = "Create finding";
      tag("Findings")
      requestBody {
        required = true;
        schema = jsonSchema<CreateFindingRequest>()
      }
      responses {
        HttpStatusCode.Created {
          schema = jsonSchema<FindingResponse>()
        };
        HttpStatusCode.BadRequest {
          schema = jsonSchema<ErrorResponse>()
        }
      }
    }

    get {
      val page = findingService.list(QueryParams.findingFilter(call), QueryParams.pageRequest(call), QueryParams.sortSpec(call))
      call.respond(FindingMapper.toPageResponse(page))
    }.describe {
      operationId = "listFindings";
      summary = "List findings";
      tag("Findings")
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
        query("engagementId") {
          required = false
        };
        query("severity") {
          required = false
        };
        query("status") {
          required = false
        };
        query("title") {
          required = false
        }
      }
      responses {
        HttpStatusCode.OK {
          schema = jsonSchema<FindingPageResponse>()
        }
      }
    }

    get("/{id}") {
      call.respond(
        FindingMapper.toResponse(
          findingService.getById(
            QueryParams.uuidParam(call, "id")
          )
        )
      )
    }.describe {
      operationId = "getFindingById";
      summary = "Get finding by ID";
      tag("Findings")
      parameters {
        path("id") {
          required = true
        }
      }
      responses {
        HttpStatusCode.OK {
          schema = jsonSchema<FindingResponse>()
        };
        HttpStatusCode.NotFound {
          schema = jsonSchema<ErrorResponse>()
        }
      }
    }

    put("/{id}") {
      val request = call.receive<UpdateFindingRequest>()
      call.respond(
        FindingMapper.toResponse(
          findingService.update(QueryParams.uuidParam(call, "id"),
            request,
            QueryParams.actor(call)
          )
        )
      )
    }.describe {
      operationId = "updateFinding";
      summary = "Update finding";
      tag("Findings")
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
        schema = jsonSchema<UpdateFindingRequest>()
      }
      responses {
        HttpStatusCode.OK {
          schema = jsonSchema<FindingResponse>()
        };
        HttpStatusCode.Conflict {
          schema = jsonSchema<ErrorResponse>()
        }
      }
    }
    delete("/{id}") {
      findingService.delete(QueryParams.uuidParam(call, "id"));
      call.respond(HttpStatusCode.NoContent)
    }.describe {
      operationId = "deleteFinding";
      summary = "Delete finding";
      tag("Findings")
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
