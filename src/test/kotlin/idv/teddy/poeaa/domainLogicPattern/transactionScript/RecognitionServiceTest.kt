package idv.teddy.poeaa.domainLogicPattern.transactionScript

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.sql.Connection
import java.sql.DriverManager
import java.time.LocalDate


@Testcontainers
class RecognitionServiceTest {
    companion object {
        @Container
        val mysql: MySQLContainer<*> = MySQLContainer("mysql:8.0.33")
            .withInitScript("sql/revenueRecognitions.sql")
            .withExposedPorts(3306)

        private val connection: Connection by lazy {
            DriverManager.getConnection("jdbc:mysql://${mysql.host}:${mysql.firstMappedPort}/${mysql.databaseName}", mysql.username, mysql.password)
        }
    }

    @AfterEach
    internal fun tearDown() {
        val statement = connection.createStatement()
        statement.execute("DELETE FROM revenueRecognitions")
        statement.execute("DELETE FROM contracts")
        statement.execute("DELETE FROM products")
    }

    @Test
    internal fun recognizedRevenue() {
        val gateway = Gateway(connection)
        gateway.insertRecognition(1, 100.toBigDecimal(), LocalDate.of(2023, 6, 1))
        gateway.insertRecognition(1, 20.toBigDecimal(), LocalDate.of(2023, 6, 2))
        gateway.insertRecognition(1, 3.toBigDecimal(), LocalDate.of(2023, 6, 3))
        val recognitionService = RecognitionService(gateway)

        val amount = recognitionService.recognizedRevenue(1, LocalDate.of(2023, 6, 2))

        amount shouldBe 120.toBigDecimal()
    }

    @Test
    internal fun calculateRevenueRecognitionsForWProduct() {
        val gateway = Gateway(connection)
        val productId = gateway.insertProduct("Office", "W")
        val dateSigned = LocalDate.of(2023, 6, 1)
        val contractId = gateway.insertContract(productId, 900.toBigDecimal(), dateSigned)
        val recognitionService = RecognitionService(gateway)

        recognitionService.calculateRevenueRecognitions(contractId)

        recognitionService.recognizedRevenue(contractId, dateSigned) shouldBe 900.toBigDecimal()
    }

    @Test
    internal fun calculateRevenueRecognitionsForDProduct() {
        val gateway = Gateway(connection)
        val productId = gateway.insertProduct("Office", "D")
        val dateSigned = LocalDate.of(2023, 6, 1)
        val contractId = gateway.insertContract(productId, 900.toBigDecimal(), dateSigned)
        val recognitionService = RecognitionService(gateway)

        recognitionService.calculateRevenueRecognitions(contractId)

        recognitionService.recognizedRevenue(contractId, dateSigned) shouldBe 300.toBigDecimal()
        recognitionService.recognizedRevenue(contractId, dateSigned.plusDays(30)) shouldBe 600.toBigDecimal()
        recognitionService.recognizedRevenue(contractId, dateSigned.plusDays(60)) shouldBe 900.toBigDecimal()
    }

    @Test
    internal fun calculateRevenueRecognitionsForSProduct() {
        val gateway = Gateway(connection)
        val productId = gateway.insertProduct("Office", "S")
        val dateSigned = LocalDate.of(2023, 6, 1)
        val contractId = gateway.insertContract(productId, 900.toBigDecimal(), dateSigned)
        val recognitionService = RecognitionService(gateway)

        recognitionService.calculateRevenueRecognitions(contractId)

        recognitionService.recognizedRevenue(contractId, dateSigned) shouldBe 300.toBigDecimal()
        recognitionService.recognizedRevenue(contractId, dateSigned.plusDays(60)) shouldBe 600.toBigDecimal()
        recognitionService.recognizedRevenue(contractId, dateSigned.plusDays(90)) shouldBe 900.toBigDecimal()
    }
}
