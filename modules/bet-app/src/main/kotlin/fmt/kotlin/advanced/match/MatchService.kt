package fmt.kotlin.advanced.match

import fmt.kotlin.advanced.bet.BetGenerator
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import kotlin.time.measureTime

class MatchService(
    private val betGenerator: BetGenerator,
    private val rubyBetRepository: MongoRugbyBetRepository,
) {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    suspend fun startMatch(nbBets: Int): String {
        val matchId = UUID.randomUUID().toString()

        measureTime {
            logger.info("[MATCH] start match")
            betGenerator.generate(matchId)
                // TODO step 2
                // use 100 coroutines to improve performance
                .take(nbBets)
                .map { bet ->
                    rubyBetRepository.storeBet(bet)
                }
                .collectIndexed { i, _ ->
                    if (i % 10_000 == 0) {
                        logger.info("[MATCH] stored bet : $i")
                    }
                }
            logger.info("[MATCH] match $matchId started (with $nbBets bets)")
        }.also { logger.info("[MATCH] took ${it.inWholeMilliseconds} ms") }
        return matchId
    }
}