package fmt.kotlin.advanced

import kotlin.uuid.Uuid

/**
 * Cette interface modélise un système externe qui réalise une transformation asynchrone, par exemple générer un PDF,
 * reformatter une image, etc.
 *
 * Pour cet exemple, on utilise une opération beaucoup plus simple : convertir un nombre en texte.
 *
 * Ce service externe fonctionne sur une base de **polling** :
 * 1. On envoie une requête avec [write], qui contient un identifiant unique
 * 2. On attend un résultat avec [read], qui contient l'identifiant
 *
 * Attention, le résultat obtenu avec [read] ne correspond pas nécessairement à la requête la plus récente envoyée avec [write].
 */
interface AsyncConverter {

    /**
     * Méthode d'action du service, doit tourner pendant toute la durée de vie du service.
     */
    suspend fun run()

    /**
     * Envoie une requête au service externe.
     *
     * La requête contient un identifiant, [RequestPayload.id], qui doit être unique.
     *
     * Quand le service a fini de traiter la requête, elle devient disponible avec [read].
     */
    suspend fun write(request: RequestPayload)

    data class RequestPayload(
        val id: Uuid,
        val data: Double,
    )

    /**
     * Lit le dernier résultat produit par le service externe.
     *
     * Chaque résultat contient un identifiant, [ResponsePayload.id], qui correspond à l'identifiant d'une requête
     * précédente passée à [write].
     *
     * Si le traitement de la requête a échoué, une exception [ConversionFailed] est levée, qui contient aussi
     * l'identifiant de la requête ([ConversionFailed.id]).
     */
    suspend fun read(): ResponsePayload

    data class ResponsePayload(
        val id: Uuid,
        val data: String,
    )

    class ConversionFailed(
        val id: Uuid,
        message: String,
    ) : RuntimeException("Conversion failed for $id: $message")
}
