package com.yamanorlon.grog.database.repository

import java.util.UUID
import java.time.ZoneOffset
import java.time.OffsetDateTime
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.deleteWhere
import com.yamanorlon.grog.domain.model.Page
import com.yamanorlon.grog.domain.model.SortSpec
import com.yamanorlon.grog.domain.model.Engagement
import com.yamanorlon.grog.domain.model.PageRequest
import com.yamanorlon.grog.domain.model.SortDirection
import com.yamanorlon.grog.database.table.ProductsTable
import com.yamanorlon.grog.domain.model.EngagementFilter
import com.yamanorlon.grog.domain.model.EngagementStatus
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import com.yamanorlon.grog.database.table.EngagementsTable
import com.yamanorlon.grog.domain.repository.EngagementRepository

class EngagementRepositoryImpl : EngagementRepository {

    override fun findById(id: UUID): Engagement? = transaction {
        EngagementsTable.selectAll()
            .where {
                EngagementsTable.id eq id
            }.singleOrNull()
            ?.toEngagement()
    }

    override fun findAll(filter: EngagementFilter, page: PageRequest, sort: SortSpec?): Page<Engagement> = transaction {
        val conditions = buildConditions(filter)
        val query = if (conditions.isEmpty()) {
            EngagementsTable.selectAll()
        } else {
            EngagementsTable.selectAll().where {
                combine(conditions)
            }
        }

        val total = query.count()
        val sortColumn = resolveSortColumn(sort?.field)
        val sortOrder = if (sort?.direction == SortDirection.DESC) SortOrder.DESC else SortOrder.ASC

        val content = query
            .orderBy(sortColumn to sortOrder)
            .limit(page.size, page.offset)
            .map { it.toEngagement() }

        Page(content, page.page, page.size, total)
    }

    override fun create(engagement: Engagement): Engagement = transaction {
        val now = OffsetDateTime.now(ZoneOffset.UTC)
        val id = EngagementsTable.insert {
            it[productId] = engagement.productId
            it[name] = engagement.name
            it[description] = engagement.description
            it[target] = engagement.target
            it[status] = engagement.status.name
            it[startDate] = OffsetDateTime.ofInstant(engagement.startDate, ZoneOffset.UTC)
            it[endDate] = engagement.endDate?.let { end -> OffsetDateTime.ofInstant(end, ZoneOffset.UTC) }
            it[createdAt] = now
            it[updatedAt] = now
            it[createdBy] = engagement.createdBy
            it[updatedBy] = engagement.updatedBy
            it[version] = 0L
        }[EngagementsTable.id]

        findById(id)!!
    }

    override fun update(engagement: Engagement): Engagement? = transaction {
        val now = OffsetDateTime.now(ZoneOffset.UTC)
        val updated = EngagementsTable.update({
            (EngagementsTable.id eq engagement.id) and (EngagementsTable.version eq engagement.version)
        }) {
            it[productId] = engagement.productId
            it[name] = engagement.name
            it[description] = engagement.description
            it[target] = engagement.target
            it[status] = engagement.status.name
            it[startDate] = OffsetDateTime.ofInstant(engagement.startDate, ZoneOffset.UTC)
            it[endDate] = engagement.endDate?.let { end -> OffsetDateTime.ofInstant(end, ZoneOffset.UTC) }
            it[updatedAt] = now
            it[updatedBy] = engagement.updatedBy
            it[version] = engagement.version + 1
        }

        if (updated == 0) null else findById(engagement.id)
    }

    override fun delete(id: UUID): Boolean = transaction {
        EngagementsTable.deleteWhere {
            EngagementsTable.id eq id
        } > 0
    }

    override fun existsProduct(productId: UUID): Boolean = transaction {
        ProductsTable.selectAll()
            .where {
                ProductsTable.id eq productId
            }.count() > 0
    }

    private fun buildConditions(filter: EngagementFilter): List<Op<Boolean>> {
        val conditions = mutableListOf<Op<Boolean>>()
        filter.productId?.let {
            conditions += EngagementsTable.productId eq it
        }
        filter.status?.let {
            conditions += EngagementsTable.status eq it.name
        }
        filter.name?.takeIf {
            it.isNotBlank()
        }?.let {
            conditions += EngagementsTable.name like "%$it%"
        }
        return conditions
    }

    private fun combine(conditions: List<Op<Boolean>>): Op<Boolean> =
        conditions.drop(1).fold(conditions.first()) { acc, op -> acc and op }

    private fun resolveSortColumn(field: String?): Column<*> = when (field?.lowercase()) {
        "startdate", "start_date" -> EngagementsTable.startDate
        "enddate", "end_date" -> EngagementsTable.endDate
        "status" -> EngagementsTable.status
        "createdat", "created_at" -> EngagementsTable.createdAt
        else -> EngagementsTable.name
    }

    private fun ResultRow.toEngagement() = Engagement(
        id = this[EngagementsTable.id],
        productId = this[EngagementsTable.productId],
        name = this[EngagementsTable.name],
        description = this[EngagementsTable.description],
        target = this[EngagementsTable.target],
        status = EngagementStatus.valueOf(this[EngagementsTable.status]),
        startDate = this[EngagementsTable.startDate].toInstant(),
        endDate = this[EngagementsTable.endDate]?.toInstant(),
        createdAt = this[EngagementsTable.createdAt].toInstant(),
        updatedAt = this[EngagementsTable.updatedAt].toInstant(),
        createdBy = this[EngagementsTable.createdBy],
        updatedBy = this[EngagementsTable.updatedBy],
        version = this[EngagementsTable.version],
    )
}
