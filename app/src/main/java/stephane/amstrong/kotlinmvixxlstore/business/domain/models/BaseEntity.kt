package stephane.amstrong.kotlinmvixxlstore.business.domain.models

import java.util.*

open class BaseEntity {
    var Id: String = ""
    var CreatedAt: Date = Date()
    var UpdatedAt: Date = Date()
    var CreatedBy: String = ""
    var UpdatedBy: String = ""
}