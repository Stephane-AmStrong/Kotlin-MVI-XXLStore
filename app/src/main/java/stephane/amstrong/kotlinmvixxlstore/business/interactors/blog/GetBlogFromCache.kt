package stephane.amstrong.kotlinmvixxlstore.business.interactors.blog

import stephane.amstrong.kotlinmvixxlstore.api.handleUseCaseException
import stephane.amstrong.kotlinmvixxlstore.business.domain.models.BlogPost
import stephane.amstrong.kotlinmvixxlstore.business.datasource.cache.blog.BlogPostDao
import stephane.amstrong.kotlinmvixxlstore.business.datasource.cache.blog.toBlogPost
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.DataState
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.ErrorHandling.Companion.ERROR_BLOG_UNABLE_TO_RETRIEVE
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.MessageType
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.Response
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.UIComponentType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class GetBlogFromCache(
    private val cache: BlogPostDao,
) {

    fun execute(
        pk: Int,
    ): Flow<DataState<BlogPost>> = flow{
        emit(DataState.loading<BlogPost>())
        val blogPost = cache.getBlogPost(pk)?.toBlogPost()

        if(blogPost != null){
            emit(DataState.data(response = null, data = blogPost))
        }
        else{
            emit(DataState.error<BlogPost>(
                response = Response(
                    message = ERROR_BLOG_UNABLE_TO_RETRIEVE,
                    uiComponentType = UIComponentType.Dialog(),
                    messageType = MessageType.Error()
                )
            ))
        }
    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}



















