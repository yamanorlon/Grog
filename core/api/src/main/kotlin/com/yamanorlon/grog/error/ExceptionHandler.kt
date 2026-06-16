package com.yamanorlon.grog.error

import org.slf4j.LoggerFactory
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.path
import io.ktor.server.response.respond
import io.ktor.server.application.install
import io.ktor.server.application.Application
import com.yamanorlon.grog.util.DateTimeUtils
import io.ktor.serialization.JsonConvertException
import kotlinx.serialization.SerializationException
import com.yamanorlon.grog.api.response.ErrorResponse
import io.ktor.server.plugins.statuspages.StatusPages
import com.yamanorlon.grog.domain.exception.DomainException
import com.yamanorlon.grog.domain.exception.ConflictException
import com.yamanorlon.grog.domain.exception.NotFoundException
import com.yamanorlon.grog.domain.exception.ValidationException
import com.yamanorlon.grog.domain.exception.ReferenceNotFoundException

fun Application.configureExceptionHandling() {
    val logger = LoggerFactory.getLogger("ExceptionHandler")

    install(StatusPages) {

        exception<ValidationException> { call, cause ->
            logger.warn("Validation failed path={} errors={}", call.request.path(), cause.errors)
            call.respond(
                HttpStatusCode.BadRequest,
                errorResponse(call.request.path(), HttpStatusCode.BadRequest, "Bad Request", cause.message ?: "Validation failed", cause.errors),
            )
        }

        exception<NotFoundException> { call, cause ->
            call.respond(
                HttpStatusCode.NotFound,
                errorResponse(call.request.path(), HttpStatusCode.NotFound, "Not Found", cause.message ?: "Resource not found"),
            )
        }

        exception<ReferenceNotFoundException> { call, cause ->
            call.respond(
                HttpStatusCode.BadRequest,
                errorResponse(call.request.path(), HttpStatusCode.BadRequest, "Bad Request", cause.message ?: "Invalid reference"),
            )
        }

        exception<ConflictException> { call, cause ->
            call.respond(
                HttpStatusCode.Conflict,
                errorResponse(call.request.path(), HttpStatusCode.Conflict, "Conflict", cause.message ?: "Conflict"),
            )
        }

        exception<JsonConvertException> { call, cause ->
            logger.warn("JSON parse error path={}", call.request.path(), cause)
            call.respond(
                HttpStatusCode.BadRequest,
                errorResponse(call.request.path(), HttpStatusCode.BadRequest, "Bad Request", "Malformed JSON request body"),
            )
        }

        exception<SerializationException> { call, cause ->
            logger.warn("JSON deserialization error path={}", call.request.path(), cause)
            call.respond(
                HttpStatusCode.BadRequest,
                errorResponse(call.request.path(), HttpStatusCode.BadRequest, "Bad Request", "Invalid JSON request body"),
            )
        }

        exception<IllegalArgumentException> { call, cause ->
            call.respond(
                HttpStatusCode.BadRequest,
                errorResponse(call.request.path(), HttpStatusCode.BadRequest, "Bad Request", cause.message ?: "Invalid request"),
            )
        }

        exception<DomainException> { call, cause ->
            logger.error("Domain error path={}", call.request.path(), cause)
            call.respond(
                HttpStatusCode.InternalServerError,
                errorResponse(call.request.path(), HttpStatusCode.InternalServerError, "Internal Server Error", cause.message ?: "Unexpected error"),
            )
        }

        exception<Throwable> { call, cause ->
            logger.error("Unhandled error path={}", call.request.path(), cause)
            call.respond(
                HttpStatusCode.InternalServerError,
                errorResponse(call.request.path(), HttpStatusCode.InternalServerError, "Internal Server Error", "An unexpected error occurred"),
            )
        }

    }
}

private fun errorResponse(
    path: String,
    status: HttpStatusCode,
    error: String,
    message: String,
    details: List<String> = emptyList(),
) = ErrorResponse(
    timestamp = DateTimeUtils.formatInstant(DateTimeUtils.now()),
    status = status.value,
    error = error,
    message = message,
    path = path,
    details = details,
)
