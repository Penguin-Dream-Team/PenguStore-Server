package store.pengu.server

import io.ktor.locations.*

@Location("/")
object Home

@Location("/dashboard")
object Dashboard

@Location("/users/update")
object UserUpdate


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

@Location("/user/pantries")
object UserGetPantries


// User Shopping List

@Location("/user/connectShoppingList/{shopping_list_id}")
data class UserConnectShoppingList(val shopping_list_id: Long)

@Location("/user/disconnectShoppingList/{shopping_list_id}")
data class UserDisconnectShoppingList(val shopping_list_id: Long)

@Location("/user/ShoppingLists")
object UserGetShoppingLists


// User Products

@Location("/user/connectProduct/{product_id}")
data class UserConnectProduct(val product_id: Long)

@Location("/user/disconnectProduct/{product_id}")
data class UserDisconnectProduct(val product_id: Long)

@Location("user/products")
object UserGetProducts


// Pantries

@Location("/pantries/add")
object AddPantry

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

@Location("product/{id}")
data class GetProduct(val id: Long)


// Shopping Lists

@Location("/shoppingLists/add")
object  AddShoppingList

@Location("/shoppingLists/update")
object  UpdateShoppingList

@Location("/shoppingList/{shopping_list_id}")
data class GenShoppingList(val shopping_list_id: Long)


// Prices

@Location("/prices/addPrice")
object AddPrice

@Location("/prices/updatePrice")
object UpdatePrice

@Location("/prices/deletePrice")
object DeletePrice

@Location("/prices/{latitude}/{longitude}")
data class GetShopPrices(val latitude: Float, val longitude: Float)

