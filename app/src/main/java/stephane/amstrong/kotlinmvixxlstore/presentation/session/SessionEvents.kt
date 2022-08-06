package stephane.amstrong.kotlinmvixxlstore.presentation.session

import stephane.amstrong.kotlinmvixxlstore.business.domain.models.Authentication

sealed class SessionEvents {

    data class Authenticate(
        val authentication: Authentication
    ): SessionEvents()

    object Logout: SessionEvents()

    data class Login(
        val authentication: Authentication
    ): SessionEvents()

    data class CheckPreviousAuthUser(
        val email: String
    ): SessionEvents()

    object OnRemoveHeadFromQueue: SessionEvents()

}