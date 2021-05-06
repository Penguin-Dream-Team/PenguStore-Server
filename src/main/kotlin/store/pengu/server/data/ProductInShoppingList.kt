package store.pengu.server.data

data class ProductInShoppingList (
    override val id: Long,
    override val listId: Long,
    override val name: String,
    override val barcode: String?,
    override val amountAvailable: Int,
    override val amountNeeded: Int,
    override val image: String?,
    val price: Double
) : ListProduct(id, listId, name, barcode, amountAvailable, amountNeeded, image)