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
import store.pengu.server.application.user
import store.pengu.server.daos.PantryDao
import store.pengu.server.data.PantryProduct
import store.pengu.server.routes.requests.CreateListRequest
import store.pengu.server.routes.requests.PantryRequest
import store.pengu.server.routes.responses.Response

fun Route.pantryRoutes(
    pantryDao: PantryDao,
) {

    authenticate {

        get<PantryLists> {
            val userId = call.user.id
            val pantries = withContext(Dispatchers.IO) {
                pantryDao.listPantries(userId.toLong())
            }
            call.respond(Response(pantries))
        }

        post<PantryLists> {
            val userId = call.user.id
            val listRequest = call.receive<CreateListRequest>()
            val pantry = withContext(Dispatchers.IO) {
                pantryDao.createPantry(listRequest, userId.toLong())
            }
            call.respond(Response(pantry))
        }

        post<ImportPantryList> { param ->
            val userId = call.user.id
            val pantry = withContext(Dispatchers.IO) {
                val pantry = pantryDao.getPantryByCode(param.code)

                if (pantryDao.userHasPantry(pantry.id, userId.toLong())) {
                    throw ConflictException("You already have this pantry")
                }

                pantryDao.addUserToPantry(pantry.id, userId.toLong())
                pantry
            }

            call.respond(Response(pantry))
        }

        get<PantryGet> { param ->
            val userId = call.user.id
            val entries = withContext(Dispatchers.IO) {
                if (!pantryDao.userHasPantry(param.id, userId.toLong())) {
                    throw NotFoundException("Pantry not found")
                }

                pantryDao.getPantryProducts(param.id)
            }

            call.respond(Response(entries))
        }

        /**
         *
         */



        put<UpdatePantry> {
            val pantry = call.receive<PantryRequest>()
            val response = withContext(Dispatchers.IO) {
                try {
                    pantryDao.updatePantry(pantry)
                } catch (e: Exception) {
                    throw BadRequestException(e.localizedMessage)
                }
            }
            call.respond(mapOf("data" to response))
        }

        get<GetPantry> { param ->
            val pantry = withContext(Dispatchers.IO) {
                pantryDao.getPantry(param.id)
            } ?: throw NotFoundException("Pantry with specified id not found")

            call.respond(mapOf("data" to pantry))
        }


        // Pantry Products

        post<PantryAddProduct> {
            val pantry_product = call.receive<PantryProduct>()
            val response = withContext(Dispatchers.IO) {
                try {
                    pantryDao.addPantryProduct(pantry_product)
                } catch (e: Exception) {
                    throw BadRequestException(e.localizedMessage)
                }
            }
            call.respond(mapOf("data" to response))
        }

        put<PantryUpdateProduct> {
            val pantry_product = call.receive<PantryProduct>()
            val response = withContext(Dispatchers.IO) {
                try {
                    pantryDao.updatePantryProduct(pantry_product)
                } catch (e: Exception) {
                    throw BadRequestException(e.localizedMessage)
                }
            }
            call.respond(mapOf("data" to response))
        }

        delete<PantryDeleteProduct> { param ->
            val response = withContext(Dispatchers.IO) {
                try {
                    pantryDao.deletePantryProduct(param.pantry_id, param.product_id)
                } catch (e: Exception) {
                    throw BadRequestException(e.localizedMessage)
                }
            }
            call.respond(mapOf("data" to response))
        }
    }

}
