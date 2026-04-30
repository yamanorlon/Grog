package context

import models.EngagementEntity
import org.springframework.stereotype.Repository
import org.springframework.data.jpa.repository.JpaRepository

@Repository
interface EngagementRepository : JpaRepository<EngagementEntity, Long> {
    fun findByProductId(productId: Long): List<EngagementEntity>
}