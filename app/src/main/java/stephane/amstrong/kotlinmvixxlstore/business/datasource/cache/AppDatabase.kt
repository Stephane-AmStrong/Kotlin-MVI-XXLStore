package stephane.amstrong.kotlinmvixxlstore.business.datasource.cache

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import stephane.amstrong.kotlinmvixxlstore.business.datasource.cache.account.AuthenticationDao
import stephane.amstrong.kotlinmvixxlstore.business.datasource.cache.account.AuthenticationEntity
import stephane.amstrong.kotlinmvixxlstore.business.datasource.cache.blog.BlogPostDao
import stephane.amstrong.kotlinmvixxlstore.business.datasource.cache.blog.BlogPostEntity
import stephane.amstrong.kotlinmvixxlstore.business.datasource.cache.category.CategoryDao
import stephane.amstrong.kotlinmvixxlstore.business.datasource.cache.category.CategoryEntity

@Database(entities = [AuthenticationEntity::class, CategoryEntity::class, BlogPostEntity::class], version = 1)
@TypeConverters(DateConverter::class)
abstract class AppDatabase: RoomDatabase() {

    abstract fun getAuthenticationDao(): AuthenticationDao

    abstract fun getCategoryDao(): CategoryDao

    abstract fun getBlogPostDao(): BlogPostDao

    companion object{
        const val DATABASE_NAME: String = "app_db"
    }


}








