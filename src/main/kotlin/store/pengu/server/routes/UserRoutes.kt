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
import store.pengu.server.daos.UserDao
import store.pengu.server.routes.requests.LoginRequest
import store.pengu.server.routes.requests.RegisterRequest
import store.pengu.server.routes.requests.UserUpdateRequest
import store.pengu.server.routes.responses.Response

fun Route.userRoutes(
    userDao: UserDao,
) {


    authenticate {

        get<Dashboard> {
            val profile = withContext(Dispatchers.IO) {
                userDao.getProfile(call.user.id)
            }
            call.respond(Response(profile))
        }

        put<UserUpdate> {
            val userId = call.user.id
            val profile = withContext(Dispatchers.IO) {
                val updateRequest =
                    call.receiveOrNull<UserUpdateRequest>() ?: throw BadRequestException("Invalid update request")
                userDao.updateUser(
                    userId,
                    email = updateRequest.email,
                    username = updateRequest.username,
                    password = updateRequest.password
                )
            }
            call.respond(profile)
        }

        get<UserProfile> {
            val userId = call.user.id
            val profile = withContext(Dispatchers.IO) {
                userDao.getProfile(userId)
            }
            call.respond(profile)
        }
    }

    guestOnly {
        post<UserLogin> {
            val response = withContext(Dispatchers.IO) {
                val loginRequest =
                    call.receiveOrNull<LoginRequest>() ?: throw BadRequestException("Invalid login request")
                userDao.loginUser(loginRequest.username, loginRequest.password)
            }
            call.respond(response)
        }

        post<UserGuestRegister> {
            val response = withContext(Dispatchers.IO) {
                val registerRequest =
                    call.receiveOrNull<RegisterRequest>() ?: throw BadRequestException("Invalid register request")
                userDao.registerGuestUser(registerRequest.username)
            }
            call.respond(response)
        }

    }

    authenticate {
        // User Pantry

        post<UserConnectPantry> { param ->
            val userId = call.user.id.toLong()
            val pantry = userDao.getPantryByCode(param.code)
                ?: throw NotFoundException("Pantry with specified code not found")

            val response = withContext(Dispatchers.IO) {
                try {
                    userDao.connectPantryToUser(pantry.id, userId)
                } catch (e: Exception) {
                    throw BadRequestException(e.localizedMessage)
                }
            }
            call.respond(mapOf("data" to response))
        }

        delete<UserDisconnectPantry> { param ->
            val userId = call.user.id.toLong()
            val response = withContext(Dispatchers.IO) {
                try {
                    userDao.disconnectPantryUser(param.pantry_id, userId)
                } catch (e: Exception) {
                    throw BadRequestException(e.localizedMessage)
                }
            }
            call.respond(mapOf("data" to response))
        }

        get<UserGetPantries> {
            val userId = call.user.id.toLong()
            val entries = withContext(Dispatchers.IO) {
                userDao.getUserPantries(userId)
            }
            call.respond(mapOf("data" to entries))
        }


        // User Shopping List

        post<UserConnectShoppingList> { param ->
            val userId = call.user.id.toLong()
            val response = withContext(Dispatchers.IO) {
                try {
                    userDao.connectShoppingList(param.shopping_list_id, userId)
                } catch (e: Exception) {
                    throw BadRequestException(e.localizedMessage)
                }
            }
            call.respond(mapOf("data" to response))
        }

        delete<UserDisconnectShoppingList> { param ->
            val userId = call.user.id.toLong()
            val response = withContext(Dispatchers.IO) {
                try {
                    userDao.disconnectShoppingList(param.shopping_list_id, userId)
                } catch (e: Exception) {
                    throw BadRequestException(e.localizedMessage)
                }
            }
            call.respond(mapOf("data" to response))
        }

        get<UserGetShoppingLists> {
            val userId = call.user.id.toLong()
            val entries = withContext(Dispatchers.IO) {
                userDao.getShoppingLists(userId)
            }

            call.respond(mapOf("data" to entries))
        }


        // User Products

        post<UserConnectProduct> { param ->
            val userId = call.user.id.toLong()
            val response = withContext(Dispatchers.IO) {
                try {
                    userDao.connectProduct(param.product_id, userId)
                } catch (e: Exception) {
                    throw BadRequestException(e.localizedMessage)
                }
            }
            call.respond(mapOf("data" to response))
        }

        delete<UserDisconnectProduct> { param ->
            val userId = call.user.id.toLong()
            val response = withContext(Dispatchers.IO) {
                try {
                    userDao.disconnectProduct(param.product_id, userId)
                } catch (e: Exception) {
                    throw BadRequestException(e.localizedMessage)
                }
            }
            call.respond(mapOf("data" to response))
        }

        get<UserGetProducts> {
            val userId = call.user.id.toLong()
            val entries = withContext(Dispatchers.IO) {
                userDao.getProducts(userId)
            }

            call.respond(mapOf("data" to entries))
        }
    }

}
