package fmt.kotlin.advanced.http

import fmt.kotlin.advanced.match.MatchService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Application.configureRouting(matchService: MatchService) {
    routing {
        post("/match") {
            val nbOfBets = call.queryParameters["nbOfBets"]?.toInt() ?: 250_000
            call.respond(matchService.startMatch(nbOfBets))
        }

        post("/match/{id}/close") {
            val matchId = call.pathParameters["id"]
            if (matchId != null) {
                matchService.closeMatch(matchId)
                call.response.status(HttpStatusCode.OK)
            }
        }

        post("/match/{id}/close/stop") {
            val matchId = call.pathParameters["id"]
            if (matchId != null) {
                matchService.stopClose(matchId)
                call.response.status(HttpStatusCode.OK)
            }
        }

        get("/match/{id}/is-fully-paid") {
            val matchId = call.pathParameters["id"]
            if (matchId != null) {
                call.respond(matchService.isFullyPaid(matchId))
            }
        }
    }
}