package io.devexpert.kmpmovies.Modules.Users.Application.LoginUser

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

    fun getEmail(): String = email.lowercase().trim()

    fun getPassword(): String = password

    fun getDeviceId(): String? = deviceId

    fun getDeviceName(): String? = deviceName

    fun getPlatform(): String? = platform
}
