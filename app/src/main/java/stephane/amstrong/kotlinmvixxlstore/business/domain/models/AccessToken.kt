package stephane.amstrong.kotlinmvixxlstore.business.domain.models

import androidx.room.ColumnInfo
import java.util.*

data class AccessToken(
    @ColumnInfo(name = "token_value")
    val value: String,
    @ColumnInfo(name = "token_expiryDate")
    val expiryDate: Date
)