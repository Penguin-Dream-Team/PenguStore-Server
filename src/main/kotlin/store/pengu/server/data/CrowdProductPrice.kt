package store.pengu.server.data

data class CrowdProductPrice(
    val barcode: String,
    val price: Double,
    val latitude: Double,
    val longitude: Double
)