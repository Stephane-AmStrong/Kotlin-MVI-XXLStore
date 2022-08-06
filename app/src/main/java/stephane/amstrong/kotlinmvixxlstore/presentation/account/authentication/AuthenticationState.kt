package stephane.amstrong.kotlinmvixxlstore.presentation.account.authentication

import stephane.amstrong.kotlinmvixxlstore.business.domain.util.Queue
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.StateMessage

data class AuthenticationState (
    val isLoading: Boolean = false,
    val email: String = "",
    val password: String = "",
    val queue: Queue<StateMessage> = Queue(mutableListOf()),
)