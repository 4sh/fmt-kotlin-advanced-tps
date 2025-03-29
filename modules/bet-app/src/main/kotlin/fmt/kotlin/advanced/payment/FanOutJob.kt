package fmt.kotlin.advanced.payment

import fmt.kotlin.advanced.bet.RugbyBet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val logger: Logger = LoggerFactory.getLogger("FanOutJob")

fun CoroutineScope.launchFanOutJob(
    source: Channel<RugbyBet>,
    targets: List<Channel<RugbyBet>>,
) {
    logger.info("[FAN-OUT] Start fan-out job")
    launch {
        var nextChannel = 0
        source.receiveAsFlow()
            .collect { bet ->
                targets[nextChannel++].send(bet)
                nextChannel = if (nextChannel >= targets.size) {
                    0
                } else {
                    nextChannel
                }
            }
    }
}
