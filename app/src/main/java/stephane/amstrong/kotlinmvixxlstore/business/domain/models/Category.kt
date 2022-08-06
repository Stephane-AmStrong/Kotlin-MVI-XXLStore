package stephane.amstrong.kotlinmvixxlstore.business.domain.models

import java.util.*

data class Category (
    val id: String,
    val name: String,
    val description: String?,
    val createdAt: Date,
    val updatedAt: Date?,
    val createdBy: String?,
    val updatedBy: String?
)