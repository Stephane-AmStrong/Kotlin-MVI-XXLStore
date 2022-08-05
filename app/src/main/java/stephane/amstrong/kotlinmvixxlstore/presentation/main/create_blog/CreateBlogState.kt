package stephane.amstrong.kotlinmvixxlstore.presentation.main.create_blog

import android.net.Uri
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.Queue
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.StateMessage

data class CreateBlogState(
    val isLoading: Boolean = false,
    val title: String = "",
    val body: String = "",
    val uri: Uri? = null,
    val onPublishSuccess: Boolean = false,
    val queue: Queue<StateMessage> = Queue(mutableListOf()),
)

