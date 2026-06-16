package com.yamanorlon.grog.config

data class DatabaseConfig(
    val url: String,
    val user: String,
    val password: String,
    val poolSize: Int = 10,
)

data class AppConfig(
    val database: DatabaseConfig,
    val observability: ObservabilityConfig = ObservabilityConfig(),
    val requestMaxSizeBytes: Long = 1_048_576,
    val openApiOutputDir: String = "./docs",
    val logsDir: String = "./logs",
)
