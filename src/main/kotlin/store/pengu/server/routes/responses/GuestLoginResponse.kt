package store.pengu.server.routes.responses

data class GuestLoginResponse(
    val password: String,
    val token: String,
    val refreshToken: String
)
