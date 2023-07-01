package idv.teddy.poeaa.domainLogicPattern.transactionScript

import java.math.BigDecimal
import java.time.LocalDate

class RecognitionService(val gateway: Gateway) {
    fun recognizedRevenue(contractId: Long, asOf: LocalDate): BigDecimal {
        val resultSet = gateway.findRecognitionsFor(contractId, asOf)
        var result = 0.toBigDecimal()
        while (resultSet.next()) {
            result = result.add(resultSet.getBigDecimal("amount"))
        }
        return result
    }
}
