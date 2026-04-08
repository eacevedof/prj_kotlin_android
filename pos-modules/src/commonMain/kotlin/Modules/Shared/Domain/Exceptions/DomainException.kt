package Modules.Shared.Domain.Exceptions

import Modules.Shared.Infrastructure.Enums.HttpResponseCodeEnum

abstract class DomainException(
    private val exceptionName: String,
    private val code: HttpResponseCodeEnum,
    override val message: String
) : Exception(message) {

    fun getStatusCode(): Int = code.value

    fun getExceptionName(): String = exceptionName
}
