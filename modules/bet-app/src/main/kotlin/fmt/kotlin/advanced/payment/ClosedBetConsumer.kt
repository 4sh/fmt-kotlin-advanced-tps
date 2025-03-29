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
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.datetime.Clock
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap

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

    private val scopes = ConcurrentHashMap<String, CoroutineScope>()

    fun launchFor(matchId: String, nbOfCollectors: Int = 10) {
        logger.info("[PAYMENT-CONSUMER] start consumer for $matchId")

        if (scopes.containsKey(matchId)) {
            scopes.getValue(matchId).cancel()
        }
        val scope = scopes.getOrPut(matchId) { CoroutineScope(Dispatchers.IO + Counters()) }

        val channels = List(nbOfCollectors) { Channel<RugbyBet>() }

        scope.launch {
            // fetch closed bets to send them to closed bert channel
            rubyBetRepository.getBetsForMatch(matchId, BetStatus.CLOSE)
                .collect {
                    closedBetChannel.send(it)
                }

            logger.info("[PAYMENT-CONSUMER] stop consumer for $matchId")
            logCounters(matchId)
            scopes.remove(matchId)
            scope.cancel()
        }

        // start closed bet consumer job
        channels.forEachIndexed { index, channel ->
            scope.launchClosedBetConsumerJob(index, channel)
        }
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
        launch {
            channel.receiveAsFlow().collect { bet ->
                try {
                    withTimeoutOrNull(250) {
                        val url = bet.buildPaymentUrl()
                        val httpResponse = client.post(url)
                        if (httpResponse.status.isSuccess()) {
                            bet.copy(paidInstant = Clock.System.now()).also {
                                rubyBetRepository.setBetAsPaid(checkNotNull(it.id), it.paidInstant!!)
                            }
                            coroutineContext[Counters]?.ok?.incrementAndGet()
                        } else {
                            coroutineContext[Counters]?.error?.incrementAndGet()
                        }
                        true
                    } ?: coroutineContext[Counters]?.timeout?.incrementAndGet()

                } catch (e: Exception) {
                    coroutineContext[Counters]?.error?.incrementAndGet()
                }
            }
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
        logger.info("[MATCH] $matchId COUNTER ERROR ${coroutineContext[Counters]?.error?.get()}")
        logger.info("[MATCH] $matchId COUNTER TIMEOUT ${coroutineContext[Counters]?.timeout?.get()}")
    }

    fun stopClose(matchId: String) {
        logger.info("[MATCH] $matchId cancel scope ${scopes[matchId]}")
        scopes[matchId]?.cancel()
    }
}

