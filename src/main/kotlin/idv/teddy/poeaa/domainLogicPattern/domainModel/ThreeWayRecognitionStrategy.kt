package idv.teddy.poeaa.domainLogicPattern.domainModel

class ThreeWayRecognitionStrategy(private val firstRecognitionOffset: Long, private val secondRecognitionOffset: Long) : RecognitionStrategy {
    override fun calculateRecognitions(contract: Contract) {
        val allocation = contract.revenue.divide(3.toBigDecimal())
        contract.addRevenueRecognition(RevenueRecognition(allocation, contract.whenSigned))
        contract.addRevenueRecognition(RevenueRecognition(allocation, contract.whenSigned.plusDays(firstRecognitionOffset)))
        contract.addRevenueRecognition(RevenueRecognition(allocation, contract.whenSigned.plusDays(secondRecognitionOffset)))
    }
}
