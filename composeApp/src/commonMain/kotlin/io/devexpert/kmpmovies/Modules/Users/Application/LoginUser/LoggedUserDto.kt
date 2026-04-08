package io.devexpert.kmpmovies.Modules.Users.Application.LoginUser

data class LoggedUserDto(
    val userUuid: String,
    val email: String,
    val fullName: String,
    val accessToken: String,
    val refreshToken: String,
    val expiresAt: Long,
    val role: String
) {
    companion object {
        fun fromPrimitives(primitives: Map<String, Any?>): LoggedUserDto {
            return LoggedUserDto(
                userUuid = primitives["user_uuid"] as? String ?: "",
                email = primitives["email"] as? String ?: "",
                fullName = primitives["full_name"] as? String ?: "",
                accessToken = primitives["access_token"] as? String ?: "",
                refreshToken = primitives["refresh_token"] as? String ?: "",
                expiresAt = (primitives["expires_at"] as? Number)?.toLong() ?: 0L,
                role = primitives["role"] as? String ?: ""
            )
        }
    }

    fun toPrimitives(): Map<String, Any?> {
        return mapOf(
            "user_uuid" to userUuid,
            "email" to email,
            "full_name" to fullName,
            "access_token" to accessToken,
            "refresh_token" to refreshToken,
            "expires_at" to expiresAt,
            "role" to role
        )
    }

    fun getUserUuid(): String = userUuid

    fun getEmail(): String = email

    fun getFullName(): String = fullName

    fun getAccessToken(): String = accessToken

    fun getRefreshToken(): String = refreshToken

    fun getExpiresAt(): Long = expiresAt

    fun getRole(): String = role
}
