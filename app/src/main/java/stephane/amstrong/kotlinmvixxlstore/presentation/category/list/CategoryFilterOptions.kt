package stephane.amstrong.kotlinmvixxlstore.presentation.category.list


enum class CategoryFilterOptions(val value: String) {
    NAME("name"),
    UPDATED_AT("updatedAt"),
}

fun getFilterFromValue(value: String?): CategoryFilterOptions {
    return when(value){
        CategoryFilterOptions.NAME.value -> CategoryFilterOptions.NAME
        CategoryFilterOptions.UPDATED_AT.value -> CategoryFilterOptions.UPDATED_AT
        else -> CategoryFilterOptions.UPDATED_AT
    }
}