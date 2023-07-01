package idv.teddy.poeaa.domainLogicPattern.transactionScript

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.sql.DriverManager
import java.time.LocalDate


@Testcontainers
class RecognitionServiceTest {
    @Container
    val mysql: MySQLContainer<*> = MySQLContainer("mysql:8.0.33")
        .withInitScript("sql/revenueRecognitions.sql")
        .withExposedPorts(3306)

    @Test
    internal fun recognizedRevenue() {
        val connection = DriverManager.getConnection("jdbc:mysql://${mysql.host}:${mysql.firstMappedPort}/${mysql.databaseName}", mysql.username, mysql.password)
        val gateway = Gateway(connection)
        gateway.insertRecognition(1, 100.toBigDecimal(), LocalDate.of(2023, 6, 1))
        gateway.insertRecognition(1, 20.toBigDecimal(), LocalDate.of(2023, 6, 2))
        gateway.insertRecognition(1, 3.toBigDecimal(), LocalDate.of(2023, 6, 3))
        val recognitionService = RecognitionService(gateway)

        val amount = recognitionService.recognizedRevenue(1, LocalDate.of(2023, 6, 2))

        amount shouldBe 120.toBigDecimal()
    }
}
