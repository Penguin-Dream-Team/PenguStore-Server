package store.pengu.server.data.productlists

data class ProductPantryListEntry(
    override val listId: Long,
    override val listName: String,
    override val color: String,
    val amountAvailable: Int,
    val amountNeeded: Int,
    val isShared: Boolean,
    override val latitude: Double,
    override val longitude: Double
) : ProductListEntry(listId, listName, color, latitude, longitude)