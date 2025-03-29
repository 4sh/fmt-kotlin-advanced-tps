package fmt.kotlin.advanced.match

import fmt.kotlin.advanced.payment.ClosedBetConsumer
import fmt.kotlin.advanced.bet.BetGenerator
import fmt.kotlin.advanced.bet.BetStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import kotlin.time.measureTime

class MatchService(
    private val betGenerator: BetGenerator,
    private val rubyBetRepository: MongoRugbyBetRepository,
    private val closedBetConsumer: ClosedBetConsumer,
) {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    suspend fun startMatch(nbBets: Int): String {
        val matchId = UUID.randomUUID().toString()

        measureTime {
            logger.info("[MATCH] start match")
            betGenerator.generate(matchId)
                .take(nbBets)
                .map { bet ->
                    channelFlow {
                        launch {
                            rubyBetRepository.storeBet(bet)
                            send(bet)
                        }
                    }
                }
                .flattenMerge(100)
                .flowOn(Dispatchers.Default)
                .collectIndexed { i, _ ->
                    if (i % 10_000 == 0) {
                        logger.info("[MATCH] stored bet : $i")
                    }
                }
            logger.info("[MATCH] match $matchId started (with $nbBets bets)")
        }.also { logger.info("[MATCH] took ${it.inWholeMilliseconds} ms") }
        return matchId
    }

    suspend fun closeMatch(matchId: String) {
        rubyBetRepository.closeMatch(matchId)
        closedBetConsumer.launchFor(matchId)
    }

    fun stopClose(matchId: String) {
        closedBetConsumer.stopClose(matchId)
    }

    suspend fun isFullyPaid(matchId: String) =
        rubyBetRepository.getBetsForMatch(matchId, BetStatus.CLOSE)
            .firstOrNull() == null

}