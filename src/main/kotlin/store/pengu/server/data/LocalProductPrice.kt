package store.pengu.server.data

data class LocalProductPrice(
    val product_id: Long,
    val price: Double,
    val latitude: Double,
    val longitude: Double
)