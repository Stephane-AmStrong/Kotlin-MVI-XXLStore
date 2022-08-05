package stephane.amstrong.kotlinmvixxlstore.presentation.main.account.password

import stephane.amstrong.kotlinmvixxlstore.business.domain.util.Queue
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.StateMessage

data class AccountPasswordState(
    val isLoading: Boolean = false,
    val isPasswordChangeComplete: Boolean = false,
    val currentPassword: String = "",
    val newPassword: String = "",
    val confirmNewPassword: String = "",
    val queue: Queue<StateMessage> = Queue(mutableListOf()),
)
