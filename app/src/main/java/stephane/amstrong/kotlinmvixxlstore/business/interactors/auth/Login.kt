package stephane.amstrong.kotlinmvixxlstore.business.interactors.auth

import stephane.amstrong.kotlinmvixxlstore.business.datasource.network.auth.OpenApiAuthService
import stephane.amstrong.kotlinmvixxlstore.api.handleUseCaseException
import stephane.amstrong.kotlinmvixxlstore.business.domain.models.Account
import stephane.amstrong.kotlinmvixxlstore.business.domain.models.AuthToken
import stephane.amstrong.kotlinmvixxlstore.business.datasource.cache.account.AccountDao
import stephane.amstrong.kotlinmvixxlstore.business.datasource.cache.account.toEntity
import stephane.amstrong.kotlinmvixxlstore.business.datasource.cache.auth.AuthTokenDao
import stephane.amstrong.kotlinmvixxlstore.business.datasource.cache.auth.toEntity
import stephane.amstrong.kotlinmvixxlstore.business.datasource.datastore.AppDataStore
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.*
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.ErrorHandling.Companion.ERROR_SAVE_AUTH_TOKEN
import stephane.amstrong.kotlinmvixxlstore.presentation.util.DataStoreKeys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import java.lang.Exception

class Login(
    private val service: OpenApiAuthService,
    private val accountDao: AccountDao,
    private val authTokenDao: AuthTokenDao,
    private val appDataStoreManager: AppDataStore,
){
    fun execute(
        email: String,
        password: String,
    ): Flow<DataState<AuthToken>> = flow {
        emit(DataState.loading<AuthToken>())
        val loginResponse = service.login(email, password)
        // Incorrect login credentials counts as a 200 response from server, so need to handle that
        if(loginResponse.errorMessage == ErrorHandling.INVALID_CREDENTIALS){
            throw Exception(ErrorHandling.INVALID_CREDENTIALS)
        }

        // cache the Account information (don't know the username yet)
        accountDao.insertOrIgnore(
            Account(
                pk = loginResponse.pk,
                email = loginResponse.email,
                username = ""
            ).toEntity()
        )

        // cache the auth token
        val authToken = AuthToken(
            loginResponse.pk,
            loginResponse.token
        )
        val result = authTokenDao.insert(authToken.toEntity())
        // can't proceed unless token can be cached
        if(result < 0){
            throw Exception(ERROR_SAVE_AUTH_TOKEN)
        }
        // save authenticated user to datastore for auto-login next time
        appDataStoreManager.setValue(DataStoreKeys.PREVIOUS_AUTH_USER, email)
        emit(DataState.data(data = authToken, response = null))
    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}














