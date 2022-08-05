package stephane.amstrong.kotlinmvixxlstore.business.interactors.account

import stephane.amstrong.kotlinmvixxlstore.api.handleUseCaseException
import stephane.amstrong.kotlinmvixxlstore.business.datasource.network.main.OpenApiMainService
import stephane.amstrong.kotlinmvixxlstore.business.datasource.network.main.toAccount
import stephane.amstrong.kotlinmvixxlstore.business.domain.models.Account
import stephane.amstrong.kotlinmvixxlstore.business.domain.models.AuthToken
import stephane.amstrong.kotlinmvixxlstore.business.datasource.cache.account.AccountDao
import stephane.amstrong.kotlinmvixxlstore.business.datasource.cache.account.toAccount
import stephane.amstrong.kotlinmvixxlstore.business.datasource.cache.account.toEntity
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.DataState
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.ErrorHandling.Companion.ERROR_AUTH_TOKEN_INVALID
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.ErrorHandling.Companion.ERROR_UNABLE_TO_RETRIEVE_ACCOUNT_DETAILS
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import java.lang.Exception

class GetAccount(
    private val service: OpenApiMainService,
    private val cache: AccountDao,
) {
    private val TAG: String = "AppDebug"

    fun execute(
        authToken: AuthToken?,
    ): Flow<DataState<Account>> = flow {
        emit(DataState.loading<Account>())
        if(authToken == null){
            throw Exception(ERROR_AUTH_TOKEN_INVALID)
        }
        // get from network
        val account = service.getAccount("Token ${authToken.token}").toAccount()

        // update/insert into the cache
        cache.insertAndReplace(account.toEntity())

        // emit from cache
        val cachedAccount = cache.searchByPk(account.pk)?.toAccount()

        if(cachedAccount == null){
            throw Exception(ERROR_UNABLE_TO_RETRIEVE_ACCOUNT_DETAILS)
        }

        emit(DataState.data(response = null, cachedAccount))
    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}















