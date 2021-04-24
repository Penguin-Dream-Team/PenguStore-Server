package store.pengu.server.routes.requests

import org.jooq.types.ULong

data class ImageRequest(
    val id: ULong,
    val barcode: String?,
    val product_id: Long?,
    val image_url: String
    )
