package stephane.amstrong.kotlinmvixxlstore.business.datasource.cache.category

class CategoryQueryUtils {
    companion object{
        private val TAG: String = "AppDebug"

        // values
        const val CATEGORY_ORDER_ASC: String = ""
        const val CATEGORY_ORDER_DESC: String = "-"
        const val CATEGORY_FILTER_USERNAME = "username"
        const val CATEGORY_FILTER_DATE_UPDATED = "date_updated"

        const val ORDER_BY_ASC_DATE_UPDATED = CATEGORY_ORDER_ASC + CATEGORY_FILTER_DATE_UPDATED
        const val ORDER_BY_DESC_DATE_UPDATED = CATEGORY_ORDER_DESC + CATEGORY_FILTER_DATE_UPDATED
        const val ORDER_BY_ASC_USERNAME = CATEGORY_ORDER_ASC + CATEGORY_FILTER_USERNAME
        const val ORDER_BY_DESC_USERNAME = CATEGORY_ORDER_DESC + CATEGORY_FILTER_USERNAME
    }
}