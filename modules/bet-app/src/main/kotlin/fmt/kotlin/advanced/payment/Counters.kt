package fmt.kotlin.advanced.payment

import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.CoroutineContext

data class Counters(
    val ok: AtomicInteger = AtomicInteger(0),
) : CoroutineContext.Element {
    override val key = Key

    companion object Key : CoroutineContext.Key<Counters>
}