package Modules.Shared.Infrastructure.Components

import kotlin.random.Random

object Uuider {

    private const val ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"

    fun getRandomUuidWithPrefix(prefix: String): String {
        return "$prefix-${generateUuid()}"
    }

    fun getRandomAlphaNumericString(length: Int = 10): String {
        return (1..length)
            .map { ALPHANUMERIC[Random.nextInt(ALPHANUMERIC.length)] }
            .joinToString("")
    }

    fun getRandomAlphaNumericStringWithPrefix(prefix: String, length: Int = 10): String {
        return "$prefix${getRandomAlphaNumericString(length)}"
    }

    private fun generateUuid(): String {
        val hexChars = "0123456789abcdef"
        fun randomHex(length: Int) = (1..length).map { hexChars[Random.nextInt(16)] }.joinToString("")

        return "${randomHex(8)}-${randomHex(4)}-4${randomHex(3)}-${randomHex(4)}-${randomHex(12)}"
    }
}
