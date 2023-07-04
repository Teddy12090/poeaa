package idv.teddy.poeaa.domainLogicPattern.tableModule

import java.sql.Connection
import java.sql.ResultSet
import java.sql.Statement

@Suppress("DuplicatedCode")
class Product(private val connection: Connection) {
    companion object {
        enum class ProductType { WP, SS, DB }
    }

    fun insertProduct(name: String, type: ProductType): Long {
        val prepareStatement = connection.prepareStatement(
            "INSERT products (name, type) VALUES (?, ?)",
            Statement.RETURN_GENERATED_KEYS
        )
        prepareStatement.setString(1, name)
        prepareStatement.setString(2, type.toString())
        prepareStatement.executeUpdate()
        return prepareStatement.generatedKeys.takeIf { it.next() }?.getLong(1)
            ?: throw Exception("Cannot get generated id of the new product")
    }

    fun getProductType(productId: Long): ProductType {
        return ProductType.valueOf(this[productId].getString("type"))
    }

    private operator fun get(productId: Long): ResultSet {
        val prepareStatement = connection.prepareStatement("SELECT * FROM products WHERE ID = ?")
        prepareStatement.setLong(1, productId)
        return requireNotNull(prepareStatement.executeQuery().takeIf { it.next() }) { "Cannot find product ($productId)" }
    }

}
