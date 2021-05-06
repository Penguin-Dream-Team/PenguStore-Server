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
import store.pengu.server.data.ShoppingList
import store.pengu.server.routes.requests.CartRequest
import store.pengu.server.routes.requests.CreateListRequest
import store.pengu.server.routes.requests.LeaveQueueRequest
import store.pengu.server.routes.requests.PriceRequest
import store.pengu.server.routes.responses.Response

fun Route.shopRoutes(
    shopDao: ShopDao,
) {

    authenticate {

        get<ShoppingLists> {
            val userId = call.user.id
            val shops = withContext(Dispatchers.IO) {
                shopDao.listShops(userId.toLong())
            }

            call.respond(Response(shops))
        }

        post<ShoppingLists> {
            val userId = call.user.id
            val listRequest = call.receive<CreateListRequest>()
            val shoppingList = withContext(Dispatchers.IO) {
                shopDao.createShoppingList(listRequest, userId.toLong())
            }
            call.respond(Response(shoppingList))
        }

        post<ImportShoppingList> { param ->
            val userId = call.user.id

            val shoppingList = withContext(Dispatchers.IO) {
                val shoppingList = shopDao.getShoppingListByCode(userId.toLong(), param.code)

                if (shopDao.userHasShoppingList(shoppingList.id, userId.toLong())) {
                    throw ConflictException("You already have this shopping list")
                }

                shopDao.addUserToShoppingList(shoppingList.id, userId.toLong())
                shoppingList
            }

            call.respond(Response(shoppingList))
        }

        get<ShoppingListGet> { param ->
            val userId = call.user.id
            val entries = withContext(Dispatchers.IO) {
                if (!shopDao.userHasShoppingList(param.id, userId.toLong())) {
                    throw NotFoundException("Shopping list not found")
                }

                shopDao.getShoppingListProducts(param.id, userId.toLong())
            }

            call.respond(Response(entries))
        }



        /**
         * here
         */

        put<UpdateShoppingList> {
            val shopping_list = call.receive<ShoppingList>()
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

        // Prices
        post<AddPrice> {
            val price_request = call.receive<PriceRequest>()
            val response = withContext(Dispatchers.IO) {
                try {
                    shopDao.addPrice(price_request)
                }
                catch (e: Exception) {
                    throw BadRequestException(e.localizedMessage)
                }
            }
            call.respond(mapOf("data" to response))
        }

        delete<DeletePrice> {
            val price_request = call.receive<PriceRequest>()
            val response = withContext(Dispatchers.IO) {
                try {
                    shopDao.deletePrice(price_request)
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


        // Carts
        post<BuyCart> {
            val cart_request = call.receive<CartRequest>()
            val entries = withContext(Dispatchers.IO) {
                shopDao.buyCart(cart_request.requests)
            }

            call.respond(mapOf("data" to entries))
        }

        get<GetProductSuggestion> { param ->
            val suggestion = withContext(Dispatchers.IO) {
                shopDao.getProductSuggestion(param.product_id)
            }

            call.respond(mapOf("data" to suggestion))
        }


        // Queue
        post<JoinQueue> { param->
            val entries = withContext(Dispatchers.IO) {
                shopDao.joinQueue(param.latitude, param.longitude, param.num_items)
            }

            call.respond(mapOf("data" to entries))
        }

        post<LeaveQueue> {
            val leaveQueueRequest = call.receive<LeaveQueueRequest>()
            val entries = withContext(Dispatchers.IO) {
                shopDao.leaveQueue(leaveQueueRequest)
            }

            call.respond(mapOf("data" to entries))
        }

        get<TimeQueue> { param->
            val entries = withContext(Dispatchers.IO) {
                shopDao.timeQueue(param.latitude, param.longitude)
            }

            call.respond(mapOf("data" to entries))
        }
    }
}
