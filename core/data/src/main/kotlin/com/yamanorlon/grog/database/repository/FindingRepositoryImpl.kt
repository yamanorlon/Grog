package com.yamanorlon.grog.database.repository

import java.util.UUID
import java.math.BigDecimal
import java.time.ZoneOffset
import java.time.OffsetDateTime
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.deleteWhere
import com.yamanorlon.grog.domain.model.Page
import com.yamanorlon.grog.domain.model.Finding
import com.yamanorlon.grog.domain.model.Severity
import com.yamanorlon.grog.domain.model.SortSpec
import com.yamanorlon.grog.domain.model.PageRequest
import com.yamanorlon.grog.domain.model.FindingStatus
import com.yamanorlon.grog.domain.model.FindingFilter
import com.yamanorlon.grog.domain.model.SortDirection
import com.yamanorlon.grog.database.table.FindingsTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import com.yamanorlon.grog.database.table.EngagementsTable
import com.yamanorlon.grog.domain.repository.FindingRepository

class FindingRepositoryImpl : FindingRepository {

    override fun findById(id: UUID): Finding? = transaction {
        FindingsTable.selectAll()
            .where {
                FindingsTable.id eq id
            }.singleOrNull()
            ?.toFinding()
    }

    override fun findAll(filter: FindingFilter, page: PageRequest, sort: SortSpec?): Page<Finding> = transaction {
        val conditions = buildConditions(filter)
        val query = if (conditions.isEmpty()) {
            FindingsTable.selectAll()
        } else {
            FindingsTable.selectAll().where { combine(conditions) }
        }

        val total = query.count()
        val sortColumn = resolveSortColumn(sort?.field)
        val sortOrder = if (sort?.direction == SortDirection.DESC) SortOrder.DESC else SortOrder.ASC

        val content = query
            .orderBy(sortColumn to sortOrder)
            .limit(page.size, page.offset)
            .map { it.toFinding() }

        Page(content, page.page, page.size, total)
    }

    override fun create(finding: Finding): Finding = transaction {
        val now = OffsetDateTime.now(ZoneOffset.UTC)
        val id = FindingsTable.insert {
            it[engagementId] = finding.engagementId
            it[title] = finding.title
            it[description] = finding.description
            it[severity] = finding.severity.name
            it[status] = finding.status.name
            it[cvssScore] = finding.cvssScore?.let { score -> BigDecimal.valueOf(score) }
            it[cve] = finding.cve
            it[cwe] = finding.cwe
            it[mitigation] = finding.mitigation
            it[impact] = finding.impact
            it[referenceLinks] = finding.references
            it[discoveredDate] = finding.discoveredDate
            it[createdAt] = now
            it[updatedAt] = now
            it[createdBy] = finding.createdBy
            it[updatedBy] = finding.updatedBy
            it[version] = 0L
        }[FindingsTable.id]

        findById(id)!!
    }

    override fun update(finding: Finding): Finding? = transaction {
        val now = OffsetDateTime.now(ZoneOffset.UTC)
        val updated = FindingsTable.update({
            (FindingsTable.id eq finding.id) and (FindingsTable.version eq finding.version)
        }) {
            it[engagementId] = finding.engagementId
            it[title] = finding.title
            it[description] = finding.description
            it[severity] = finding.severity.name
            it[status] = finding.status.name
            it[cvssScore] = finding.cvssScore?.let { score -> BigDecimal.valueOf(score) }
            it[cve] = finding.cve
            it[cwe] = finding.cwe
            it[mitigation] = finding.mitigation
            it[impact] = finding.impact
            it[referenceLinks] = finding.references
            it[discoveredDate] = finding.discoveredDate
            it[updatedAt] = now
            it[updatedBy] = finding.updatedBy
            it[version] = finding.version + 1
        }

        if (updated == 0) null else findById(finding.id)
    }

    override fun delete(id: UUID): Boolean = transaction {
        FindingsTable.deleteWhere {
            FindingsTable.id eq id
        } > 0
    }

    override fun existsEngagement(engagementId: UUID): Boolean = transaction {
        EngagementsTable.selectAll()
            .where {
                EngagementsTable.id eq engagementId
            }.count() > 0
    }

    private fun buildConditions(filter: FindingFilter): List<Op<Boolean>> {
        val conditions = mutableListOf<Op<Boolean>>()
        filter.engagementId?.let {
            conditions += FindingsTable.engagementId eq it
        }
        filter.severity?.let {
            conditions += FindingsTable.severity eq it.name
        }
        filter.status?.let {
            conditions += FindingsTable.status eq it.name
        }
        filter.title?.takeIf {
            it.isNotBlank()
        }?.let {
            conditions += FindingsTable.title like "%$it%"
        }
        return conditions
    }

    private fun combine(conditions: List<Op<Boolean>>): Op<Boolean> =
        conditions.drop(1).fold(conditions.first()) { acc, op -> acc and op }

    private fun resolveSortColumn(field: String?): Column<*> = when (field?.lowercase()) {
        "severity" -> FindingsTable.severity
        "status" -> FindingsTable.status
        "cvssscore", "cvss_score" -> FindingsTable.cvssScore
        "discovereddate", "discovered_date" -> FindingsTable.discoveredDate
        "createdat", "created_at" -> FindingsTable.createdAt
        else -> FindingsTable.title
    }

    private fun ResultRow.toFinding() = Finding(
        id = this[FindingsTable.id],
        engagementId = this[FindingsTable.engagementId],
        title = this[FindingsTable.title],
        description = this[FindingsTable.description],
        severity = Severity.valueOf(this[FindingsTable.severity]),
        status = FindingStatus.valueOf(this[FindingsTable.status]),
        cvssScore = this[FindingsTable.cvssScore]?.toDouble(),
        cve = this[FindingsTable.cve],
        cwe = this[FindingsTable.cwe],
        mitigation = this[FindingsTable.mitigation],
        impact = this[FindingsTable.impact],
        references = this[FindingsTable.referenceLinks],
        discoveredDate = this[FindingsTable.discoveredDate],
        createdAt = this[FindingsTable.createdAt].toInstant(),
        updatedAt = this[FindingsTable.updatedAt].toInstant(),
        createdBy = this[FindingsTable.createdBy],
        updatedBy = this[FindingsTable.updatedBy],
        version = this[FindingsTable.version],
    )
}
