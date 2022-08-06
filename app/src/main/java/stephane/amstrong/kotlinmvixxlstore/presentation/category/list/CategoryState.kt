package stephane.amstrong.kotlinmvixxlstore.presentation.category.list

import stephane.amstrong.kotlinmvixxlstore.business.domain.models.Category
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.Queue
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.StateMessage

data class CategoryState(
    val isLoading: Boolean = false,
    val categories: List<Category> = listOf(),
    val searchTerm: String = "",
    val withTheName: String = "",
    val orderBy: String = "",
    val pageNumber: Int = 1,
    val isQueryExhausted: Boolean = false, // no more results available, prevent next page
    val filter: CategoryFilterOptions = CategoryFilterOptions.UPDATED_AT,
    val order: CategoryOrderOptions = CategoryOrderOptions.DESC,
    val queue: Queue<StateMessage> = Queue(mutableListOf()),
)