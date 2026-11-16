package fmt.kotlin.advanced

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * Client qui expose une méthode [convert] qui cache toute la mécanique de [AsyncConverter] derrière une
 * simple méthode `suspend`.
 */
class Client(
    private val converter: AsyncConverter,
) {

    private class Request(
        val data: Double,
    )

    private val requests = Channel<Request>()

    /**
     * Cette méthode s'exécute pendant toute la durée de vie du client.
     *
     * Elle doit contenir tous les traitements.
     */
    suspend fun run(): Unit = coroutineScope {
        launch { converter.run() }

        for (request in requests) {
            TODO()
        }
    }

    /**
     * Cette méthode convertit [data] en chaîne de caractères, en appelant le [converter].
     */
    suspend fun convert(data: Double): String {
        TODO()
    }
}
