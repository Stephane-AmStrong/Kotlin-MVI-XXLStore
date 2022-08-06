package stephane.amstrong.kotlinmvixxlstore.business.interactors.blog

import stephane.amstrong.kotlinmvixxlstore.business.datasource.network.handleUseCaseException
import stephane.amstrong.kotlinmvixxlstore.business.datasource.network.main.OpenApiMainService
import stephane.amstrong.kotlinmvixxlstore.business.datasource.network.main.toBlogPost
import stephane.amstrong.kotlinmvixxlstore.business.domain.models.BlogPost
import stephane.amstrong.kotlinmvixxlstore.business.datasource.cache.blog.*
import stephane.amstrong.kotlinmvixxlstore.presentation.main.blog.list.BlogFilterOptions
import stephane.amstrong.kotlinmvixxlstore.presentation.main.blog.list.BlogOrderOptions
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.DataState
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.ErrorHandling.Companion.ERROR_AUTH_TOKEN_INVALID
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.MessageType
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.Response
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.UIComponentType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import stephane.amstrong.kotlinmvixxlstore.business.domain.models.Authentication

class SearchBlogs(
    private val service: OpenApiMainService,
    private val cache: BlogPostDao,
) {

    private val TAG: String = "AppDebug"

    fun execute(
        authentication: Authentication?,
        query: String,
        page: Int,
        filter: BlogFilterOptions,
        order: BlogOrderOptions,
    ): Flow<DataState<List<BlogPost>>> = flow {
        emit(DataState.loading<List<BlogPost>>())
        if(authentication == null){
            throw Exception(ERROR_AUTH_TOKEN_INVALID)
        }
        // get Blogs from network
        val filterAndOrder = order.value + filter.value // Ex: -date_updated

        try{ // catch network exception
            val blogs = service.searchListBlogPosts(
                "Token ${authentication.accessToken.value}",
                query = query,
                ordering = filterAndOrder,
                page = page
            ).results.map { it.toBlogPost() }

            // Insert into cache
            for(blog in blogs){
                try{
                    cache.insert(blog.toEntity())
                }catch (e: Exception){
                    e.printStackTrace()
                }
            }
        }catch (e: Exception){
            emit(
                DataState.error<List<BlogPost>>(
                    response = Response(
                        message = "Unable to update the cache.",
                        uiComponentType = UIComponentType.None(),
                        messageType = MessageType.Error()
                    )
                )
            )
        }

        // emit from cache
        val cachedBlogs = cache.returnOrderedBlogQuery(
            query = query,
            filterAndOrder = filterAndOrder,
            page = page
        ).map { it.toBlogPost() }

        emit(DataState.data(response = null, data = cachedBlogs))
    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}



















