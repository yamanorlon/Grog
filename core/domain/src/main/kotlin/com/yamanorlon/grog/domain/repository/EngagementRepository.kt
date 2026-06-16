package com.yamanorlon.grog.domain.repository

import java.util.UUID
import com.yamanorlon.grog.domain.model.Page
import com.yamanorlon.grog.domain.model.SortSpec
import com.yamanorlon.grog.domain.model.Engagement
import com.yamanorlon.grog.domain.model.PageRequest
import com.yamanorlon.grog.domain.model.EngagementFilter

interface EngagementRepository {
    fun findById(id: UUID): Engagement?
    fun findAll(filter: EngagementFilter, page: PageRequest, sort: SortSpec?): Page<Engagement>
    fun create(engagement: Engagement): Engagement
    fun update(engagement: Engagement): Engagement?
    fun delete(id: UUID): Boolean
    fun existsProduct(productId: UUID): Boolean
}
