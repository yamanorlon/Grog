package com.yamanorlon.grog.config

data class ObservabilityConfig(
    val serviceName: String = "vulnerability-manager",
    val environment: String = "local",
    val logstashEnabled: Boolean = false,
    val logstashHost: String = "localhost",
    val logstashPort: Int = 5044,
    val metricsEnabled: Boolean = true,
)
