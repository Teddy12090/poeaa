package idv.teddy.poeaa.domainLogicPattern.transactionScript

import java.math.BigDecimal
import java.sql.Connection
import java.sql.Date
import java.sql.ResultSet
import java.time.LocalDate

class Gateway(private val connection: Connection) {

    fun findRecognitionsFor(contractId: Long, asOf: LocalDate): ResultSet {
        val prepareStatement = connection.prepareStatement(
            "SELECT amount FROM revenueRecognitions WHERE contract = ? AND recognizedOn <= ?"
        )
        prepareStatement.setLong(1, contractId)
        prepareStatement.setDate(2, Date.valueOf(asOf))
        return prepareStatement.executeQuery()
    }

    fun insertRecognition(contractId: Long, amount: BigDecimal, asOf: LocalDate) {
        val prepareStatement = connection.prepareStatement(
            "INSERT INTO revenueRecognitions VALUES (?, ?, ?)"
        )
        prepareStatement.setLong(1, contractId)
        prepareStatement.setBigDecimal(2, amount)
        prepareStatement.setDate(3, Date.valueOf(asOf))
        prepareStatement.executeUpdate()
    }
}
