package mapper

import models.EngagementDto
import models.ProductEntity
import models.EngagementEntity

object EngagementMapper {
    fun toEntity(dto: EngagementDto, product: ProductEntity): EngagementEntity {
        return EngagementEntity(
            id = dto.id,
            name = dto.name,
            product = product,
            active = dto.active,
            targetStart = dto.targetStart,
            targetEnd = dto.targetEnd
        )
    }

    fun toDto(entity: EngagementEntity): EngagementDto {
        return EngagementDto(
            id = entity.id,
            name = entity.name,
            productId = entity.product.id!!,
            active = entity.active,
            targetStart = entity.targetStart,
            targetEnd = entity.targetEnd
        )
    }
}