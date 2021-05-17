package store.pengu.server.routes.requests

data class EditProductRequest(
    val name: String,
    val barcode: String?,
    val imageData: String?,
)
