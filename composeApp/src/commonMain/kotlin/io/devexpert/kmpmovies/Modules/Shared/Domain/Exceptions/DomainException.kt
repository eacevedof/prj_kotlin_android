package io.devexpert.kmpmovies.Modules.Shared.Domain.Exceptions

import io.devexpert.kmpmovies.Modules.Shared.Infrastructure.Enums.HttpResponseCodeEnum

abstract class DomainException(
    private val exceptionName: String,
    private val code: HttpResponseCodeEnum,
    override val message: String
) : Exception(message) {

    fun getStatusCode(): Int = code.value

    fun getMessage(): String = message

    fun getName(): String = exceptionName
}
