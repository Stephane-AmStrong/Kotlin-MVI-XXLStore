package stephane.amstrong.kotlinmvixxlstore.business.datasource.cache.account

import androidx.room.*
import stephane.amstrong.kotlinmvixxlstore.business.datasource.network.account.AuthenticationDto
import stephane.amstrong.kotlinmvixxlstore.business.domain.models.*
import java.util.*

@Entity
data class AuthenticationEntity(
    @Embedded
    val accessToken: AccessToken,
    @Embedded
    val refreshToken: RefreshToken,
    @Embedded
    val userInfo: UserInfo,
){

    @PrimaryKey(autoGenerate = true)
    var pk: Long = 0
}

fun AuthenticationDto.AuthenticationResponse.toAuthentication(): Authentication {
    return Authentication(
        accessToken = accessToken,
        userInfo = userInfo,
        refreshToken = refreshToken,
    )
}

fun AuthenticationEntity.toAuthentication(): Authentication {
    return Authentication(
        accessToken = accessToken,
        userInfo = userInfo,
        refreshToken = refreshToken,
    )
}

fun Authentication.toEntity(): AuthenticationEntity {
    return AuthenticationEntity(
        accessToken = accessToken,
        userInfo = userInfo,
        refreshToken = refreshToken,
    )
}