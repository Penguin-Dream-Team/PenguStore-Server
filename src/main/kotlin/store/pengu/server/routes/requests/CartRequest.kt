package store.pengu.server.routes.requests

import store.pengu.server.data.Cart

data class CartRequest(
    val requests: List<Cart>
)
