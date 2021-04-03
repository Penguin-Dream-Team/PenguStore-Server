package store.pengu.server.data


data class ProductInShoppingList(
    val product_id: Long,
    val pantry_id: Long,
    val shop_id: Long,
    val product_name: String,
    val shop_name: String,
    val barcode: String,
    val reviewScore: Double?,
    val reviewNumber: Int,
    val amountAvailable: Int,
    val amountNeeded: Int,
    val price: Double
)