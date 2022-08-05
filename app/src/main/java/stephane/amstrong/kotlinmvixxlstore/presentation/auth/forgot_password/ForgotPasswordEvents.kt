package stephane.amstrong.kotlinmvixxlstore.presentation.auth.forgot_password

import stephane.amstrong.kotlinmvixxlstore.business.domain.util.StateMessage

sealed class ForgotPasswordEvents {

    object OnPasswordResetLinkSent: ForgotPasswordEvents()

    data class Error(val stateMessage: StateMessage): ForgotPasswordEvents()

    object OnRemoveHeadFromQueue: ForgotPasswordEvents()
}
