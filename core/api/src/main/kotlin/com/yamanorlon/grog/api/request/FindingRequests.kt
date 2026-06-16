package com.yamanorlon.grog.api.request

import io.ktor.openapi.JsonSchema
import kotlinx.serialization.Serializable
import com.yamanorlon.grog.domain.model.Severity
import com.yamanorlon.grog.domain.model.FindingStatus

@Serializable
@JsonSchema.Description("Данные для создания Finding")
data class CreateFindingRequest(
    @JsonSchema.Description("UUID Engagement")
    @JsonSchema.Format("uuid")
    val engagementId: String,
    @JsonSchema.Description("Имя сработки")
    val title: String,
    @JsonSchema.Description("Описание сработки")
    val description: String,
    @JsonSchema.Description("Критичность сработки")
    val severity: Severity,
    @JsonSchema.Description("Статус работы над сработкой")
    val status: FindingStatus = FindingStatus.Open,
    @JsonSchema.Description("CVSS score")
    val cvssScore: Double? = null,
    @JsonSchema.Description("CVE сработки (для SCA (поддержка будет в будущем))")
    val cve: String? = null,
    @JsonSchema.Description("CWE сработки (для SAST выставлять руками или брать из анализаторов, для SCA из отчетов)")
    val cwe: String? = null,
    @JsonSchema.Description("Предлагаемое решение митигирования сработки (для SAST выставляется руками AppSec инженерами, для SCA берется из отчета)")
    val mitigation: String? = null,
    @JsonSchema.Description("Импакт на приложение (выставляется руками AppSec инженерами)")
    val impact: String? = null,
    @JsonSchema.Description("Полезные ссылки")
    val references: List<String> = emptyList(),
    @JsonSchema.Description("Дата создание сработки")
    @JsonSchema.Format("date")
    val discoveredDate: String? = null,
)

@Serializable
@JsonSchema.Description("Данные для обновление Finding")
data class UpdateFindingRequest(
    @JsonSchema.Description("Новый UUID сработки")
    @JsonSchema.Format("uuid")
    val engagementId: String,
    @JsonSchema.Description("Новое название сработки")
    val title: String,
    @JsonSchema.Description("Новое описание сработки")
    val description: String,
    @JsonSchema.Description("Новая критичность")
    val severity: Severity,
    @JsonSchema.Description("Новый статус сработки")
    val status: FindingStatus,
    @JsonSchema.Description("Новый CVSS score")
    val cvssScore: Double? = null,
    @JsonSchema.Description("Новый CVE")
    val cve: String? = null,
    @JsonSchema.Description("Новый CWE")
    val cwe: String? = null,
    @JsonSchema.Description("Новое предлагаемое решение митигирования сработки ")
    val mitigation: String? = null,
    @JsonSchema.Description("Новый импакт на приложение ")
    val impact: String? = null,
    @JsonSchema.Description("Новые полезные ссылки")
    val references: List<String> = emptyList(),
    @JsonSchema.Description("Новая дата обнаружения сработки")
    @JsonSchema.Format("date")
    val discoveredDate: String? = null,
    @JsonSchema.Description("Версия для оптимистической блокировки")
    val version: Long,
)
