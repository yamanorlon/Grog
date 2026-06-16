package com.yamanorlon.grog.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class EngagementStatus {
    @SerialName("Planned") Planned,
    @SerialName("InProgress") InProgress,
    @SerialName("Completed") Completed,
    @SerialName("Cancelled") Cancelled,
}
