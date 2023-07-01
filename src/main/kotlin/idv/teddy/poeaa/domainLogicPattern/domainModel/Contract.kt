package idv.teddy.poeaa.domainLogicPattern.domainModel

import java.math.BigDecimal
import java.time.LocalDate

data class Contract(private val product: Product, val revenue: BigDecimal, val whenSigned: LocalDate) {
    private val revenueRecognitions = ArrayList<RevenueRecognition>()
    fun calculateRecognitions() {
        product.calculateRecognitions(this)
    }

    fun addRevenueRecognition(revenueRecognition: RevenueRecognition) {
        revenueRecognitions.add(revenueRecognition)
    }

    fun recognizedRevenue(asOf: LocalDate): BigDecimal {
        var result = 0.toBigDecimal()
        for (revenueRecognition in revenueRecognitions) {
            revenueRecognition.takeIf { it.isRecognizableBy(asOf) }?.let {
                result = result.plus(revenueRecognition.revenue)
            }
        }
        return result
    }
}
