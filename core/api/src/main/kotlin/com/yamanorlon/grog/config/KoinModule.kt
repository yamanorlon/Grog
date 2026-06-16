package com.yamanorlon.grog.config

import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import io.ktor.server.application.install
import io.ktor.server.application.Application
import com.yamanorlon.grog.service.ProductService
import com.yamanorlon.grog.service.FindingService
import com.yamanorlon.grog.service.EngagementService
import com.yamanorlon.grog.domain.repository.FindingRepository
import com.yamanorlon.grog.domain.repository.ProductRepository
import com.yamanorlon.grog.domain.repository.EngagementRepository
import com.yamanorlon.grog.database.repository.FindingRepositoryImpl
import com.yamanorlon.grog.database.repository.ProductRepositoryImpl
import com.yamanorlon.grog.database.repository.EngagementRepositoryImpl

fun Application.configureDependencyInjection(config: AppConfig) {
    install(Koin) {
        slf4jLogger()
        modules(appModule(config))
    }
}

fun appModule(config: AppConfig) = module {
    single { config }

    single<ProductRepository> { ProductRepositoryImpl() }
    single<EngagementRepository> { EngagementRepositoryImpl() }
    single<FindingRepository> { FindingRepositoryImpl() }

    single { ProductService(get()) }
    single { EngagementService(get()) }
    single { FindingService(get()) }
}
