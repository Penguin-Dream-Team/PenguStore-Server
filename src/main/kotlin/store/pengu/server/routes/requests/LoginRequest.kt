package store.pengu.server.routes.requests

data class LoginRequest(
    val username: String,
    val password: String
)
