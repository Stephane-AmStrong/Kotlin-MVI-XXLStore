package stephane.amstrong.kotlinmvixxlstore.business.datasource.network.account

import stephane.amstrong.kotlinmvixxlstore.business.domain.models.*

sealed class AuthenticationDto {

    data class AuthenticationRequest(
        val email : String,
        val password : String,
    ) : AuthenticationDto()

    data class AuthenticationResponse(
        val accessToken: AccessToken,
        val refreshToken: RefreshToken,
        val userInfo: UserInfo,
        val Message: String?,
        val StatusCode: Int?
    ) : AuthenticationDto()
}