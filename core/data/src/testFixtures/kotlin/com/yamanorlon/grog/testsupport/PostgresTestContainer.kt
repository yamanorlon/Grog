package com.yamanorlon.grog.testsupport

import org.testcontainers.containers.PostgreSQLContainer

object PostgresTestContainer {
    private val container: PostgreSQLContainer<*> = PostgreSQLContainer("postgres:17-alpine")
        .withDatabaseName("vulnmanager")
        .withUsername("postgres")
        .withPassword("postgres")

    val jdbcUrl: String
        get() {
            ensureStarted()
            return container.jdbcUrl
        }

    val username: String
        get() {
            ensureStarted()
            return container.username
        }

    val password: String
        get() {
            ensureStarted()
            return container.password
        }

    fun ensureStarted() {
        if (!container.isRunning) {
            container.start()
        }
    }
}
