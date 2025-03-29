package fmt.kotlin.advanced.payment

import fmt.kotlin.advanced.bet.BetStatus
import fmt.kotlin.advanced.bet.RugbyBet
import fmt.kotlin.advanced.match.MongoRugbyBetRepository
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.datetime.Clock
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ClosedBetConsumer(
    private val rubyBetRepository: MongoRugbyBetRepository,
    private val walletServiceBaseUrl: String = "http://localhost:8085",
) {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    private val closedBetChannel = Channel<RugbyBet>()

    private val client = HttpClient(Apache) {
        install(ContentNegotiation) {
            json()
        }
    }

    fun launchFor(matchId: String, nbOfCollectors: Int = 10) {
        logger.info("[PAYMENT-CONSUMER] start consumer for $matchId")

        // TODO step 4
        // create scope to launch coroutines
        // init channels to distribute bets


        // TODO step 4
        // start coroutine to get closed bets and send them to the closedBetChannel
        // after collect, log counters and stop scope
        rubyBetRepository.getBetsForMatch(matchId, BetStatus.CLOSE)
            .collect {

            }

        // TODO step 4
        // start closed bet consumer job for each channel
        scope.launchClosedBetConsumerJob(index, channel)

        // TODO step 4
        // start fan out job from closed bets channel
        scope.launchFanOutJob(closedBetChannel, channels)
        // start supervisor job
        scope.launchSupervisorJob(matchId)
    }

    private fun CoroutineScope.launchClosedBetConsumerJob(
        index: Int,
        channel: Channel<RugbyBet>,
    ) {
        logger.info("[CONSUMER-$index] Start consumer job")
        // TODO step 4
        // start coroutine and process channel as flow with receiveAsFlow
        // increment a counter from coroutine context to count correct request

        val url = bet.buildPaymentUrl()
        client.post(url)
        bet.copy(paidInstant = Clock.System.now()).also {
            rubyBetRepository.setBetAsPaid(checkNotNull(it.id), it.paidInstant!!)
        }
    }

    private fun RugbyBet.buildPaymentUrl() = URLBuilder(walletServiceBaseUrl).apply {
        appendPathSegments(
            "wallet",
            userId,
            "add",
            (betAmount * betOdds).toString(),
            "for-bet",
            id.toString()
        )
    }.buildString()

    private fun CoroutineScope.launchSupervisorJob(matchId: String) {
        launch {
            logger.info("[PAYMENT-CONSUMER] [SUPERVISOR] Start supervisor job for match $matchId")
            while (true) {
                logCounters(matchId)
                delay(5_000)
            }
        }
    }

    private fun CoroutineScope.logCounters(matchId: String) {
        logger.info("#####################################################################")
        logger.info("[MATCH] $matchId COUNTER OK ${coroutineContext[Counters]?.ok?.get()}")
    }
}

