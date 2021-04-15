package store.pengu.server.routes.responses.lists

import store.pengu.server.data.UserList

data class UserListResponse(
    val type: UserListType,
    val list: UserList
)
