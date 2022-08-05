package stephane.amstrong.kotlinmvixxlstore.presentation.session

import stephane.amstrong.kotlinmvixxlstore.business.domain.models.AuthToken

sealed class SessionEvents {

    object Logout: SessionEvents()

    data class Login(
        val authToken: AuthToken
    ): SessionEvents()

    data class CheckPreviousAuthUser(
        val email: String
    ): SessionEvents()

    object OnRemoveHeadFromQueue: SessionEvents()

}
