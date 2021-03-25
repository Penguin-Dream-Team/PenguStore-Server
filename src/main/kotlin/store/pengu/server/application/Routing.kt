package store.pengu.server.application

import io.ktor.routing.*
import org.koin.core.Koin
import store.pengu.server.daos.PantryDao
import store.pengu.server.daos.ProductDao
import store.pengu.server.daos.ShopDao
import store.pengu.server.routes.userRoutes
import store.pengu.server.daos.UserDao
import store.pengu.server.routes.pantryRoutes
import store.pengu.server.routes.productRoutes
import store.pengu.server.routes.shopRoutes

fun Route.loadRoutes(koin: Koin) {
    val userDao = koin.get<UserDao>()
    val pantryDao = koin.get<PantryDao>()
    val productDao = koin.get<ProductDao>()
    val shopDao = koin.get<ShopDao>()

    /* CONTROLLERS */
    userRoutes(userDao = userDao)
    pantryRoutes(pantryDao = pantryDao)
    productRoutes(productDao = productDao)
    shopRoutes(shopDao = shopDao)
}
