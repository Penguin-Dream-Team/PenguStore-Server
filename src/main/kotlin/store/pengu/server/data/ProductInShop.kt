package store.pengu.server.data


data class ProductInShop(
    val product_id: Long,
    val name: String,
    val barcode: String,
    val review_score: Double?,
    val review_number: Int,
    val price: Double
)