package stephane.amstrong.kotlinmvixxlstore.business.datasource.cache.category

import androidx.room.*
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.Constants.Companion.PAGINATION_PAGE_SIZE

@Dao
interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: CategoryEntity): Long

    @Delete
    suspend fun deleteCategory(category: CategoryEntity)

    @Query("DELETE FROM category WHERE id = :id")
    suspend fun deleteCategory(id: String)

    @Query(
        """
        UPDATE category SET name = :name, description = :description
        WHERE id = :id
        """
    )

    suspend fun updateCategory(id: String, name: String, description: String)

    @Query("SELECT * FROM category WHERE id = :id")
    suspend fun getCategory(id: String): CategoryEntity?

/*
    searchTerm: String,
    withTheName: String,
    orderBy: String,
    pageNumber: Int,
    pageSize: Int,
*/

    // TODO: ordonner selon orderBy

    @Query(
        """
        SELECT * FROM category 
        LIMIT (:pageNumber * :pageSize)
        """
    )
    suspend fun getAllCategoriesOrderByNameASC(
        pageNumber: Int?,
        pageSize: Int = PAGINATION_PAGE_SIZE
    ): List<CategoryEntity>


    @Query(
        """
        SELECT * FROM category 
        WHERE name = :withTheName  
        AND (name LIKE '%' || :searchTerm || '%' OR description LIKE '%' || :searchTerm || '%') 
        LIMIT (:pageNumber * :pageSize)
        """
    )
    suspend fun getAllCategoriesOrderByNameASC(
        searchTerm: String,
        withTheName: String,
        //orderBy: String,
        pageNumber: Int,
        pageSize: Int = PAGINATION_PAGE_SIZE
    ): List<CategoryEntity>

    @Query(
        """
        SELECT * FROM category 
        WHERE name = :withTheName  
        LIMIT (:pageNumber * :pageSize)
        """
    )
    suspend fun getAllCategoriesOrderByNameASC(
        withTheName: String,
        //orderBy: String,
        pageNumber: Int,
        pageSize: Int = PAGINATION_PAGE_SIZE
    ): List<CategoryEntity>

    //Desc
    @Query(
        """
        SELECT * FROM category 
        WHERE name = :withTheName  
        AND (name LIKE '%' || :searchTerm || '%' OR description LIKE '%' || :searchTerm || '%') 
        Order By name
        LIMIT (:pageNumber * :pageSize)
        """
    )
    suspend fun getAllCategoriesOrderByNameDESC(
        searchTerm: String,
        withTheName: String,
        //orderBy: String,
        pageNumber: Int,
        pageSize: Int = PAGINATION_PAGE_SIZE
    ): List<CategoryEntity>


}

suspend fun CategoryDao.returnOrderedCategoryQuery(
    searchTerm: String?,
    withTheName: String?,
    orderBy: String?,
    pageNumber: Int?,
    pageSize: Int?
): List<CategoryEntity> {

    return (getAllCategoriesOrderByNameASC(pageNumber ?: 1, pageSize ?: PAGINATION_PAGE_SIZE))

    /*

    when {
        filterAndOrder.contains(CategoryQueryUtils.ORDER_BY_DESC_DATE_UPDATED) -> {
            return searchCategoriesOrderByDateDESC(
                query = query,
                page = page
            )
        }

        filterAndOrder.contains(CategoryQueryUtils.ORDER_BY_ASC_DATE_UPDATED) -> {
            return searchCategoriesOrderByDateASC(
                query = query,
                page = page
            )
        }

        filterAndOrder.contains(CategoryQueryUtils.ORDER_BY_DESC_DATE_UPDATED) -> {
            return searchCategoriesOrderByAuthorDESC(
                query = query,
                page = page
            )
        }

        filterAndOrder.contains(CategoryQueryUtils.ORDER_BY_ASC_DATE_UPDATED) -> {
            return searchCategoriesOrderByAuthorASC(
                query = query,
                page = page
            )
        }
        else ->
            return searchCategoriesOrderByDateDESC(
                query = query,
                page = page
            )
    }

    */

}