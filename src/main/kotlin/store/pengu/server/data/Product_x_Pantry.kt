package store.pengu.server.data

data class Product_x_Pantry(
    val pantry_id: Long,
    val product_id: Long,
    val have_qty: Int,
    val want_qty: Int
)