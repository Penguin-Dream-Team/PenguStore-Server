package store.pengu.server

import io.ktor.locations.*

@Location("/")
object Home

@Location("/dashboard")
object Dashboard

@Location("/users")
object UsersList

@Location("/users/{id}")
data class UserGet(val id: Long)

@Location("/users/add")
object UserPost

@Location("/users/update")
object UserPut

@Location("/users/login")
object UserLogin

@Location("/users/guestLogin")
object UserGuestLogin

@Location("/users/addPantry")
object UserPostPantry

@Location("/users/deletePantry")
object UserDeletePantry

@Location("/users/{id}/pantries")
data class UserGetPantries(val id: Long)

@Location("/users/{id}/shoppingList")
data class UserGenerateShoppingList(val id: Long)

@Location("/users/addShoppingList")
object UserPostShoppingList

@Location("/users/updateShoppingList")
object UserPutShoppingList

@Location("/users/deleteShoppingList")
object UserDeleteShoppingList

@Location("/users/{id}/ShoppingLists")
data class UserGetShoppingLists(val id: Long)

@Location("/users/{user_id}/ShoppingList/{shop_id}")
data class UserGetShoppingList(val user_id: Long, val shop_id: Long)



@Location("/pantries")
object PantriesList

@Location("/pantries/{id}")
data class PantryGet(val id: Long)

@Location("/pantries/add")
object PantryPost

@Location("/pantries/update")
object PantryPut

@Location("/pantries/addProduct")
object PantryPostProduct

@Location("/pantries/updateProduct")
object PantryPutProduct

@Location("/pantries/deleteProduct")
object PantryDeleteProduct

@Location("/pantries/{id}/products")
data class  PantryGetProducts(val id: Long)



@Location("/products")
object ProductsList

@Location("/products/{id}")
data class ProductGet(val id: Long)

@Location("/products/add")
object ProductPost

@Location("/products/update")
object ProductPut




@Location("/shops")
object ShopsList

@Location("/shops/{id}")
data class ShopGet(val id: Long)

@Location("/shops/add")
object ShopPost

@Location("/shops/update")
object ShopPut

@Location("/shops/addProduct")
object  ShopPostProduct

@Location("/shops/updateProduct")
object  ShopPutProduct

@Location("/shops/deleteProduct")
object  ShopDeleteProduct

@Location("/shops/{id}/products")
data class  ShopGetProducts(val id: Long)
