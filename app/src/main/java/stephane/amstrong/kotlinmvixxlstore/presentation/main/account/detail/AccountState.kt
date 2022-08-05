package stephane.amstrong.kotlinmvixxlstore.presentation.main.account.detail

import stephane.amstrong.kotlinmvixxlstore.business.domain.models.Account
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.Queue
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.StateMessage

data class AccountState(
    val isLoading: Boolean = false,
    val account: Account? = null,
    val queue: Queue<StateMessage> = Queue(mutableListOf()),
)
