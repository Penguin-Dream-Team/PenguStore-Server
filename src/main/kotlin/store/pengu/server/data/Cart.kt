package store.pengu.server.data

data class Cart(
    val product_id: Long,
    val pantry_id: Long,
    val amount: Int,
)