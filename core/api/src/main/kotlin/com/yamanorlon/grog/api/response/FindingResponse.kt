package com.yamanorlon.grog.api.response

import io.ktor.openapi.JsonSchema
import kotlinx.serialization.Serializable
import com.yamanorlon.grog.domain.model.Severity
import com.yamanorlon.grog.domain.model.FindingStatus

@Serializable
@JsonSchema.Description("Ответ Api по Finding")
data class FindingResponse(
    @JsonSchema.Description("UUID сработки")
    @JsonSchema.Format("uuid")
    val id: String,
    @JsonSchema.Description("UUID Engagement")
    @JsonSchema.Format("uuid")
    val engagementId: String,
    @JsonSchema.Description("Имя сработки")
    val title: String,
    @JsonSchema.Description("Описание сработки")
    val description: String,
    @JsonSchema.Description("Критичность сработки")
    val severity: Severity,
    @JsonSchema.Description("Статус сработки")
    val status: FindingStatus,
    @JsonSchema.Description("CVSS score")
    val cvssScore: Double? = null,
    @JsonSchema.Description("CVE сработки")
    val cve: String? = null,
    @JsonSchema.Description("CWE сработки")
    val cwe: String? = null,
    @JsonSchema.Description("Предлагаемое решение митигирования сработки ")
    val mitigation: String? = null,
    @JsonSchema.Description("Импакт на приложение")
    val impact: String? = null,
    @JsonSchema.Description("Полезные ссылки")
    val references: List<String> = emptyList(),
    @JsonSchema.Description("Дата обнаружения сработки")
    @JsonSchema.Format("date")
    val discoveredDate: String? = null,
    @JsonSchema.Description("Дата создания сработки")
    @JsonSchema.Format("date-time")
    val createdAt: String,
    @JsonSchema.Description("Дата последнего обновления сработки")
    @JsonSchema.Format("date-time")
    val updatedAt: String,
    @JsonSchema.Description("Автор сработки (можно брать из коммита Gitlab) (сделаю либо тот кто виновен в сработки либо AppSec/DevSecOps кто обнаружил)")
    val createdBy: String? = null,
    @JsonSchema.Description("Кто последний обновлял сработку")
    val updatedBy: String? = null,
    @JsonSchema.Description("Версия для оптимистической блокировки")
    val version: Long,
)