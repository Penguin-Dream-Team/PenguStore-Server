package store.pengu.server.application

import io.ktor.routing.*
import org.koin.core.Koin
import store.pengu.server.controllers.usersRoutes
import store.pengu.server.daos.UserDao

fun Route.loadRoutes(koin: Koin) {
    val userDao = koin.get<UserDao>()

    /* CONTROLLERS */
    usersRoutes(userDao = userDao)
}
