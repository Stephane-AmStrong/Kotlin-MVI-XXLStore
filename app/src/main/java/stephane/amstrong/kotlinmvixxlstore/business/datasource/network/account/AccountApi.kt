package stephane.amstrong.kotlinmvixxlstore.business.datasource.network.account

import retrofit2.http.Body
import retrofit2.http.POST

interface AccountApi {

    @POST("authentication/registration")
    suspend fun registerUser(
        @Body userRegistrationRequest: RegistrationDto.RegistrationRequest
    ): RegistrationDto.RegistrationResponse

    //@FormUrlEncoded
    @POST("account/authenticate")
    suspend fun authenticate(
        @Body authentication: AuthenticationDto.AuthenticationRequest
    ): AuthenticationDto.AuthenticationResponse
}