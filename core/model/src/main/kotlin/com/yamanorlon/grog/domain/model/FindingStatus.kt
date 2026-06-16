package com.yamanorlon.grog.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class FindingStatus {
    @SerialName("Open") Open,
    @SerialName("AcceptedRisk") AcceptedRisk,
    @SerialName("Fixed") Fixed,
    @SerialName("FalsePositive") FalsePositive,
}
