package com.yamanorlon.grog.observability

import org.slf4j.LoggerFactory
import io.ktor.server.application.Application
import com.yamanorlon.grog.config.ObservabilityConfig

fun Application.configureObservability(config: ObservabilityConfig) {
    val logger = LoggerFactory.getLogger("Observability")

    configureRequestTracing(config)
    configureMetrics(config)

    logger.info(
        "Observability enabled (service={}, environment={}, logstash={}:{}, metrics={})",
        config.serviceName,
        config.environment,
        if (config.logstashEnabled) config.logstashHost else "disabled",
        if (config.logstashEnabled) config.logstashPort else "-",
        config.metricsEnabled,
    )
}
