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
import store.pengu.server.daos.ListDao
import store.pengu.server.daos.ShopDao
import store.pengu.server.data.ShoppingList
import store.pengu.server.routes.requests.*
import store.pengu.server.routes.responses.Response

fun Route.shopRoutes(
    listDao: ListDao,
    shopDao: ShopDao,
) {

    authenticate {

        get<ShoppingLists> {
            val userId = call.user.id
            val shops = withContext(Dispatchers.IO) {
                shopDao.listShops(userId)
            }

            call.respond(Response(shops))
        }

        post<ShoppingLists> {
            val userId = call.user.id
            val listRequest = call.receive<CreateListRequest>()
            val shoppingList = withContext(Dispatchers.IO) {
                shopDao.createShoppingList(listRequest, userId)
            }
            call.respond(Response(shoppingList))
        }

        post<ImportShoppingList> { param ->
            val userId = call.user.id

            val shoppingList = withContext(Dispatchers.IO) {
                val shoppingList = shopDao.getShoppingListByCode(userId, param.code)

                if (shopDao.userHasShoppingList(shoppingList.id, userId)) {
                    throw ConflictException("You already have this shopping list")
                }

                shopDao.addUserToShoppingList(shoppingList.id, userId)
                shoppingList
            }

            call.respond(Response(shoppingList))
        }

        get<ShoppingListGet> { param ->
            val userId = call.user.id
            val entries = withContext(Dispatchers.IO) {
                if (!shopDao.userHasShoppingList(param.id, userId)) {
                    throw NotFoundException("Shopping list not found")
                }

                shopDao.getShoppingListProducts(param.id, userId, call.requestUrl)
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
                } catch (e: Exception) {
                    throw BadRequestException(e.localizedMessage)
                }
            }
            call.respond(mapOf("data" to response))
        }

        put<UpdateSmartSortingEntries> { param ->
            val request = call.receive<UpdateSmartSortingRequest>()
            val response = withContext(Dispatchers.IO) {
                shopDao.updateSmartSortingEntries(param.shopping_list_id, param.barcode, request.remainingItems)
            }

            call.respond(mapOf("data" to response))
        }

        // Prices
        post<AddPrice> {
            val price_request = call.receive<PriceRequest>()
            val response = withContext(Dispatchers.IO) {
                try {
                    shopDao.addPrice(price_request)
                } catch (e: Exception) {
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
                } catch (e: Exception) {
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
        post<BuyCart> { param ->
            val userId = call.user.id
            val request = call.receive<CartRequest>()
            withContext(Dispatchers.IO) {
                shopDao.buyCart(userId, request.cartItems)
            }

            call.respond(Response("ok"))
        }


        // Queue
        post<JoinQueue> { param ->
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

        get<TimeQueue> { params ->
            val userId = call.user.id
            val entries = withContext(Dispatchers.IO) {
                val list = listDao.findNearbyShoppingList(userId, params.latitude, params.longitude)
                shopDao.timeQueue(list.latitude, list.longitude)
            }

            call.respond(mapOf("data" to entries))
        }
    }
}
