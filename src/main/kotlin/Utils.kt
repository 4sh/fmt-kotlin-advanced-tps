package fmt.kotlin.advanced

import kotlinx.coroutines.*

/**
 * Cette fonction est **la** bonne manière de lancer un service qui contient des actions par coroutines.
 *
 * Les coroutines sont principalement voulues pour être utilisées dans des _fonctions_.
 * Attacher le cycle de vie d'une _classe_ aux mécanismes des coroutines est très subtil si on veut être sûr que tout se comporte correctement.
 *
 * Aujourd'hui, cette fonction est la manière connue la plus simple pour instancier une classe qui contient une `CoroutineScope`.
 * **Tout autre manière est très probablement subtilement buggée.**
 *
 * ### Utilisation
 *
 * Créer une classe qui utilise des coroutines :
 * ```kotlin
 * class MonService {
 *
 *     suspend fun run(): Unit = coroutineScope {
 *         // …les actions du service…
 *         // Par exemple, ping une base de données toutes les 10 secondes
 *     }
 * }
 * ```
 *
 * Pour instancier ce service :
 * ```kotlin
 * suspend fun main() {
 *     val service = MonService()
 *     withService { service.run() } {
 *         // À l'intérieur, on peut utiliser le service
 *     }
 *     // Ici, le service a été arrêté
 * }
 * ```
 *
 * [Plus d'informations](https://slack-chats.kotlinlang.org/t/36516376/what-are-your-approaches-to-encapsulate-a-class-that-perform#47e85650-a931-4883-8c56-9adfccc43743)
 */
suspend fun <T> withService(
    keepAlive: suspend CoroutineScope.() -> Unit,
    usage: suspend CoroutineScope.() -> T,
): T = coroutineScope {
    /*
     * On commence par ouvrir une coroutine scope, qu'on va appeler "extérieure".
     * Elle va encapsuler la durée de vie totale de l'action du service.
     */

    /*
     * On crée un nouveau job, qui exécute l'action du service.
     * Ce job n'a volontairement pas de parent, on est donc responsables de gérer l'annulation et les erreurs
     * pour garantir la structured concurrency : par défaut, toute erreur disparait dans la nature.
     * Pour cela, on utilise le fait que 'async' renvoie toutes ses exceptions via le 'await'.
     */
    val untrackedJob = CoroutineScope(coroutineContext.minusKey(Job)).async {
        keepAlive()
    }

    /*
     * On lance une coroutine qui a comme seul rôle d'attendre que l'action du service soit terminée, ce qui
     * arrive quand :
     * • Elle termine normalement.
     * • Elle est annulée.
     * • Elle jette une exception.
     *
     * Puisque l'action du service est dans un job sans parent, dans chacun de ces cas on doit rebrancher
     * le comportement comme si le job était un enfant de la coroutine qui a appelé cette fonction.
     *
     * Comme cette coroutine ne va rien faire d'autre qu'attendre, elle ne va jamais être exécutée
     * (sauf la toute première fois pour atteindre le point de suspension). Comme optimisation, on utilise
     * CoroutineStart.UNDISPATCHED pour éviter de changer de thread; on exécute les deux lignes suivantes immédiatement.
     */
    val keepAliveWatchdog = launch(start = CoroutineStart.UNDISPATCHED) {

        /*
         * On veut être sûrs que l'action du service ne termine qu'après l'utilisation.
         * Sinon, on risquerait que le service soit annulé (et arrête de fonctionner) alors que l'utilisation
         * en a encore besoin pour gérer sa propre annulation (par exemple : utilisation du service dans le bloc `finally`).
         *
         * Cette coroutine, qui est lancée comme enfant de la coroutine qui appelé cette fonction, refuse
         * donc d'être annulée, pour être absolument sûr que l'action termine ensuite.
         */
        withContext(NonCancellable) {

            /*
             * On attend que l'action du service soit terminée.
             *
             * Si l'action termine normalement : cette coroutine termine. Le service n'est plus en fonctionnement
             * alors que l'utilisation n'a potentiellement pas terminé. Des messages risquent d'être perdus !
             *
             * Si l'action est annulée : cette coroutine termine.
             *
             * Si l'action échoue : cette coroutine termine et l'exception remonte, annulant l'utilisation.
             */
            untrackedJob.await()
        }
    }

    try {
        coroutineScope {
            keepAliveWatchdog.invokeOnCompletion {
                if (it == null)
                    this.cancel("The service action finished successfully before the usage was done")
            }

            usage()
        }
    } finally {
        /**
         * Quand l'utilisation est terminée (quelle que soit la raison), le service n'est plus nécessaire et
         * peut être terminé.
         */
        untrackedJob.cancel()
    }
}
