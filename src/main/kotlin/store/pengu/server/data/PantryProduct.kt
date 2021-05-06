package store.pengu.server.data

data class PantryProduct(
    val pantry_id: Long,
    val product_id: Long,
    val have_qty: Int,
    val want_qty: Int
)