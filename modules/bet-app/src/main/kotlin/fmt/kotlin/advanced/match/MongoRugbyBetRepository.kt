package fmt.kotlin.advanced.match

import com.mongodb.client.model.Filters.eq
import fmt.kotlin.advanced.bet.BetStatus
import fmt.kotlin.advanced.bet.RugbyBet
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.bson.types.ObjectId
import org.litote.kmongo.and
import org.litote.kmongo.combine
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.setValue

class MongoRugbyBetRepository(database: CoroutineDatabase, private val clock: Clock) {
    private val collection = database.getCollection<RugbyBet>()

    suspend fun storeBet(rugbyBet: RugbyBet) {
        collection.insertOne(rugbyBet)
    }

    suspend fun setBetAsPaid(betId: ObjectId, paidInstant: Instant) {
        collection.updateOne(
            eq("_id", betId),
            combine(
                setValue(RugbyBet::status, BetStatus.PAID),
                setValue(RugbyBet::paidInstant, paidInstant)
            )
        )
    }

    fun getBetsForMatch(matchId: String, status: BetStatus): Flow<RugbyBet> {
        return collection.find(
            and(
                RugbyBet::matchId eq matchId,
                RugbyBet::status eq status
            )
        ).toFlow()
    }

    suspend fun closeMatch(matchId: String) {
        collection.updateMany(
            and(
                RugbyBet::matchId eq matchId,
                RugbyBet::status eq BetStatus.OPEN
            ),
            combine(
                setValue(RugbyBet::status, BetStatus.CLOSE),
                setValue(RugbyBet::closeInstant, clock.now())
            )
        )
    }
}