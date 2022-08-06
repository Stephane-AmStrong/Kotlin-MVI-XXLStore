package stephane.amstrong.kotlinmvixxlstore.presentation.category.list

enum class CategoryOrderOptions(val value: String)  {
    ASC(""),
    DESC(" desc")
}

fun getOrderFromValue(value: String?): CategoryOrderOptions {
    return when(value){
        CategoryOrderOptions.ASC.value -> CategoryOrderOptions.ASC
        CategoryOrderOptions.DESC.value -> CategoryOrderOptions.DESC
        else -> CategoryOrderOptions.DESC
    }
}