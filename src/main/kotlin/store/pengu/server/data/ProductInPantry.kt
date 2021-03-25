package store.pengu.server.data


data class ProductInPantry(
    val product_id: Long,
    val pantry_id: Long,
    val name: String,
    val barcode: String,
    val review_score: Double?,
    val review_number: Int,
    val have_qty: Int,
    val want_qty: Int
)