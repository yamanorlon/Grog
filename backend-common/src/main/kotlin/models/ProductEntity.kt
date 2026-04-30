package models

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "products")
data class ProductEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val name: String,

    @Column(columnDefinition = "TEXT")
    val description: String? = null,

    @Column(name = "created", updatable = false)
    val created: LocalDateTime = LocalDateTime.now()
)