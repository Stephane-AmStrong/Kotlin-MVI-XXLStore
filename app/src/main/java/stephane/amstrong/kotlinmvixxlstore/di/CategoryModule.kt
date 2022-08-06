package stephane.amstrong.kotlinmvixxlstore.di

import stephane.amstrong.kotlinmvixxlstore.business.datasource.cache.category.CategoryDao
import stephane.amstrong.kotlinmvixxlstore.business.datasource.datastore.AppDataStore
import stephane.amstrong.kotlinmvixxlstore.business.datasource.network.main.OpenApiMainService
import stephane.amstrong.kotlinmvixxlstore.business.interactors.category.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import stephane.amstrong.kotlinmvixxlstore.business.datasource.network.category.CategoryApi
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CategoryModule {

    @Singleton
    @Provides
    fun provideGetCategoryFromCache(
        dao: CategoryDao
    ): GetCategoryFromCache{
        return GetCategoryFromCache(dao)
    }

    @Singleton
    @Provides
    fun provideSearchCategories(
        categoryApi: CategoryApi,
        categoryDao: CategoryDao
    ): SearchCategories{
        return SearchCategories(categoryApi, categoryDao)
    }

    @Singleton
    @Provides
    fun provideDeleteCategory(
        categoryApi: CategoryApi,
        categoryDao: CategoryDao
    ): DeleteCategory{
        return DeleteCategory(categoryApi, categoryDao)
    }

    @Singleton
    @Provides
    fun provideUpdateCategory(
        categoryApi: CategoryApi,
        categoryDao: CategoryDao
    ): UpdateCategory{
        return UpdateCategory(categoryApi, categoryDao)
    }

    @Singleton
    @Provides
    fun providePublishCategory(
        categoryApi: CategoryApi,
        categoryDao: CategoryDao
    ): PublishCategory{
        return PublishCategory(categoryApi, categoryDao)
    }

    @Singleton
    @Provides
    fun provideGetOrderAndFilter(
        appDataStoreManager: AppDataStore
    ): GetOrderAndFilter{
        return GetOrderAndFilter(appDataStoreManager)
    }

    @Singleton
    @Provides
    fun provideConfirmCategoryExistsOnServer(
        categoryApi: CategoryApi,
        categoryDao: CategoryDao
    ): ConfirmCategoryExistsOnServer{
        return ConfirmCategoryExistsOnServer(categoryApi, categoryDao)
    }
}

















