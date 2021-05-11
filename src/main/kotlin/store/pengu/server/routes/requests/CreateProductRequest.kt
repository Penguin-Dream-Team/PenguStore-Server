package store.pengu.server.routes.requests

data class CreateProductRequest(
    val name: String,
    val barcode: String?,
    val image: String?
)
