package store.pengu.server.data

data class User(
    val id: Long,
    val username: String,
    val email: String,
    val password: String
)