package store.pengu.server.data

data class ShoppingList(
    override val id: Long,
    override val name: String,
    override val latitude: Float,
    override val longitude: Float,
) : UserList(id, name, latitude, longitude)