package stephane.amstrong.kotlinmvixxlstore.business.domain.models

data class Authentication(
    val accessToken: AccessToken,
    val refreshToken: RefreshToken,
    val userInfo: UserInfo,
)