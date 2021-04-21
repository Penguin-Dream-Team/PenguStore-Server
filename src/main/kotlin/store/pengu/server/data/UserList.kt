package store.pengu.server.data

abstract class UserList(
    open val id: Long,
    open val name: String,
    open val latitude: Double,
    open val longitude: Double
)