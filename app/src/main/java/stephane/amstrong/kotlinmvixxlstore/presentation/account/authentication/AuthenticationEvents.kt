package stephane.amstrong.kotlinmvixxlstore.presentation.account.authentication


sealed class AuthenticationEvents {

    data class Authenticate(
        val email:String,
        val password:String,
    ): AuthenticationEvents()

    data class OnUpdateEmail(
        val email: String
    ): AuthenticationEvents()

    data class OnUpdatePassword(
        val password: String
    ): AuthenticationEvents()

    data class OnForgotPassword(
        val email:String,
    ): AuthenticationEvents()

    data class OnRefreshAccessToken(
        val accessToken:String,
        val refreshToken:String,
    ): AuthenticationEvents()

    data class OnResetPassword(
        val email:String,
        val token:String,
        val password:String,
        val ConfirmPassword:String,
    ): AuthenticationEvents()

    object RevokeAccessToken: AuthenticationEvents()

    object OnRemoveHeadFromQueue: AuthenticationEvents()

}