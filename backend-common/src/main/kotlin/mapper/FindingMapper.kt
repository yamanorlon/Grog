package mapper

import models.FindingDto
import models.FindingEntity
import models.EngagementEntity

object FindingMapper {
    fun toEntity(dto: FindingDto, engagement: EngagementEntity): FindingEntity {
        return FindingEntity(
            id = dto.id,
            title = dto.title,
            severity = dto.severity,
            description = dto.description,
            engagement = engagement,
            filePath = dto.filePath,
            line = dto.line,
            cwe = dto.cwe,
            cve = dto.cve,
            isActive = dto.isActive,
            verified = dto.verified,
            created = dto.created ?: java.time.LocalDateTime.now()
        )
    }

    fun toDto(entity: FindingEntity): FindingDto {
        return FindingDto(
            id = entity.id,
            title = entity.title,
            severity = entity.severity,
            description = entity.description,
            engagementId = entity.engagement.id!!,
            filePath = entity.filePath,
            line = entity.line,
            cwe = entity.cwe,
            cve = entity.cve,
            isActive = entity.isActive,
            verified = entity.verified,
            created = entity.created
        )
    }
}