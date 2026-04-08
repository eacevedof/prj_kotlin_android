package plugins

import Modules.Shared.Infrastructure.Components.ResponseDto
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import routes.usersRoutes

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respond(
                ResponseDto.success(
                    message = "POS API v1.0.0"
                )
            )
        }

        get("/health") {
            call.respond(
                ResponseDto.success(
                    message = "OK"
                )
            )
        }

        route("/api/v1") {
            usersRoutes()
        }
    }
}
