package com.yamanorlon.grog.domain.model

data class PageRequest(
    val page: Int = 0,
    val size: Int = 20,
    val sort: String? = null,
) {
    init {
        require(page >= 0) { "Номер страницы не может быть отрицательным" }
        require(size in 1..100) { "Размер не может быть > 1 и < 100" }
    }

    val offset: Long get() = page.toLong() * size
}

data class Page<T>(
    val content: List<T>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
) {
    val totalPages: Int
        get() = if (totalElements == 0L) 0 else ((totalElements + size - 1) / size).toInt()
}

data class ProductFilter(
    val name: String? = null,
    val owner: String? = null,
    val tag: String? = null,
)

data class EngagementFilter(
    val productId: java.util.UUID? = null,
    val status: EngagementStatus? = null,
    val name: String? = null,
)

data class FindingFilter(
    val engagementId: java.util.UUID? = null,
    val severity: Severity? = null,
    val status: FindingStatus? = null,
    val title: String? = null,
)

enum class SortDirection {
    ASC,
    DESC,
}

data class SortSpec(
    val field: String,
    val direction: SortDirection = SortDirection.ASC,
)
