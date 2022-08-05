package stephane.amstrong.kotlinmvixxlstore.presentation.main.blog.update

import android.net.Uri
import stephane.amstrong.kotlinmvixxlstore.business.domain.models.BlogPost
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.Queue
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.StateMessage

data class UpdateBlogState(
    val isLoading: Boolean = false,
    val isUpdateComplete: Boolean = false,
    val blogPost: BlogPost? = null,
    val newImageUri: Uri? = null, // Only set if the user has selected a new image
    val queue: Queue<StateMessage> = Queue(mutableListOf()),
)
