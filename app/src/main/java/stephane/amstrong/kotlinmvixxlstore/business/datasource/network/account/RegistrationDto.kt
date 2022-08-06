package stephane.amstrong.kotlinmvixxlstore.business.datasource.network.account

import stephane.amstrong.kotlinmvixxlstore.business.domain.models.*

sealed class RegistrationDto {

    data class RegistrationRequest(
        val firstName: String,
        val lastName: String,
        val email: String,
        val role: String,
        val password: String,
        val confirmPassword: String,
    ) : RegistrationDto()

    data class RegistrationResponse(
        val Message: String?,
        val StatusCode: Int?
    ) : RegistrationDto()
}