package store.pengu.server.routes.requests

data class CreateListRequest(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val color: String
)
