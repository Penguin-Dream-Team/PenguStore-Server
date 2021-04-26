package store.pengu.server.routes.requests

import org.jooq.types.ULong

data class GetImageRequest(
    val barcode: String?,
    val product_id: Long?,
    )
