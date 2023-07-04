package idv.teddy.poeaa.domainLogicPattern.tableModule

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.sql.Connection
import java.sql.DriverManager
import java.time.LocalDate

@Testcontainers
class ContractTest {
    companion object {
        @Container
        val mysql: MySQLContainer<*> = MySQLContainer("mysql:8.0.33")
            .withInitScript("sql/revenueRecognitions.sql")
            .withExposedPorts(3306)

        private val connection: Connection by lazy {
            DriverManager.getConnection("jdbc:mysql://${mysql.host}:${mysql.firstMappedPort}/${mysql.databaseName}", mysql.username, mysql.password)
        }
    }

    @Test
    internal fun calculateRecognitionsForWordProcessor() {
        val contract = Contract(connection)
        val product = Product(connection)
        val revenueRecognition = RevenueRecognition(connection)
        val productId = product.insertProduct("Thinking Word", Product.Companion.ProductType.WP)
        val contractId = contract.insertContract(productId, 300.toBigDecimal(), LocalDate.of(2023, 6, 1))

        // when
        contract.calculateRecognitions(contractId)

        // then
        revenueRecognition.recognizedRevenue(contractId, LocalDate.of(2023, 6, 1)) shouldBe 300.toBigDecimal()
    }

    @Test
    internal fun calculateRecognitionsForSpreadsheet() {
        val contract = Contract(connection)
        val product = Product(connection)
        val revenueRecognition = RevenueRecognition(connection)
        val productId = product.insertProduct("Thinking Calc", Product.Companion.ProductType.SS)
        val contractId = contract.insertContract(productId, 300.toBigDecimal(), LocalDate.of(2023, 6, 1))

        // when
        contract.calculateRecognitions(contractId)

        // then
        revenueRecognition.recognizedRevenue(contractId, LocalDate.of(2023, 6, 1)) shouldBe 100.toBigDecimal()
        revenueRecognition.recognizedRevenue(contractId, LocalDate.of(2023, 6, 1).plusDays(60)) shouldBe 200.toBigDecimal()
        revenueRecognition.recognizedRevenue(contractId, LocalDate.of(2023, 6, 1).plusDays(90)) shouldBe 300.toBigDecimal()
    }

    @Test
    internal fun calculateRecognitionsForDatabase() {
        val contract = Contract(connection)
        val product = Product(connection)
        val revenueRecognition = RevenueRecognition(connection)
        val productId = product.insertProduct("Thinking Database", Product.Companion.ProductType.DB)
        val contractId = contract.insertContract(productId, 300.toBigDecimal(), LocalDate.of(2023, 6, 1))

        // when
        contract.calculateRecognitions(contractId)

        // then
        revenueRecognition.recognizedRevenue(contractId, LocalDate.of(2023, 6, 1)) shouldBe 100.toBigDecimal()
        revenueRecognition.recognizedRevenue(contractId, LocalDate.of(2023, 6, 1).plusDays(30)) shouldBe 200.toBigDecimal()
        revenueRecognition.recognizedRevenue(contractId, LocalDate.of(2023, 6, 1).plusDays(60)) shouldBe 300.toBigDecimal()
    }
}