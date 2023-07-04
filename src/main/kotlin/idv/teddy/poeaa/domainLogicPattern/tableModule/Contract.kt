package idv.teddy.poeaa.domainLogicPattern.tableModule

import java.math.BigDecimal
import java.sql.Connection
import java.sql.Date
import java.sql.ResultSet
import java.sql.Statement
import java.time.LocalDate

@Suppress("DuplicatedCode")
class Contract(private val connection: Connection) {

    fun calculateRecognitions(contractId: Long) {
        val contractRow = this[contractId]
        val amount = contractRow.getBigDecimal("revenue")
        val rr = RevenueRecognition(connection)
        val product = Product(connection)
        val productId = getProductId(contractId)
        val whenSigned = getWhenSigned(contractId)
        when (product.getProductType(productId)) {
            Product.Companion.ProductType.WP -> {
                rr.insert(contractId, amount, whenSigned)
            }

            Product.Companion.ProductType.SS -> {
                val allocation = amount.divide(3.toBigDecimal())
                rr.insert(contractId, allocation, whenSigned)
                rr.insert(contractId, allocation, whenSigned.plusDays(60))
                rr.insert(contractId, allocation, whenSigned.plusDays(90))
            }

            Product.Companion.ProductType.DB -> {
                val allocation = amount.divide(3.toBigDecimal())
                rr.insert(contractId, allocation, whenSigned)
                rr.insert(contractId, allocation, whenSigned.plusDays(30))
                rr.insert(contractId, allocation, whenSigned.plusDays(60))
            }
        }
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

    private fun getWhenSigned(contractId: Long): LocalDate {
        return this[contractId].getDate("dateSigned").toLocalDate()
    }

    private fun getProductId(contractId: Long): Long {
        return this[contractId].getLong("product")
    }

    private operator fun get(contractId: Long): ResultSet {
        val prepareStatement = connection.prepareStatement("SELECT * FROM contracts WHERE ID = ?")
        prepareStatement.setLong(1, contractId)
        return requireNotNull(prepareStatement.executeQuery().takeIf { it.next() }) { "Cannot find contract ($contractId)" }
    }
}
