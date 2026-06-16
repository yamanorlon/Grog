package com.yamanorlon.grog.observability

import org.slf4j.MDC
import java.util.UUID
import org.slf4j.LoggerFactory
import io.ktor.util.AttributeKey
import io.ktor.server.request.path
import io.ktor.server.response.header
import io.ktor.server.application.call
import io.ktor.server.request.httpMethod
import io.ktor.server.application.Application
import io.ktor.serialization.JsonConvertException
import com.yamanorlon.grog.config.ObservabilityConfig
import io.ktor.server.application.ApplicationCallPipeline
import com.yamanorlon.grog.domain.exception.ConflictException
import com.yamanorlon.grog.domain.exception.NotFoundException
import com.yamanorlon.grog.domain.exception.ValidationException
import com.yamanorlon.grog.domain.exception.ReferenceNotFoundException

private val accessLogger = LoggerFactory.getLogger("http.access")

private const val TRACE_HEADER = "X-Trace-Id"
private const val MDC_TRACE_ID = "traceId"
private const val MDC_METHOD = "method"
private const val MDC_PATH = "path"
private const val MDC_STATUS = "status"
private const val MDC_DURATION_MS = "durationMs"

private val TraceIdKey = AttributeKey<String>("TraceId")

fun Application.configureRequestTracing(config: ObservabilityConfig) {
    intercept(ApplicationCallPipeline.Setup) {
        val traceId = call.request.headers[TRACE_HEADER]?.takeIf { it.isNotBlank() }
            ?: UUID.randomUUID().toString()

        call.attributes.put(TraceIdKey, traceId)
        MDC.put(MDC_TRACE_ID, traceId)
        call.response.header(TRACE_HEADER, traceId)
    }

    intercept(ApplicationCallPipeline.Monitoring) {
        val startedAt = System.nanoTime()
        var failure: Throwable? = null
        try {
            proceed()
        } catch (cause: Throwable) {
            failure = cause
            throw cause
        } finally {
            val path = call.request.path()
            if (!path.startsWith("/health") && !path.startsWith("/metrics") && !path.startsWith("/debug")) {
                logAccess(call, startedAt, failure)
            }
            clearRequestMdc()
        }
    }
}

private fun logAccess(
    call: io.ktor.server.application.ApplicationCall,
    startedAt: Long,
    failure: Throwable?,
) {
    val durationMs = (System.nanoTime() - startedAt) / 1_000_000
    val traceId = call.attributes.getOrNull(TraceIdKey)

    traceId?.let { MDC.put(MDC_TRACE_ID, it) }
    MDC.put(MDC_METHOD, call.request.httpMethod.value)
    MDC.put(MDC_PATH, call.request.path())
    MDC.put(MDC_STATUS, resolveStatus(call, failure))
    MDC.put(MDC_DURATION_MS, durationMs.toString())

    if (failure != null) {
        val statusCode = resolveStatus(call, failure)
        if (statusCode.startsWith("5")) {
            accessLogger.error("request failed", failure)
        } else {
            accessLogger.info("request completed")
        }
    } else {
        accessLogger.info("request completed")
    }
}

private fun clearRequestMdc() {
    listOf(MDC_TRACE_ID, MDC_METHOD, MDC_PATH, MDC_STATUS, MDC_DURATION_MS)
        .forEach(MDC::remove)
}

private fun resolveStatus(
    call: io.ktor.server.application.ApplicationCall,
    failure: Throwable?,
): String {
    if (failure == null) {
        return call.response.status()?.value?.toString() ?: "0"
    }
    return when (failure) {
        is NotFoundException -> "404"
        is ValidationException -> "400"
        is ReferenceNotFoundException -> "400"
        is ConflictException -> "409"
        is JsonConvertException -> "400"
        is IllegalArgumentException -> "400"
        else -> call.response.status()?.value?.toString() ?: "500"
    }
}
