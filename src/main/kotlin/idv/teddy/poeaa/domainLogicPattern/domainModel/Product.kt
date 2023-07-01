package idv.teddy.poeaa.domainLogicPattern.domainModel

data class Product(private val name: String, private val recognitionStrategy: RecognitionStrategy) {
    fun calculateRecognitions(contract: Contract) {
        recognitionStrategy.calculateRecognitions(contract)
    }

    companion object {
        fun newWordProcessor(name: String): Product {
            return Product(name, CompleteRecognitionStrategy())
        }

        fun newSpreadsheet(name: String): Product {
            return Product(name, ThreeWayRecognitionStrategy(60, 90))
        }

        fun newDatabase(name: String): Product {
            return Product(name, ThreeWayRecognitionStrategy(30, 60))
        }
    }
}
