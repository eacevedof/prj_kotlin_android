package Modules.Users.Application.LoginUser

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoggedUserDto(
    @SerialName("user_uuid")
    val userUuid: String,
    val email: String,
    @SerialName("full_name")
    val fullName: String,
    @SerialName("access_token")
    val accessToken: String,
    @SerialName("refresh_token")
    val refreshToken: String,
    @SerialName("expires_at")
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
}
