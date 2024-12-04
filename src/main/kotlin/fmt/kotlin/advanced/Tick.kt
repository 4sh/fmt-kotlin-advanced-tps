package fmt.kotlin.advanced

import kotlin.time.Duration

//
// !!!! NE PAS MODIFIER !!!!
//

data class Tick(
    /**
     * Le nombre de ticks générés par la [SimuClock] avant celui-ci.
     */
    val index: Int,

    /**
     * Le temps de simulation théorique écoulé depuis le début de la simulation.
     *
     * Dépend du temps de cycle de simulation de l'horloge et de l'index du tick :
     * ```
     * index * timeBetweenTicks
     * ```
     */
    val expectedElapsed: Duration,

    /**
     * Le temps de simulation mesuré (réel) écoulé depuis le début de la simulation.
     *
     * Cette valeur ne peut pas être inférieure à [expectedElapsed] si l'horloge est implémentée correctement.
     * En revanche, elle peut être supérieure à [expectedElapsed] si l'horloge prend du retard sur le rythme théorique.
     */
    val actualElapsed: Duration,
) {

    /**
     * La différence totale entre le temps réel écoulé et le temps théorique écoulé.
     *
     * Cette valeur représente le temps supplémentaire qui a été nécessaire pour réaliser la simulation,
     * c'est-à-dire le temps perdu dans la machinerie des threads, etc.
     */
    val totalLag: Duration = actualElapsed - expectedElapsed

    /**
     * La différence moyenne par seconde entre le temps réel écoulé et le temps théorique écoulé.
     *
     * Cette valeur représente le temps supplémentaire qui a été nécessaire pour réaliser la simulation,
     * ramené à chaque seconde de simulation.
     *
     * C'est-à-dire, le temps perdu par seconde dans la machinerie des threads, etc.
     */
    val lagPerSecond: Duration = totalLag / (expectedElapsed.inWholeMicroseconds / 1_000_000.0)
}
