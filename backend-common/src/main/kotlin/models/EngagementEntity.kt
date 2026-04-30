package models

import java.time.LocalDate
import jakarta.persistence.*

@Entity
@Table(name = "engagements")
data class EngagementEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val name: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    val product: ProductEntity,

    @Column(nullable = false)
    val active: Boolean = true,

    @Column(name = "target_start")
    val targetStart: LocalDate? = null,

    @Column(name = "target_end")
    val targetEnd: LocalDate? = null
)