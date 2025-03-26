package fmt.kotlin.advanced.bet

import kotlinx.datetime.Instant
import org.bson.codecs.pojo.annotations.BsonProperty
import org.bson.types.ObjectId
import java.math.BigDecimal

data class RugbyBet(
    @BsonProperty("_id")
    val id: ObjectId? = null,
    val userId: String,
    val matchId: String,
    val betAmount: BigDecimal,
    val winCondition: RugbyWinCondition,
    val betOdds: BigDecimal,
    val status: BetStatus,
    val openInstant: Instant? = null,
    val closeInstant: Instant? = null,
    val paidInstant: Instant? = null,
)

enum class BetStatus {
    OPEN,
    CLOSE,
    PAID
}

enum class RugbyWinCondition {
    VISITOR_WIN,
    HOME_WIN,
}