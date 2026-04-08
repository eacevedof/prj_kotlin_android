package io.devexpert.kmpmovies.Modules.Shared.Infrastructure.Components

import io.devexpert.kmpmovies.Modules.Shared.Infrastructure.Enums.HttpResponseCodeEnum

data class ResponseDto(
    val code: Int = HttpResponseCodeEnum.OK.value,
    val message: String = "",
    val data: Any? = null
) {
    companion object {
        fun fromPrimitives(primitives: Map<String, Any?>): ResponseDto {
            return ResponseDto(
                code = (primitives["code"] as? Number)?.toInt() ?: HttpResponseCodeEnum.OK.value,
                message = primitives["message"] as? String ?: "",
                data = primitives["data"]
            )
        }

        fun success(data: Any? = null, message: String = "OK"): ResponseDto {
            return ResponseDto(
                code = HttpResponseCodeEnum.OK.value,
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

    fun toPrimitives(): Map<String, Any?> {
        return mapOf(
            "code" to code,
            "message" to message,
            "data" to data
        )
    }

    fun isSuccess(): Boolean = code in 200..299
}
