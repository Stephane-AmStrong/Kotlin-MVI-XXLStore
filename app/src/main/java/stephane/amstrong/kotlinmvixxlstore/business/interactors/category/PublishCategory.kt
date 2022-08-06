package stephane.amstrong.kotlinmvixxlstore.business.interactors.category

import stephane.amstrong.kotlinmvixxlstore.business.datasource.cache.category.CategoryDao
import stephane.amstrong.kotlinmvixxlstore.business.datasource.network.category.CategoryApi
import stephane.amstrong.kotlinmvixxlstore.business.datasource.network.category.CategoryDto
import stephane.amstrong.kotlinmvixxlstore.business.datasource.network.category.toCategory
import stephane.amstrong.kotlinmvixxlstore.business.datasource.network.category.toEntity
import stephane.amstrong.kotlinmvixxlstore.business.datasource.network.handleUseCaseException
import stephane.amstrong.kotlinmvixxlstore.business.domain.models.Authentication
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.*
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.ErrorHandling.Companion.ERROR_AUTH_TOKEN_INVALID
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.SuccessHandling.Companion.SUCCESS_CREATED
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

class PublishCategory(
    private val categoryApi: CategoryApi,
    private val cache: CategoryDao,
){
    private val TAG: String = "AppDebug"

    fun execute(
        authentication: Authentication?,
        category: CategoryDto.CategoryRequest
    ): Flow<DataState<Response>> = flow {
        emit(DataState.loading<Response>())
        if(authentication == null) throw Exception(ERROR_AUTH_TOKEN_INVALID)

        // attempt update
        val createResponse = categoryApi.create(
            "Bearer ${authentication.accessToken}",
            category
        )

        //if(createResponse.response == ErrorHandling.GENERIC_ERROR) throw Exception(createResponse.errorMessage)

        // insert the new category into the cache
        cache.insert(createResponse.toCategory().toEntity())

        // Tell the UI it was successful
        emit(DataState.data<Response>(
            data = Response(
                message = SUCCESS_CREATED,
                uiComponentType = UIComponentType.None(),
                messageType = MessageType.Success()
            ),
            response = null,
        ))
    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}