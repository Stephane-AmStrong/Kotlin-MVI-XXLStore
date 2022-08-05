package stephane.amstrong.kotlinmvixxlstore.business.datasource.network.main

import stephane.amstrong.kotlinmvixxlstore.business.domain.models.Account
import com.google.gson.annotations.SerializedName

class AccountDto(

    @SerializedName("pk")
    val pk: Int,

    @SerializedName("email")
    val email: String,

    @SerializedName("username")
    val username: String
)

fun AccountDto.toAccount(): Account {
    return Account(
        pk = pk,
        email = email,
        username = username
    )
}









