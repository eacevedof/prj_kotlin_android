package Modules.Users.Domain.Exceptions

import Modules.Shared.Domain.Exceptions.DomainException
import Modules.Shared.Infrastructure.Enums.HttpResponseCodeEnum

class UsersException private constructor(
    code: HttpResponseCodeEnum,
    message: String
) : DomainException("UsersException", code, message) {

    companion object {

        fun badRequest(message: String): Nothing {
            throw UsersException(HttpResponseCodeEnum.BAD_REQUEST, message)
        }

        fun notFound(message: String): Nothing {
            throw UsersException(HttpResponseCodeEnum.NOT_FOUND, message)
        }

        fun conflict(message: String): Nothing {
            throw UsersException(HttpResponseCodeEnum.CONFLICT, message)
        }

        fun unauthorized(message: String): Nothing {
            throw UsersException(HttpResponseCodeEnum.UNAUTHORIZED, message)
        }

        fun forbidden(message: String): Nothing {
            throw UsersException(HttpResponseCodeEnum.FORBIDDEN, message)
        }

        fun invalidCredentials(): Nothing {
            throw UsersException(HttpResponseCodeEnum.UNAUTHORIZED, "Credenciales inválidas")
        }

        fun userNotFound(): Nothing {
            throw UsersException(HttpResponseCodeEnum.NOT_FOUND, "Usuario no encontrado")
        }

        fun userAlreadyExists(): Nothing {
            throw UsersException(HttpResponseCodeEnum.CONFLICT, "El usuario ya existe")
        }

        fun userDisabled(): Nothing {
            throw UsersException(HttpResponseCodeEnum.FORBIDDEN, "Usuario deshabilitado")
        }

        fun emailRequired(): Nothing {
            throw UsersException(HttpResponseCodeEnum.BAD_REQUEST, "El email es requerido")
        }

        fun passwordRequired(): Nothing {
            throw UsersException(HttpResponseCodeEnum.BAD_REQUEST, "La contraseña es requerida")
        }

        fun invalidEmailFormat(): Nothing {
            throw UsersException(HttpResponseCodeEnum.BAD_REQUEST, "Formato de email inválido")
        }
    }
}
