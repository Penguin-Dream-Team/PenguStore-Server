package store.pengu.server.data

import store.pengu.server.data.productlists.ProductPantryListEntry

data class ProductInShoppingList (
    override val id: Long,
    val pantries: List<ProductPantryListEntry>,
    override val listId: Long,
    override val name: String,
    override val barcode: String?,
    override val amountAvailable: Int,
    override val amountNeeded: Int,
    override val image: String?,
    val price: Double
) : ListProduct(id, listId, name, barcode, amountAvailable, amountNeeded, image)