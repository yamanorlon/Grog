package com.yamanorlon.grog.observability

import java.time.Duration
import io.ktor.server.application.install
import io.ktor.server.application.Application
import com.yamanorlon.grog.config.ObservabilityConfig
import io.micrometer.prometheusmetrics.PrometheusConfig
import io.ktor.server.metrics.micrometer.MicrometerMetrics
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics
import io.micrometer.core.instrument.binder.system.ProcessorMetrics
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig

object MetricsRegistry {
    private var meterRegistry: PrometheusMeterRegistry? = null

    fun register(registry: PrometheusMeterRegistry) {
        meterRegistry = registry
    }

    fun scrape(): String = meterRegistry?.scrape()
        ?: throw IllegalStateException("Metrics registry is not initialized")

    val isInitialized: Boolean
        get() = meterRegistry != null
}

fun Application.configureMetrics(config: ObservabilityConfig) {
    if (!config.metricsEnabled) {
        return
    }

    val prometheusRegistry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)
    MetricsRegistry.register(prometheusRegistry)

    install(MicrometerMetrics) {
        registry = prometheusRegistry
        distributionStatisticConfig = DistributionStatisticConfig.Builder()
            .percentilesHistogram(true)
            .maximumExpectedValue(Duration.ofSeconds(30).toNanos().toDouble())
            .serviceLevelObjectives(
                Duration.ofMillis(50).toNanos().toDouble(),
                Duration.ofMillis(100).toNanos().toDouble(),
                Duration.ofMillis(250).toNanos().toDouble(),
                Duration.ofMillis(500).toNanos().toDouble(),
                Duration.ofSeconds(1).toNanos().toDouble(),
            )
            .build()
        meterBinders = listOf(
            JvmMemoryMetrics(),
            JvmGcMetrics(),
            ProcessorMetrics(),
        )
    }
}
