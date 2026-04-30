package mapper

import models.UserDto
import models.UserEntity

object UserMapper {
    fun toEntity(dto: UserDto): UserEntity {
        return UserEntity(
            id = dto.id,
            username = dto.username,
            email = dto.email
        )
    }

    fun toDto(entity: UserEntity): UserDto {
        return UserDto(
            id = entity.id,
            username = entity.username,
            email = entity.email
        )
    }
}