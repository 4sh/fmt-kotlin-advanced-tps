package fmt.kotlin.advanced.payment

import fmt.kotlin.advanced.bet.RugbyBet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val logger: Logger = LoggerFactory.getLogger("FanOutJob")

// TODO step 4
// collect source channel to distribute each bet on different target channels (one after one)

fun CoroutineScope.launchFanOutJob(
    source: Channel<RugbyBet>,
    targets: List<Channel<RugbyBet>>,
) {
    logger.info("[FAN-OUT] Start fan-out job")
    launch {

    }
}
