package stephane.amstrong.kotlinmvixxlstore.presentation.main.blog.list

import stephane.amstrong.kotlinmvixxlstore.business.domain.models.BlogPost
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.Queue
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.StateMessage
import stephane.amstrong.kotlinmvixxlstore.presentation.main.blog.list.BlogFilterOptions.*
import stephane.amstrong.kotlinmvixxlstore.presentation.main.blog.list.BlogOrderOptions.*

data class BlogState(
    val isLoading: Boolean = false,
    val blogList: List<BlogPost> = listOf(),
    val query: String = "",
    val page: Int = 1,
    val isQueryExhausted: Boolean = false, // no more results available, prevent next page
    val filter: BlogFilterOptions = DATE_UPDATED,
    val order: BlogOrderOptions = DESC,
    val queue: Queue<StateMessage> = Queue(mutableListOf()),
)
