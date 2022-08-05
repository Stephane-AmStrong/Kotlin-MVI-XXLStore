package stephane.amstrong.kotlinmvixxlstore.presentation.auth.register

import stephane.amstrong.kotlinmvixxlstore.business.domain.util.Queue
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.StateMessage

data class RegisterState(
    val isLoading: Boolean = false,
    val email: String = "",
    val username: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val queue: Queue<StateMessage> = Queue(mutableListOf()),
)
