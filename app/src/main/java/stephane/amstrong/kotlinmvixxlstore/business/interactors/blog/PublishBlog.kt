package stephane.amstrong.kotlinmvixxlstore.business.interactors.blog

import stephane.amstrong.kotlinmvixxlstore.business.datasource.network.handleUseCaseException
import stephane.amstrong.kotlinmvixxlstore.business.datasource.network.main.OpenApiMainService
import stephane.amstrong.kotlinmvixxlstore.business.datasource.network.main.responses.toBlogPost
import stephane.amstrong.kotlinmvixxlstore.business.datasource.cache.blog.BlogPostDao
import stephane.amstrong.kotlinmvixxlstore.business.datasource.cache.blog.toEntity
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.*
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.ErrorHandling.Companion.ERROR_AUTH_TOKEN_INVALID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import stephane.amstrong.kotlinmvixxlstore.business.domain.models.Authentication
import java.lang.Exception

class PublishBlog(
    private val service: OpenApiMainService,
    private val cache: BlogPostDao,
){
    private val TAG: String = "AppDebug"

    fun execute(
        authentication: Authentication?,
        title: RequestBody,
        body: RequestBody,
        image: MultipartBody.Part?,
    ): Flow<DataState<Response>> = flow {
        emit(DataState.loading<Response>())
        if(authentication == null){
            throw Exception(ERROR_AUTH_TOKEN_INVALID)
        }
        // attempt update
        val createResponse = service.createBlog(
            "Token ${authentication.accessToken.value}",
            title,
            body,
            image
        )

        // If they don't have a paid membership account it will still return a 200 with failure message
        // Need to account for that
        if (createResponse.response.equals(SuccessHandling.RESPONSE_MUST_BECOME_CODINGWITHMITCH_MEMBER)) { // failure
            throw Exception(SuccessHandling.RESPONSE_MUST_BECOME_CODINGWITHMITCH_MEMBER)
        }

        if(createResponse.response == ErrorHandling.GENERIC_ERROR){
            throw Exception(createResponse.errorMessage)
        }

        // insert the new blog into the cache
        cache.insert(createResponse.toBlogPost().toEntity())

        // Tell the UI it was successful
        emit(DataState.data<Response>(
            data = Response(
                message = SuccessHandling.SUCCESS_CREATED,
                uiComponentType = UIComponentType.None(),
                messageType = MessageType.Success()
            ),
            response = null,
        ))
    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}






















