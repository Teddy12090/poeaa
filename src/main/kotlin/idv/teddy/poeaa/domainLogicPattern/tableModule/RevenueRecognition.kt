package idv.teddy.poeaa.domainLogicPattern.tableModule

import java.math.BigDecimal
import java.sql.Connection
import java.sql.Date
import java.sql.ResultSet
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class RevenueRecognition(val connection: Connection) {
    fun insert(contractId: Long, amount: BigDecimal, whenSigned: LocalDate) {
        val prepareStatement = connection.prepareStatement(
            "INSERT INTO revenueRecognitions VALUES (?, ?, ?)"
        )
        prepareStatement.setLong(1, contractId)
        prepareStatement.setBigDecimal(2, amount)
        prepareStatement.setDate(3, Date.valueOf(whenSigned))
        prepareStatement.executeUpdate()
    }

    fun recognizedRevenue(contractId: Long, asOf: LocalDate): BigDecimal {
        val resultSet = select("contract = $contractId AND recognizedOn <= '${asOf.format(DateTimeFormatter.ISO_LOCAL_DATE)}'")
        var result = 0.toBigDecimal()
        while (resultSet.next()) {
            result = result.add(resultSet.getBigDecimal("amount"))
        }
        return result
    }

    fun recognizedRevenue2(contractId: Long, asOf: LocalDate): BigDecimal {
        val resultSet = compute("sum(amount)", "contract = $contractId AND recognizedOn <= '${asOf.format(DateTimeFormatter.ISO_LOCAL_DATE)}'")
        return resultSet.getBigDecimal(1)
    }

    private fun select(condition: String): ResultSet {
        val prepareStatement = connection.prepareStatement("SELECT * from revenueRecognitions WHERE $condition")
        return prepareStatement.executeQuery()
    }

    private fun compute(computeExpression: String, condition: String): ResultSet {
        val prepareStatement = connection.prepareStatement("SELECT $computeExpression from revenueRecognitions WHERE $condition")
        return requireNotNull(prepareStatement.executeQuery().takeIf { it.next() }) { "Cannot compute $computeExpression on table revenueRecognitions for data $condition" }
    }
}
