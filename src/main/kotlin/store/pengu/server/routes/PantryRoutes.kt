package store.pengu.server.routes

import io.ktor.application.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import store.pengu.server.*
import store.pengu.server.daos.PantryDao
import store.pengu.server.data.Pantry
import store.pengu.server.data.Pantry_plus_user
import store.pengu.server.data.Pantry_x_User
import store.pengu.server.data.Product_x_Pantry

fun Route.pantryRoutes(
    pantryDao: PantryDao,
) {
    get<PantriesList> {
        val entries = withContext(Dispatchers.IO) {
            pantryDao.getPantries()
        }

        call.respond(mapOf("data" to entries))
    }

    get<PantryGet> { param ->
        val pantry = withContext(Dispatchers.IO) {
            pantryDao.getPantry(param.id)
        } ?: throw NotFoundException("Pantry with specified id not found")

        call.respond("data" to pantry)
    }

    post<PantryPost> {
        val pantry_plus_user = call.receive<Pantry_plus_user>()
        val pantry = Pantry(pantry_plus_user.id, pantry_plus_user.code, pantry_plus_user.name, pantry_plus_user.latitude, pantry_plus_user.longitude)
        val response = withContext(Dispatchers.IO) {
            try {
                val pantry2 = pantryDao.addPantry(pantry) ?: throw NotFoundException("Pantry with specified id not found")
                val pantry_x_user = Pantry_x_User(pantry2.id, pantry_plus_user.user_id)
                pantryDao.addPantryToUser(pantry_x_user)
            }
            catch (e: Exception) {
                throw BadRequestException(e.localizedMessage)
            }
        }
        call.respond(mapOf("data" to response))
    }

    put<PantryPut> {
        val pantry = call.receive<Pantry>()
        val response = withContext(Dispatchers.IO) {
            try {
                pantryDao.updatePantry(pantry)
            }
            catch (e: Exception) {
                throw BadRequestException(e.localizedMessage)
            }
        }
        call.respond("data" to response)
    }

    post<PantryPostProduct> {
        val product_x_pantry = call.receive<Product_x_Pantry>()
        val response = withContext(Dispatchers.IO) {
            try {
                pantryDao.addProductToPantry(product_x_pantry)
            }
            catch (e: Exception) {
                throw BadRequestException(e.localizedMessage)
            }
        }
        call.respond("data" to response)
    }

    put <PantryPutProduct> {
        val product_x_pantry = call.receive<Product_x_Pantry>()
        val response = withContext(Dispatchers.IO) {
            try {
                pantryDao.updateProductPantry(product_x_pantry)
            }
            catch (e: Exception) {
                throw BadRequestException(e.localizedMessage)
            }
        }
        call.respond("data" to response)
    }

    delete <PantryDeleteProduct> {
        val product_x_pantry = call.receive<Product_x_Pantry>()
        val response = withContext(Dispatchers.IO) {
            try {
                pantryDao.deleteProductPantry(product_x_pantry)
            }
            catch (e: Exception) {
                throw BadRequestException(e.localizedMessage)
            }
        }
        call.respond("data" to response)
    }

    get<PantryGetProducts> { param ->
        val entries = withContext(Dispatchers.IO) {
            pantryDao.getProductsInPantry(param.id)
        }

        call.respond(mapOf("data" to entries))
    }
}
