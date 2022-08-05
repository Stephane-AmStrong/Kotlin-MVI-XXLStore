package stephane.amstrong.kotlinmvixxlstore.presentation.main.account.update

import stephane.amstrong.kotlinmvixxlstore.business.domain.models.Account
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.Queue
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.StateMessage

data class UpdateAccountState(
    val isLoading: Boolean = false,
    val isUpdateComplete: Boolean = false,
    val account: Account? = null,
    val queue: Queue<StateMessage> = Queue(mutableListOf()),
)
