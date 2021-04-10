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
import store.pengu.server.data.Pantry
import store.pengu.server.data.Pantry_Product
import store.pengu.server.routes.requests.PantryRequest

fun Route.pantryRoutes(
    pantryDao: PantryDao,
) {

    authenticate {


        // Pantries

        post<AddPantry> {
            val userId = call.user.id.toLong()
            val pantry = call.receive<PantryRequest>()
            val response = withContext(Dispatchers.IO) {
                try {
                    val pantry2 = pantryDao.addPantry(pantry) ?: throw NotFoundException("Pantry with specified id not found")
                    pantryDao.connectPantryToUser(pantry2.id, userId)
                }
                catch (e: Exception) {
                    throw BadRequestException(e.localizedMessage)
                }
            }
            call.respond(mapOf("data" to response))
        }

        put<UpdatePantry> {
            val pantry = call.receive<PantryRequest>()
            val response = withContext(Dispatchers.IO) {
                try {
                    pantryDao.updatePantry(pantry)
                }
                catch (e: Exception) {
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
            val pantry_product = call.receive<Pantry_Product>()
            val response = withContext(Dispatchers.IO) {
                try {
                    pantryDao.addPantryProduct(pantry_product)
                }
                catch (e: Exception) {
                    throw BadRequestException(e.localizedMessage)
                }
            }
            call.respond(mapOf("data" to response))
        }

        put <PantryUpdateProduct> {
            val pantry_product = call.receive<Pantry_Product>()
            val response = withContext(Dispatchers.IO) {
                try {
                    pantryDao.updatePantryProduct(pantry_product)
                }
                catch (e: Exception) {
                    throw BadRequestException(e.localizedMessage)
                }
            }
            call.respond(mapOf("data" to response))
        }

        delete <PantryDeleteProduct> { param ->
            val response = withContext(Dispatchers.IO) {
                try {
                    pantryDao.deletePantryProduct(param.pantry_id, param.product_id)
                }
                catch (e: Exception) {
                    throw BadRequestException(e.localizedMessage)
                }
            }
            call.respond(mapOf("data" to response))
        }

        get<PantryGetProducts> { param ->
            val entries = withContext(Dispatchers.IO) {
                pantryDao.getPantryProducts(param.id)
            }

            call.respond(mapOf("data" to entries))
        }
    }

}
