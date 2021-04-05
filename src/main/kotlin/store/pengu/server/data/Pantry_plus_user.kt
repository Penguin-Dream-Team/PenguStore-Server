package store.pengu.server.data

data class Pantry_plus_user(
    val id: Long,
    val code: String,
    val name: String,
    val latitude: Float,
    val longitude: Float,
    val user_id: Long
)