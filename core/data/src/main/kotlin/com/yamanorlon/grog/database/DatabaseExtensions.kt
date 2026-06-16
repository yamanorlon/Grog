package com.yamanorlon.grog.database

import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.QueryBuilder
import org.jetbrains.exposed.sql.VarCharColumnType

fun arrayContains(column: Column<List<String>>, value: String): Op<Boolean> = object : Op<Boolean>() {
    override fun toQueryBuilder(queryBuilder: QueryBuilder) {
        queryBuilder.append(column)
        queryBuilder.append(" @> ARRAY[")
        queryBuilder.registerArgument(VarCharColumnType(), value)
        queryBuilder.append("]::text[]")
    }
}
