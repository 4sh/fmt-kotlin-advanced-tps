package fmt.kotlin.advanced

import fmt.kotlin.advanced.Color.*
import fmt.kotlin.advanced.*
import fmt.kotlin.advanced.Region.*
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class WineCellarOrganizerTest {

    fun commonRedBottle() = Bottle("Château Beau Rivage", 2012, BORDEAUX, RED, 1, null)
    fun goodRedBottle() = Bottle("Château Saint-Pierre", 2016, BORDEAUX, RED, 15, null)
    fun bestRedBottle() = Bottle("Château Latour", 2012, BORDEAUX, RED, 18, null)
    fun redBottleToKeep() = Bottle("Château Meyney", 2018, BORDEAUX, RED, 19, keepUntil = 2042)

    fun commonPinkBottle() = Bottle("Mouton Cadet", 2023, BORDEAUX, PINK, 14, null)
    fun goodPinkBottle() = Bottle("Château Saint Catherine", 2023, BORDEAUX, PINK, 15, null)
    fun bestPinkBottle() = Bottle("Château Malagar", 2020, BORDEAUX, PINK, 18, null)
    fun pinkBottleToKeep() = Bottle("Château Simone", 2023, BORDEAUX, PINK, 19, keepUntil = 2033)


    fun commonWhiteBottle() = Bottle("Château Les Maubats", 2022, BORDEAUX, WHITE, 14, null)
    fun goodWhiteBottle() = Bottle("Château Suau", 2020, BORDEAUX, WHITE, 15, null)
    fun bestWhiteBottle() = Bottle("Château d'Yquem", 2005, BORDEAUX, WHITE, 18, null)
    fun whiteBottleToKeep() = Bottle("Château Meyney", 2018, BORDEAUX, WHITE, 19, keepUntil = 2042)


    @Nested
    inner class `with racks having sufficent space` {

        val wineOrganizer = WineCellarOrganizer(3 to (Capacity(4, 6)))

        @Test
        fun `a stored bottle should can be viewed`() {
            // Given
            val commonRedBottle = commonRedBottle()
            wineOrganizer.storeBottle(commonRedBottle)

            // When
            val viewedBottle = wineOrganizer.viewCommonBottleOf(commonRedBottle.color, BORDEAUX)

            // Then
            println(wineOrganizer.viewWineRackOf(BORDEAUX))
            viewedBottle shouldBeSameInstanceAs commonRedBottle
            wineOrganizer.viewCommonBottleOf(commonRedBottle.color, BORDEAUX) shouldNotBe null
        }

        @Test
        fun `a stored bottle should can be taken`() {
            // Given
            val commonRedBottle = commonRedBottle()
            wineOrganizer.storeBottle(commonRedBottle)

            // When
            val takenBottle = wineOrganizer.takeCommonBottleOf(commonRedBottle.color, BORDEAUX)

            // Then
            println(wineOrganizer.viewWineRackOf(BORDEAUX))
            takenBottle shouldBeSameInstanceAs commonRedBottle
            wineOrganizer.viewCommonBottleOf(commonRedBottle.color, BORDEAUX) shouldBeSameInstanceAs null
        }

        @Nested
        inner class `a common bottle` {

            @Test
            fun `of red wine should be stored from the beginning on second shelf`() {
                // Given
                val commonRedBottle1 = commonRedBottle()
                val commonRedBottle2 = commonRedBottle()

                // When
                wineOrganizer.storeBottle(commonRedBottle1)
                wineOrganizer.storeBottle(commonRedBottle2)

                // Then
                val wineRack = wineOrganizer.viewWineRackOf(BORDEAUX)
                println(wineRack)
                wineRack shouldNotBeNull {
                    getAt(1, 0) shouldBeSameInstanceAs commonRedBottle1
                    getAt(1, 1) shouldBeSameInstanceAs commonRedBottle2
                }
            }

            @Test
            fun `of pink wine should be stored from the beginning on third shelf`() {
                // Given
                val commonPinkBottle1 = commonPinkBottle()
                val commonPinkBottle2 = commonPinkBottle()

                // When
                wineOrganizer.storeBottle(commonPinkBottle1)
                wineOrganizer.storeBottle(commonPinkBottle2)

                // Then
                val wineRack = wineOrganizer.viewWineRackOf(BORDEAUX)
                println(wineRack)
                wineRack shouldNotBeNull {
                    getAt(2, 0) shouldBeSameInstanceAs commonPinkBottle1
                    getAt(2, 1) shouldBeSameInstanceAs commonPinkBottle2
                }
            }

            @Test
            fun `of white wine should be stored from the beginning on fourth shelf`() {
                // Given
                val commonWhiteBottle1 = commonWhiteBottle()
                val commonWhiteBottle2 = commonWhiteBottle()

                // When
                wineOrganizer.storeBottle(commonWhiteBottle1)
                wineOrganizer.storeBottle(commonWhiteBottle2)

                // Then
                val wineRack = wineOrganizer.viewWineRackOf(BORDEAUX)
                println(wineRack)
                wineRack shouldNotBeNull {
                    getAt(3, 0) shouldBeSameInstanceAs commonWhiteBottle1
                    getAt(3, 1) shouldBeSameInstanceAs commonWhiteBottle2
                }
            }
        }

        @Nested
        inner class `a good bottle` {
            @Test
            fun `of red wine should be stored from middle on second shelf`() {
                // Given
                val goodRedBottle1 = goodRedBottle()
                val goodRedBottle2 = goodRedBottle()

                // When
                wineOrganizer.storeBottle(goodRedBottle1)
                wineOrganizer.storeBottle(goodRedBottle2)

                // Then
                val wineRack = wineOrganizer.viewWineRackOf(BORDEAUX)
                println(wineRack)
                wineRack shouldNotBe null
                wineRack shouldNotBeNull {
                    getAt(1, 3) shouldBeSameInstanceAs goodRedBottle1
                    getAt(1, 4) shouldBeSameInstanceAs goodRedBottle2
                }
            }

            @Test
            fun `of pink wine should be stored from middle on third shelf`() {
                // Given
                val goodPinkBottle1 = goodPinkBottle()
                val goodPinkBottle2 = goodPinkBottle()

                // When
                wineOrganizer.storeBottle(goodPinkBottle1)
                wineOrganizer.storeBottle(goodPinkBottle2)

                // Then
                val wineRack = wineOrganizer.viewWineRackOf(BORDEAUX)
                println(wineRack)
                wineRack shouldNotBe null
                wineRack shouldNotBeNull {
                    getAt(2, 3) shouldBeSameInstanceAs goodPinkBottle1
                    getAt(2, 4) shouldBeSameInstanceAs goodPinkBottle2
                }
            }

            @Test
            fun `of white wine should be stored from the middle on fourth shelf`() {
                // Given
                val goodWhiteBottle1 = goodWhiteBottle()
                val goodWhiteBottle2 = goodWhiteBottle()

                // When
                wineOrganizer.storeBottle(goodWhiteBottle1)
                wineOrganizer.storeBottle(goodWhiteBottle2)

                // Then
                val wineRack = wineOrganizer.viewWineRackOf(BORDEAUX)
                println(wineRack)
                wineRack shouldNotBe null
                wineRack shouldNotBeNull {
                    getAt(3, 3) shouldBeSameInstanceAs goodWhiteBottle1
                    getAt(3, 4) shouldBeSameInstanceAs goodWhiteBottle2
                }
            }
        }

        @Nested
        inner class `a best bottle` {

            @Test
            fun `of red wine should be stored on first shelf`() {
                // Given
                val goodWhiteBottle1 = bestRedBottle()
                val goodWhiteBottle2 = bestRedBottle()

                // When
                wineOrganizer.storeBottle(goodWhiteBottle1)
                wineOrganizer.storeBottle(goodWhiteBottle2)

                // Then
                val wineRack = wineOrganizer.viewWineRackOf(BORDEAUX)
                println(wineRack)
                wineRack shouldNotBe null
                wineRack shouldNotBeNull {
                    getAt(0, 0) shouldBeSameInstanceAs goodWhiteBottle1
                    getAt(0, 1) shouldBeSameInstanceAs goodWhiteBottle2
                }
            }

            @Test
            fun `of pink wine should be stored on first shelf`() {
                // Given
                val bestPinkBottle1 = bestPinkBottle()
                val bestPinkBottle2 = bestPinkBottle()

                // When
                wineOrganizer.storeBottle(bestPinkBottle1)
                wineOrganizer.storeBottle(bestPinkBottle2)

                // Then
                val wineRack = wineOrganizer.viewWineRackOf(BORDEAUX)
                println(wineRack)
                wineRack shouldNotBe null
                wineRack shouldNotBeNull {
                    getAt(0, 0) shouldBeSameInstanceAs bestPinkBottle1
                    getAt(0, 1) shouldBeSameInstanceAs bestPinkBottle2
                }
            }

            @Test
            fun `of white wine should be stored on first shelf`() {
                // Given
                val bestWhiteBottle1 = bestWhiteBottle()
                val bestWhiteBottle2 = bestWhiteBottle()

                // When
                wineOrganizer.storeBottle(bestWhiteBottle1)
                wineOrganizer.storeBottle(bestWhiteBottle2)

                // Then
                val wineRack = wineOrganizer.viewWineRackOf(BORDEAUX)
                println(wineRack)
                wineRack shouldNotBe null
                wineRack shouldNotBeNull {
                    getAt(0, 0) shouldBeSameInstanceAs bestWhiteBottle1
                    getAt(0, 1) shouldBeSameInstanceAs bestWhiteBottle2
                }
            }

        }

        @Nested
        inner class `a bottle to keep` {
            @Test
            fun `of red wine should be stored on second shelf by the end`() {
                // Given
                val redBottleToKeep1 = redBottleToKeep()
                val redBottleToKeep2 = redBottleToKeep()

                // When
                wineOrganizer.storeBottle(redBottleToKeep1)
                wineOrganizer.storeBottle(redBottleToKeep2)

                // Then
                val wineRack = wineOrganizer.viewWineRackOf(BORDEAUX)
                println(wineRack)
                wineRack shouldNotBe null
                wineRack shouldNotBeNull {
                    getAt(1, 5) shouldBeSameInstanceAs redBottleToKeep1
                    getAt(1, 4) shouldBeSameInstanceAs redBottleToKeep2
                }
            }

            @Test
            fun `of pink wine should be stored on third shelf by the end`() {
                // Given
                val pinkBottleToKeep1 = pinkBottleToKeep()
                val pinkBottleToKeep2 = pinkBottleToKeep()

                // When
                wineOrganizer.storeBottle(pinkBottleToKeep1)
                wineOrganizer.storeBottle(pinkBottleToKeep2)

                // Then
                val wineRack = wineOrganizer.viewWineRackOf(BORDEAUX)
                println(wineRack)
                wineRack shouldNotBe null
                wineRack shouldNotBeNull {
                    getAt(2, 5) shouldBeSameInstanceAs pinkBottleToKeep1
                    getAt(2, 4) shouldBeSameInstanceAs pinkBottleToKeep2
                }
            }

            @Test
            fun `of white wine should be stored on fourth shelf by the end`() {
                // Given
                val whiteBottleToKeep1 = whiteBottleToKeep()
                val whiteBottleToKeep2 = whiteBottleToKeep()

                // When
                wineOrganizer.storeBottle(whiteBottleToKeep1)
                wineOrganizer.storeBottle(whiteBottleToKeep2)

                // Then
                val wineRack = wineOrganizer.viewWineRackOf(BORDEAUX)
                println(wineRack)
                wineRack shouldNotBe null
                wineRack shouldNotBeNull {
                    getAt(3, 5) shouldBeSameInstanceAs whiteBottleToKeep1
                    getAt(3, 4) shouldBeSameInstanceAs whiteBottleToKeep2
                }
            }
        }

        @Nested
        inner class `fill with` {
            @Test
            fun `common bottles should occupy the first half`() {
                // Given
                val commonRedBottle = commonRedBottle()

                // When
                wineOrganizer.storeBottle(commonRedBottle)
                wineOrganizer.storeBottle(commonRedBottle)
                wineOrganizer.storeBottle(commonRedBottle)


                // Then
                println(wineOrganizer.viewWineRackOf(BORDEAUX))
                wineOrganizer.viewWineRackOf(BORDEAUX) shouldNotBeNull {
                    getAt(1) shouldContainExactly listOf(
                        commonRedBottle,
                        commonRedBottle,
                        commonRedBottle,
                        null,
                        null,
                        null
                    )
                }
            }

            @Test
            fun `good bottles should occupy the second half`() {
                // Given
                val goodRedBottle = goodRedBottle()

                // When
                wineOrganizer.storeBottle(goodRedBottle)
                wineOrganizer.storeBottle(goodRedBottle)
                wineOrganizer.storeBottle(goodRedBottle)


                // Then
                println(wineOrganizer.viewWineRackOf(BORDEAUX))
                wineOrganizer.viewWineRackOf(BORDEAUX) shouldNotBeNull {
                    getAt(1) shouldContainExactly listOf(null, null, null, goodRedBottle, goodRedBottle, goodRedBottle)
                }
            }

            @Test
            fun `to keep bottles should occupy the second half`() {
                // Given
                val redBottleToKeep = redBottleToKeep()

                // When
                wineOrganizer.storeBottle(redBottleToKeep)
                wineOrganizer.storeBottle(redBottleToKeep)
                wineOrganizer.storeBottle(redBottleToKeep)

                // Then
                println(wineOrganizer.viewWineRackOf(BORDEAUX))
                wineOrganizer.viewWineRackOf(BORDEAUX) shouldNotBeNull {
                    getAt(1) shouldContainExactly listOf(
                        null,
                        null,
                        null,
                        redBottleToKeep,
                        redBottleToKeep,
                        redBottleToKeep
                    )
                }
            }
        }

        @Nested
        inner class `insufficient space with ` {


            @Test
            fun `a common red bottle should not be storede`() {
                // Given
                val commonRedBottle = commonRedBottle()

                wineOrganizer.storeBottle(commonRedBottle)
                wineOrganizer.storeBottle(commonRedBottle)
                wineOrganizer.storeBottle(commonRedBottle)

                // When // Then
                shouldThrow<InsufficientSpace> {
                    wineOrganizer.storeBottle(commonRedBottle)
                    println(wineOrganizer.viewWineRackOf(BORDEAUX))
                }
            }

            @Test
            fun `a good red bottle should not be stored`() {
                // Given
                val goodRedBottle = goodRedBottle()

                wineOrganizer.storeBottle(goodRedBottle)
                wineOrganizer.storeBottle(goodRedBottle)
                wineOrganizer.storeBottle(goodRedBottle)

                // When // Then
                shouldThrow<InsufficientSpace> {
                    wineOrganizer.storeBottle(goodRedBottle)
                    println(wineOrganizer.viewWineRackOf(BORDEAUX))
                }

            }

            @Test
            fun `a best red bottle should not be stored`() {
                // Given
                val bestRedBottle = bestRedBottle()

                wineOrganizer.storeBottle(bestRedBottle)
                wineOrganizer.storeBottle(bestRedBottle)
                wineOrganizer.storeBottle(bestRedBottle)
                wineOrganizer.storeBottle(bestRedBottle)
                wineOrganizer.storeBottle(bestRedBottle)
                wineOrganizer.storeBottle(bestRedBottle)

                // When // Then
                shouldThrow<InsufficientSpace> {
                    wineOrganizer.storeBottle(bestRedBottle)
                    println(wineOrganizer.viewWineRackOf(BORDEAUX))
                }

            }

            @Test
            fun `a red bottle to keep should not be stored`() {
                // Given
                val redBottleToKeep = redBottleToKeep()

                wineOrganizer.storeBottle(redBottleToKeep)
                wineOrganizer.storeBottle(redBottleToKeep)
                wineOrganizer.storeBottle(redBottleToKeep)

                // When // Then
                shouldThrow<InsufficientSpace> {
                    wineOrganizer.storeBottle(redBottleToKeep)
                    println(wineOrganizer.viewWineRackOf(BORDEAUX))
                }
            }
        }

        @Nested
        inner class `take` {
            @Test
            fun `common bottles should be got by head of color shelf`() {
                // Given
                val commonRedBottle1 = commonRedBottle()
                val commonRedBottle2 = commonRedBottle()
                wineOrganizer.storeBottle(commonRedBottle1)
                wineOrganizer.storeBottle(bestRedBottle())
                wineOrganizer.storeBottle(redBottleToKeep())
                wineOrganizer.storeBottle(goodRedBottle())
                wineOrganizer.storeBottle(commonRedBottle2)
                println(wineOrganizer.viewWineRackOf(BORDEAUX))

                // When
                val firstTaken = wineOrganizer.takeCommonBottleOf(RED, BORDEAUX)
                val secondTaken = wineOrganizer.takeCommonBottleOf(RED, BORDEAUX)


                // Then
                println(wineOrganizer.viewWineRackOf(BORDEAUX))
                firstTaken shouldBeSameInstanceAs commonRedBottle1
                secondTaken shouldBeSameInstanceAs commonRedBottle2
                wineOrganizer.viewWineRackOf(BORDEAUX)?.numberOfBottles shouldBe 3
            }

            @Test
            fun `good bottles should be got by middle of color shelf`() {
                // Given
                val goodRedBottle1 = goodRedBottle()
                val goodRedBottle2 = goodRedBottle()
                wineOrganizer.storeBottle(goodRedBottle1)
                wineOrganizer.storeBottle(commonRedBottle())
                wineOrganizer.storeBottle(redBottleToKeep())
                wineOrganizer.storeBottle(bestRedBottle())
                wineOrganizer.storeBottle(goodRedBottle2)
                println(wineOrganizer.viewWineRackOf(BORDEAUX))

                // When
                val firstTaken = wineOrganizer.takeGoodBottleOf(RED, BORDEAUX)
                val secondTaken = wineOrganizer.takeGoodBottleOf(RED, BORDEAUX)

                // Then
                println(wineOrganizer.viewWineRackOf(BORDEAUX))
                firstTaken shouldBeSameInstanceAs goodRedBottle1
                secondTaken shouldBeSameInstanceAs goodRedBottle2
                wineOrganizer.viewWineRackOf(BORDEAUX)?.numberOfBottles shouldBe 3
            }

            @Test
            fun `best bottles should be got by middle of color shelf`() {
                // Given
                val bestRedBottle1 = bestRedBottle()
                val bestRedBottle2 = bestRedBottle()
                wineOrganizer.storeBottle(bestRedBottle1)
                wineOrganizer.storeBottle(commonRedBottle())
                wineOrganizer.storeBottle(redBottleToKeep())
                wineOrganizer.storeBottle(goodRedBottle())
                wineOrganizer.storeBottle(bestRedBottle2)
                println(wineOrganizer.viewWineRackOf(BORDEAUX))

                // When
                val firstTaken = wineOrganizer.takeBestBottleOf(RED, BORDEAUX)
                val secondTaken = wineOrganizer.takeBestBottleOf(RED, BORDEAUX)

                // Then
                println(wineOrganizer.viewWineRackOf(BORDEAUX))
                firstTaken shouldBeSameInstanceAs bestRedBottle1
                secondTaken shouldBeSameInstanceAs bestRedBottle2
                wineOrganizer.viewWineRackOf(BORDEAUX)?.numberOfBottles shouldBe 3
            }
        }
    }

    @Nested
    inner class `with racks having unique shelf` {

        val wineOrganizer = WineCellarOrganizer(3 to Capacity(1, 6))

        @Nested
        inner class `a common bottle` {

            @Test
            fun `of red wine should be stored from the beginning on first shelf`() {
                // Given
                val commonRedBottle1 = commonRedBottle()
                val commonRedBottle2 = commonRedBottle()

                // When
                wineOrganizer.storeBottle(commonRedBottle1)
                wineOrganizer.storeBottle(commonRedBottle2)

                // Then
                val wineRack = wineOrganizer.viewWineRackOf(BORDEAUX)
                println(wineRack)
                wineRack shouldNotBeNull {
                    getAt(0, 0) shouldBeSameInstanceAs commonRedBottle1
                    getAt(0, 1) shouldBeSameInstanceAs commonRedBottle2
                }
            }

            @Test
            fun `of pink wine should be stored from the beginning on first shelf`() {
                // Given
                val commonPinkBottle1 = commonPinkBottle()
                val commonPinkBottle2 = commonPinkBottle()

                // When
                wineOrganizer.storeBottle(commonPinkBottle1)
                wineOrganizer.storeBottle(commonPinkBottle2)

                // Then
                val wineRack = wineOrganizer.viewWineRackOf(BORDEAUX)
                println(wineRack)
                wineRack shouldNotBeNull {
                    getAt(0, 0) shouldBeSameInstanceAs commonPinkBottle1
                    getAt(0, 1) shouldBeSameInstanceAs commonPinkBottle2
                }
            }

            @Test
            fun `of white wine should be stored from the beginning on first shelf`() {
                // Given
                val commonWhiteBottle1 = commonWhiteBottle()
                val commonWhiteBottle2 = commonWhiteBottle()

                // When
                wineOrganizer.storeBottle(commonWhiteBottle1)
                wineOrganizer.storeBottle(commonWhiteBottle2)

                // Then
                val wineRack = wineOrganizer.viewWineRackOf(BORDEAUX)
                println(wineRack)
                wineRack shouldNotBeNull {
                    getAt(0, 0) shouldBeSameInstanceAs commonWhiteBottle1
                    getAt(0, 1) shouldBeSameInstanceAs commonWhiteBottle2
                }
            }
        }

        @Nested
        inner class `a good bottle` {
            @Test
            fun `of red wine should be stored from middle on second shelf`() {
                // Given
                val goodRedBottle1 = goodRedBottle()
                val goodRedBottle2 = goodRedBottle()

                // When
                wineOrganizer.storeBottle(goodRedBottle1)
                wineOrganizer.storeBottle(goodRedBottle2)

                // Then
                val wineRack = wineOrganizer.viewWineRackOf(BORDEAUX)
                println(wineRack)
                wineRack shouldNotBe null
                wineRack shouldNotBeNull {
                    getAt(0, 3) shouldBeSameInstanceAs goodRedBottle1
                    getAt(0, 4) shouldBeSameInstanceAs goodRedBottle2
                }
            }

            @Test
            fun `of pink wine should be stored from middle on third shelf`() {
                // Given
                val goodPinkBottle1 = goodPinkBottle()
                val goodPinkBottle2 = goodPinkBottle()

                // When
                wineOrganizer.storeBottle(goodPinkBottle1)
                wineOrganizer.storeBottle(goodPinkBottle2)

                // Then
                val wineRack = wineOrganizer.viewWineRackOf(BORDEAUX)
                println(wineRack)
                wineRack shouldNotBe null
                wineRack shouldNotBeNull {
                    getAt(0, 3) shouldBeSameInstanceAs goodPinkBottle1
                    getAt(0, 4) shouldBeSameInstanceAs goodPinkBottle2
                }
            }

            @Test
            fun `of white wine should be stored from the middle on fourth shelf`() {
                // Given
                val goodWhiteBottle1 = goodWhiteBottle()
                val goodWhiteBottle2 = goodWhiteBottle()

                // When
                wineOrganizer.storeBottle(goodWhiteBottle1)
                wineOrganizer.storeBottle(goodWhiteBottle2)

                // Then
                val wineRack = wineOrganizer.viewWineRackOf(BORDEAUX)
                println(wineRack)
                wineRack shouldNotBe null
                wineRack shouldNotBeNull {
                    getAt(0, 3) shouldBeSameInstanceAs goodWhiteBottle1
                    getAt(0, 4) shouldBeSameInstanceAs goodWhiteBottle2
                }
            }
        }

        @Nested
        inner class `a best bottle` {

            @Test
            fun `of red wine should be stored on first shelf`() {
                // Given
                val goodWhiteBottle1 = bestRedBottle()
                val goodWhiteBottle2 = bestRedBottle()

                // When
                wineOrganizer.storeBottle(goodWhiteBottle1)
                wineOrganizer.storeBottle(goodWhiteBottle2)

                // Then
                val wineRack = wineOrganizer.viewWineRackOf(BORDEAUX)
                println(wineRack)
                wineRack shouldNotBe null
                wineRack shouldNotBeNull {
                    getAt(0, 3) shouldBeSameInstanceAs goodWhiteBottle1
                    getAt(0, 4) shouldBeSameInstanceAs goodWhiteBottle2
                }
            }

            @Test
            fun `of pink wine should be stored on first shelf`() {
                // Given
                val bestPinkBottle1 = bestPinkBottle()
                val bestPinkBottle2 = bestPinkBottle()

                // When
                wineOrganizer.storeBottle(bestPinkBottle1)
                wineOrganizer.storeBottle(bestPinkBottle2)

                // Then
                val wineRack = wineOrganizer.viewWineRackOf(BORDEAUX)
                println(wineRack)
                wineRack shouldNotBe null
                wineRack shouldNotBeNull {
                    getAt(0, 3) shouldBeSameInstanceAs bestPinkBottle1
                    getAt(0, 4) shouldBeSameInstanceAs bestPinkBottle2
                }
            }

            @Test
            fun `of white wine should be stored on first shelf`() {
                // Given
                val bestWhiteBottle1 = bestWhiteBottle()
                val bestWhiteBottle2 = bestWhiteBottle()

                // When
                wineOrganizer.storeBottle(bestWhiteBottle1)
                wineOrganizer.storeBottle(bestWhiteBottle2)

                // Then
                val wineRack = wineOrganizer.viewWineRackOf(BORDEAUX)
                println(wineRack)
                wineRack shouldNotBe null
                wineRack shouldNotBeNull {
                    getAt(0, 3) shouldBeSameInstanceAs bestWhiteBottle1
                    getAt(0, 4) shouldBeSameInstanceAs bestWhiteBottle2
                }
            }

        }

        @Nested
        inner class `a bottle to keep` {
            @Test
            fun `of red wine should be stored on first shelf by the end`() {
                // Given
                val redBottleToKeep1 = redBottleToKeep()
                val redBottleToKeep2 = redBottleToKeep()

                // When
                wineOrganizer.storeBottle(redBottleToKeep1)
                wineOrganizer.storeBottle(redBottleToKeep2)

                // Then
                val wineRack = wineOrganizer.viewWineRackOf(BORDEAUX)
                println(wineRack)
                wineRack shouldNotBe null
                wineRack shouldNotBeNull {
                    getAt(0, 5) shouldBeSameInstanceAs redBottleToKeep1
                    getAt(0, 4) shouldBeSameInstanceAs redBottleToKeep2
                }
            }

            @Test
            fun `of pink wine should be stored on first shelf by the end`() {
                // Given
                val pinkBottleToKeep1 = pinkBottleToKeep()
                val pinkBottleToKeep2 = pinkBottleToKeep()

                // When
                wineOrganizer.storeBottle(pinkBottleToKeep1)
                wineOrganizer.storeBottle(pinkBottleToKeep2)

                // Then
                val wineRack = wineOrganizer.viewWineRackOf(BORDEAUX)
                println(wineRack)
                wineRack shouldNotBe null
                wineRack shouldNotBeNull {
                    getAt(0, 5) shouldBeSameInstanceAs pinkBottleToKeep1
                    getAt(0, 4) shouldBeSameInstanceAs pinkBottleToKeep2
                }
            }

            @Test
            fun `of white wine should be stored on fourth shelf by the end`() {
                // Given
                val whiteBottleToKeep1 = whiteBottleToKeep()
                val whiteBottleToKeep2 = whiteBottleToKeep()

                // When
                wineOrganizer.storeBottle(whiteBottleToKeep1)
                wineOrganizer.storeBottle(whiteBottleToKeep2)

                // Then
                val wineRack = wineOrganizer.viewWineRackOf(BORDEAUX)
                println(wineRack)
                wineRack shouldNotBe null
                wineRack shouldNotBeNull {
                    getAt(0, 5) shouldBeSameInstanceAs whiteBottleToKeep1
                    getAt(0, 4) shouldBeSameInstanceAs whiteBottleToKeep2
                }
            }
        }

        @Nested
        inner class `take` {
            @Test
            fun `common bottles should be got by head of color shelf`() {
                // Given
                val commonRedBottle1 = commonRedBottle()
                val commonRedBottle2 = commonRedBottle()
                wineOrganizer.storeBottle(commonRedBottle1)
                wineOrganizer.storeBottle(bestRedBottle())
                wineOrganizer.storeBottle(redBottleToKeep())
                wineOrganizer.storeBottle(goodRedBottle())
                wineOrganizer.storeBottle(commonRedBottle2)
                println(wineOrganizer.viewWineRackOf(BORDEAUX))

                // When
                val firstTaken = wineOrganizer.takeCommonBottleOf(RED, BORDEAUX)
                val secondTaken = wineOrganizer.takeCommonBottleOf(RED, BORDEAUX)


                // Then
                println(wineOrganizer.viewWineRackOf(BORDEAUX))
                firstTaken shouldBeSameInstanceAs commonRedBottle1
                secondTaken shouldBeSameInstanceAs commonRedBottle2
                wineOrganizer.viewWineRackOf(BORDEAUX)?.numberOfBottles shouldBe 3
            }

            @Test
            fun `good bottles should be got by middle of color shelf`() {
                // Given
                val goodRedBottle1 = goodRedBottle()
                val goodRedBottle2 = goodRedBottle()
                wineOrganizer.storeBottle(goodRedBottle1)
                wineOrganizer.storeBottle(commonRedBottle())
                wineOrganizer.storeBottle(goodRedBottle2)
                println(wineOrganizer.viewWineRackOf(BORDEAUX))

                // When
                val firstTaken = wineOrganizer.takeGoodBottleOf(RED, BORDEAUX)
                val secondTaken = wineOrganizer.takeGoodBottleOf(RED, BORDEAUX)

                // Then
                println(wineOrganizer.viewWineRackOf(BORDEAUX))
                firstTaken shouldBeSameInstanceAs goodRedBottle1
                secondTaken shouldBeSameInstanceAs goodRedBottle2
                wineOrganizer.viewWineRackOf(BORDEAUX)?.numberOfBottles shouldBe 1
            }

            @Test
            fun `best bottles should be got by middle of color shelf`() {
                // Given
                val bestRedBottle1 = bestRedBottle()
                val bestRedBottle2 = bestRedBottle()
                wineOrganizer.storeBottle(bestRedBottle1)
                wineOrganizer.storeBottle(commonRedBottle())
                wineOrganizer.storeBottle(bestRedBottle2)
                println(wineOrganizer.viewWineRackOf(BORDEAUX))

                // When
                val firstTaken = wineOrganizer.takeBestBottleOf(RED, BORDEAUX)
                val secondTaken = wineOrganizer.takeBestBottleOf(RED, BORDEAUX)

                // Then
                println(wineOrganizer.viewWineRackOf(BORDEAUX))
                firstTaken shouldBeSameInstanceAs bestRedBottle1
                secondTaken shouldBeSameInstanceAs bestRedBottle2
                wineOrganizer.viewWineRackOf(BORDEAUX)?.numberOfBottles shouldBe 1
            }
        }
    }

    @Nested
    inner class `with racks having two shelves` {

        val wineOrganizer = WineCellarOrganizer(3 to Capacity(2, 6))

        @Nested
        inner class `a common bottle` {

            @Test
            fun `of red wine should be stored from the beginning on second shelf`() {
                // Given
                val commonRedBottle1 = commonRedBottle()
                val commonRedBottle2 = commonRedBottle()

                // When
                wineOrganizer.storeBottle(commonRedBottle1)
                wineOrganizer.storeBottle(commonRedBottle2)

                // Then
                val wineRack = wineOrganizer.viewWineRackOf(BORDEAUX)
                println(wineRack)
                wineRack shouldNotBeNull {
                    getAt(1, 0) shouldBeSameInstanceAs commonRedBottle1
                    getAt(1, 1) shouldBeSameInstanceAs commonRedBottle2
                }
            }

            @Test
            fun `of pink wine should be stored from the beginning on second shelf`() {
                // Given
                val commonPinkBottle1 = commonPinkBottle()
                val commonPinkBottle2 = commonPinkBottle()

                // When
                wineOrganizer.storeBottle(commonPinkBottle1)
                wineOrganizer.storeBottle(commonPinkBottle2)

                // Then
                val wineRack = wineOrganizer.viewWineRackOf(BORDEAUX)
                println(wineRack)
                wineRack shouldNotBeNull {
                    getAt(1, 0) shouldBeSameInstanceAs commonPinkBottle1
                    getAt(1, 1) shouldBeSameInstanceAs commonPinkBottle2
                }
            }

            @Test
            fun `of white wine should be stored from the beginning on second shelf`() {
                // Given
                val commonWhiteBottle1 = commonWhiteBottle()
                val commonWhiteBottle2 = commonWhiteBottle()

                // When
                wineOrganizer.storeBottle(commonWhiteBottle1)
                wineOrganizer.storeBottle(commonWhiteBottle2)

                // Then
                val wineRack = wineOrganizer.viewWineRackOf(BORDEAUX)
                println(wineRack)
                wineRack shouldNotBeNull {
                    getAt(1, 0) shouldBeSameInstanceAs commonWhiteBottle1
                    getAt(1, 1) shouldBeSameInstanceAs commonWhiteBottle2
                }
            }
        }

        @Nested
        inner class `a best bottle` {

            @Test
            fun `of red wine should be stored on first shelf`() {
                // Given
                val goodWhiteBottle1 = bestRedBottle()
                val goodWhiteBottle2 = bestRedBottle()

                // When
                wineOrganizer.storeBottle(goodWhiteBottle1)
                wineOrganizer.storeBottle(goodWhiteBottle2)

                // Then
                val wineRack = wineOrganizer.viewWineRackOf(BORDEAUX)
                println(wineRack)
                wineRack shouldNotBe null
                wineRack shouldNotBeNull {
                    getAt(0, 0) shouldBeSameInstanceAs goodWhiteBottle1
                    getAt(0, 1) shouldBeSameInstanceAs goodWhiteBottle2
                }
            }

            @Test
            fun `of pink wine should be stored on first shelf`() {
                // Given
                val bestPinkBottle1 = bestPinkBottle()
                val bestPinkBottle2 = bestPinkBottle()

                // When
                wineOrganizer.storeBottle(bestPinkBottle1)
                wineOrganizer.storeBottle(bestPinkBottle2)

                // Then
                val wineRack = wineOrganizer.viewWineRackOf(BORDEAUX)
                println(wineRack)
                wineRack shouldNotBe null
                wineRack shouldNotBeNull {
                    getAt(0, 0) shouldBeSameInstanceAs bestPinkBottle1
                    getAt(0, 1) shouldBeSameInstanceAs bestPinkBottle2
                }
            }

            @Test
            fun `of white wine should be stored on first shelf`() {
                // Given
                val bestWhiteBottle1 = bestWhiteBottle()
                val bestWhiteBottle2 = bestWhiteBottle()

                // When
                wineOrganizer.storeBottle(bestWhiteBottle1)
                wineOrganizer.storeBottle(bestWhiteBottle2)

                // Then
                val wineRack = wineOrganizer.viewWineRackOf(BORDEAUX)
                println(wineRack)
                wineRack shouldNotBe null
                wineRack shouldNotBeNull {
                    getAt(0, 0) shouldBeSameInstanceAs bestWhiteBottle1
                    getAt(0, 1) shouldBeSameInstanceAs bestWhiteBottle2
                }
            }

        }

        @Nested
        inner class `take` {
            @Test
            fun `common bottles should be got by head of color shelf`() {
                // Given
                val commonRedBottle1 = commonRedBottle()
                val commonRedBottle2 = commonRedBottle()
                wineOrganizer.storeBottle(commonRedBottle1)
                wineOrganizer.storeBottle(bestRedBottle())
                wineOrganizer.storeBottle(redBottleToKeep())
                wineOrganizer.storeBottle(goodRedBottle())
                wineOrganizer.storeBottle(commonRedBottle2)
                println(wineOrganizer.viewWineRackOf(BORDEAUX))

                // When
                val firstTaken = wineOrganizer.takeCommonBottleOf(RED, BORDEAUX)
                val secondTaken = wineOrganizer.takeCommonBottleOf(RED, BORDEAUX)


                // Then
                println(wineOrganizer.viewWineRackOf(BORDEAUX))
                firstTaken shouldBeSameInstanceAs commonRedBottle1
                secondTaken shouldBeSameInstanceAs commonRedBottle2
                wineOrganizer.viewWineRackOf(BORDEAUX)?.numberOfBottles shouldBe 3
            }

            @Test
            fun `good bottles should be got by middle of color shelf`() {
                // Given
                val goodRedBottle1 = goodRedBottle()
                val goodRedBottle2 = goodRedBottle()
                wineOrganizer.storeBottle(goodRedBottle1)
                wineOrganizer.storeBottle(commonRedBottle())
                wineOrganizer.storeBottle(redBottleToKeep())
                wineOrganizer.storeBottle(bestRedBottle())
                wineOrganizer.storeBottle(goodRedBottle2)
                println(wineOrganizer.viewWineRackOf(BORDEAUX))

                // When
                val firstTaken = wineOrganizer.takeGoodBottleOf(RED, BORDEAUX)
                val secondTaken = wineOrganizer.takeGoodBottleOf(RED, BORDEAUX)

                // Then
                println(wineOrganizer.viewWineRackOf(BORDEAUX))
                firstTaken shouldBeSameInstanceAs goodRedBottle1
                secondTaken shouldBeSameInstanceAs goodRedBottle2
                wineOrganizer.viewWineRackOf(BORDEAUX)?.numberOfBottles shouldBe 3
            }

            @Test
            fun `best bottles should be got by middle of color shelf`() {
                // Given
                val bestRedBottle1 = bestRedBottle()
                val bestRedBottle2 = bestRedBottle()
                wineOrganizer.storeBottle(bestRedBottle1)
                wineOrganizer.storeBottle(commonRedBottle())
                wineOrganizer.storeBottle(redBottleToKeep())
                wineOrganizer.storeBottle(goodRedBottle())
                wineOrganizer.storeBottle(bestRedBottle2)
                println(wineOrganizer.viewWineRackOf(BORDEAUX))

                // When
                val firstTaken = wineOrganizer.takeBestBottleOf(RED, BORDEAUX)
                val secondTaken = wineOrganizer.takeBestBottleOf(RED, BORDEAUX)

                // Then
                println(wineOrganizer.viewWineRackOf(BORDEAUX))
                firstTaken shouldBeSameInstanceAs bestRedBottle1
                secondTaken shouldBeSameInstanceAs bestRedBottle2
                wineOrganizer.viewWineRackOf(BORDEAUX)?.numberOfBottles shouldBe 3
            }
        }
    }

    @Nested
    inner class `with racks having three shelves` {

        val wineOrganizer = WineCellarOrganizer(3 to Capacity(3, 6))

        @Nested
        inner class `a common bottle` {

            @Test
            fun `of red wine should be stored from the beginning on second shelf`() {
                // Given
                val commonRedBottle1 = commonRedBottle()
                val commonRedBottle2 = commonRedBottle()

                // When
                wineOrganizer.storeBottle(commonRedBottle1)
                wineOrganizer.storeBottle(commonRedBottle2)

                // Then
                val wineRack = wineOrganizer.viewWineRackOf(BORDEAUX)
                println(wineRack)
                wineRack shouldNotBeNull {
                    getAt(1, 0) shouldBeSameInstanceAs commonRedBottle1
                    getAt(1, 1) shouldBeSameInstanceAs commonRedBottle2
                }
            }

            @Test
            fun `of pink wine should be stored from the beginning on second shelf`() {
                // Given
                val commonPinkBottle1 = commonPinkBottle()
                val commonPinkBottle2 = commonPinkBottle()

                // When
                wineOrganizer.storeBottle(commonPinkBottle1)
                wineOrganizer.storeBottle(commonPinkBottle2)

                // Then
                val wineRack = wineOrganizer.viewWineRackOf(BORDEAUX)
                println(wineRack)
                wineRack shouldNotBeNull {
                    getAt(2, 0) shouldBeSameInstanceAs commonPinkBottle1
                    getAt(2, 1) shouldBeSameInstanceAs commonPinkBottle2
                }
            }

            @Test
            fun `of white wine should be stored from the beginning on second shelf`() {
                // Given
                val commonWhiteBottle1 = commonWhiteBottle()
                val commonWhiteBottle2 = commonWhiteBottle()

                // When
                wineOrganizer.storeBottle(commonWhiteBottle1)
                wineOrganizer.storeBottle(commonWhiteBottle2)

                // Then
                val wineRack = wineOrganizer.viewWineRackOf(BORDEAUX)
                println(wineRack)
                wineRack shouldNotBeNull {
                    getAt(2, 0) shouldBeSameInstanceAs commonWhiteBottle1
                    getAt(2, 1) shouldBeSameInstanceAs commonWhiteBottle2
                }
            }
        }

        @Nested
        inner class `a best bottle` {

            @Test
            fun `of red wine should be stored on first shelf`() {
                // Given
                val goodWhiteBottle1 = bestRedBottle()
                val goodWhiteBottle2 = bestRedBottle()

                // When
                wineOrganizer.storeBottle(goodWhiteBottle1)
                wineOrganizer.storeBottle(goodWhiteBottle2)

                // Then
                val wineRack = wineOrganizer.viewWineRackOf(BORDEAUX)
                println(wineRack)
                wineRack shouldNotBe null
                wineRack shouldNotBeNull {
                    getAt(0, 0) shouldBeSameInstanceAs goodWhiteBottle1
                    getAt(0, 1) shouldBeSameInstanceAs goodWhiteBottle2
                }
            }
        }
    }

    @Nested
    inner class `with one rack having sufficent space` {

        val wineOrganizer = WineCellarOrganizer(1 to Capacity(4, 6))

        @Test
        fun `stored bottle from regions should can be viewed`() {
            // Given
            val bordeauxRedBottle = commonRedBottle()
            val alsaceBottle = copyBottleFor(bordeauxRedBottle, ALSACE)
            val bourgogneBottle = copyBottleFor(bordeauxRedBottle, BOURGOGNE)
            wineOrganizer.storeBottle(bordeauxRedBottle)
            wineOrganizer.storeBottle(alsaceBottle)
            wineOrganizer.storeBottle(bourgogneBottle)

            // When
            val viewedBordeauxBottle = wineOrganizer.viewCommonBottleOf(bordeauxRedBottle.color, BORDEAUX)
            val viewedAlsaceBottle = wineOrganizer.viewCommonBottleOf(alsaceBottle.color, ALSACE)
            val viewedBourgogneBottle = wineOrganizer.viewCommonBottleOf(bourgogneBottle.color, BOURGOGNE)

            // Then
            println(wineOrganizer.viewWineRackOf(BORDEAUX))
            println(wineOrganizer.viewWineRackOf(ALSACE))
            println(wineOrganizer.viewWineRackOf(BOURGOGNE))

            wineOrganizer.viewWineRackOf(BORDEAUX)?.rackId shouldBe wineOrganizer.viewWineRackOf(BOURGOGNE)?.rackId
            wineOrganizer.viewWineRackOf(ALSACE)?.rackId shouldBe wineOrganizer.viewWineRackOf(BOURGOGNE)?.rackId

            viewedBordeauxBottle shouldBe bordeauxRedBottle
            viewedAlsaceBottle shouldBe alsaceBottle
            viewedBourgogneBottle shouldBe bourgogneBottle

            wineOrganizer.viewNumberOfWineRacks() shouldBe 1
            wineOrganizer.numberOfBottlesFrom(BORDEAUX) shouldBe 1
            wineOrganizer.numberOfBottlesFrom(ALSACE) shouldBe 1
            wineOrganizer.numberOfBottlesFrom(BOURGOGNE) shouldBe 1
        }

        @Test
        fun `stored bottle from regions should can be taken`() {
            // Given
            val bordeauxRedBottle = commonRedBottle()
            val alsaceBottle = copyBottleFor(bordeauxRedBottle, ALSACE)
            val bourgogneBottle = copyBottleFor(bordeauxRedBottle, BOURGOGNE)
            wineOrganizer.storeBottle(bordeauxRedBottle)
            wineOrganizer.storeBottle(alsaceBottle)
            wineOrganizer.storeBottle(bourgogneBottle)

            // When
            val takenBordeauxBottle = wineOrganizer.takeCommonBottleOf(bordeauxRedBottle.color, BORDEAUX)
            val takenAlsaceBottle = wineOrganizer.takeCommonBottleOf(alsaceBottle.color, ALSACE)
            val takenBourgogneBottle = wineOrganizer.takeCommonBottleOf(bourgogneBottle.color, BOURGOGNE)

            // Then
            println(wineOrganizer.viewWineRackOf(BORDEAUX))
            println(wineOrganizer.viewWineRackOf(ALSACE))
            println(wineOrganizer.viewWineRackOf(BOURGOGNE))

            wineOrganizer.viewWineRackOf(BORDEAUX)?.rackId shouldBe wineOrganizer.viewWineRackOf(BOURGOGNE)?.rackId
            wineOrganizer.viewWineRackOf(ALSACE)?.rackId shouldBe wineOrganizer.viewWineRackOf(BOURGOGNE)?.rackId

            takenBordeauxBottle shouldBe bordeauxRedBottle
            takenAlsaceBottle shouldBe alsaceBottle
            takenBourgogneBottle shouldBe bourgogneBottle

            wineOrganizer.viewNumberOfWineRacks() shouldBe 1
            wineOrganizer.numberOfBottlesFrom(BORDEAUX) shouldBe 0
            wineOrganizer.numberOfBottlesFrom(ALSACE) shouldBe 0
            wineOrganizer.numberOfBottlesFrom(BOURGOGNE) shouldBe 0
        }
    }

    @Nested
    inner class `with two racks having sufficent space` {

        val wineOrganizer = WineCellarOrganizer(2 to Capacity(4, 6))

        @Test
        fun `stored bottle from regions should can be viewed`() {
            // Given
            val bordeauxRedBottle = commonRedBottle()
            val alsaceBottle = copyBottleFor(bordeauxRedBottle, ALSACE)
            val bourgogneBottle = copyBottleFor(bordeauxRedBottle, BOURGOGNE)
            wineOrganizer.storeBottle(bordeauxRedBottle)
            wineOrganizer.storeBottle(alsaceBottle)
            wineOrganizer.storeBottle(bourgogneBottle)

            // When
            val viewedBordeauxBottle = wineOrganizer.viewCommonBottleOf(bordeauxRedBottle.color, BORDEAUX)
            val viewedAlsaceBottle = wineOrganizer.viewCommonBottleOf(alsaceBottle.color, ALSACE)
            val viewedBourgogneBottle = wineOrganizer.viewCommonBottleOf(bourgogneBottle.color, BOURGOGNE)

            // Then
            println(wineOrganizer.viewWineRackOf(BORDEAUX))
            println(wineOrganizer.viewWineRackOf(ALSACE))
            println(wineOrganizer.viewWineRackOf(BOURGOGNE))

            wineOrganizer.viewWineRackOf(BORDEAUX)?.rackId shouldNotBe wineOrganizer.viewWineRackOf(BOURGOGNE)?.rackId
            wineOrganizer.viewWineRackOf(ALSACE)?.rackId shouldBe wineOrganizer.viewWineRackOf(BOURGOGNE)?.rackId

            viewedBordeauxBottle shouldBeSameInstanceAs bordeauxRedBottle
            viewedAlsaceBottle shouldBeSameInstanceAs alsaceBottle
            viewedBourgogneBottle shouldBeSameInstanceAs bourgogneBottle

            wineOrganizer.viewNumberOfWineRacks() shouldBe 2
            wineOrganizer.numberOfBottlesFrom(BORDEAUX) shouldBe 1
            wineOrganizer.numberOfBottlesFrom(ALSACE) shouldBe 1
            wineOrganizer.numberOfBottlesFrom(BOURGOGNE) shouldBe 1
        }

        @Test
        fun `stored bottle from regions should can be taken`() {
            // Given
            val bordeauxRedBottle = commonRedBottle()
            val alsaceBottle = copyBottleFor(bordeauxRedBottle, ALSACE)
            val bourgogneBottle = copyBottleFor(bordeauxRedBottle, BOURGOGNE)
            wineOrganizer.storeBottle(bordeauxRedBottle)
            wineOrganizer.storeBottle(alsaceBottle)
            wineOrganizer.storeBottle(bourgogneBottle)

            // When
            val takenBordeauxBottle = wineOrganizer.takeCommonBottleOf(bordeauxRedBottle.color, BORDEAUX)
            val takenAlsaceBottle = wineOrganizer.takeCommonBottleOf(alsaceBottle.color, ALSACE)
            val takenBourgogneBottle = wineOrganizer.takeCommonBottleOf(bourgogneBottle.color, BOURGOGNE)

            // Then
            println(wineOrganizer.viewWineRackOf(BORDEAUX))
            println(wineOrganizer.viewWineRackOf(ALSACE))
            println(wineOrganizer.viewWineRackOf(BOURGOGNE))

            wineOrganizer.viewWineRackOf(BORDEAUX)?.rackId shouldNotBe wineOrganizer.viewWineRackOf(BOURGOGNE)?.rackId
            wineOrganizer.viewWineRackOf(ALSACE)?.rackId shouldBe wineOrganizer.viewWineRackOf(BOURGOGNE)?.rackId

            takenBordeauxBottle shouldBe bordeauxRedBottle
            takenAlsaceBottle shouldBe alsaceBottle
            takenBourgogneBottle shouldBe bourgogneBottle

            wineOrganizer.viewNumberOfWineRacks() shouldBe 2
            wineOrganizer.numberOfBottlesFrom(BORDEAUX) shouldBe 0
            wineOrganizer.numberOfBottlesFrom(ALSACE) shouldBe 0
            wineOrganizer.numberOfBottlesFrom(BOURGOGNE) shouldBe 0
        }
    }

    private fun copyBottleFor(bordeauxRedBottle: Bottle, region: Region) = Bottle(
        name = bordeauxRedBottle.name,
        year = bordeauxRedBottle.year,
        region = region,
        color = bordeauxRedBottle.color,
        rate = bordeauxRedBottle.rate,
        keepUntil = bordeauxRedBottle.keepUntil,
    )

    private fun WineRack.getAt(shelfIndex: Int, slotIndex: Int) = at(shelfIndex, slotIndex)
    private fun WineRack.getAt(shelfIndex: Int) = at(shelfIndex)
}

