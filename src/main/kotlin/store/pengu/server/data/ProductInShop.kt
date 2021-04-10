package store.pengu.server.data


data class ProductInShop(
    val product_id: Long,
    val name: String,
    val barcode: String,
    val price: Double
)