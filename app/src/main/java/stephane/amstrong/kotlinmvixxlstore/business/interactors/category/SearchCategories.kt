package stephane.amstrong.kotlinmvixxlstore.business.interactors.category

import android.util.Log
import stephane.amstrong.kotlinmvixxlstore.business.datasource.cache.category.CategoryDao
import stephane.amstrong.kotlinmvixxlstore.business.datasource.cache.category.returnOrderedCategoryQuery
import stephane.amstrong.kotlinmvixxlstore.business.datasource.cache.category.toCategory
import stephane.amstrong.kotlinmvixxlstore.business.datasource.network.category.CategoryApi
import stephane.amstrong.kotlinmvixxlstore.business.datasource.network.category.CategoryDto
import stephane.amstrong.kotlinmvixxlstore.business.datasource.network.category.toCategory
import stephane.amstrong.kotlinmvixxlstore.business.datasource.network.category.toEntity
import stephane.amstrong.kotlinmvixxlstore.business.datasource.network.handleUseCaseException
import stephane.amstrong.kotlinmvixxlstore.business.domain.models.Authentication
import stephane.amstrong.kotlinmvixxlstore.business.domain.models.Category
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

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
        //pageSize: Int?,
    ): Flow<DataState<List<Category>>> = flow {
        emit(DataState.loading<List<Category>>())

        Log.d(TAG, "execute SearchCategories")

        if (authentication == null) throw Exception(ErrorHandling.ERROR_AUTH_TOKEN_INVALID)

        try {
            val categories = categoryApi.search(
                "Bearer ${authentication.accessToken}",
                searchTerm = searchTerm,
                withTheName = withTheName,
                orderBy = orderBy,
                pageNumber = pageNumber,
                pageSize = Constants.PAGINATION_PAGE_SIZE,
            ).map { it.toCategory() }

            Log.d(TAG, "execute SearchCategories: accessToken : ${authentication.accessToken}")

            // Insert into cache
            for (category in categories) {
                Log.d(TAG, "execute: Insert into cache : $category")
                try {
                    categoryDao.insert(category.toEntity())
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            emit(
                DataState.error<List<Category>>(
                    response = Response(
                        message = "Unable to update the cache.",
                        uiComponentType = UIComponentType.None(),
                        messageType = MessageType.Error()
                    )
                )
            )
        }

        // emit from cache
        val cachedCategories = categoryDao.returnOrderedCategoryQuery(
            searchTerm = searchTerm,
            withTheName = withTheName,
            orderBy = orderBy,
            pageNumber = pageNumber,
            pageSize = Constants.PAGINATION_PAGE_SIZE,
        ).map { it.toCategory() }

        emit(DataState.data(response = null, data = cachedCategories))
    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}