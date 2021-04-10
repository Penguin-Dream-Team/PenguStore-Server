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
import store.pengu.server.daos.ShopDao
import store.pengu.server.data.Crowd_Product_Price
import store.pengu.server.data.Shopping_list

fun Route.shopRoutes(
    shopDao: ShopDao,
) {

    authenticate {

        // Shopping Lists

        post<AddShoppingList> {
            val userId = call.user.id.toLong()
            val shopping_list = call.receive<Shopping_list>()
            val response = withContext(Dispatchers.IO) {
                try {
                    val shopping_list2 = shopDao.addShoppingList(shopping_list) ?: throw NotFoundException("Shopping List with specified id not found")
                    shopDao.connectShoppingList(shopping_list2.id, userId)
                }
                catch (e: Exception) {
                    throw BadRequestException(e.localizedMessage)
                }
            }
            call.respond(mapOf("data" to response))
        }

        put<UpdateShoppingList> {
            val shopping_list = call.receive<Shopping_list>()
            val response = withContext(Dispatchers.IO) {
                try {
                    shopDao.updateShoppingList(shopping_list)
                }
                catch (e: Exception) {
                    throw BadRequestException(e.localizedMessage)
                }
            }
            call.respond(mapOf("data" to response))
        }

        get<GenShoppingList> { param ->
            val userId = call.user.id.toLong()
            val entries = withContext(Dispatchers.IO) {
                val shoppingList = shopDao.getShoppingList(param.shopping_list_id) ?: throw NotFoundException("Shopping List with specified id not found")
                shopDao.genShoppingList(userId, shoppingList.latitude, shoppingList.longitude)
            }

            call.respond(mapOf("data" to entries))
        }


        // Prices

        post<AddPrice> {
            val crowd_Product_Price = call.receive<Crowd_Product_Price>()
            val response = withContext(Dispatchers.IO) {
                try {
                    shopDao.addPrice(crowd_Product_Price)
                }
                catch (e: Exception) {
                    throw BadRequestException(e.localizedMessage)
                }
            }
            call.respond(mapOf("data" to response))
        }

        put<UpdatePrice> {
            val crowd_Product_Price = call.receive<Crowd_Product_Price>()
            val response = withContext(Dispatchers.IO) {
                try {
                    shopDao.updatePrice(crowd_Product_Price)
                }
                catch (e: Exception) {
                    throw BadRequestException(e.localizedMessage)
                }
            }
            call.respond(mapOf("data" to response))
        }

        delete<DeletePrice> {
            val crowd_Product_Price = call.receive<Crowd_Product_Price>()
            val response = withContext(Dispatchers.IO) {
                try {
                    shopDao.deletePrice(crowd_Product_Price)
                }
                catch (e: Exception) {
                    throw BadRequestException(e.localizedMessage)
                }
            }
            call.respond(mapOf("data" to response))
        }

        get<GetShopPrices> { param ->
            val entries = withContext(Dispatchers.IO) {
                shopDao.getShopPrices(param.latitude, param.longitude)
            }

            call.respond(mapOf("data" to entries))
        }
    }

}
