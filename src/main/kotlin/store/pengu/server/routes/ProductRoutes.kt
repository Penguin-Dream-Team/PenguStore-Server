package store.pengu.server.routes

import io.ktor.application.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import store.pengu.server.*
import store.pengu.server.daos.ProductDao
import store.pengu.server.data.Product

fun Route.productRoutes(
    productDao: ProductDao,
) {
    get<ProductsList> {
        val entries = withContext(Dispatchers.IO) {
            productDao.getProducts()
        }

        call.respond(mapOf("data" to entries))
    }

    get<ProductGet> { param ->
        val product = withContext(Dispatchers.IO) {
            productDao.getProduct(param.id)
        } ?: throw NotFoundException("Product with specified id not found")

        call.respond("data" to product)
    }

    post<ProductPost> {
        val product = call.receive<Product>()
        val response = withContext(Dispatchers.IO) {
            try {
                productDao.addProduct(product)
            }
            catch (e: Exception) {
                throw BadRequestException(e.localizedMessage)
            }
        }
        call.respond("data" to response)
    }

    put<ProductPut> {
        val product = call.receive<Product>()
        val response = withContext(Dispatchers.IO) {
            try {
                productDao.updateProduct(product)
            }
            catch (e: Exception) {
                throw BadRequestException(e.localizedMessage)
            }
        }
        call.respond("data" to response)
    }
}
