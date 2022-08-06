package stephane.amstrong.kotlinmvixxlstore.di

import stephane.amstrong.kotlinmvixxlstore.business.datasource.datastore.AppDataStore
import stephane.amstrong.kotlinmvixxlstore.business.interactors.session.CheckPreviousAuthUser
import stephane.amstrong.kotlinmvixxlstore.business.interactors.session.Logout
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.FlowPreview
import retrofit2.Retrofit
import stephane.amstrong.kotlinmvixxlstore.business.datasource.cache.account.AuthenticationDao
import stephane.amstrong.kotlinmvixxlstore.business.datasource.network.account.AccountApi
import stephane.amstrong.kotlinmvixxlstore.business.interactors.account.Authenticate
import javax.inject.Singleton

@FlowPreview
@Module
@InstallIn(SingletonComponent::class)
object AuthModule{

    @Singleton
    @Provides
    fun provideAccountApi(retrofitBuilder: Retrofit.Builder): AccountApi {
        return retrofitBuilder
            .build()
            .create(AccountApi::class.java)
    }

    @Singleton
    @Provides
    fun provideCheckPrevAuthUser(
        authenticationDao: AuthenticationDao,
    ): CheckPreviousAuthUser {
        return CheckPreviousAuthUser(
            authenticationDao
        )
    }

    @Singleton
    @Provides
    fun provideAuthenticate(
        accountApi: AccountApi,
        authenticationDao: AuthenticationDao,
        appDataStoreManager: AppDataStore,
    ): Authenticate {
        return Authenticate(
            accountApi,
            authenticationDao,
            appDataStoreManager,
        )
    }

    /*
    accountApi: AccountApi,
    authenticationDao: AuthenticationDao,
    appDataStoreManager: AppDataStore,
    */

    @Singleton
    @Provides
    fun provideLogout(
        authTokenDao: AuthenticationDao,
    ): Logout {
        return Logout(authTokenDao)
    }

    /*
    @Singleton
    @Provides
    fun provideRegister(
        service: OpenApiAuthService,
        accountDao: AccountDao,
        authTokenDao: AuthTokenDao,
        appDataStoreManager: AppDataStore,
    ): Register {
        return Register(
            service,
            accountDao,
            authTokenDao,
            appDataStoreManager
        )
    }
    */
}









