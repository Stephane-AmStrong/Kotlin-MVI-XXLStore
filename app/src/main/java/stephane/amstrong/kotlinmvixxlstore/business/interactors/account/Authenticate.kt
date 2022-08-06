package stephane.amstrong.kotlinmvixxlstore.business.interactors.account

import stephane.amstrong.kotlinmvixxlstore.business.datasource.cache.account.AuthenticationDao
import stephane.amstrong.kotlinmvixxlstore.business.datasource.datastore.AppDataStore
import stephane.amstrong.kotlinmvixxlstore.business.datasource.network.handleUseCaseException
import stephane.amstrong.kotlinmvixxlstore.business.domain.models.Authentication
import stephane.amstrong.kotlinmvixxlstore.business.domain.models.RefreshToken
import stephane.amstrong.kotlinmvixxlstore.business.domain.models.UserInfo
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.DataState
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.ErrorHandling
import stephane.amstrong.kotlinmvixxlstore.presentation.util.DataStoreKeys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import stephane.amstrong.kotlinmvixxlstore.business.datasource.cache.account.toEntity
import stephane.amstrong.kotlinmvixxlstore.business.datasource.network.account.AccountApi
import stephane.amstrong.kotlinmvixxlstore.business.datasource.network.account.AuthenticationDto

class Authenticate(
    private val accountApi: AccountApi,
    private val authenticationDao: AuthenticationDao,
    private val appDataStoreManager: AppDataStore,
){
    private val TAG = "Authenticate"

    fun execute(
        email: String,
        password: String,
    ): Flow<DataState<Authentication>> = flow {
        emit(DataState.loading<Authentication>())
        val authenticationResponse = accountApi.authenticate(AuthenticationDto.AuthenticationRequest(email, password))
/*
        Log.d(TAG, "Authenticate execute: " +
                "\nexpireDate =${authenticationResponse.expireDate} " +
                "\nuserInfo =${authenticationResponse.userInfo} " +
                "\nrefreshToken =${authenticationResponse.refreshToken} ")
*/


        val userInfo = UserInfo(
            name = authenticationResponse.userInfo.name,
            email = authenticationResponse.userInfo.email,
        )

        val refreshToken = RefreshToken(
            id = authenticationResponse.refreshToken.id,
            value = authenticationResponse.refreshToken.value,
            expiryDate = authenticationResponse.refreshToken.expiryDate
        )

        val authentication = Authentication(
            accessToken = authenticationResponse.accessToken,
            refreshToken = authenticationResponse.refreshToken,
            userInfo = authenticationResponse.userInfo,
        )

        // cache the Authentication information (don't know the username yet)
        val result = authenticationDao.insertAndReplace(
            authentication.toEntity()
        )

        // can't proceed unless token can be cached
        if(result < 0) throw Exception(ErrorHandling.ERROR_SAVE_AUTH_TOKEN)

        // save authenticated user to datastore for auto-login next time
        appDataStoreManager.setValue(DataStoreKeys.PREVIOUS_AUTH_USER, email)
        emit(DataState.data(data = authentication, response = null))

    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}