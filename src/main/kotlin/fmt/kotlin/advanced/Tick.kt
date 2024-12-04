package fmt.kotlin.advanced

//
// !!!! NE PAS MODIFIER !!!!
//

data class Tick(
    val index: Int,
    val simulatedElapsedMs: Long,
    val physicalElapsedMs: Long,
    val actualElapsedMs: Long,
) {
    val lagInMsPerSecond: Double = (actualElapsedMs - physicalElapsedMs) / (physicalElapsedMs / 1000.0)
    val absoluteLagInMs: Long = actualElapsedMs - physicalElapsedMs
}
