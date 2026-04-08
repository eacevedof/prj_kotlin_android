package Modules.Users.Application.LoginUser

import Modules.Shared.Infrastructure.Bootstrap.AppGlobalMap
import Modules.Shared.Infrastructure.Bootstrap.AppKeyEnum
import Modules.Shared.Infrastructure.Components.DateTimer
import Modules.Shared.Infrastructure.Components.Logger
import Modules.Shared.Infrastructure.Components.Uuider
import Modules.Users.Domain.Exceptions.UsersException

class LoginUserService {

    private lateinit var loginUserDto: LoginUserDto

    companion object {
        private const val EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
        private const val TOKEN_EXPIRATION_MS = 3600000L * 24 // 24 horas

        fun getInstance(): LoginUserService = LoginUserService()
    }

    suspend fun invoke(inputDto: LoginUserDto): LoggedUserDto {
        this.loginUserDto = inputDto

        Logger.logDebug("Login attempt for: ${inputDto.getEmailNormalized()}", "LoginUserService")

        failIfInvalidInput()
        val user = findUserByCredentials()
        failIfUserDisabled(user)
        val tokens = generateTokens(user)
        saveSession(user, tokens)

        return LoggedUserDto.fromPrimitives(
            mapOf(
                "user_uuid" to user["uuid"],
                "email" to user["email"],
                "full_name" to user["full_name"],
                "access_token" to tokens["access_token"],
                "refresh_token" to tokens["refresh_token"],
                "expires_at" to tokens["expires_at"],
                "role" to user["role"]
            )
        )
    }

    private fun failIfInvalidInput() {
        if (loginUserDto.getEmailNormalized().isEmpty()) {
            UsersException.emailRequired()
        }

        if (loginUserDto.password.isEmpty()) {
            UsersException.passwordRequired()
        }

        if (!isValidEmail(loginUserDto.getEmailNormalized())) {
            UsersException.invalidEmailFormat()
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return EMAIL_REGEX.toRegex().matches(email)
    }

    private suspend fun findUserByCredentials(): Map<String, Any?> {
        // TODO: Reemplazar con UsersReaderRepository real
        // val user = usersReaderRepository.findByEmailAndPassword(
        //     loginUserDto.getEmailNormalized(),
        //     hashPassword(loginUserDto.password)
        // )

        val mockUser = getMockUser()

        if (mockUser == null) {
            UsersException.invalidCredentials()
        }

        return mockUser
    }

    private fun failIfUserDisabled(user: Map<String, Any?>) {
        val isEnabled = user["is_enabled"] as? Boolean ?: true
        if (!isEnabled) {
            UsersException.userDisabled()
        }
    }

    private fun generateTokens(user: Map<String, Any?>): Map<String, Any> {
        val accessToken = Uuider.getRandomAlphaNumericStringWithPrefix("at_", 64)
        val refreshToken = Uuider.getRandomAlphaNumericStringWithPrefix("rt_", 64)
        val expiresAt = DateTimer.getNowAsTimestamp() + TOKEN_EXPIRATION_MS

        return mapOf(
            "access_token" to accessToken,
            "refresh_token" to refreshToken,
            "expires_at" to expiresAt
        )
    }

    private fun saveSession(user: Map<String, Any?>, tokens: Map<String, Any>) {
        AppGlobalMap.set(AppKeyEnum.USER_UUID, user["uuid"])
        AppGlobalMap.set(AppKeyEnum.SESSION_TOKEN, tokens["access_token"])

        Logger.logInfo("User logged in: ${user["email"]}", "LoginUserService")
    }

    // Mock - eliminar cuando se implemente repository real
    private fun getMockUser(): Map<String, Any?> {
        if (loginUserDto.getEmailNormalized() == "admin@pos.com" && loginUserDto.password == "admin123") {
            return mapOf(
                "uuid" to "usr-00000000-0000-4000-0000-000000000001",
                "email" to "admin@pos.com",
                "full_name" to "Administrador",
                "role" to "admin",
                "is_enabled" to true
            )
        }

        if (loginUserDto.getEmailNormalized() == "cajero@pos.com" && loginUserDto.password == "cajero123") {
            return mapOf(
                "uuid" to "usr-00000000-0000-4000-0000-000000000002",
                "email" to "cajero@pos.com",
                "full_name" to "Cajero Demo",
                "role" to "cashier",
                "is_enabled" to true
            )
        }

        UsersException.invalidCredentials()
    }
}
