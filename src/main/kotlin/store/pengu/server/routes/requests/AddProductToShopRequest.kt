package store.pengu.server.routes.requests

data class AddProductToShopRequest(
    val shoppingListId: Long,
    val price: Double
)
