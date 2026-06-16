package com.yamanorlon.grog.database.repository

import java.util.UUID
import java.time.ZoneOffset
import java.time.OffsetDateTime
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.deleteWhere
import com.yamanorlon.grog.domain.model.Page
import com.yamanorlon.grog.domain.model.Product
import com.yamanorlon.grog.domain.model.SortSpec
import com.yamanorlon.grog.database.arrayContains
import com.yamanorlon.grog.domain.model.PageRequest
import com.yamanorlon.grog.domain.model.ProductFilter
import com.yamanorlon.grog.domain.model.SortDirection
import com.yamanorlon.grog.database.table.ProductsTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import com.yamanorlon.grog.domain.repository.ProductRepository

class ProductRepositoryImpl : ProductRepository {

    override fun findById(id: UUID): Product? = transaction {
        ProductsTable.selectAll()
            .where {
                ProductsTable.id eq id
            }.singleOrNull()
            ?.toProduct()
    }

    override fun findAll(filter: ProductFilter, page: PageRequest, sort: SortSpec?): Page<Product> = transaction {
        val conditions = buildConditions(filter)
        val query = if (conditions.isEmpty()) {
            ProductsTable.selectAll()
        } else {
            ProductsTable.selectAll().where { combine(conditions) }
        }

        val total = query.count()
        val sortColumn = resolveSortColumn(sort?.field)
        val sortOrder = if (sort?.direction == SortDirection.DESC) SortOrder.DESC else SortOrder.ASC

        val content = query
            .orderBy(sortColumn to sortOrder)
            .limit(page.size, page.offset)
            .map { it.toProduct() }

        Page(content, page.page, page.size, total)
    }

    override fun create(product: Product): Product = transaction {
        val now = OffsetDateTime.now(ZoneOffset.UTC)
        val id = ProductsTable.insert {
            it[name] = product.name
            it[description] = product.description
            it[owner] = product.owner
            it[tags] = product.tags
            it[createdAt] = now
            it[updatedAt] = now
            it[createdBy] = product.createdBy
            it[updatedBy] = product.updatedBy
            it[version] = 0L
        }[ProductsTable.id]

        findById(id)!!
    }

    override fun update(product: Product): Product? = transaction {
        val now = OffsetDateTime.now(ZoneOffset.UTC)
        val updated = ProductsTable.update({
            (ProductsTable.id eq product.id) and (ProductsTable.version eq product.version)
        }) {
            it[name] = product.name
            it[description] = product.description
            it[owner] = product.owner
            it[tags] = product.tags
            it[updatedAt] = now
            it[updatedBy] = product.updatedBy
            it[version] = product.version + 1
        }

        if (updated == 0) null else findById(product.id)
    }

    override fun delete(id: UUID): Boolean = transaction {
        ProductsTable.deleteWhere {
            ProductsTable.id eq id
        } > 0
    }

    private fun buildConditions(filter: ProductFilter): List<Op<Boolean>> {
        val conditions = mutableListOf<Op<Boolean>>()
        filter.name?.takeIf {
            it.isNotBlank()
        }?.let {
            conditions += ProductsTable.name like "%$it%"
        }

        filter.owner?.takeIf {
            it.isNotBlank()
        }?.let {
            conditions += ProductsTable.owner eq it
        }

        filter.tag?.takeIf {
            it.isNotBlank()
        }?.let { tag ->
            conditions += arrayContains(ProductsTable.tags, tag)
        }
        return conditions
    }

    private fun combine(conditions: List<Op<Boolean>>): Op<Boolean> =
        conditions.drop(1).fold(conditions.first()) { acc, op -> acc and op }

    private fun resolveSortColumn(field: String?): Column<*> = when (field?.lowercase()) {
        "owner" -> ProductsTable.owner
        "createdat", "created_at" -> ProductsTable.createdAt
        "updatedat", "updated_at" -> ProductsTable.updatedAt
        else -> ProductsTable.name
    }

    private fun ResultRow.toProduct() = Product(
        id = this[ProductsTable.id],
        name = this[ProductsTable.name],
        description = this[ProductsTable.description],
        owner = this[ProductsTable.owner],
        tags = this[ProductsTable.tags],
        createdAt = this[ProductsTable.createdAt].toInstant(),
        updatedAt = this[ProductsTable.updatedAt].toInstant(),
        createdBy = this[ProductsTable.createdBy],
        updatedBy = this[ProductsTable.updatedBy],
        version = this[ProductsTable.version],
    )
}
