package fmt.kotlin.advanced.http

import fmt.kotlin.advanced.bet.BetGenerator
import fmt.kotlin.advanced.match.MatchService
import fmt.kotlin.advanced.match.MongoRugbyBetRepository
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.datetime.Clock
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val logger: Logger = LoggerFactory.getLogger(Application::class.java)

private const val MONGO_CONNECTION = "mongodb://127.0.0.1:27117"
private const val DATABASE_NAME = "bet"
private val clock = Clock.System

fun main() {
    embeddedServer(
        Netty,
        port = System.getenv("PORT")?.toInt() ?: 8080,
        host = "0.0.0.0",
        module = Application::configureModule
    ).start(wait = true)
}

fun Application.configureModule() {
    val database = MONGO_CONNECTION.initKMongo()
    val rugbyBetRepo = MongoRugbyBetRepository(database, clock)
    val betGenerator = BetGenerator()
    val matchService = MatchService(betGenerator, rugbyBetRepo)

    install(ContentNegotiation) {
        json()
    }
    configureRouting(matchService)
}

private fun String.initKMongo() =
    KMongo.createClient(this).coroutine.getDatabase(DATABASE_NAME)
