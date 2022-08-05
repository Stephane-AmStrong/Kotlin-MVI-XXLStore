package stephane.amstrong.kotlinmvixxlstore.presentation.main.blog.detail

import stephane.amstrong.kotlinmvixxlstore.business.domain.models.BlogPost
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.Queue
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.StateMessage

data class ViewBlogState(
    val isLoading: Boolean = false,
    val isDeleteComplete: Boolean = false,
    val blogPost: BlogPost? = null,
    val isAuthor: Boolean = false,
    val queue: Queue<StateMessage> = Queue(mutableListOf()),
)
