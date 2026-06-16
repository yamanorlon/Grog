package com.yamanorlon.grog.database.table

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.timestampWithTimeZone

object ProductsTable : Table("products") {
    val id = uuid("id").autoGenerate()
    val name = varchar("name", 255)
    val description = text("description").nullable()
    val owner = varchar("owner", 255).nullable()
    val tags = array<String>("tags")
    val createdAt = timestampWithTimeZone("created_at")
    val updatedAt = timestampWithTimeZone("updated_at")
    val createdBy = varchar("created_by", 255).nullable()
    val updatedBy = varchar("updated_by", 255).nullable()
    val version = long("version")

    override val primaryKey = PrimaryKey(id)
}

object EngagementsTable : Table("engagements") {
    val id = uuid("id").autoGenerate()
    val productId = uuid("product_id").references(ProductsTable.id)
    val name = varchar("name", 255)
    val description = text("description").nullable()
    val target = varchar("target", 500).nullable()
    val status = varchar("status", 50)
    val startDate = timestampWithTimeZone("start_date")
    val endDate = timestampWithTimeZone("end_date").nullable()
    val createdAt = timestampWithTimeZone("created_at")
    val updatedAt = timestampWithTimeZone("updated_at")
    val createdBy = varchar("created_by", 255).nullable()
    val updatedBy = varchar("updated_by", 255).nullable()
    val version = long("version")

    override val primaryKey = PrimaryKey(id)
}

object FindingsTable : Table("findings") {
    val id = uuid("id").autoGenerate()
    val engagementId = uuid("engagement_id").references(EngagementsTable.id)
    val title = varchar("title", 500)
    val description = text("description")
    val severity = varchar("severity", 50)
    val status = varchar("status", 50)
    val cvssScore = decimal("cvss_score", 3, 1).nullable()
    val cve = varchar("cve", 50).nullable()
    val cwe = varchar("cwe", 50).nullable()
    val mitigation = text("mitigation").nullable()
    val impact = text("impact").nullable()
    val referenceLinks = array<String>("reference_links")
    val discoveredDate = date("discovered_date").nullable()
    val createdAt = timestampWithTimeZone("created_at")
    val updatedAt = timestampWithTimeZone("updated_at")
    val createdBy = varchar("created_by", 255).nullable()
    val updatedBy = varchar("updated_by", 255).nullable()
    val version = long("version")

    override val primaryKey = PrimaryKey(id)
}
