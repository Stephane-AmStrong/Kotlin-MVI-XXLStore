package stephane.amstrong.kotlinmvixxlstore.business.interactors.category

import stephane.amstrong.kotlinmvixxlstore.business.datasource.cache.category.CategoryDao
import stephane.amstrong.kotlinmvixxlstore.business.datasource.network.category.CategoryApi
import stephane.amstrong.kotlinmvixxlstore.business.datasource.network.category.toEntity
import stephane.amstrong.kotlinmvixxlstore.business.domain.models.Authentication
import stephane.amstrong.kotlinmvixxlstore.business.domain.models.Category
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.*
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.ErrorHandling.Companion.ERROR_AUTH_TOKEN_INVALID
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.SuccessHandling.Companion.SUCCESS_DELETED
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import stephane.amstrong.kotlinmvixxlstore.business.datasource.network.handleUseCaseException

class DeleteCategory(
    private val categoryApi: CategoryApi,
    private val cache: CategoryDao,
) {
    /**
     * If successful this will emit a string saying: 'deleted'
     */
    fun execute(
        authentication: Authentication?,
        category: Category,
    ): Flow<DataState<Response>> = flow{
        emit(DataState.loading<Response>())
        if(authentication == null){
            throw Exception(ERROR_AUTH_TOKEN_INVALID)
        }
        // attempt delete from network
        val response = categoryApi.delete(
            "Bearer ${authentication.accessToken}",
            category.id
        )

        // delete from cache
        cache.deleteCategory(category.toEntity())
        // Tell the UI it was successful
        emit(DataState.data<Response>(
            data = Response(
                message = SUCCESS_DELETED,
                uiComponentType = UIComponentType.None(),
                messageType = MessageType.Success()
            ),
            response = null
        ))

        /*
        if(response.response == GENERIC_ERROR
            && response.errorMessage != ERROR_DELETE_BLOG_DOES_NOT_EXIST){ // failure
            throw Exception(response.errorMessage)
        }else{
            // delete from cache
            cache.deleteCategory(category.toEntity())
            // Tell the UI it was successful
            emit(DataState.data<Response>(
                data = Response(
                    message = SUCCESS_BLOG_DELETED,
                    uiComponentType = UIComponentType.None(),
                    messageType = MessageType.Success()
                ),
                response = null
            ))
        }
        */
    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}