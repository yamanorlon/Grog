package models

import com.fasterxml.jackson.annotation.JsonProperty

data class UserDto(
    @field:JsonProperty("id")
    val id: Long? = null,

    @field:JsonProperty("username")
    val username: String,

    @field:JsonProperty("email")
    val email: String? = null
)