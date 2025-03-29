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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ClosedBetConsumer(
    private val rubyBetRepository: MongoRugbyBetRepository,
    private val walletServiceBaseUrl: String = "http://localhost:8085",
) {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    private val client = HttpClient(Apache) {
        install(ContentNegotiation) {
            json()
        }
    }

    fun launchFor(scope: CoroutineScope, matchId: String) {
        logger.info("[PAYMENT-CONSUMER] start consumer for $matchId")

        scope.launch {
            rubyBetRepository.getBetsForMatch(matchId, BetStatus.CLOSE)
                .collectIndexed { i, bet ->
                    sendPayment(bet)
                    if (i % 10_000 == 0) {
                        logger.info("[PAYMENT-CONSUMER] paid bets : $i")
                    }
                }
        }
    }

    private suspend fun sendPayment(bet: RugbyBet) {
        val url = bet.buildPaymentUrl()
        val httpResponse = client.post(url)
        if (httpResponse.status.isSuccess()) {
            bet.copy(paidInstant = Clock.System.now()).also {
                rubyBetRepository.setBetAsPaid(checkNotNull(it.id), checkNotNull(it.paidInstant))
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

}

