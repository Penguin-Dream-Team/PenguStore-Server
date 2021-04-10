package store.pengu.server.data

data class Pantry_Product(
    val pantry_id: Long,
    val product_id: Long,
    val have_qty: Int,
    val want_qty: Int
)