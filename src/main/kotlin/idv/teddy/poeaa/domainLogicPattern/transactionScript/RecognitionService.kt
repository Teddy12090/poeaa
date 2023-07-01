package idv.teddy.poeaa.domainLogicPattern.transactionScript

import java.math.BigDecimal
import java.time.LocalDate

class RecognitionService(private val gateway: Gateway) {
    fun recognizedRevenue(contractId: Long, asOf: LocalDate): BigDecimal {
        val resultSet = gateway.findRecognitionsFor(contractId, asOf)
        var result = 0.toBigDecimal()
        while (resultSet.next()) {
            result = result.add(resultSet.getBigDecimal("amount"))
        }
        return result
    }

    fun calculateRevenueRecognitions(contractId: Long) {
        val contract = gateway.findContract(contractId)
        contract.takeIf { it.next() }?.run {
            val totalRevenue = getBigDecimal("revenue")
            val recognitionDate = getDate("dateSigned")
            when (getString("type")) {
                "S" -> {
                    val allocation = totalRevenue.divide(3.toBigDecimal())
                    gateway.insertRecognition(contractId, allocation, recognitionDate.toLocalDate())
                    gateway.insertRecognition(contractId, allocation, recognitionDate.toLocalDate().plusDays(60))
                    gateway.insertRecognition(contractId, allocation, recognitionDate.toLocalDate().plusDays(90))
                }

                "W" -> {
                    gateway.insertRecognition(contractId, totalRevenue, recognitionDate.toLocalDate())
                }

                "D" -> {
                    val allocation = totalRevenue.divide(3.toBigDecimal())
                    gateway.insertRecognition(contractId, allocation, recognitionDate.toLocalDate())
                    gateway.insertRecognition(contractId, allocation, recognitionDate.toLocalDate().plusDays(30))
                    gateway.insertRecognition(contractId, allocation, recognitionDate.toLocalDate().plusDays(60))
                }

                else -> Exception("Unsupported type $type")
            }
        } ?: throw Exception("Cannot find contract $contractId")
    }
}
