package store.pengu.server.data.productlists

data class ProductShoppingListEntry(
    override val listId: Long,
    override val listName: String,
    override val color: String,
    val price: Double?,
    override val latitude: Double,
    override val longitude: Double
) : ProductListEntry(listId, listName, color, latitude, longitude)

