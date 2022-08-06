package stephane.amstrong.kotlinmvixxlstore.business.interactors.blog

import stephane.amstrong.kotlinmvixxlstore.business.datasource.network.main.OpenApiMainService
import stephane.amstrong.kotlinmvixxlstore.business.domain.models.BlogPost
import stephane.amstrong.kotlinmvixxlstore.business.datasource.cache.blog.BlogPostDao
import stephane.amstrong.kotlinmvixxlstore.business.datasource.cache.blog.toEntity
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.*
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.ErrorHandling.Companion.ERROR_AUTH_TOKEN_INVALID
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.SuccessHandling.Companion.SUCCESS_DELETED
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import stephane.amstrong.kotlinmvixxlstore.business.datasource.network.handleUseCaseException
import stephane.amstrong.kotlinmvixxlstore.business.domain.models.Authentication
import java.lang.Exception

class DeleteBlogPost(
    private val service: OpenApiMainService,
    private val cache: BlogPostDao,
) {
    /**
     * If successful this will emit a string saying: 'deleted'
     */
    fun execute(
        authentication: Authentication?,
        blogPost: BlogPost,
    ): Flow<DataState<Response>> = flow{
        emit(DataState.loading<Response>())
        if(authentication == null){
            throw Exception(ERROR_AUTH_TOKEN_INVALID)
        }
        // attempt delete from network
        val response = service.deleteBlogPost(
            "Token ${authentication.accessToken.value}",
            blogPost.slug
        )

        // delete from cache
        cache.deleteBlogPost(blogPost.toEntity())
        // Tell the UI it was successful
        emit(DataState.data<Response>(
            data = Response(
                message = SUCCESS_DELETED,
                uiComponentType = UIComponentType.None(),
                messageType = MessageType.Success()
            ),
            response = null
        ))
    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}
















