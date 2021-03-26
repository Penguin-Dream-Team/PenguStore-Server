package store.pengu.server.routes

import io.ktor.application.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import store.pengu.server.*
import store.pengu.server.application.features.ResourceAccessControl
import store.pengu.server.application.features.controlledAccess
import store.pengu.server.daos.PantryDao
import store.pengu.server.daos.UserDao
import store.pengu.server.data.Pantry_x_User
import store.pengu.server.data.Pantry_x_User_Request
import store.pengu.server.data.User

fun Route.userRoutes(
    userDao: UserDao,
) {

    get<UsersList> {
        val entries = withContext(Dispatchers.IO) {
            userDao.getUsers()
        }

        call.respond(entries)
    }

    get<UserGet> { param ->
        val user = withContext(Dispatchers.IO) {
            userDao.getUser(param.id)
        } ?: throw NotFoundException("User with specified id not found")

        call.respond(user)
    }

    post<UserPost> {
        val user = call.receive<User>()
        val response = withContext(Dispatchers.IO) {
            try {
                userDao.addUser(user)
            }
            catch (e: Exception) {
                throw BadRequestException(e.localizedMessage)
            }
        }
        call.respond(response)
    }

    put<UserPut> {
        val user = call.receive<User>()
        val response = withContext(Dispatchers.IO) {
            try {
                userDao.updateUser(user)
            }
            catch (e: Exception) {
                throw BadRequestException(e.localizedMessage)
            }
        }
        call.respond(response)
    }

    post<UserLogin>{
        val user = call.receive<User>()
        val response = withContext(Dispatchers.IO) {
            try {
                userDao.loginUser(user)
            }
            catch (e: Exception) {
                throw BadRequestException(e.localizedMessage)
            }
        }
        call.respond(response)
    }

    post<UserPostPantry> {
        val request = call.receive<Pantry_x_User_Request>()
        val pantry = userDao.getPantryByCode(request.code) ?: throw NotFoundException("Pantry with specified code not found")
        val pantry_x_user = Pantry_x_User(request.user_id, pantry.id)
        val response = withContext(Dispatchers.IO) {
            try {

                userDao.addPantryToUser(pantry_x_user)
            }
            catch (e: Exception) {
                throw BadRequestException(e.localizedMessage)
            }
        }
        call.respond(response)
    }

    delete<UserDeletePantry>{
        val pantry_x_user = call.receive<Pantry_x_User>()
        val response = withContext(Dispatchers.IO) {
            try {
                userDao.deletePantryUser(pantry_x_user)
            }
            catch (e: Exception) {
                throw BadRequestException(e.localizedMessage)
            }
        }
        call.respond(response)
    }

    get<UserGetPantries>{ param ->
        val entries = withContext(Dispatchers.IO) {
            userDao.getUserPantries(param.id)
        }

        call.respond(entries)
    }

    get<UserGenerateShoppingList>{ param ->
        val entries = withContext(Dispatchers.IO) {
            userDao.generateShoppingList(param.id)
        }

        call.respond(entries)
    }

}
