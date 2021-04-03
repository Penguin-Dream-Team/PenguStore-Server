package store.pengu.server.data

data class Pantry(
    val id: Long,
    val code: String,
    val name: String,
    val latitude: Float,
    val longitude: Float
)