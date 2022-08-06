package stephane.amstrong.kotlinmvixxlstore.business.datasource.cache

import java.util.*

data class BaseEntity(
    var id : String?,
    var createdAt : Date,
    var updatedAt : Date,
    var createdBy : String,
    var updatedBy : String,
)
