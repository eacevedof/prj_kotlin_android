import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import plugins.configureRouting
import plugins.configureSerialization
import plugins.configureStatusPages
import plugins.configureCORS

fun main() {
    embeddedServer(Netty, port = 9090, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureSerialization()
    configureStatusPages()
    configureCORS()
    configureRouting()
}
