package store.pengu.server.data

data class Pantry_details(
    val id: Long,
    val code: String,
    val name: String,
    val latitude: Float,
    val longitude: Float,
    val product_num: Int
)