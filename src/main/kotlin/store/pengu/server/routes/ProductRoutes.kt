package store.pengu.server.routes

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.content.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jooq.tools.json.JSONObject
import org.jooq.types.ULong
import store.pengu.server.*
import store.pengu.server.application.user
import store.pengu.server.daos.ProductDao
import store.pengu.server.data.Product
import store.pengu.server.routes.requests.ImageRequest
import store.pengu.server.routes.responses.Response
import java.io.File

fun Route.productRoutes(
    productDao: ProductDao,
) {
    authenticate {
        get<ListProducts> {
            val userId = call.user.id

            val products = withContext(Dispatchers.IO) {
                productDao.getAllProducts(userId)
            }

            call.respond(Response(products))
        }
    }


    authenticate {
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

        put<AddBarcode> {
            val product = call.receive<Product>()
            val response = withContext(Dispatchers.IO) {
                try {
                    productDao.addBarcode(product)
                }
                catch (e: Exception) {
                    throw BadRequestException(e.localizedMessage)
                }
            }
            call.respond("data" to response)
        }

        get<GetProduct> { param ->
            val userId = call.user.id.toLong()
            val product = withContext(Dispatchers.IO) {
                productDao.getProduct(userId, param.id)
            } ?: throw NotFoundException("Product with specified id not found")

            call.respond(mapOf("data" to product))
        }


        // Images
        post<AddImage> {
            val multipartData = call.receiveMultipart()
            val fileDescription = JSONObject()
            var fileName = ""

            multipartData.forEachPart { part ->
                when (part) {
                    is PartData.FormItem -> {
                        fileDescription[part.name] = part.value
                    }
                    is PartData.FileItem -> {
                        fileName = part.originalFileName as String
                        val fileBytes = part.streamProvider().readBytes()
                        File("uploads/$fileName").writeBytes(fileBytes)
                    }
                }
            }

            val id : String = fileDescription["id"] as String

            var barcode: String? = null
            if (fileDescription["barcode"] != null)
                barcode = fileDescription["barcode"] as String

            var productId: String? = null
            if (fileDescription["product_id"] != null)
                productId = fileDescription["product_id"] as String

            val imageRequest = ImageRequest(
                ULong.valueOf(id), barcode, productId?.toLong(), "uploads/$fileName"
            )

            val response = withContext(Dispatchers.IO) {
                try {
                    productDao.addImage(imageRequest)
                }
                catch (e: Exception) {
                    throw BadRequestException(e.localizedMessage)
                }
            }
            call.respond("data" to response)

        }

        delete<DeleteImage> {
            val imageRequest = call.receive<ImageRequest>()
            val response = withContext(Dispatchers.IO) {
                try {
                    productDao.deleteImage(imageRequest)
                }
                catch (e: Exception) {
                    throw BadRequestException(e.localizedMessage)
                }
            }
            call.respond("data" to response)
        }

        get<GetProductImagesBarcode> { param ->
            val response = withContext(Dispatchers.IO) {
                try {
                    productDao.getImageBarcode(param.barcode)
                }
                catch (e: Exception) {
                    throw BadRequestException(e.localizedMessage)
                }
            }
            call.respond(mapOf("data" to response))
        }

        get<GetProductImagesProductId> { param ->
            val response = withContext(Dispatchers.IO) {
                try {
                    productDao.getImageProductId(param.product_id)
                }
                catch (e: Exception) {
                    throw BadRequestException(e.localizedMessage)
                }
            }
            call.respond(mapOf("data" to response))
        }

        // Ratings
        post<Ratings> { param ->
            val userId = call.user.id.toLong()
            try {
                if (param.rating < 1 || param.rating > 5) throw IllegalArgumentException("Rating must be between 1 - 5")

                val product = withContext(Dispatchers.IO) {
                    productDao.addRating(userId, param.barcode, param.rating)
                }
                call.respond(mapOf("data" to product))

            } catch (e: Exception) {
                throw BadRequestException(e.localizedMessage)
            }
        }
    }
}
