package store.pengu.server.data


data class Local_Product_Price(
    val product_id: Long,
    val price: Double,
    val latitude: Float,
    val longitude: Float
)