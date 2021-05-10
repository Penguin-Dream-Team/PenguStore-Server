package store.pengu.server.data

data class Product(
    val id: Long,
    val name: String,
    val barcode: String?,
    val productRating: Float,
    val userRating: Int,
    val ratings: List<Int>,
    val image: String?
)