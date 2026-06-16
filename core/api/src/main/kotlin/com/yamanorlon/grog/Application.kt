package com.yamanorlon.grog

import org.slf4j.event.Level
import org.slf4j.LoggerFactory
import io.ktor.http.ContentType
import org.koin.ktor.ext.inject
import io.ktor.server.routing.get
import io.ktor.http.HttpStatusCode
import io.ktor.openapi.OpenApiInfo
import io.ktor.server.request.path
import io.ktor.server.routing.routing
import io.ktor.server.netty.EngineMain
import io.ktor.server.response.respond
import kotlinx.serialization.json.Json
import io.ktor.server.application.install
import io.ktor.server.response.respondText
import com.yamanorlon.grog.config.AppConfig
import io.ktor.server.application.Application
import io.ktor.server.plugins.openapi.openAPI
import io.ktor.serialization.kotlinx.json.json
import com.yamanorlon.grog.config.StartupPaths
import io.ktor.server.plugins.swagger.swaggerUI
import com.yamanorlon.grog.config.DatabaseConfig
import com.yamanorlon.grog.service.FindingService
import com.yamanorlon.grog.service.ProductService
import com.yamanorlon.grog.config.DatabaseFactory
import com.yamanorlon.grog.api.route.findingRoutes
import com.yamanorlon.grog.api.route.productRoutes
import com.yamanorlon.grog.service.EngagementService
import io.ktor.server.plugins.calllogging.CallLogging
import com.yamanorlon.grog.api.route.engagementRoutes
import com.yamanorlon.grog.config.ObservabilityConfig
import io.ktor.server.routing.openapi.OpenApiDocSource
import com.yamanorlon.grog.observability.MetricsRegistry
import com.yamanorlon.grog.error.configureExceptionHandling
import com.yamanorlon.grog.security.configureSecurityHeaders
import com.yamanorlon.grog.config.configureDependencyInjection
import com.yamanorlon.grog.observability.configureObservability
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation

fun main(args: Array<String>) { EngineMain.main(args) }

fun Application.module() {
    val logger = LoggerFactory.getLogger("Application")
    val appConfig = loadAppConfig()
    val paths = StartupPaths.ensureWritableDirectories(appConfig.openApiOutputDir, appConfig.logsDir)

    DatabaseFactory.init(appConfig.database)

    configureDependencyInjection(appConfig)
    configureObservability(appConfig.observability)
    configureSecurityHeaders()
    configureExceptionHandling()

    install(ContentNegotiation) { json(Json { prettyPrint = false; ignoreUnknownKeys = true; encodeDefaults = true; isLenient = false }) }
    install(CallLogging) {
        level = Level.INFO
        filter { call ->
            val path = call.request.path()
            !path.startsWith("/health") && !path.startsWith("/metrics") && !path.startsWith("/debug")
        }
    }

    val productService by inject<ProductService>()
    val engagementService by inject<EngagementService>()
    val findingService by inject<FindingService>()

    val openApiOutputPath = paths.openApiOutputDir.toAbsolutePath().toString()
    val openApiEnabled = environment.config.config("app")
        .propertyOrNull("openapi.enabled")?.getString()?.toBooleanStrictOrNull() ?: true

    val openApiSource = OpenApiDocSource.Routing(contentType = ContentType.Application.Json)

    routing {
        if (openApiEnabled) {
            logger.info("Инициализация генерации OpenAPI документации")
            openAPI(path = "openapi") {
                outputPath = openApiOutputPath
                info = OpenApiInfo("Vulnerability Manager API", "0.0.1")
                source = openApiSource
            }
            swaggerUI(path = "swagger") {
                info = OpenApiInfo("Vulnerability Manager API", "0.0.1")
                source = openApiSource
            }
            logger.info("Генерация OpenAPI документации успешно завершена")
        }
        get("/health") { call.respond(HttpStatusCode.OK, mapOf("status" to "UP")) }
        if (MetricsRegistry.isInitialized) {
            get("/metrics") {
                call.respondText(
                    text = MetricsRegistry.scrape(),
                    contentType = ContentType.parse("text/plain; version=0.0.4; charset=utf-8"),
                )
            }
        }
        get("/debug/log-test") {
            val testLogger = LoggerFactory.getLogger("http.access")
            val traceId = java.util.UUID.randomUUID().toString()
            org.slf4j.MDC.put("traceId", traceId)
            org.slf4j.MDC.put("method", "GET")
            org.slf4j.MDC.put("path", "/debug/log-test")
            org.slf4j.MDC.put("status", "200")
            org.slf4j.MDC.put("durationMs", "0")
            testLogger.info("request completed")
            org.slf4j.MDC.clear()
            call.response.headers.append("X-Trace-Id", traceId)
            call.respond(HttpStatusCode.OK, mapOf("status" to "log emitted", "traceId" to traceId))
        }
        productRoutes(productService)
        engagementRoutes(engagementService)
        findingRoutes(findingService)
    }

    if (!openApiEnabled) {
        logger.info("Генерация OpenAPI документации отключена")
    }
}

private fun Application.loadAppConfig(): AppConfig {
    val config = environment.config.config("app")
    return AppConfig(
        database = DatabaseConfig(
            url = config.property("database.url").getString(),
            user = config.property("database.user").getString(),
            password = config.property("database.password").getString(),
            poolSize = config.propertyOrNull("database.poolSize")?.getString()?.toIntOrNull() ?: 10,
        ),
        observability = ObservabilityConfig(
            serviceName = config.propertyOrNull("observability.serviceName")?.getString()
                ?: System.getenv("SERVICE_NAME") ?: "vulnerability-manager",
            environment = config.propertyOrNull("observability.environment")?.getString()
                ?: System.getenv("ENV") ?: "local",
            logstashEnabled = config.propertyOrNull("observability.logstash.enabled")?.getString()?.toBooleanStrictOrNull()
                ?: System.getenv("LOGSTASH_ENABLED")?.toBooleanStrictOrNull() ?: false,
            logstashHost = config.propertyOrNull("observability.logstash.host")?.getString()
                ?: System.getenv("LOGSTASH_HOST") ?: "localhost",
            logstashPort = config.propertyOrNull("observability.logstash.port")?.getString()?.toIntOrNull()
                ?: System.getenv("LOGSTASH_PORT")?.toIntOrNull() ?: 5044,
            metricsEnabled = config.propertyOrNull("observability.metrics.enabled")?.getString()?.toBooleanStrictOrNull()
                ?: System.getenv("METRICS_ENABLED")?.toBooleanStrictOrNull() ?: true,
        ),
        requestMaxSizeBytes = config.propertyOrNull("request.maxSizeBytes")?.getString()?.toLongOrNull() ?: 1_048_576,
        openApiOutputDir = config.propertyOrNull("openapi.outputDir")?.getString() ?: System.getenv("OPENAPI_OUTPUT_DIR") ?: "./docs",
        logsDir = config.propertyOrNull("logs.dir")?.getString() ?: System.getenv("LOGS_DIR") ?: "./logs",
    )
}
