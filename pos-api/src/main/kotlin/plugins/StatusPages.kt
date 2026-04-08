package plugins

import Modules.Shared.Domain.Exceptions.DomainException
import Modules.Shared.Infrastructure.Components.ResponseDto
import Modules.Shared.Infrastructure.Enums.HttpResponseCodeEnum
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<DomainException> { call, cause ->
            val response = ResponseDto.error(
                code = HttpResponseCodeEnum.entries.find { it.value == cause.getStatusCode() }
                    ?: HttpResponseCodeEnum.INTERNAL_SERVER_ERROR,
                message = cause.message
            )
            call.respond(HttpStatusCode.fromValue(cause.getStatusCode()), response)
        }

        exception<Throwable> { call, cause ->
            val response = ResponseDto.error(
                code = HttpResponseCodeEnum.INTERNAL_SERVER_ERROR,
                message = cause.message ?: "Error interno del servidor"
            )
            call.respond(HttpStatusCode.InternalServerError, response)
        }

        status(HttpStatusCode.NotFound) { call, status ->
            val response = ResponseDto.error(
                code = HttpResponseCodeEnum.NOT_FOUND,
                message = "Recurso no encontrado"
            )
            call.respond(status, response)
        }
    }
}
