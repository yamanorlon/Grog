package com.yamanorlon.grog.service

import java.util.UUID
import org.slf4j.LoggerFactory
import com.yamanorlon.grog.domain.model.Page
import com.yamanorlon.grog.util.DateTimeUtils
import com.yamanorlon.grog.domain.model.Product
import com.yamanorlon.grog.domain.model.SortSpec
import com.yamanorlon.grog.domain.model.PageRequest
import com.yamanorlon.grog.domain.model.ProductFilter
import com.yamanorlon.grog.validation.RequestValidators
import com.yamanorlon.grog.api.request.CreateProductRequest
import com.yamanorlon.grog.api.request.UpdateProductRequest
import com.yamanorlon.grog.domain.exception.ConflictException
import com.yamanorlon.grog.domain.exception.NotFoundException
import com.yamanorlon.grog.domain.repository.ProductRepository
import com.yamanorlon.grog.domain.exception.ValidationException

class ProductService(
    private val productRepository: ProductRepository,
) {
    private val logger = LoggerFactory.getLogger(ProductService::class.java)

    fun create(request: CreateProductRequest, actor: String?): Product {
        val errors = RequestValidators.validateCreateProduct(request)
        if (errors.isNotEmpty()) throw ValidationException(errors)

        logger.info("Создание Product с именем={}", request.name)
        val now = DateTimeUtils.now()
        val product = Product(
            id = UUID.randomUUID(),
            name = request.name.trim(),
            description = request.description?.trim(),
            owner = request.owner?.trim(),
            tags = request.tags.map { it.trim() }.filter { it.isNotEmpty() },
            createdAt = now,
            updatedAt = now,
            createdBy = actor,
            updatedBy = actor,
            version = 0,
        )
        return productRepository.create(product)
    }

    fun update(id: UUID, request: UpdateProductRequest, actor: String?): Product {
        val errors = RequestValidators.validateUpdateProduct(request)
        if (errors.isNotEmpty()) throw ValidationException(errors)

        val existing = productRepository.findById(id)
            ?: throw NotFoundException("Продукт с ID=$id не найден")

        val updated = existing.copy(
            name = request.name.trim(),
            description = request.description?.trim(),
            owner = request.owner?.trim(),
            tags = request.tags.map { it.trim() }.filter { it.isNotEmpty() },
            updatedBy = actor,
            version = request.version,
        )

        val result = productRepository.update(updated)
            ?: throw ConflictException("Продукт был изменен в другой транзакции.")

        logger.info("Успешно обновлен продукт c ID={}", id)
        return result
    }

    fun getById(id: UUID): Product =
        productRepository.findById(id) ?: throw NotFoundException("Продукт с ID=$id не найден")

    fun list(filter: ProductFilter, page: PageRequest, sort: SortSpec?): Page<Product> =
        productRepository.findAll(filter, page, sort)

    fun delete(id: UUID) {
        val deleted = productRepository.delete(id)
        if (!deleted) throw NotFoundException("Продукт с ID=$id не найден")
        logger.info("Успешно удален проект с ID={}", id)
    }
}
