package stephane.amstrong.kotlinmvixxlstore.business.interactors.category

import stephane.amstrong.kotlinmvixxlstore.business.datasource.cache.category.CategoryDao
import stephane.amstrong.kotlinmvixxlstore.business.datasource.network.category.CategoryApi
import stephane.amstrong.kotlinmvixxlstore.business.datasource.network.handleUseCaseException
import stephane.amstrong.kotlinmvixxlstore.business.domain.models.Authentication
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.*
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.ErrorHandling.Companion.UNABLE_TO_RESOLVE_HOST
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.SuccessHandling.Companion.SUCCESS_DOES_NOT_EXIST_IN_CACHE
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.SuccessHandling.Companion.SUCCESS_EXISTS_ON_SERVER
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

/**
 * If a category exist in the cache but does not exist on server, we need to delete from cache.
 */
class ConfirmCategoryExistsOnServer(
    private val categoryApi: CategoryApi,
    private val cache: CategoryDao
) {

    fun execute(
        authentication: Authentication?,
        id: String,
    ): Flow<DataState<Response>> =  flow {
        emit(DataState.loading<Response>())
        val cachedCategory = cache.getCategory(id)
        if(cachedCategory == null){
            // It doesn't exist in cache. Finish.
            emit(DataState.data<Response>(
                //resources.getString(R.string.pattern_ca_value, value.toDecimalFormat())
                data = Response(
                    message = "Category $SUCCESS_DOES_NOT_EXIST_IN_CACHE",
                    uiComponentType = UIComponentType.None(),
                    messageType = MessageType.Success()
                ),
                response = null,
            ))
        }else{
            if(authentication == null) throw Exception(ErrorHandling.ERROR_AUTH_TOKEN_INVALID)

            // confirm it exists on server (throws 404 if does not exist)
            var isNetworkError = false
            val category = try {
                categoryApi.getById(
                    authorization = "Bearer ${authentication.accessToken}",
                    id = id,
                )
            }catch (e1: Exception){
                if(e1.message?.contains(UNABLE_TO_RESOLVE_HOST) == true){ // network error
                    isNetworkError = true
                }
                e1.printStackTrace()
                null
            }
            if(isNetworkError){
                emit(
                    DataState.error<Response>(
                        response = Response(
                            message = "Network Error.",
                            uiComponentType = UIComponentType.None(),
                            messageType = MessageType.Error()
                        )
                    )
                )
            }
            else{
                // if it exists on server but not in cache. Delete from cache and emit error.
                if(category == null){
                    cache.deleteCategory(id)
                    emit(DataState.error<Response>(
                        response = Response(
                            message = ErrorHandling.ERROR_CATEGORY_DOES_NOT_EXIST,
                            uiComponentType = UIComponentType.Dialog(),
                            messageType = MessageType.Error()
                        )
                    ))
                }else{ // if it exists in the cache and on the server. Everything is fine.
                    emit(DataState.data<Response>(
                        data = Response(
                            message = "Category $SUCCESS_EXISTS_ON_SERVER",
                            uiComponentType = UIComponentType.None(),
                            messageType = MessageType.Success()
                        ),
                        response = null,
                    ))
                }
            }
        }
    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}