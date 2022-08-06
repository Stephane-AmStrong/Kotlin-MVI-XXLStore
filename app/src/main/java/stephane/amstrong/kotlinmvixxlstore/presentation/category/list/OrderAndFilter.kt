package stephane.amstrong.kotlinmvixxlstore.presentation.category.list

import stephane.amstrong.kotlinmvixxlstore.presentation.category.list.CategoryFilterOptions
import stephane.amstrong.kotlinmvixxlstore.presentation.category.list.CategoryOrderOptions

data class OrderAndFilter(
    val order: CategoryOrderOptions,
    val filter: CategoryFilterOptions,
)