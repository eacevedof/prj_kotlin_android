package Modules.Shared.Infrastructure.Enums

enum class HttpRequestMethodEnum(val value: String) {
    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    DELETE("DELETE"),
    PATCH("PATCH"),
    OPTIONS("OPTIONS"),
    HEAD("HEAD")
}
