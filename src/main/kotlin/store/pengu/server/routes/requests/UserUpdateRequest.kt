package store.pengu.server.routes.requests

data class UserUpdateRequest(
    val username: String?,
    val email: String?,
    val password: String?
)
