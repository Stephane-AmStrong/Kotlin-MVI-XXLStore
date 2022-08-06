package stephane.amstrong.kotlinmvixxlstore.business.datasource.cache.account

import androidx.room.*
import java.util.*

@Dao
interface AuthenticationDao {

    @Query("SELECT * FROM AuthenticationEntity WHERE email = :email")
    suspend fun searchByEmail(email: String): AuthenticationEntity?

    @Query("SELECT * FROM AuthenticationEntity")
    suspend fun search(): AuthenticationEntity?

    @Query("DELETE FROM AuthenticationEntity")
    suspend fun clearTokens()

    /*
    @Query("SELECT * FROM AuthenticationEntity WHERE userId = :userId")
    suspend fun searchByUserId(userId: String): AuthenticationEntity?
*/

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAndReplace(authentication: AuthenticationEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnore(authentication: AuthenticationEntity): Long

    @Query("UPDATE AuthenticationEntity SET name = :name, email = :email, token_value = :accessValue, token_expiryDate = :accessExpiryDate, refresh_value = :refreshValue, refresh_expiryDate = :refreshExpiryDate WHERE id = :id")
    suspend fun updateAuthentication(name : String, email : String, accessValue: String, accessExpiryDate: Date, id: String, refreshValue: String, refreshExpiryDate: Date)
}
/* name : String, email : String, expiryDate: Date, value: String id: String, value: String, expiryDate: Date
*/