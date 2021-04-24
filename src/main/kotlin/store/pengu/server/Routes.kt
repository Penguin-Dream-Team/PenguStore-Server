package store.pengu.server

import io.ktor.locations.*

@Location("/")
object Home

@Location("/dashboard")
object Dashboard

@Location("/users/update")
object UserUpdate

@Location("/profile")
object UserProfile

@Location("/lists/find")
data class LocationList(val latitude: Double, val longitude: Double)

@Location("/lists/pantries")
object PantryLists


@Location("/lists/shops")
object ShoppingLists

// User Login

@Location("/login")
object UserLogin

@Location("/register/guest")
object UserGuestRegister

@Location("/login/refresh")
object UserLoginRefresh


// User Pantry

@Location("/user/connectPantry/{code}")
data class UserConnectPantry(val code: String)

@Location("/user/disconnectPantry/{pantry_id}")
data class UserDisconnectPantry(val pantry_id: Long)


// User Shopping List

@Location("/user/connectShoppingList/{shopping_list_id}")
data class UserConnectShoppingList(val shopping_list_id: Long)

@Location("/user/disconnectShoppingList/{shopping_list_id}")
data class UserDisconnectShoppingList(val shopping_list_id: Long)

// User Products

@Location("/user/connectProduct/{product_id}")
data class UserConnectProduct(val product_id: Long)

@Location("/user/disconnectProduct/{product_id}")
data class UserDisconnectProduct(val product_id: Long)

@Location("user/products")
object UserGetProducts


// Pantries

@Location("/pantries/update")
object UpdatePantry

@Location("/pantry/{id}")
data class GetPantry(val id: Long)


// Pantry Products

@Location("/pantries/addProduct")
object  PantryAddProduct

@Location("/pantries/updateProduct")
object PantryUpdateProduct

@Location("/pantry/{pantry_id}/deleteProduct/{product_id}")
data class PantryDeleteProduct(val pantry_id: Long, val product_id: Long)

@Location("/pantries/{id}/products")
data class PantryGetProducts(val id: Long)


// Products

@Location("/products/add")
object AddProduct

@Location("/products/update")
object UpdateProduct

@Location("/product/addBarcode")
object AddBarcode

@Location("product/{id}")
data class GetProduct(val id: Long)


// Shopping Lists

@Location("/shoppingLists/update")
object  UpdateShoppingList

@Location("/shoppingList/{shopping_list_id}")
data class GenShoppingList(val shopping_list_id: Long)


// Prices

@Location("/prices/addPrice")
object AddPrice

@Location("/prices/deletePrice")
object DeletePrice

@Location("/prices/{latitude}/{longitude}")
data class GetShopPrices(val latitude: Double, val longitude: Double)


// Images

@Location("/images/addImage")
object AddImage

@Location("/images/deleteImage")
object DeleteImage

@Location("/images/updateImage")
object UpdateImage

@Location("/images/get")
object GetProductImages



// Carts

@Location("/cart")
object BuyCart

// Queue

@Location("/queue/join/{latitude}/{longitude}/{num_items}")
data class JoinQueue(val latitude: Double, val longitude: Double, val num_items: Int)

@Location("/queue/leave")
object LeaveQueue

@Location("/queue/time/{latitude}/{longitude}")
data class TimeQueue(val latitude: Double, val longitude: Double)
