package store.pengu.server.routes

import io.ktor.application.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import store.pengu.server.UsersList
import store.pengu.server.application.features.ResourceAccessControl
import store.pengu.server.application.features.controlledAccess
import store.pengu.server.daos.UserDao

fun Route.userRoutes(
    userDao: UserDao,
) {

    controlledAccess(ResourceAccessControl.Type.API_TOKEN) {
        get<UsersList> {
            val entries = withContext(Dispatchers.IO) {
                userDao.getUsers()
            }

            call.respond(entries)
        }
    }
}
