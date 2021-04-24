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
import java.io.File

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
            val product = withContext(Dispatchers.IO) {
                productDao.getProduct(param.id)
            } ?: throw NotFoundException("Product with specified id not found")

            call.respond(mapOf("data" to product))
        }


        // Images

        post<AddImage> {
            val multipartData = call.receiveMultipart()
            var fileDescription = JSONObject()
            var fileName = ""

            multipartData.forEachPart { part ->
                when (part) {
                    is PartData.FormItem -> {
                        fileDescription[part.name] = part.value
                    }
                    is PartData.FileItem -> {
                        fileName = part.originalFileName as String
                        var fileBytes = part.streamProvider().readBytes()
                        File("uploads/$fileName").writeBytes(fileBytes)
                    }
                }
            }

            var id : String = fileDescription["id"] as String

            var barcode: String? = null
            if (fileDescription["barcode"] != null)
                barcode = fileDescription["barcode"] as String

            var product_id: String? = null
            if (fileDescription["product_id"] != null)
                product_id = fileDescription["product_id"] as String

            val imageRequest = ImageRequest(
                ULong.valueOf(id), barcode, product_id?.toLong(), "uploads/$fileName"
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
    }

}
