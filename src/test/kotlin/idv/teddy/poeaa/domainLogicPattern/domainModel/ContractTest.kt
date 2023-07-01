package idv.teddy.poeaa.domainLogicPattern.domainModel

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.time.LocalDate

class ContractTest {
    @Test
    internal fun calculateRecognitionsForWordProcessor() {
        val whenSigned = LocalDate.of(2023, 7, 1)
        val contract = Contract(Product.newWordProcessor("Thinking Word"), 300.toBigDecimal(), whenSigned)

        contract.calculateRecognitions()

        contract.recognizedRevenue(whenSigned.minusDays(1)) shouldBe 0.toBigDecimal()
        contract.recognizedRevenue(whenSigned) shouldBe 300.toBigDecimal()
    }


    @Test
    internal fun calculateRecognitionsForSpreadsheet() {
        val whenSigned = LocalDate.of(2023, 7, 1)
        val contract = Contract(Product.newSpreadsheet("Thinking Calc"), 300.toBigDecimal(), whenSigned)

        contract.calculateRecognitions()

        contract.recognizedRevenue(whenSigned.minusDays(1)) shouldBe 0.toBigDecimal()
        contract.recognizedRevenue(whenSigned) shouldBe 100.toBigDecimal()
        contract.recognizedRevenue(whenSigned.plusDays(60)) shouldBe 200.toBigDecimal()
        contract.recognizedRevenue(whenSigned.plusDays(90)) shouldBe 300.toBigDecimal()
    }

    @Test
    internal fun calculateRecognitionsForDatabase() {
        val whenSigned = LocalDate.of(2023, 7, 1)
        val contract = Contract(Product.newDatabase("Thinking DB"), 300.toBigDecimal(), whenSigned)

        contract.calculateRecognitions()

        contract.recognizedRevenue(whenSigned.minusDays(1)) shouldBe 0.toBigDecimal()
        contract.recognizedRevenue(whenSigned) shouldBe 100.toBigDecimal()
        contract.recognizedRevenue(whenSigned.plusDays(30)) shouldBe 200.toBigDecimal()
        contract.recognizedRevenue(whenSigned.plusDays(60)) shouldBe 300.toBigDecimal()
    }
}
