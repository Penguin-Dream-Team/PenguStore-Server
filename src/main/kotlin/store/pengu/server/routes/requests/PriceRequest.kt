package store.pengu.server.routes.requests

data class PriceRequest(
    val barcode: String?,
    val product_id: Long?,
    val price: Double,
    val latitude: Double,
    val longitude: Double
)
