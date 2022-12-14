package stephane.amstrong.kotlinmvixxlstore.presentation.session

import stephane.amstrong.kotlinmvixxlstore.business.domain.models.Authentication
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.Queue
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.StateMessage

data class SessionState(
    val isLoading: Boolean = false,
    val authentication: Authentication? = null,
    val didCheckForPreviousAuthUser: Boolean = false,
    val queue: Queue<StateMessage> = Queue(mutableListOf()),
)