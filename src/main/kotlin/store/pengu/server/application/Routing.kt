package store.pengu.server.application

import io.ktor.routing.*
import org.koin.core.Koin
import store.pengu.server.daos.*
import store.pengu.server.routes.*

fun Route.loadRoutes(koin: Koin) {
    val userDao = koin.get<UserDao>()
    val pantryDao = koin.get<PantryDao>()
    val productDao = koin.get<ProductDao>()
    val shopDao = koin.get<ShopDao>()
    val listDao = koin.get<ListDao>()

    /* CONTROLLERS */
    userRoutes(userDao = userDao)
    listRoutes(listDao = listDao)
    pantryRoutes(pantryDao = pantryDao)
    productRoutes(productDao = productDao)
    shopRoutes(shopDao = shopDao)
}
