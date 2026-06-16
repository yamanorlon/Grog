package com.yamanorlon.grog.testsupport

import com.yamanorlon.grog.database.table.EngagementsTable
import com.yamanorlon.grog.database.table.FindingsTable
import com.yamanorlon.grog.database.table.ProductsTable
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object TestDatabaseBootstrap {
    private val lock = Any()

    @Volatile
    private var initialized = false

    fun initialize() {
        if (initialized) return

        synchronized(lock) {
            if (initialized) return

            PostgresTestContainer.ensureStarted()

            val jdbcUrl = PostgresTestContainer.jdbcUrl
            val username = PostgresTestContainer.username
            val password = PostgresTestContainer.password

            migrateSchema(jdbcUrl, username, password)

            Database.connect(
                url = jdbcUrl,
                driver = "org.postgresql.Driver",
                user = username,
                password = password,
            )

            ensureExposedSchema()
            initialized = true
        }
    }

    private fun migrateSchema(jdbcUrl: String, username: String, password: String) {
        Flyway.configure()
            .dataSource(jdbcUrl, username, password)
            .locations("classpath:db/migration")
            .validateOnMigrate(true)
            .load()
            .migrate()
    }

    private fun ensureExposedSchema() {
        transaction {
            SchemaUtils.createMissingTablesAndColumns(
                ProductsTable,
                EngagementsTable,
                FindingsTable,
            )
        }
    }
}

