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
import store.pengu.server.daos.ProductDao
import store.pengu.server.data.Product

fun Route.productRoutes(
    productDao: ProductDao,
) {

    authenticate {

        // Products

        post<AddProduct> {
            val userId = call.user.id.toLong()
            val product = call.receive<Product>()
            val response = withContext(Dispatchers.IO) {
                try {
                    val product2 = productDao.addProduct(product) ?: throw NotFoundException("Product with specified id not found")
                    productDao.connectProduct(product2.id, userId)
                }
                catch (e: Exception) {
                    throw BadRequestException(e.localizedMessage)
                }
            }
            call.respond(mapOf("data" to response))
        }

        put<UpdateProduct> {
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

        get<GetProduct> { param ->
            val product = withContext(Dispatchers.IO) {
                productDao.getProduct(param.id)
            } ?: throw NotFoundException("Product with specified id not found")

            call.respond(mapOf("data" to product))
        }
    }

}
