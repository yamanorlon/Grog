package models

import java.time.LocalDate
import com.fasterxml.jackson.annotation.JsonProperty

data class EngagementDto(
    @field:JsonProperty("id")
    val id: Long? = null,

    @field:JsonProperty("name")
    val name: String,

    @field:JsonProperty("product_id")
    val productId: Long,

    @field:JsonProperty("active")
    val active: Boolean = true,

    @field:JsonProperty("target_start")
    val targetStart: LocalDate? = null,

    @field:JsonProperty("target_end")
    val targetEnd: LocalDate? = null
)