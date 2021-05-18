package store.pengu.server.data

data class Cart(
    val productId: Long,
    val pantryId: Long,
    val amount: Int,
)