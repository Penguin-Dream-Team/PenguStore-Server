package store.pengu.server.routes.requests

import store.pengu.server.data.Cart

data class CartRequest(
    val cartItems: List<Cart>
)
