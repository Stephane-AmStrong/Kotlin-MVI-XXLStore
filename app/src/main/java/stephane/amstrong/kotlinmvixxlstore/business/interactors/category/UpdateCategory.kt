package stephane.amstrong.kotlinmvixxlstore.business.interactors.category

import stephane.amstrong.kotlinmvixxlstore.business.datasource.cache.category.CategoryDao
import stephane.amstrong.kotlinmvixxlstore.business.datasource.network.category.CategoryApi
import stephane.amstrong.kotlinmvixxlstore.business.datasource.network.category.CategoryDto
import stephane.amstrong.kotlinmvixxlstore.business.datasource.network.handleUseCaseException
import stephane.amstrong.kotlinmvixxlstore.business.domain.models.Authentication
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.DataState
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.ErrorHandling.Companion.ERROR_AUTH_TOKEN_INVALID
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.ErrorHandling.Companion.GENERIC_ERROR
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.MessageType
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.Response
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.SuccessHandling.Companion.SUCCESS_UPDATED
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.UIComponentType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

class UpdateCategory(
    private val categoryApi: CategoryApi,
    private val cache: CategoryDao,
) {

    /**
     * If successful this will emit a string saying: 'updated'
     */
    fun execute(
        authentication: Authentication?,
        id: String,
        category: CategoryDto.CategoryRequest
    ): Flow<DataState<Response>> = flow{
        emit(DataState.loading<Response>())
        if(authentication == null){
            throw Exception(ERROR_AUTH_TOKEN_INVALID)
        }
        // attempt update
        val createUpdateResponse = categoryApi.update(
            "Bearer ${authentication.accessToken}",
            id,
            category
        )

        cache.updateCategory(
            id = createUpdateResponse.id,
            name = createUpdateResponse.name,
            description = createUpdateResponse.description,
        )

        // Tell the UI it was successful
        emit(
            DataState.data<Response>(
                data = Response(
                    message = SUCCESS_UPDATED,
                    uiComponentType = UIComponentType.None(),
                    messageType = MessageType.Success()
                ),
                response = null,
            ))
    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}