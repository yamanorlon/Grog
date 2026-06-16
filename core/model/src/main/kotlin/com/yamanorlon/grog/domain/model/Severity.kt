package com.yamanorlon.grog.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class Severity {
    @SerialName("Critical") Critical,
    @SerialName("High") High,
    @SerialName("Medium") Medium,
    @SerialName("Low") Low,
    @SerialName("Info") Info,
}
