package stephane.amstrong.kotlinmvixxlstore.business.datasource.network.account

import stephane.amstrong.kotlinmvixxlstore.business.domain.models.*
import java.util.*

sealed class RefreshTokenDto {

    data class Request(
        val accessToken: String,
        val refreshToken: String
    ) : RefreshTokenDto()

    data class Response(
        val accessToken: AccessToken,
        val refreshToken: RefreshToken,
    ) : RefreshTokenDto()

}