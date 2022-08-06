package stephane.amstrong.kotlinmvixxlstore.business.interactors.session


import stephane.amstrong.kotlinmvixxlstore.business.datasource.cache.account.AuthenticationDao
import stephane.amstrong.kotlinmvixxlstore.business.domain.models.Authentication
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.*
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.ErrorHandling.Companion.ERROR_NO_PREVIOUS_AUTH_USER
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import stephane.amstrong.kotlinmvixxlstore.business.datasource.cache.account.toAuthentication

/**
 * Attempt to authenticate as soon as the user launches the app.
 * If no user was authenticated in a previous session then do nothing.
 */
class CheckPreviousAuthUser(
    private val authenticationDao: AuthenticationDao,
) {
    fun execute(
        email: String,
    ): Flow<DataState<Authentication>> = flow {
        emit(DataState.loading<Authentication>())
        var authentication: Authentication? = null
        //val entity = authenticationDao.searchByEmail(email)
        if(authentication != null){
            authentication = authenticationDao.search()?.toAuthentication()
            if(authentication != null){
                emit(DataState.data(response = null, data = authentication))
            }
        }
        if(authentication == null){
            throw Exception(ERROR_NO_PREVIOUS_AUTH_USER)
        }
    }.catch{ e ->
        e.printStackTrace()
        emit(returnNoPreviousAuthUser())
    }

    /**
     * If no user was previously authenticated then emit this error. The UI is waiting for it.
     */
    private fun returnNoPreviousAuthUser(): DataState<Authentication> {
        return DataState.error<Authentication>(
            response = Response(
                SuccessHandling.RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE,
                UIComponentType.None(),
                MessageType.Error()
            )
        )
    }
}