package fmt.kotlin.advanced.bet

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.math.BigDecimal
import java.util.*
import kotlin.random.Random

class BetGenerator {
    fun generate(matchId: String): Flow<RugbyBet> = flow {
        while (true) {
            /**
             * TODO step 1
             * generate bets
             */
        }

    }

    private fun generateRugbyBet(matchId: String): RugbyBet {
        val userId = Random.nextInt(1, 5000)
        val betAmount =
            BigDecimal("%.2f".format(Locale.US, Random.nextDouble(1.0, 100.0)))
        val winCondition = RugbyWinCondition.entries.toTypedArray().random()
        val betOdds = BigDecimal("%.2f".format(Locale.US, Random.nextDouble(1.0, 3.0)))
        val status = BetStatus.OPEN

        val rugbyBet = RugbyBet(null, userId.toString(), matchId, betAmount, winCondition, betOdds, status)
        return rugbyBet
    }
}
