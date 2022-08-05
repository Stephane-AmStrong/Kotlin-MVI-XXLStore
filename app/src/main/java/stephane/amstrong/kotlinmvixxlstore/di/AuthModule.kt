package stephane.amstrong.kotlinmvixxlstore.di

import stephane.amstrong.kotlinmvixxlstore.business.datasource.cache.account.AccountDao
import stephane.amstrong.kotlinmvixxlstore.business.datasource.cache.auth.AuthTokenDao
import stephane.amstrong.kotlinmvixxlstore.business.datasource.datastore.AppDataStore
import stephane.amstrong.kotlinmvixxlstore.business.datasource.network.auth.OpenApiAuthService
import stephane.amstrong.kotlinmvixxlstore.business.interactors.auth.Login
import stephane.amstrong.kotlinmvixxlstore.business.interactors.auth.Register
import stephane.amstrong.kotlinmvixxlstore.business.interactors.session.CheckPreviousAuthUser
import stephane.amstrong.kotlinmvixxlstore.business.interactors.session.Logout
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.FlowPreview
import retrofit2.Retrofit
import javax.inject.Singleton

@FlowPreview
@Module
@InstallIn(SingletonComponent::class)
object AuthModule{

    @Singleton
    @Provides
    fun provideOpenApiAuthService(retrofitBuilder: Retrofit.Builder): OpenApiAuthService {
        return retrofitBuilder
            .build()
            .create(OpenApiAuthService::class.java)
    }

    @Singleton
    @Provides
    fun provideCheckPrevAuthUser(
        accountDao: AccountDao,
        authTokenDao: AuthTokenDao,
    ): CheckPreviousAuthUser {
        return CheckPreviousAuthUser(
            accountDao,
            authTokenDao
        )
    }

    @Singleton
    @Provides
    fun provideLogin(
        service: OpenApiAuthService,
        accountDao: AccountDao,
        authTokenDao: AuthTokenDao,
        appDataStoreManager: AppDataStore,
    ): Login {
        return Login(
            service,
            accountDao,
            authTokenDao,
            appDataStoreManager
        )
    }

    @Singleton
    @Provides
    fun provideLogout(
        authTokenDao: AuthTokenDao,
    ): Logout {
        return Logout(authTokenDao)
    }

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
}









