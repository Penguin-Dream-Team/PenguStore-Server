package store.pengu.server.data.productlists

abstract class ProductListEntry(
    open val listId: Long,
    open val listName: String,
    open val color: String,
    open val latitude: Double,
    open val longitude: Double
)