package idv.teddy.poeaa.domainLogicPattern.transactionScript

import java.math.BigDecimal
import java.sql.Connection
import java.sql.Date
import java.sql.ResultSet
import java.sql.Statement
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

    fun insertContract(productId: Long, revenue: BigDecimal, dateSigned: LocalDate): Long {
        val prepareStatement = connection.prepareStatement(
            "INSERT INTO contracts (product, revenue, dateSigned) VALUES (?, ?, ?)",
            Statement.RETURN_GENERATED_KEYS
        )
        prepareStatement.setLong(1, productId)
        prepareStatement.setBigDecimal(2, revenue)
        prepareStatement.setDate(3, Date.valueOf(dateSigned))
        prepareStatement.executeUpdate()
        return prepareStatement.generatedKeys.takeIf { it.next() }?.getLong(1)
            ?: throw Exception("Cannot get generated id of the new contract")
    }

    fun insertProduct(name: String, type: String): Long {
        val prepareStatement = connection.prepareStatement(
            "INSERT products (name, type) VALUES (?, ?)",
            Statement.RETURN_GENERATED_KEYS
        )
        prepareStatement.setString(1, name)
        prepareStatement.setString(2, type)
        prepareStatement.executeUpdate()
        return prepareStatement.generatedKeys.takeIf { it.next() }?.getLong(1)
            ?: throw Exception("Cannot get generated id of the new product")
    }

    fun findContract(contractId: Long): ResultSet {
        val prepareStatement = connection.prepareStatement(
            "SELECT * FROM contracts c, products p WHERE c.ID = ? AND c.product = p.ID"
        )
        prepareStatement.setLong(1, contractId)
        return prepareStatement.executeQuery()
    }
}
