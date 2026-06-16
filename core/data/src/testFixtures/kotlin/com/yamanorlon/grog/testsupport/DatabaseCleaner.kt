package com.yamanorlon.grog.testsupport

import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseCleaner {
    fun cleanAll() {
        transaction {
            exec("TRUNCATE TABLE findings, engagements, products RESTART IDENTITY CASCADE")
        }
    }
}

