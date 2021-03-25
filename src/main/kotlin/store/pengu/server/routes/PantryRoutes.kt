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
import store.pengu.server.data.Pantry
import store.pengu.server.data.Product_x_Pantry
import store.pengu.server.data.User

fun Route.pantryRoutes(
    pantryDao: PantryDao,
) {

    get<PantriesList> {
        val entries = withContext(Dispatchers.IO) {
            pantryDao.getPantries()
        }

        call.respond(entries)
    }

    get<PantryGet> { param ->
        val pantry = withContext(Dispatchers.IO) {
            pantryDao.getPantry(param.id)
        } ?: throw NotFoundException("Pantry with specified id not found")

        call.respond(pantry)
    }

    post<PantryPost> {
        val pantry = call.receive<Pantry>()
        val response = withContext(Dispatchers.IO) {
            try {
                pantryDao.addPantry(pantry)
            }
            catch (e: Exception) {
                throw BadRequestException(e.localizedMessage)
            }
        }
        call.respond(response)
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
        call.respond(response)
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
        call.respond(response)
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
        call.respond(response)
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
        call.respond(response)
    }

    get<PantryGetProducts> { param ->
        val entries = withContext(Dispatchers.IO) {
            pantryDao.getProductsInPantry(param.id)
        }

        call.respond(entries)
    }

}
