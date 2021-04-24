package store.pengu.server.data


data class ProductInShoppingList(
    val product_id: Long,
    val pantry_id: Long,
    val name: String,
    val barcode: String?,
    val amountAvailable: Int,
    val amountNeeded: Int,
    val price: Double
)