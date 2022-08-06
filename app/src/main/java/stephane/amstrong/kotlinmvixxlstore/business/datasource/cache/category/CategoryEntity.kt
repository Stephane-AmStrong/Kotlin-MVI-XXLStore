package stephane.amstrong.kotlinmvixxlstore.business.datasource.cache.category

import androidx.room.Entity
import androidx.room.PrimaryKey
import stephane.amstrong.kotlinmvixxlstore.business.domain.models.Category
import java.util.*

@Entity(tableName = "category")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val name: String,
    val description: String,
    val createdAt: Date,
    val updatedAt: Date,
    val createdBy: String,
    val updatedBy: String,
)

fun CategoryEntity.toCategory(): Category {
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

/*
    public Category()
    {
        Items = new HashSet<Item>();
    }

    [Required]
    public string Name { get; set; }
    public string? Description { get; set; }

    public virtual ICollection<Item> Items { get; set; }
*/