package fmt.kotlin.advanced

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.delay
import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun main() {
    embeddedServer(
        Netty,
        port = System.getenv("PORT")?.toInt() ?: 8085,
        host = "0.0.0.0",
        module = Application::module
    )
        .start(wait = true)
}

fun Application.module() {
    install(ContentNegotiation) {
        json()
    }
    configureRouting()
}

fun Application.configureRouting() {

    val logger: Logger = LoggerFactory.getLogger(Application::class.java)

    routing {
        post("/wallet/{userId}/add/{value}/for-bet/{{betId}}") {

            when ((1..1000).random()) {
                1 -> {
                    logger.info("RESPOND ERROR")
                    error("unavailable")
                }

                2 -> {
                    logger.info("RESPOND AFTER 5s")
                    delay(500)
                    call.respondText { "OK" }
                }

                else -> {
                    logger.info("RESPOND DIRECT")
                    call.respondText { "OK" }
                }
            }
        }
    }
}