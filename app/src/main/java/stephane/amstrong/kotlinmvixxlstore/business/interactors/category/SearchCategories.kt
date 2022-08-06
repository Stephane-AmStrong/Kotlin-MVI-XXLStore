package stephane.amstrong.kotlinmvixxlstore.business.interactors.category

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import stephane.amstrong.kotlinmvixxlstore.business.datasource.cache.category.CategoryDao
import stephane.amstrong.kotlinmvixxlstore.business.datasource.cache.category.returnOrderedCategoryQuery
import stephane.amstrong.kotlinmvixxlstore.business.datasource.cache.category.toCategory
import stephane.amstrong.kotlinmvixxlstore.business.datasource.network.category.CategoryApi
import stephane.amstrong.kotlinmvixxlstore.business.datasource.network.category.toCategory
import stephane.amstrong.kotlinmvixxlstore.business.datasource.network.category.toEntity
import stephane.amstrong.kotlinmvixxlstore.business.datasource.network.handleUseCaseException
import stephane.amstrong.kotlinmvixxlstore.business.domain.models.Authentication
import stephane.amstrong.kotlinmvixxlstore.business.domain.models.Category
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.*
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.ErrorHandling.Companion.ERROR_AUTH_TOKEN_INVALID
import stephane.amstrong.kotlinmvixxlstore.presentation.category.list.CategoryFilterOptions
import stephane.amstrong.kotlinmvixxlstore.presentation.category.list.CategoryOrderOptions

class SearchCategories(
    private val categoryApi: CategoryApi,
    private val categoryDao: CategoryDao
) {

    private val TAG: String = "AppDebug"

    fun execute(
        authentication: Authentication?,
        searchTerm: String?,
        withTheName: String?,
        orderBy: String?,
        pageNumber: Int?,
        //filter: CategoryFilterOptions,
        //order: CategoryOrderOptions,
    ): Flow<DataState<List<Category>>> = flow {
        emit(DataState.loading<List<Category>>())
        if (authentication == null) throw Exception(ERROR_AUTH_TOKEN_INVALID)

        // get Categories from network
        //val filterAndOrder = order.value + filter.value // Ex: -date_updated

        try { // catch network exception
            Log.d(TAG, "trying to map fetched categories")

            val categories = categoryApi.search(
                "Bearer ${authentication.accessToken.value}",
                searchTerm = searchTerm,
                withTheName = withTheName,
                orderBy = orderBy,
                pageNumber = pageNumber,
                pageSize = Constants.PAGINATION_PAGE_SIZE,
            ).map { it.toCategory() }

            // Insert into categoryDao
            for (category in categories) {
                try {
                    categoryDao.insert(category.toEntity())
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error: ", e )

            emit(
                DataState.error<List<Category>>(
                    response = Response(
                        message = "Unable to update the categoryDao.",
                        uiComponentType = UIComponentType.None(),
                        messageType = MessageType.Error()
                    )
                )
            )
        }

        // emit from categoryDao
        val cachedCategories = categoryDao.returnOrderedCategoryQuery(
            searchTerm = searchTerm,
            withTheName = withTheName,
            orderBy = orderBy,
            pageNumber = pageNumber,
            pageSize = Constants.PAGINATION_PAGE_SIZE,
        ).map { it.toCategory() }

        emit(DataState.data(response = null, data = cachedCategories))
    }.catch { e ->
        Log.e(TAG, "mapping2 Error: ", e )
        emit(handleUseCaseException(e))
    }
}



















