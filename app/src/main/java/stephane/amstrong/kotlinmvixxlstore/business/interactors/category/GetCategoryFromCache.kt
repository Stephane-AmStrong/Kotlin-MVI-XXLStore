package stephane.amstrong.kotlinmvixxlstore.business.interactors.category

import stephane.amstrong.kotlinmvixxlstore.business.datasource.cache.category.CategoryDao
import stephane.amstrong.kotlinmvixxlstore.business.datasource.cache.category.toCategory
import stephane.amstrong.kotlinmvixxlstore.business.datasource.network.handleUseCaseException
import stephane.amstrong.kotlinmvixxlstore.business.domain.models.Category
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.DataState
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.ErrorHandling.Companion.ERROR_BLOG_UNABLE_TO_RETRIEVE
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.ErrorHandling.Companion.ERROR_CATEGORY_UNABLE_TO_RETRIEVE
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.MessageType
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.Response
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.UIComponentType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class GetCategoryFromCache(
    private val cache: CategoryDao,
) {

    fun execute(
        id: String,
    ): Flow<DataState<Category>> = flow {
        emit(DataState.loading<Category>())
        val category = cache.getCategory(id)?.toCategory()

        if (category != null) {
            emit(DataState.data(response = null, data = category))
        } else {
            emit(
                DataState.error<Category>(
                    response = Response(
                        message = ERROR_CATEGORY_UNABLE_TO_RETRIEVE,
                        uiComponentType = UIComponentType.Dialog(),
                        messageType = MessageType.Error()
                    )
                )
            )
        }
    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}