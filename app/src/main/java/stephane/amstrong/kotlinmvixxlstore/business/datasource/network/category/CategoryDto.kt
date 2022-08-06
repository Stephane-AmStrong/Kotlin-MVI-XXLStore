package stephane.amstrong.kotlinmvixxlstore.business.datasource.network.category

import stephane.amstrong.kotlinmvixxlstore.business.datasource.cache.category.CategoryEntity
import stephane.amstrong.kotlinmvixxlstore.business.domain.models.Category
import java.util.*

sealed class CategoryDto {

/*
    protected var errors: List<String> = arrayListOf()
    protected var message: String? = null
    protected var statusCode: Int? = null
*/

    data class CategoryQueryParameters(
        val withTheName: String?,
        val pageNumber: Int?,
        val pageSize: Int?,
        val orderBy: String?,
        val searchTerm: String?,
    ) : CategoryDto()

    data class CategoryRequest(
        val name: String,
        val description: String?,
    ) : CategoryDto()

    data class CategoryResponse(
        val id: String,
        val name: String,
        val description: String,
        val createdAt: Date,
        val updatedAt: Date,
        val createdBy: String,
        val updatedBy: String,

        var errors: List<String>,
        var message: String?,
        var statusCode: Int?,
    ) : CategoryDto()
}

fun CategoryDto.CategoryResponse.toCategory(): Category {
    return Category(
        id = id,
        name = name,
        description = description,
        createdAt = createdAt,
        updatedAt = updatedAt,
        createdBy = createdBy,
        updatedBy = updatedBy,
    )
}

fun Category.toEntity(): CategoryEntity {
    return CategoryEntity(
        id = id,
        name = name,
        description = description,
        createdAt = createdAt,
        updatedAt = updatedAt,
        createdBy = createdBy,
        updatedBy = updatedBy,
    )
}