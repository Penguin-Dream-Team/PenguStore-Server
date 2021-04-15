package store.pengu.server.routes

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import store.pengu.server.*
import store.pengu.server.application.features.guestOnly
import store.pengu.server.application.user
import store.pengu.server.daos.ListDao
import store.pengu.server.daos.UserDao
import store.pengu.server.routes.requests.LoginRequest
import store.pengu.server.routes.requests.RegisterRequest
import store.pengu.server.routes.requests.UserUpdateRequest
import store.pengu.server.routes.responses.Response
import store.pengu.server.routes.responses.lists.UserListResponse

fun Route.listRoutes(
    listDao: ListDao,
) {
    authenticate {
        get<LocationList> { params ->
            val userId = call.user.id
            val (type, list) = withContext(Dispatchers.IO) {
                listDao.findNearbyList(userId.toLong(), params.latitude, params.longitude)
            }
            call.respond(UserListResponse(type, list))
        }
    }
}
