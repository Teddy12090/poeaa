package idv.teddy.poeaa.domainLogicPattern.domainModel

class CompleteRecognitionStrategy : RecognitionStrategy {
    override fun calculateRecognitions(contract: Contract) {
        contract.addRevenueRecognition(RevenueRecognition(contract.revenue, contract.whenSigned))
    }
}
