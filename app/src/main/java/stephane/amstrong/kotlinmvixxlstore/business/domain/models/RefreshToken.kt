package stephane.amstrong.kotlinmvixxlstore.business.domain.models

import androidx.room.ColumnInfo
import java.util.*

data class RefreshToken(
    val id: String,
    @ColumnInfo(name = "refresh_value")
    val value: String,
    @ColumnInfo(name = "refresh_expiryDate")
    val expiryDate: Date
)