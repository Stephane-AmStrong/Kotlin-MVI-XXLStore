package stephane.amstrong.kotlinmvixxlstore.business.datasource.network.category

import stephane.amstrong.kotlinmvixxlstore.business.datasource.network.GenericResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface CategoryApi {

/*
    @GET("v1/categories")
    suspend fun search(
        @Header("Authorization") authorization: String,
        @Query("withTheName") withTheName: String?,
        @Query("pageNumber") pageNumber: Int?,
        @Query("pageSize") pageSize: Int?,
        @Query("orderBy") orderBy: String?,
        @Query("searchTerm") searchTerm: String?,
        //@Path("version") version: Int,
    ): List<CategoryDto.CategoryResponse>


    @DELETE("v1/categories/delete")
    suspend fun delete(
        @Header("Authorization") authorization: String,
        @Path("version") version: Int
    )

    @Multipart
    @PUT("v1/categories/update")
    suspend fun update(
        @Header("Authorization") authorization: String,
        @Path("version") version: Int,
        @Part("title") title: RequestBody,
        @Part("body") body: RequestBody,
        @Part image: MultipartBody.Part?
    ): CategoryDto.CategoryResponse


    @Multipart
    @POST("category/create")
    suspend fun create(
        @Header("Authorization") authorization: String,
        @Part("title") title: RequestBody,
        @Part("body") body: RequestBody,
        @Part image: MultipartBody.Part?
    ): CategoryDto.CategoryResponse

    @GET("v1/categories")
    suspend fun get(
        @Header("Authorization") authorization: String,
        @Path("version") version: Int,
    ): CategoryDto.CategoryResponse
*/

    @GET("v1/Categories")
    suspend fun search(
        @Header("Authorization") authorization: String,
        @Query("withTheName") withTheName: String?,
        @Query("pageNumber") pageNumber: Int?,
        @Query("pageSize") pageSize: Int?,
        @Query("orderBy") orderBy: String?,
        @Query("searchTerm") searchTerm: String?,
        //@Path("version") version: Int,
    ): List<CategoryDto.CategoryResponse>

    @GET("v1/categories/{id}")
    suspend fun getById(
        @Header("Authorization") authorization: String,
        @Path("id") id: String
    ): CategoryDto.CategoryResponse

    @POST("v1/categories")
    suspend fun create(
        @Header("Authorization") authorization: String,
        @Body category: CategoryDto.CategoryRequest
    ): CategoryDto.CategoryResponse

    @PUT("v1/categories/{id}")
    suspend fun update(
        @Header("Authorization") authorization: String,
        @Path("id") id:String,
        @Body category: CategoryDto.CategoryRequest
    ): CategoryDto.CategoryResponse

    @DELETE("v1/categories/{id}")
    suspend fun delete(
        @Header("Authorization") authorization: String,
        @Path("id") id:String
    )
}