package store.pengu.server.data


data class ProductInShoppingList(
    val product_id: Long,
    val pantries: List<Pantry>,
    val name: String,
    val barcode: String?,
    val amountAvailable: Int,
    val amountNeeded: Int,
    val price: Double
)