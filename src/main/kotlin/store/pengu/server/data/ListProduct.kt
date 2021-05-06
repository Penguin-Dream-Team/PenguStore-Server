package store.pengu.server.data

abstract class ListProduct(
    open val id: Long,
    open val listId: Long,
    open val name: String,
    open val barcode: String?,
    open val amountAvailable: Int,
    open val amountNeeded: Int,
    open val image: String?
)