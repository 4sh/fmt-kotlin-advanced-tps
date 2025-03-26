package fmt.kotlin.advanced.http

import fmt.kotlin.advanced.match.MatchService
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Application.configureRouting(matchService: MatchService) {
    routing {
        post("/match") {
            val nbOfBets = call.queryParameters["nbOfBets"]?.toInt() ?: 250_000
            call.respond(matchService.startMatch(nbOfBets))
        }
    }
}