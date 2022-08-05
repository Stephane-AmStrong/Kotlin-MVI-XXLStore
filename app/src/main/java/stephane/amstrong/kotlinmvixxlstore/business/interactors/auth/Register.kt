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
import stephane.amstrong.kotlinmvixxlstore.business.datasource.datastore.AppDataStoreManager
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.*
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.ErrorHandling.Companion.ERROR_PASSWORDS_MUST_MATCH
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.ErrorHandling.Companion.ERROR_SAVE_AUTH_TOKEN
import stephane.amstrong.kotlinmvixxlstore.presentation.util.DataStoreKeys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import java.lang.Exception

class Register(
    private val service: OpenApiAuthService,
    private val accountDao: AccountDao,
    private val authTokenDao: AuthTokenDao,
    private val appDataStoreManager: AppDataStore,
){
    fun execute(
        email: String,
        username: String,
        password: String,
        confirmPassword: String,
    ): Flow<DataState<AuthToken>> = flow {
        emit(DataState.loading<AuthToken>())
        val registerResponse = service.register(
            email = email,
            username = username,
            password = password,
            password2 = confirmPassword,
        )
        // Incorrect login credentials counts as a 200 response from server, so need to handle that
        if(registerResponse.response.equals(ErrorHandling.GENERIC_AUTH_ERROR)){
            throw Exception(registerResponse.errorMessage)
        }

        // cache account information
        accountDao.insertAndReplace(
            Account(
                registerResponse.pk,
                registerResponse.email,
                registerResponse.username
            ).toEntity()
        )

        // cache the auth token
        val authToken = AuthToken(
            registerResponse.pk,
            registerResponse.token
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














