package store.pengu.server.data

data class User(
    val username: String,
    val email: String,
    val guest: Boolean
)