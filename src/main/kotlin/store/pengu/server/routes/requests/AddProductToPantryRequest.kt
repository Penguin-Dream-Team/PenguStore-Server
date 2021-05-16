package store.pengu.server.routes.requests

data class AddProductToPantryRequest(
    val pantryId: Long,
    val haveQuantity: Int,
    val needQuantity: Int
)
