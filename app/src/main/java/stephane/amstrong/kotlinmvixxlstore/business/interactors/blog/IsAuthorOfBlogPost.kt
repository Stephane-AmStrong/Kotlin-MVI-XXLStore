package stephane.amstrong.kotlinmvixxlstore.business.interactors.blog

import stephane.amstrong.kotlinmvixxlstore.business.datasource.network.main.OpenApiMainService
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.DataState
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.ErrorHandling.Companion.ERROR_AUTH_TOKEN_INVALID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import stephane.amstrong.kotlinmvixxlstore.business.datasource.network.handleUseCaseException
import stephane.amstrong.kotlinmvixxlstore.business.domain.models.Authentication
import java.lang.Exception

class IsAuthorOfBlogPost(
    private val service: OpenApiMainService,
) {

    fun execute(
        authentication: Authentication?,
        slug: String,
    ): Flow<DataState<Boolean>> = flow {
        emit(DataState.loading<Boolean>())
        if(authentication == null){
            throw Exception(ERROR_AUTH_TOKEN_INVALID)
        }
        try{ // catch network exception
            val response = service.isAuthorOfBlogPost(
                "Token ${authentication.accessToken.value}",
                slug
            )
            emit(DataState.data(response = null, false))

        }catch (e: Exception){
            emit(DataState.data(response = null, false))
        }
    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}



























