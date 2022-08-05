package stephane.amstrong.kotlinmvixxlstore.business.interactors.blog

import stephane.amstrong.kotlinmvixxlstore.api.handleUseCaseException
import stephane.amstrong.kotlinmvixxlstore.business.datasource.cache.blog.BlogPostDao
import stephane.amstrong.kotlinmvixxlstore.business.datasource.network.main.OpenApiMainService
import stephane.amstrong.kotlinmvixxlstore.business.domain.models.AuthToken
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.DataState
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.ErrorHandling.Companion.ERROR_AUTH_TOKEN_INVALID
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.ErrorHandling.Companion.GENERIC_ERROR
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.MessageType
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.Response
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.SuccessHandling.Companion.SUCCESS_BLOG_UPDATED
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.UIComponentType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

class UpdateBlogPost(
    private val service: OpenApiMainService,
    private val cache: BlogPostDao,
) {

    /**
     * If successful this will emit a string saying: 'updated'
     */
    fun execute(
        authToken: AuthToken?,
        slug: String,
        title: RequestBody,
        body: RequestBody,
        image: MultipartBody.Part?,
    ): Flow<DataState<Response>> = flow{
        emit(DataState.loading<Response>())
        if(authToken == null){
            throw Exception(ERROR_AUTH_TOKEN_INVALID)
        }
        // attempt update
        val createUpdateResponse = service.updateBlog(
            "Token ${authToken.token}",
            slug,
            title,
            body,
            image
        )

        if(createUpdateResponse.response == GENERIC_ERROR){ // failure
            throw Exception(createUpdateResponse.errorMessage)
        }else{ // success
            cache.updateBlogPost(
                pk = createUpdateResponse.pk,
                title = createUpdateResponse.title,
                body = createUpdateResponse.body,
                image = createUpdateResponse.image
            )
            // Tell the UI it was successful
            emit(DataState.data<Response>(
                data = Response(
                    message = SUCCESS_BLOG_UPDATED,
                    uiComponentType = UIComponentType.None(),
                    messageType = MessageType.Success()
                ),
                response = null,
            ))
        }
    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}









