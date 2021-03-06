package store.pengu.server.controllers

import store.pengu.server.utils.ResourceAccessControl
import io.ktor.application.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import store.pengu.server.UsersList
import store.pengu.server.daos.UserDao

fun Route.usersRoutes(
    userDao: UserDao,
) {

    get<UsersList> {
        val entries = withContext(Dispatchers.IO) {
            ResourceAccessControl.checkShouldHaveAccess(call.request, ResourceAccessControl.Type.API_TOKEN)
            userDao.getUsers()
        }

        call.respond(entries)
    }
}
