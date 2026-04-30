package models

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class ProductDto(
    @field:JsonProperty("id")
    val id: Long? = null,

    @field:JsonProperty("name")
    val name: String,

    @field:JsonProperty("description")
    val description: String? = null,

    @field:JsonProperty("created")
    val created: LocalDateTime? = null
)