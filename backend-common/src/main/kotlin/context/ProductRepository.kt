package context

import models.ProductEntity
import org.springframework.stereotype.Repository
import org.springframework.data.jpa.repository.JpaRepository

@Repository
interface ProductRepository : JpaRepository<ProductEntity, Long>