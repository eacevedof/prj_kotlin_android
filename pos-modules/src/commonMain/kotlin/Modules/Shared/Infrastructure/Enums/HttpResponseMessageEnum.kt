package Modules.Shared.Infrastructure.Enums

enum class HttpResponseMessageEnum(val value: String) {
    OK("OK"),
    CREATED("Created"),
    BAD_REQUEST("Bad Request"),
    UNAUTHORIZED("Unauthorized"),
    FORBIDDEN("Forbidden"),
    NOT_FOUND("Not Found"),
    CONFLICT("Conflict"),
    UNPROCESSABLE_ENTITY("Unprocessable Entity"),
    INTERNAL_SERVER_ERROR("Internal Server Error"),
    SERVICE_UNAVAILABLE("Service Unavailable")
}
