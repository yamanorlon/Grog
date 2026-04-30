package models

import java.time.LocalDateTime
import com.fasterxml.jackson.annotation.JsonProperty

data class FindingDto(
    @field:JsonProperty("id")
    val id: Long? = null,

    @field:JsonProperty("title")
    val title: String,

    @field:JsonProperty("severity")
    val severity: String, // Info, Low, Medium, High, Critical

    @field:JsonProperty("description")
    val description: String? = null,

    @field:JsonProperty("engagement_id")
    val engagementId: Long,

    @field:JsonProperty("file_path")
    val filePath: String? = null,

    @field:JsonProperty("line")
    val line: Int? = null,

    @field:JsonProperty("cwe")
    val cwe: Int? = null,

    @field:JsonProperty("cve")
    val cve: String? = null,

    @field:JsonProperty("is_active")
    val isActive: Boolean = true,

    @field:JsonProperty("verified")
    val verified: Boolean = false,

    @field:JsonProperty("created")
    val created: LocalDateTime? = null
)