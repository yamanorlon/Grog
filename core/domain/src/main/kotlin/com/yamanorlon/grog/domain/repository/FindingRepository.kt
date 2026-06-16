package com.yamanorlon.grog.domain.repository

import java.util.UUID
import com.yamanorlon.grog.domain.model.Page
import com.yamanorlon.grog.domain.model.Finding
import com.yamanorlon.grog.domain.model.SortSpec
import com.yamanorlon.grog.domain.model.PageRequest
import com.yamanorlon.grog.domain.model.FindingFilter

interface FindingRepository {
    fun findById(id: UUID): Finding?
    fun findAll(filter: FindingFilter, page: PageRequest, sort: SortSpec?): Page<Finding>
    fun create(finding: Finding): Finding
    fun update(finding: Finding): Finding?
    fun delete(id: UUID): Boolean
    fun existsEngagement(engagementId: UUID): Boolean
}
