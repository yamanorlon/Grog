package com.yamanorlon.grog.config

import org.slf4j.LoggerFactory
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database

object DatabaseFactory {
    private val logger = LoggerFactory.getLogger(DatabaseFactory::class.java)

    @Volatile
    private var initialized: Boolean = false

    private val lock = Any()

    fun init(config: DatabaseConfig) {
        if (initialized) {
            logger.debug("База данных уже инициализирована, скип повторной инициализации")
            return
        }

        synchronized(lock) {
            if (initialized) {
                return
            }

            logger.info("Запуск миграций с помощью Flyway {}", config.url)
            Flyway.configure()
                .dataSource(config.url, config.user, config.password)
                .locations("classpath:db/migration")
                .validateOnMigrate(true)
                .load()
                .migrate()

            Database.connect(
                url = config.url,
                driver = "org.postgresql.Driver",
                user = config.user,
                password = config.password,
            )

            initialized = true
            logger.info("Инициализация базы данных прошла успешно")
        }
    }
}

