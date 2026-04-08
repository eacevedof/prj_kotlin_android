package Modules.Shared.Infrastructure.Components

import Modules.Shared.Infrastructure.Enums.HttpResponseCodeEnum
import kotlinx.serialization.Serializable

@Serializable
data class ResponseDto(
    val code: Int = HttpResponseCodeEnum.OK.value,
    val message: String = "",
    val data: String? = null
) {
    companion object {
        fun success(data: String? = null, message: String = "OK"): ResponseDto {
            return ResponseDto(
                code = HttpResponseCodeEnum.OK.value,
                message = message,
                data = data
            )
        }

        fun created(data: String? = null, message: String = "Created"): ResponseDto {
            return ResponseDto(
                code = HttpResponseCodeEnum.CREATED.value,
                message = message,
                data = data
            )
        }

        fun error(code: HttpResponseCodeEnum, message: String): ResponseDto {
            return ResponseDto(
                code = code.value,
                message = message,
                data = null
            )
        }
    }

    fun isSuccess(): Boolean = code in 200..299
}
