package stephane.amstrong.kotlinmvixxlstore.business.interactors.blog

import stephane.amstrong.kotlinmvixxlstore.business.datasource.datastore.AppDataStore
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.DataState
import stephane.amstrong.kotlinmvixxlstore.presentation.main.blog.list.*
import stephane.amstrong.kotlinmvixxlstore.presentation.util.DataStoreKeys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import stephane.amstrong.kotlinmvixxlstore.business.datasource.network.handleUseCaseException

class GetOrderAndFilter(
    private val appDataStoreManager: AppDataStore
) {
    fun execute(): Flow<DataState<OrderAndFilter>> = flow {
        emit(DataState.loading<OrderAndFilter>())
        val filter = appDataStoreManager.readValue(DataStoreKeys.BLOG_FILTER)?.let { filter ->
            getFilterFromValue(filter)
        }?: getFilterFromValue(BlogFilterOptions.DATE_UPDATED.value)
        val order = appDataStoreManager.readValue(DataStoreKeys.BLOG_ORDER)?.let { order ->
            getOrderFromValue(order)
        }?: getOrderFromValue(BlogOrderOptions.DESC.value)
        emit(DataState.data(
            response = null,
            data = OrderAndFilter(order = order, filter = filter)
        ))
    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}










