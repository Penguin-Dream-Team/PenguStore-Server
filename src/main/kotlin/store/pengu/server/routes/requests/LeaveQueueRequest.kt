package store.pengu.server.routes.requests

import store.pengu.server.data.Cart

data class LeaveQueueRequest(
    val num_items: Int,
    val time: Int,
    val latitude: Float,
    val longitude: Float
)
