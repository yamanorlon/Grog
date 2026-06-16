package com.yamanorlon.grog.testcontainers

import com.yamanorlon.grog.module
import com.yamanorlon.grog.testsupport.AbstractIntegrationTest
import com.yamanorlon.grog.testsupport.DatabaseCleaner
import com.yamanorlon.grog.testsupport.PostgresTestContainer
import com.yamanorlon.grog.testsupport.TestDatabaseBootstrap
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.testing.ApplicationTestBuilder
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.BeforeAll
import java.nio.file.Files

abstract class ApiIntegrationTest : AbstractIntegrationTest() {

    companion object {
        @JvmStatic
        protected lateinit var openApiOutputDir: String
        @JvmStatic
        protected lateinit var logsDir: String

        @JvmStatic
        @BeforeAll
        fun setupFilesystem() {
            val tempRoot = Files.createTempDirectory("grog-api-test")
            openApiOutputDir = tempRoot.resolve("docs").toString()
            logsDir = tempRoot.resolve("logs").toString()
        }
    }

    protected fun ApplicationTestBuilder.configureTestApplication() {
        TestDatabaseBootstrap.initialize()

        environment {
            config = MapApplicationConfig(
                "ktor.deployment.port" to "8080",
                "app.database.url" to PostgresTestContainer.jdbcUrl,
                "app.database.user" to PostgresTestContainer.username,
                "app.database.password" to PostgresTestContainer.password,
                "app.database.poolSize" to "5",
                "app.openapi.outputDir" to openApiOutputDir,
                "app.openapi.enabled" to "false",
                "app.logs.dir" to logsDir,
                "app.observability.logstash.enabled" to "false",
                "app.observability.metrics.enabled" to "false",
                "app.observability.environment" to "test",
            )
        }
        application { module() }
        DatabaseCleaner.cleanAll()
    }

    protected fun ApplicationTestBuilder.createJsonClient(): HttpClient = createClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true; encodeDefaults = true })
        }
    }
}
