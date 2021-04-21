package store.pengu.server.routes.requests

data class PantryRequest(
    val id: Long,
    val code: String,
    val name: String,
    val latitude: Double,
    val longitude: Double
)
