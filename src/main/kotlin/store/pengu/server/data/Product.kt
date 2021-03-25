package store.pengu.server.data


data class Product(
    val id: Long,
    val name: String,
    val barcode: String,
    val review_score: Double,
    val review_number: Int
)