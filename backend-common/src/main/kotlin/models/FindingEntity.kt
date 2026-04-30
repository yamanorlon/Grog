package models

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "findings")
data class FindingEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val title: String,

    @Column(nullable = false)
    val severity: String,

    @Column(columnDefinition = "TEXT")
    val description: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "engagement_id", nullable = false)
    val engagement: EngagementEntity,

    @Column(name = "file_path")
    val filePath: String? = null,

    @Column(name = "line")
    val line: Int? = null,

    @Column(name = "cwe")
    val cwe: Int? = null,

    @Column(name = "cve")
    val cve: String? = null,

    @Column(name = "is_active")
    val isActive: Boolean = true,

    @Column(name = "verified")
    val verified: Boolean = false,

    @Column(name = "created", updatable = false)
    val created: LocalDateTime = LocalDateTime.now()
)