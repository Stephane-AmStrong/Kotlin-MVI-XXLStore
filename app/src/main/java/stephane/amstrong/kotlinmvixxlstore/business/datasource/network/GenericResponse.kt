package stephane.amstrong.kotlinmvixxlstore.business.datasource.network

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

class GenericResponse(
    var id : String,
    var createdAt : Date,
    var updatedAt : Date,
    var createdBy : String,
    var updatedBy : String,


//    @SerializedName("response")
//    @Expose
//    val response: String?,
//
//    @SerializedName("error_message")
//    val errorMessage: String?,
)