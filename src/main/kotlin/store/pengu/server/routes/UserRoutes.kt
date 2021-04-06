package store.pengu.server.routes

import io.ktor.application.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import store.pengu.server.*
import store.pengu.server.daos.UserDao
import store.pengu.server.data.Pantry_x_User
import store.pengu.server.data.Pantry_x_User_Request
import store.pengu.server.data.Shopping_list
import store.pengu.server.data.User

fun Route.userRoutes(
    userDao: UserDao,
) {
    get<UsersList> {
        val entries = withContext(Dispatchers.IO) {
            userDao.getUsers()
        }

        call.respond(mapOf("data" to entries))
    }

    get<UserGet> { param ->
        val user = withContext(Dispatchers.IO) {
            userDao.getUser(param.id)
        } ?: throw NotFoundException("User with specified id not found")

        call.respond("data" to user)
    }

    post<UserPost> {
        val user = call.receive<User>()
        val response = withContext(Dispatchers.IO) {
            try {
                userDao.addUser(user)
            }
            catch (e: Exception) {
                throw BadRequestException(e.localizedMessage)
            } ?: throw NotFoundException("User with specified id not found")
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
        call.respond("data" to response)
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

    post<UserGuestLogin>{
        val user = call.receive<User>()
        val response = withContext(Dispatchers.IO) {
            try {
                userDao.loginGuest(user)
            }
            catch (e: Exception) {
                throw BadRequestException(e.localizedMessage)
            }
        }
        call.respond(response)
    }

    post<UserPostPantry> {
        val request = call.receive<Pantry_x_User_Request>()
        val pantry = userDao.getPantryByCode(request.pantryCode) ?: throw NotFoundException("Pantry with specified code not found")
        val pantry_x_user = Pantry_x_User(pantry.id, request.userId)
        val response = withContext(Dispatchers.IO) {
            try {
                userDao.addPantryToUser(pantry_x_user)
            }
            catch (e: Exception) {
                throw BadRequestException(e.localizedMessage)
            }
        }
        call.respond(mapOf("data" to response))
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
        call.respond("data" to response)
    }

    get<UserGetPantries>{ param ->
        val entries = withContext(Dispatchers.IO) {
            userDao.getUserPantries(param.id)
        }

        call.respond(mapOf("data" to entries))
    }

    get<UserGenerateShoppingList>{ param ->
        val entries = withContext(Dispatchers.IO) {
            userDao.generateShoppingList(param.id)
        }

        call.respond(mapOf("data" to entries))
    }

    post<UserPostShoppingList>{
        val shopping_list = call.receive<Shopping_list>()
        val response = withContext(Dispatchers.IO) {
            try {
                userDao.addShoppingList(shopping_list)
            }
            catch (e: Exception) {
                throw BadRequestException(e.localizedMessage)
            }
        }
        call.respond("data" to response)
    }

    put<UserPutShoppingList>{
        val shopping_list = call.receive<Shopping_list>()
        val response = withContext(Dispatchers.IO) {
            try {
                userDao.updateShopppingList(shopping_list)
            }
            catch (e: Exception) {
                throw BadRequestException(e.localizedMessage)
            }
        }
        call.respond("data" to response)
    }


    delete<UserDeleteShoppingList>{
        val shopping_list = call.receive<Shopping_list>()
        val response = withContext(Dispatchers.IO) {
            try {
                userDao.deleteShoppingList(shopping_list)
            }
            catch (e: Exception) {
                throw BadRequestException(e.localizedMessage)
            }
        }
        call.respond("data" to response)
    }

    get<UserGetShoppingLists>{ param ->
        val entries = withContext(Dispatchers.IO) {
            userDao.getShoppingLists(param.id)
        }

        call.respond(mapOf("data" to entries))
    }
    get<UserGetShoppingList>{ param ->
        val entries = withContext(Dispatchers.IO) {
            userDao.getShoppingList(param.user_id, param.shop_id)
        }

        call.respond(mapOf("data" to entries))
    }
}
