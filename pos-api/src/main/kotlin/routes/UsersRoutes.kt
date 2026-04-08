package routes

import Modules.Shared.Infrastructure.Components.ResponseDto
import Modules.Users.Application.LoginUser.LoginUserDto
import Modules.Users.Application.LoginUser.LoginUserService
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

fun Route.usersRoutes() {
    route("/users") {

        post("/login") {
            val body = call.receiveText()
            val json = Json.parseToJsonElement(body).jsonObject

            val loginDto = LoginUserDto(
                email = json["email"]?.jsonPrimitive?.content ?: "",
                password = json["password"]?.jsonPrimitive?.content ?: "",
                deviceId = json["device_id"]?.jsonPrimitive?.content,
                deviceName = json["device_name"]?.jsonPrimitive?.content,
                platform = json["platform"]?.jsonPrimitive?.content
            )

            val result = LoginUserService.getInstance().invoke(loginDto)

            call.respond(
                HttpStatusCode.OK,
                ResponseDto(
                    code = 200,
                    message = "Login exitoso",
                    data = Json.encodeToString(
                        Modules.Users.Application.LoginUser.LoggedUserDto.serializer(),
                        result
                    )
                )
            )
        }

        get("/profile") {
            // TODO: Implementar con autenticación JWT
            call.respond(
                HttpStatusCode.OK,
                ResponseDto.success(message = "Profile endpoint - requiere autenticación")
            )
        }
    }
}
