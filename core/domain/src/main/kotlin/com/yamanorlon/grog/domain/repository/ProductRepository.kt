package com.yamanorlon.grog.domain.repository

import java.util.UUID
import com.yamanorlon.grog.domain.model.Page
import com.yamanorlon.grog.domain.model.Product
import com.yamanorlon.grog.domain.model.SortSpec
import com.yamanorlon.grog.domain.model.PageRequest
import com.yamanorlon.grog.domain.model.ProductFilter

interface ProductRepository {
    fun findById(id: UUID): Product?
    fun findAll(filter: ProductFilter, page: PageRequest, sort: SortSpec?): Page<Product>
    fun create(product: Product): Product
    fun update(product: Product): Product?
    fun delete(id: UUID): Boolean
}
