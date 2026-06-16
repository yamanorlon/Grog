package com.yamanorlon.grog.testsupport

import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers(disabledWithoutDocker = true)
@Execution(ExecutionMode.SAME_THREAD)
abstract class AbstractIntegrationTest {

    companion object {
        @JvmStatic
        @BeforeAll
        fun bootstrapDatabase() {
            TestDatabaseBootstrap.initialize()
        }
    }

    @BeforeEach
    fun prepareDatabase() {
        TestDatabaseBootstrap.initialize()
        DatabaseCleaner.cleanAll()
    }
}
