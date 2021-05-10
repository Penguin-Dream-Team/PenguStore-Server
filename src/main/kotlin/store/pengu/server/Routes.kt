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

/**
 * Pantry Lists
 */
@Location("/lists/pantries")
object PantryLists

@Location("/lists/pantries/import/{code}")
data class ImportPantryList(val code: String)

@Location("/lists/pantries/{id}")
data class PantryGet(val id: Long)


/**
 * Shopping Lists
 */
@Location("/lists/shops")
object ShoppingLists

@Location("/lists/shops/import/{code}")
data class ImportShoppingList(val code: String)

@Location("/lists/shops/{id}")
data class ShoppingListGet(val id: Long)


/**
 * Products
 */

@Location("products")
object ListProducts

/**
 * NEEDS REWRITE
 */


/**
 * User Login
 */
@Location("/login")
object UserLogin

@Location("/register/guest")
object UserGuestRegister


/**
 * User Pantry
 */
@Location("/user/disconnectPantry/{pantry_id}")
data class UserDisconnectPantry(val pantry_id: Long)


/**
 * User Shopping List
 */
@Location("/user/disconnectShoppingList/{shopping_list_id}")
data class UserDisconnectShoppingList(val shopping_list_id: Long)


/**
 * User Products
 */
@Location("/user/connectProduct/{product_id}")
data class UserConnectProduct(val product_id: Long)

@Location("/user/disconnectProduct/{product_id}")
data class UserDisconnectProduct(val product_id: Long)


/**
 * Pantries
 */
@Location("/pantries/update")
object UpdatePantry

@Location("/pantry/{id}")
data class GetPantry(val id: Long)


/**
 * Pantry Products
 */
@Location("/pantries/addProduct")
object  PantryAddProduct

@Location("/pantries/updateProduct")
object PantryUpdateProduct

@Location("/pantry/{pantry_id}/deleteProduct/{product_id}")
data class PantryDeleteProduct(val pantry_id: Long, val product_id: Long)


/**
 * Products
 */
@Location("/products/add")
object AddProduct

@Location("/products/update")
object UpdateProduct

@Location("/product/addBarcode")
object AddBarcode

@Location("/product/{id}")
data class GetProduct(val id: Long)


/**
 * Shopping List
 */
@Location("/shoppingLists/update")
object  UpdateShoppingList

@Location("/shoppingList/{shopping_list_id}/smartSortingInfo/{barcode}")
data class UpdateSmartSortingEntries(val shopping_list_id: Long, val barcode: String)


/**
 * Prices
 */
@Location("/prices/addPrice")
object AddPrice

@Location("/prices/deletePrice")
object DeletePrice

@Location("/prices/{latitude}/{longitude}")
data class GetShopPrices(val latitude: Double, val longitude: Double)


/**
 * Images
 */
@Location("/images/addImage")
object AddImage

@Location("/images/deleteImage")
object DeleteImage

@Location("/images/barcode/{barcode}")
data class GetProductImagesBarcode(val barcode: String)

@Location("/images/productId/{product_id}")
data class GetProductImagesProductId(val product_id: Long)


/**
 * Carts
 */
@Location("/cart/{shopping_list_id}")
data class BuyCart(val shopping_list_id: Long)

@Location("/cart/{shopping_list_id}/suggestion/{barcode}")
data class GetProductSuggestion(val shopping_list_id: Long, val barcode: String)


/**
 * Queue
 */
@Location("/queue/join/{latitude}/{longitude}/{num_items}")
data class JoinQueue(val latitude: Double, val longitude: Double, val num_items: Int)

@Location("/queue/leave")
object LeaveQueue

@Location("/queue/time/{latitude}/{longitude}")
data class TimeQueue(val latitude: Double, val longitude: Double)


/**
 * Translation
 */
@Location("/translation/{string}")
data class Translation(val string: String)


/**
 * Ratings
 */
@Location("/ratings/{barcode}/{rating}")
data class Ratings(val barcode: String, val rating: Int)
