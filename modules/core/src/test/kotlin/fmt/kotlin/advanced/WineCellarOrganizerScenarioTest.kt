package fmt.kotlin.advanced

import fmt.kotlin.advanced.Color.RED
import fmt.kotlin.advanced.Magnum
import fmt.kotlin.advanced.Region.BORDEAUX
import fmt.kotlin.advanced.test.dsl.OrganizeWineCellar
import fmt.kotlin.advanced.test.dsl.from
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.properties.shouldHaveValue
import org.junit.jupiter.api.Test


class WineCellarOrganizerScenarioTest {

    @Test
    fun `should get next best bottle`() {
        val organizeWineCellar: WineCellarOrganizer<Magnum> = OrganizeWineCellar {
            // Given
           wineRack(4 by 10)
           wineRack(4 by 10)
           wineRack(4 by 10)

            store {
                +bottle {
                    region = BORDEAUX
                    rate = 10
                }
                +bottle {
                    region = BORDEAUX
                    name = "my best bottle"
                    rate = 18
                }
                +bottle {
                    region = BORDEAUX
                    rate = 10
                }
            }

            displayWinRacks()

            displayNextBest(RED from BORDEAUX)
        }

        // When
        val bottle = organizeWineCellar.takeBestBottleOf(RED, BORDEAUX)

        // Then
        bottle shouldNotBeNull {
            this::name shouldHaveValue "my best bottle"
        }
    }

}


