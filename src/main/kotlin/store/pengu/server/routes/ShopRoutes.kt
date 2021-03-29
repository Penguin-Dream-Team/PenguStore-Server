package store.pengu.server.routes

import io.ktor.application.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import store.pengu.server.*
import store.pengu.server.daos.ShopDao
import store.pengu.server.data.Shop
import store.pengu.server.data.Shop_x_Product

fun Route.shopRoutes(
    shopDao: ShopDao,
) {
    get<ShopsList> {
        val entries = withContext(Dispatchers.IO) {
            shopDao.getShops()
        }

        call.respond(mapOf("data" to entries))
    }

    get<ShopGet> { param ->
        val shop = withContext(Dispatchers.IO) {
            shopDao.getShop(param.id)
        } ?: throw NotFoundException("Shop with specified id not found")

        call.respond("data" to shop)
    }

    post<ShopPost> {
        val shop = call.receive<Shop>()
        val response = withContext(Dispatchers.IO) {
            try {
                shopDao.addShop(shop)
            }
            catch (e: Exception) {
                throw BadRequestException(e.localizedMessage)
            }
        }
        call.respond("data" to response)
    }

    put<ShopPut> {
        val shop = call.receive<Shop>()
        val response = withContext(Dispatchers.IO) {
            try {
                shopDao.updateShop(shop)
            }
            catch (e: Exception) {
                throw BadRequestException(e.localizedMessage)
            }
        }
        call.respond("data" to response)
    }

    post<ShopPostProduct> {
        val shop_x_product = call.receive<Shop_x_Product>()
        val response = withContext(Dispatchers.IO) {
            try {
                shopDao.addProductToShop(shop_x_product)
            }
            catch (e: Exception) {
                throw BadRequestException(e.localizedMessage)
            }
        }
        call.respond("data" to response)
    }

    put<ShopPutProduct> {
        val shop_x_product = call.receive<Shop_x_Product>()
        val response = withContext(Dispatchers.IO) {
            try {
                shopDao.updateProductShop(shop_x_product)
            }
            catch (e: Exception) {
                throw BadRequestException(e.localizedMessage)
            }
        }
        call.respond("data" to response)
    }

    delete<ShopDeleteProduct> {
        val shop_x_product = call.receive<Shop_x_Product>()
        val response = withContext(Dispatchers.IO) {
            try {
                shopDao.deleteProductShop(shop_x_product)
            }
            catch (e: Exception) {
                throw BadRequestException(e.localizedMessage)
            }
        }
        call.respond("data" to response)
    }

    get<ShopGetProducts> { param ->
        val entries = withContext(Dispatchers.IO) {
            shopDao.getShopProducts(param.id)
        }

        call.respond("data" to entries)
    }
}
