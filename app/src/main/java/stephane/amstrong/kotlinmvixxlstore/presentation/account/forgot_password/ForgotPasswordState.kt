package stephane.amstrong.kotlinmvixxlstore.presentation.account.forgot_password

import stephane.amstrong.kotlinmvixxlstore.business.domain.util.Queue
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.StateMessage

data class ForgotPasswordState(
    val isLoading: Boolean = false,
    val isPasswordResetLinkSent: Boolean = false,
    val queue: Queue<StateMessage> = Queue(mutableListOf()),
)
