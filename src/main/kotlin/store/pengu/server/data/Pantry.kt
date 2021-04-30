package store.pengu.server.data

data class Pantry(
    override val id: Long,
    override val name: String,
    override val code: String,
    override val latitude: Double,
    override val longitude: Double,
    val productCount: Int,
    override val color: String,
    override val shared: Boolean
) : UserList(id, name, code, latitude, longitude, color, shared)
