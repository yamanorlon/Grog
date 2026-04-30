package mapper

import models.ProductDto
import models.ProductEntity

object ProductMapper {
    fun toEntity(dto: ProductDto): ProductEntity {
        return ProductEntity(
            id = dto.id,
            name = dto.name,
            description = dto.description,
            created = dto.created ?: java.time.LocalDateTime.now()
        )
    }

    fun toDto(entity: ProductEntity): ProductDto {
        return ProductDto(
            id = entity.id,
            name = entity.name,
            description = entity.description,
            created = entity.created
        )
    }
}