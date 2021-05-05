package store.pengu.server.data

data class Product(
    val id: Long,
    val name: String,
    val barcode: String?,
    val rating: Float,
    val ratings: List<Int>
)