package fmt.kotlin.advanced

import kotlin.time.Duration
import kotlin.time.Duration.Companion.nanoseconds

fun Collection<Duration>.average(): Duration =
    (sumOf(Duration::inWholeNanoseconds) / size.toLong())
        .nanoseconds
