package idv.teddy.poeaa.domainLogicPattern.domainModel

import java.math.BigDecimal
import java.time.LocalDate

data class RevenueRecognition(val revenue: BigDecimal, private val date: LocalDate) {
    fun isRecognizableBy(asOf: LocalDate): Boolean {
        return asOf.isAfter(date) || asOf == date
    }

}
