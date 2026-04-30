package context

import models.FindingEntity
import org.springframework.stereotype.Repository
import org.springframework.data.jpa.repository.JpaRepository

@Repository
interface FindingRepository : JpaRepository<FindingEntity, Long> {
    fun findByEngagementId(engagementId: Long): List<FindingEntity>
}