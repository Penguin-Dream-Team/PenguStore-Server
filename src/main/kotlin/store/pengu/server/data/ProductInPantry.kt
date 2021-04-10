package store.pengu.server.data


data class ProductInPantry(
    val productId: Long,
    val pantryId: Long,
    val name: String,
    val barcode: String?,
    val amountAvailable: Int,
    val amountNeeded: Int
)