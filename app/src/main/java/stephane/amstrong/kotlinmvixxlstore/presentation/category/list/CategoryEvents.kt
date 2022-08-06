package stephane.amstrong.kotlinmvixxlstore.presentation.category.list

import stephane.amstrong.kotlinmvixxlstore.business.domain.util.StateMessage


sealed class CategoryEvents {

    object NewSearch : CategoryEvents()

    object NextPage: CategoryEvents()

    data class UpdateSearchTerm(val searchTerm: String): CategoryEvents()

    data class UpdateFilter(val filter: CategoryFilterOptions): CategoryEvents()

    data class UpdateOrder(val order: CategoryOrderOptions): CategoryEvents()

    object GetOrderAndFilter: CategoryEvents()

    data class Error(val stateMessage: StateMessage): CategoryEvents()

    object OnRemoveHeadFromQueue: CategoryEvents()

}