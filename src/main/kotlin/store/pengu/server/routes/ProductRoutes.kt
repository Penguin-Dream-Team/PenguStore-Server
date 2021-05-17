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
import store.pengu.server.routes.requests.AddProductToPantryRequest
import store.pengu.server.routes.requests.AddProductToShopRequest
import store.pengu.server.routes.requests.CreateProductRequest
import store.pengu.server.routes.requests.ImageRequest
import store.pengu.server.routes.responses.Response
import java.io.File
import java.time.Instant
import java.util.*

fun Route.productRoutes(
    productDao: ProductDao,
) {
    authenticate {
        get<ListProducts> {
            val userId = call.user.id

            val products = withContext(Dispatchers.IO) {
                productDao.getAllProducts(userId, call.requestUrl)
            }

            call.respond(Response(products))
        }

        post<CreateProduct> {
            val userId = call.user.id
            val request = call.receive<CreateProductRequest>()
            val response = withContext(Dispatchers.IO) {
                val image = request.image?.let {
                    val bytes = Base64.getMimeDecoder().decode(it)
                    val imageName = request.name.run { replace(" ", "-") }
                    val path = "uploads/${imageName}_${Instant.now().epochSecond}.jpg"
                    File(path).writeBytes(bytes)
                    path
                }
                try {
                    productDao.createProduct(userId, request.name, request.barcode, image, call.requestUrl)
                } catch (e: Exception) {
                    image?.let {
                        File(it).delete()
                    }
                    throw e
                }
            }
            call.respond(Response(response))
        }

        post<ProductPantryLists> { params ->
            val userId = call.user.id
            val request = call.receiveOrNull<AddProductToPantryRequest>()
                ?: throw BadRequestException("Missing request information")

            val products = withContext(Dispatchers.IO) {
                productDao.addProductToPantryList(
                    userId,
                    params.id,
                    request.pantryId,
                    request.haveQuantity,
                    request.needQuantity
                )
            }

            call.respond(Response(products))
        }

        get<MissingProductPantryList> { params ->
            val userId = call.user.id

            val products = withContext(Dispatchers.IO) {
                productDao.getMissingProductPantryList(userId, params.id, call.requestUrl)
            }

            call.respond(Response(products))
        }

        get<ProductPantryLists> { params ->
            val userId = call.user.id

            val products = withContext(Dispatchers.IO) {
                productDao.getProductPantryLists(userId, params.id)
            }

            call.respond(Response(products))
        }

        post<ProductShoppingLists> { params ->
            val userId = call.user.id
            val request = call.receiveOrNull<AddProductToShopRequest>()
                ?: throw BadRequestException("Missing request information")

            val products = withContext(Dispatchers.IO) {
                productDao.addProductToShoppingList(
                    userId,
                    params.id,
                    request.shoppingListId,
                    request.price
                )
            }

            call.respond(Response(products))
        }

        get<ProductShoppingLists> { params ->
            val userId = call.user.id

            val products = withContext(Dispatchers.IO) {
                productDao.getProductShoppingLists(userId, params.id)
            }

            call.respond(Response(products))
        }

        get<GetProduct> { params ->
            val userId = call.user.id
            val product = withContext(Dispatchers.IO) {
                productDao.getProduct(userId, params.id, call.requestUrl)
            }

            call.respond(Response(product))
        }

        get<ProductImages> { params ->
            val userId = call.user.id
            val images = withContext(Dispatchers.IO) {
                productDao.getProductImages(userId, params.id, call.requestUrl)
            }

            call.respond(Response(images))
        }

        post<RateProduct> { params ->
            val userId = call.user.id
            if (params.rating !in 0..5) throw BadRequestException("Rating must be between 0 - 5")

            val ratings = withContext(Dispatchers.IO) {
                productDao.rateProduct(userId, params.id, params.rating)
            }
            call.respond(Response(ratings))
        }

        get<GetProductSuggestion> { param ->
            val userId = call.user.id
            val suggestion = withContext(Dispatchers.IO) {
                productDao.getProductSuggestion(userId, param.barcode, call.requestUrl)
            }

            call.respond(Response(suggestion))
        }
    }

    /**
     * HERE
     */

    authenticate {
        put<UpdateProduct> {
            val product = call.receive<Product>()
            val response = withContext(Dispatchers.IO) {
                try {
                    productDao.updateProduct(product)
                } catch (e: Exception) {
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
                } catch (e: Exception) {
                    throw BadRequestException(e.localizedMessage)
                }
            }
            call.respond("data" to response)
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

            val id: String = fileDescription["id"] as String

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
                } catch (e: Exception) {
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
                } catch (e: Exception) {
                    throw BadRequestException(e.localizedMessage)
                }
            }
            call.respond("data" to response)
        }

    }
}
