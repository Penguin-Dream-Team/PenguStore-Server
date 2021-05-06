package store.pengu.server.data

data class Pantry(
    override val id: Long,
    override val name: String,
    override val code: String,
    override val latitude: Double,
    override val longitude: Double,
    override val color: String,
    override val shared: Boolean,
    override val productCount: Int
) : UserList(id, name, code, latitude, longitude, color, shared, productCount)
