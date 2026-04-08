package Modules.Users.Application.LoginUser

import kotlinx.serialization.Serializable

@Serializable
data class LoginUserDto(
    val email: String,
    val password: String,
    val deviceId: String? = null,
    val deviceName: String? = null,
    val platform: String? = null
) {
    companion object {
        fun fromPrimitives(primitives: Map<String, Any?>): LoginUserDto {
            return LoginUserDto(
                email = (primitives["email"] as? String)?.trim() ?: "",
                password = primitives["password"] as? String ?: "",
                deviceId = primitives["device_id"] as? String,
                deviceName = primitives["device_name"] as? String,
                platform = primitives["platform"] as? String
            )
        }
    }

    fun getEmailNormalized(): String = email.lowercase().trim()
}
